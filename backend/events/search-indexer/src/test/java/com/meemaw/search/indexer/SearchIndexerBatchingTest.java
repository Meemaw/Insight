package com.meemaw.search.indexer;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.with;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.meemaw.events.model.external.UserEvent;
import com.meemaw.events.model.internal.AbstractBrowserEvent;
import com.meemaw.test.testconainers.elasticsearch.Elasticsearch;
import com.meemaw.test.testconainers.elasticsearch.ElasticsearchExtension;
import com.meemaw.test.testconainers.kafka.Kafka;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;

@Elasticsearch
@Kafka
@Slf4j
public class SearchIndexerBatchingTest extends AbstractSearchIndexerTest {

  @Test
  public void indexBatches() throws IOException, URISyntaxException {
    // setup ElasticSearch
    RestHighLevelClient client = ElasticsearchExtension.getInstance().restHighLevelClient();
    createIndex(client);

    // setup Kafka
    KafkaProducer<String, UserEvent<AbstractBrowserEvent>> producer = configureProducer();
    writeSmallBatch(producer);
    writeLargeBatch(producer);

    spawnIndexer();

    // initially nothing is indexed
    assertEquals(
        0, client.search(SEARCH_REQUEST, RequestOptions.DEFAULT).getHits().getTotalHits().value);

    // should index records that were created earlier
    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              SearchResponse response = client.search(SEARCH_REQUEST, RequestOptions.DEFAULT);
              log.info("Total hits: {}", response.getHits().getTotalHits().value);
              return response.getHits().getTotalHits().value == 383;
            });

    writeSmallBatch(producer);

    // should index live events
    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              SearchResponse response = client.search(SEARCH_REQUEST, RequestOptions.DEFAULT);
              log.info("Total hits: {}", response.getHits().getTotalHits().value);
              return response.getHits().getTotalHits().value == 384;
            });

    // spawn a few more indexers
    for (int i = 0; i < 5; i++) {
      spawnIndexer();
    }

    // spawn many more batches
    int numExtraBatches = 100;
    for (int i = 0; i < numExtraBatches; i++) {
      writeSmallBatch(producer);
    }

    // should index live events only once
    with()
        .atMost(5, TimeUnit.SECONDS)
        .until(
            () -> {
              SearchResponse response = client.search(SEARCH_REQUEST, RequestOptions.DEFAULT);
              return response.getHits().getTotalHits().value == 384 + numExtraBatches;
            });
  }
}
