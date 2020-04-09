package com.meemaw.rec.beacon.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meemaw.rec.shared.Recorded;
import com.meemaw.shared.event.model.AbstractBrowserEvent;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class BeaconDTO extends Recorded {

  @JsonProperty("s")
  @NotNull(message = "s may not be null")
  @Min(message = "s must be greater than 0", value = 1)
  private int sequence;

  @JsonProperty("e")
  @NotEmpty(message = "e may not be empty")
  private List<AbstractBrowserEvent> events;

  public int getSequence() {
    return sequence;
  }

  public List<AbstractBrowserEvent> getEvents() {
    return events;
  }
}
