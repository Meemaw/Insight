package com.meemaw.shared.event.kafka.serialization;

import com.meemaw.shared.event.model.AbstractBrowserEventBatch;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class BrowserEventBatchDeserializer extends ObjectMapperDeserializer<AbstractBrowserEventBatch> {

  public BrowserEventBatchDeserializer() {
    super(AbstractBrowserEventBatch.class);
  }
}
