package com.meemaw.search.indexer;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.with;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.meemaw.events.model.external.UserEvent;
import com.meemaw.events.model.internal.AbstractBrowserEvent;
import com.meemaw.test.testconainers.elasticsearch.ElasticsearchTestContainer;
import com.meemaw.test.testconainers.kafka.Kafka;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;

@Kafka
@Slf4j
public class SearchIndexerConnectionRecoverTest extends AbstractSearchIndexerTest {

  private final ElasticsearchTestContainer ELASTICSEARCH = ElasticsearchTestContainer.newInstance();

  @Test
  public void canRecoverAfterConnectionRefused() throws IOException, URISyntaxException {
    // setup Kafka
    KafkaProducer<String, UserEvent<AbstractBrowserEvent>> producer = configureProducer();
    KafkaConsumer<String, UserEvent<AbstractBrowserEvent>> retryQueueConsumer =
        retryQueueConsumer();

    writeSmallBatch(producer);

    // setup ElasticSearch
    RestHighLevelClient client =
        new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 10000, "http")));

    spawnIndexer(bootstrapServers(), client);

    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              ConsumerRecords<String, UserEvent<AbstractBrowserEvent>> records =
                  retryQueueConsumer.poll(Duration.ofMillis(1000));
              log.info("retry queue record count: {}", records.count());
              return records.count() == 1;
            });

    // Reconfigure ElasticSearch to actual node
    ELASTICSEARCH.start();
    client
        .getLowLevelClient()
        .setNodes(
            Stream.of(ELASTICSEARCH.getHttpHost())
                .map(Node::new)
                .collect(Collectors.toList()));

    createIndex(client);
    writeSmallBatch(producer);

    // initially nothing is indexed
    assertEquals(
        0, client.search(SEARCH_REQUEST, RequestOptions.DEFAULT).getHits().getTotalHits().value);

    with()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              SearchResponse response = client.search(SEARCH_REQUEST, RequestOptions.DEFAULT);
              log.info("totalHits: {}", response.getHits().getTotalHits().value);
              return response.getHits().getTotalHits().value == 1;
            });
  }
}
