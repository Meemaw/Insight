package com.meemaw.rec.beacon.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.meemaw.rec.beacon.model.event.AbstractBeaconEvent;
import com.meemaw.rec.shared.Recorded;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class BeaconDTO extends Recorded {

  @JsonProperty("s")
  @NotNull(message = "s may not be null")
  @Min(message = "s must be greater than 0", value = 1)
  private int sequence;

  @JsonProperty("e")
  @NotEmpty(message = "e may not be empty")
  private List<AbstractBeaconEvent> events;

  public int getSequence() {
    return sequence;
  }

  public List<AbstractBeaconEvent> getEvents() {
    return events;
  }
}
