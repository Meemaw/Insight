package com.meemaw.search.indexer;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.with;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.meemaw.shared.event.kafka.EventsChannel;
import com.meemaw.shared.event.kafka.serialization.BrowserEventBatchSerializer;
import com.meemaw.shared.event.model.AbstractBrowserEventBatch;
import com.meemaw.test.rest.mappers.JacksonMapper;
import com.meemaw.test.testconainers.elasticsearch.Elasticsearch;
import com.meemaw.test.testconainers.elasticsearch.ElasticsearchExtension;
import com.meemaw.test.testconainers.kafka.Kafka;
import com.meemaw.test.testconainers.kafka.KafkaExtension;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.junit.jupiter.api.Test;

@Elasticsearch
@Kafka
public class SearchIndexerIntegrationTest {

  @Test
  public void indexBatches()
      throws IOException, URISyntaxException, ExecutionException, InterruptedException {
    // setup Elasticsearch
    RestHighLevelClient client = ElasticsearchExtension.getInstance().restHighLevelClient();
    CreateIndexRequest createIndexRequest = new CreateIndexRequest(EventIndex.NAME);
    client.indices().create(createIndexRequest, RequestOptions.DEFAULT);

    // setup Kafka
    KafkaProducer<String, AbstractBrowserEventBatch> producer = configureProducer();
    producer
        .send(readKafkaRecord("eventsBatch/small.json"), (meta, exception) -> {
          if (exception != null) {
            throw new RuntimeException(exception);
          }
          System.out
              .println(String
                  .format("Wrote small batch to topic=%s, partition=%d, offset=%d", meta.topic(),
                      meta.partition(), meta.offset()));
        }).get();

    producer
        .send(readKafkaRecord("eventsBatch/large.json"), (meta, exception) -> {
          if (exception != null) {
            throw new RuntimeException(exception);
          }
          System.out
              .println(String
                  .format("Wrote large batch to topic=%s, partition=%d, offset=%d", meta.topic(),
                      meta.partition(), meta.offset()));
        }).get();

    spawnIndexer();

    SearchRequest searchRequest = new SearchRequest().indices(EventIndex.NAME);

    // initially nothing is indexed
    assertEquals(0,
        client.search(searchRequest, RequestOptions.DEFAULT).getHits().getTotalHits().value);

    // should index records that were created earlier
    await().atMost(10, TimeUnit.SECONDS).until(() -> {
      SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
      long totalHits = response.getHits().getTotalHits().value;
      System.out.println("Total hits: " + totalHits);
      return totalHits == 383;
    });

    producer
        .send(readKafkaRecord("eventsBatch/small.json"), (meta, exception) -> {
          if (exception != null) {
            throw new RuntimeException(exception);
          }
          System.out
              .println(String
                  .format("Wrote small batch to topic=%s, partition=%d, offset=%d", meta.topic(),
                      meta.partition(), meta.offset()));
        }).get();

    // should index live events
    await().atMost(10, TimeUnit.SECONDS).until(() -> {
      SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
      long totalHits = response.getHits().getTotalHits().value;
      System.out.println("Total hits: " + totalHits);
      return totalHits == 384;
    });

    // spawn a few more indexers
    for (int i = 0; i < 5; i++) {
      spawnIndexer();
    }

    int numExtraEvents = 100;
    for (int i = 0; i < numExtraEvents; i++) {
      producer
          .send(readKafkaRecord("eventsBatch/small.json"), (meta, exception) -> {
            if (exception != null) {
              throw new RuntimeException(exception);
            }
            System.out
                .println(String
                    .format("Wrote small batch to topic=%s, partition=%d, offset=%d", meta.topic(),
                        meta.partition(), meta.offset()));
          }).get();
    }

    // should index live events only once
    with()
        .pollDelay(5, TimeUnit.SECONDS)
        .atMost(10, TimeUnit.SECONDS)
        .until(() -> {
          SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
          long totalHits = response.getHits().getTotalHits().value;
          System.out.println("Total hits: " + totalHits);
          return totalHits == 384 + numExtraEvents;
        });
  }

  private void spawnIndexer() {
    String bootstrapServers = KafkaExtension.getInstance().getBootstrapServers();
    CompletableFuture.runAsync(() -> {
      SearchIndexer searchIndexer = new SearchIndexer(bootstrapServers,
          ElasticsearchExtension.getInstance().getHttpHost());
      searchIndexer.start();
    });
  }

  private KafkaProducer<String, AbstractBrowserEventBatch> configureProducer() {
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        KafkaExtension.getInstance().getBootstrapServers());
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        BrowserEventBatchSerializer.class.getName());
    return new KafkaProducer<>(props);
  }

  private ProducerRecord<String, AbstractBrowserEventBatch> kafkaRecord(
      AbstractBrowserEventBatch batch) {
    return new ProducerRecord<>(EventsChannel.ALL, batch);
  }

  private ProducerRecord<String, AbstractBrowserEventBatch> readKafkaRecord(String path)
      throws IOException, URISyntaxException {
    String payload = Files
        .readString(Path.of(getClass().getClassLoader().getResource(path).toURI()));
    AbstractBrowserEventBatch batch = JacksonMapper.get()
        .readValue(payload, AbstractBrowserEventBatch.class);
    return kafkaRecord(batch);
  }
}
