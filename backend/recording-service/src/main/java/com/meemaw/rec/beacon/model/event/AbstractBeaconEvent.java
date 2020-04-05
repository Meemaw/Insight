package com.meemaw.rec.beacon.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.util.List;

@JsonTypeInfo(
    use = Id.NAME,
    property = "e",
    defaultImpl = AbstractBeaconEvent.class)
@JsonSubTypes({
    @Type(value = NavigateBeaconEvent.class, name = BeaconEventTypeConstants.NAVIGATE),
    @Type(value = UnloadBeaconEvent.class, name = BeaconEventTypeConstants.UNLOAD),
    @Type(value = ResizeBeaconEvent.class, name = BeaconEventTypeConstants.RESIZE),
    @Type(value = PerformanceBeaconEvent.class, name = BeaconEventTypeConstants.PERFORMANCE),
    @Type(value = ClickBeaconEvent.class, name = BeaconEventTypeConstants.CLICK),
    @Type(value = MouseMoveBeaconEvent.class, name = BeaconEventTypeConstants.MOUSEMOVE),
    @Type(value = MouseDownBeaconEvent.class, name = BeaconEventTypeConstants.MOUSEDOWN),
    @Type(value = MouseUpBeaconEvent.class, name = BeaconEventTypeConstants.MOUSEUP),
})
public abstract class AbstractBeaconEvent {

  @JsonProperty("t")
  int timestamp;

  @JsonProperty("e")
  BeaconEventType eventType;

  @JsonProperty("a")
  List<Object> args;

}
