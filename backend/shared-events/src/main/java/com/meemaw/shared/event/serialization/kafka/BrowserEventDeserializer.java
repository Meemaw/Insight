package com.meemaw.shared.event.serialization.kafka;

import com.meemaw.shared.event.model.AbstractBrowserEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class BrowserEventDeserializer extends ObjectMapperDeserializer<AbstractBrowserEvent> {

  public BrowserEventDeserializer() {
    super(AbstractBrowserEvent.class);
  }
}
