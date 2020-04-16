package com.meemaw.search.indexer;

import com.meemaw.shared.event.kafka.EventsChannel;
import com.meemaw.shared.event.kafka.serialization.BrowserEventBatchDeserializer;
import com.meemaw.shared.event.model.AbstractBrowserEventBatch;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

@Slf4j
public class EventBatchConsumer {

  private static final String GROUP_ID = "search-indexer";
  private static final Duration ONE_SECOND = Duration.ofMillis(1000);

  private final Duration poolDuration;
  private final KafkaConsumer<String, AbstractBrowserEventBatch> consumer;

  private KafkaConsumer<String, AbstractBrowserEventBatch> configureConsumer(
      String bootstrapServers) {
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        BrowserEventBatchDeserializer.class.getName());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    KafkaConsumer<String, AbstractBrowserEventBatch> consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Collections.singletonList(EventsChannel.ALL));
    return consumer;
  }

  public EventBatchConsumer(String bootstrapServers) {
    this(ONE_SECOND, bootstrapServers);
  }

  private EventBatchConsumer(Duration poolDuration, String bootstrapServers) {
    this.poolDuration = poolDuration;
    this.consumer = configureConsumer(bootstrapServers);
  }

  public ConsumerRecords<String, AbstractBrowserEventBatch> poll() {
    return consumer.poll(poolDuration);
  }

  public void commit() {
    consumer.commitAsync((meta, ex) -> {
      if (ex != null) {
        log.error("Something went wrong while committing offset", ex);
      }
    });
  }

}
