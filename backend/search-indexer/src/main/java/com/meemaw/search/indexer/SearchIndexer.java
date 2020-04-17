package com.meemaw.search.indexer;

import com.meemaw.search.indexer.exception.ElasticsearchRecoverableException;
import com.meemaw.shared.event.model.AbstractBrowserEventBatch;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import kafka.cluster.Partition;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;

@Log4j2
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

  public void onShutdown() {
    log.info("Shutting down ...");
    batchConsumer.shutdown();
    try {
      batchProcessor.shutdown();
    } catch (IOException ex) {
      log.error("Failed to gracefully shutdown batchProcessor", ex);
    }
  }

  public void start() {
    log.info("Starting search indexer");

    // TODO: bulk records together and send failed to DLQ
    while (true) {
      ConsumerRecords<String, AbstractBrowserEventBatch> records = batchConsumer.poll();
      int numMessagesInBatch = records.count();
      if (numMessagesInBatch == 0) {
        log.info("No messages received during this poll");
        continue;
      }

      Set<TopicPartition> partitions = records.partitions();
      int numFailedMessagesInBatch = 0;
      boolean isFirstRecordInPool = true;
      long poolStartMillis = 0L;

      for (ConsumerRecord<String, AbstractBrowserEventBatch> record : records) {
        int partition = record.partition();
        long offset = record.offset();
        AbstractBrowserEventBatch value = record.value();
        log.debug("received record: partition: {}, offset: {}, value: {}", partition, offset,
            value);

        if (isFirstRecordInPool) {
          isFirstRecordInPool = false;
          log.info("Start offset for partition {} in this poll : {}", partition, offset);
          poolStartMillis = System.currentTimeMillis();
        }

        try {
          boolean processedOK = batchProcessor.process(value);
          if (!processedOK) {
            log.error("Failed to process record: {} offset {}", value, offset);
            numFailedMessagesInBatch++;
          }
        } catch (InterruptedException | ElasticsearchRecoverableException ex) {
          numFailedMessagesInBatch++;
          log.error("Failed to process record: {} offset {}", value, offset, ex);
        }

        if (numFailedMessagesInBatch == 0) {
          batchConsumer.commit(partitions);
        }

        long endOfPollLoopMillis = System.currentTimeMillis();
        long timeToProcessLoopMillis = endOfPollLoopMillis - poolStartMillis;
        log.info(
            "Last poll snapshot: numMessagesInBatch: {}, numFailedMessages: {}, timeToProcessLoop: {}ms, partitions: {}",
            numMessagesInBatch,
            numFailedMessagesInBatch,
            timeToProcessLoopMillis,
            partitions);
      }
    }
  }


}
