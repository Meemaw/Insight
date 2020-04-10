package com.meemaw.shared.event.kafka.serialization;

import com.meemaw.shared.event.model.AbstractBrowserEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class BrowserEventSerializer extends ObjectMapperSerializer<AbstractBrowserEvent> {

}
