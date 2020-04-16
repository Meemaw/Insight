package com.meemaw.search.indexer;

import com.meemaw.shared.event.model.AbstractBrowserEventBatch;
import java.util.Arrays;
import java.util.Objects;
import javax.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.log4j.BasicConfigurator;

@Slf4j
public class SearchIndexer {

  private final EventBatchConsumer batchConsumer;
  private final EventBatchProcessor batchProcessor;

  public SearchIndexer(String bootstrapServers, HttpHost... hosts) {
    if (Objects.requireNonNull(hosts).length == 0) {
      throw new IllegalArgumentException("Please provide at least 1 Elasticsearch host");
    }
    this.batchConsumer = new EventBatchConsumer(Objects.requireNonNull(bootstrapServers));
    this.batchProcessor = new EventBatchProcessor(hosts);
  }

  public void start() {
    BasicConfigurator.configure();
    log.info("Starting search indexer");
    while (true) {
      ConsumerRecords<String, AbstractBrowserEventBatch> records = batchConsumer.poll();
      records.forEach(record -> batchProcessor.process(record.value()));
      batchConsumer.commit();
    }
  }

}
