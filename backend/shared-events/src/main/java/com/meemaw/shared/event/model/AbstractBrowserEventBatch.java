package com.meemaw.shared.event.model;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AbstractBrowserEventBatch {

  List<AbstractBrowserEvent> events;

  UUID pageId;

}
