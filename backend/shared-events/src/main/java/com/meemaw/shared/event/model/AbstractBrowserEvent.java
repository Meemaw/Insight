package com.meemaw.shared.event.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.util.List;
import lombok.Getter;

@JsonTypeInfo(
    use = Id.NAME,
    property = "e",
    defaultImpl = AbstractBrowserEvent.class)
@JsonSubTypes({
    @Type(value = BrowserNavigateEvent.class, name = BrowserEventTypeConstants.NAVIGATE),
    @Type(value = BrowserUnloadEvent.class, name = BrowserEventTypeConstants.UNLOAD),
    @Type(value = BrowserResizeEvent.class, name = BrowserEventTypeConstants.RESIZE),
    @Type(value = BrowserPerformanceEvent.class, name = BrowserEventTypeConstants.PERFORMANCE),
    @Type(value = BrowserClickEvent.class, name = BrowserEventTypeConstants.CLICK),
    @Type(value = BrowserMouseMoveEvent.class, name = BrowserEventTypeConstants.MOUSEMOVE),
    @Type(value = BrowserMouseDownEvent.class, name = BrowserEventTypeConstants.MOUSEDOWN),
    @Type(value = BrowserMouseUpEvent.class, name = BrowserEventTypeConstants.MOUSEUP),
})
@Getter
public abstract class AbstractBrowserEvent {

  @JsonProperty("t")
  int timestamp;

  @JsonProperty("e")
  BrowserEventType eventType;

  @JsonProperty("a")
  List<Object> args;

}
