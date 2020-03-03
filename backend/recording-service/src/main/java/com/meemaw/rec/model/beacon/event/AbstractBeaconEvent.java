package com.meemaw.rec.model.beacon.event;

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
})
public abstract class AbstractBeaconEvent {

  @JsonProperty("t")
  int timestamp;

  @JsonProperty("e")
  BeaconEventType eventType;

  @JsonProperty("a")
  List<Object> args;

}
