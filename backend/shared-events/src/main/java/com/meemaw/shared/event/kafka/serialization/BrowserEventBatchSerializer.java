package com.meemaw.shared.event.kafka.serialization;

import com.meemaw.shared.event.model.AbstractBrowserEventBatch;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class BrowserEventBatchSerializer extends ObjectMapperSerializer<AbstractBrowserEventBatch> {

}
