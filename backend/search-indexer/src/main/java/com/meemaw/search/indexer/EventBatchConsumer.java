package com.meemaw.search.indexer;

import com.meemaw.search.indexer.logging.OffsetLoggingCallbackImpl;
import com.meemaw.shared.event.kafka.EventsChannel;
import com.meemaw.shared.event.kafka.serialization.BrowserEventBatchDeserializer;
import com.meemaw.shared.event.model.AbstractBrowserEventBatch;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

@Log4j2
public class EventBatchConsumer {

  private static final String GROUP_ID = "search-indexer";
  private static final Duration ONE_SECOND = Duration.ofMillis(1000);

  private final Duration poolDuration;
  private final KafkaConsumer<String, AbstractBrowserEventBatch> consumer;
  private final OffsetLoggingCallbackImpl offsetLoggingCallback;

  private KafkaConsumer<String, AbstractBrowserEventBatch> configureConsumer(
      String bootstrapServers) {
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        BrowserEventBatchDeserializer.class.getName());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    KafkaConsumer<String, AbstractBrowserEventBatch> consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Collections.singletonList(EventsChannel.ALL), offsetLoggingCallback);
    return consumer;
  }

  public EventBatchConsumer(String bootstrapServers) {
    this(ONE_SECOND, bootstrapServers);
  }

  private EventBatchConsumer(Duration poolDuration, String bootstrapServers) {
    this.poolDuration = poolDuration;
    this.offsetLoggingCallback = new OffsetLoggingCallbackImpl();
    this.consumer = configureConsumer(bootstrapServers);
  }

  public ConsumerRecords<String, AbstractBrowserEventBatch> poll() {
    return consumer.poll(poolDuration);
  }

  public void commit(Set<TopicPartition> partitions) {
    Map<TopicPartition, OffsetAndMetadata> offsets = getOffsets(partitions);
    log.info("Committing offsets {}", offsets);
    consumer.commitAsync(offsets, offsetLoggingCallback);
  }

  public void shutdown() {
    offsetLoggingCallback.getPartitionOffsetMap()
        .forEach((topicPartition, offset)
            -> log.info("Offset position during the shutdown: partition : {}, offset : {}",
            topicPartition.partition(),
            offset.offset()));
    consumer.close();
  }

  public Map<TopicPartition, OffsetAndMetadata> getOffsets(Set<TopicPartition> partitions) {
    Map<TopicPartition, OffsetAndMetadata> nextCommitableOffset = new HashMap<>(partitions.size());
    for (TopicPartition topicPartition : partitions) {
      long topicPosition = consumer.position(topicPartition);
      nextCommitableOffset.put(topicPartition, new OffsetAndMetadata(topicPosition));
    }
    return nextCommitableOffset;
  }


  public Map<TopicPartition, OffsetAndMetadata> getOffsets() {
    Set<TopicPartition> partitions = consumer.assignment();
    return getOffsets(partitions);
  }

}
