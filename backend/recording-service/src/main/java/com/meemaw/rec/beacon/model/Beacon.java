package com.meemaw.rec.beacon.model;

import com.meemaw.rec.beacon.model.event.AbstractBeaconEvent;
import java.util.List;
import java.util.Objects;
import lombok.ToString;

@ToString
public class Beacon {

  private final int timestamp;
  private final int sequence;
  private final List<AbstractBeaconEvent> events;

  private Beacon(BeaconDTO dto) {
    this.timestamp = dto.getTimestamp();
    this.sequence = dto.getSequence();
    this.events = dto.getEvents();
  }

  public static Beacon from(BeaconDTO dto) {
    return new Beacon(Objects.requireNonNull(dto));
  }

  public int getTimestamp() {
    return timestamp;
  }

  public int getSequence() {
    return sequence;
  }

  public List<AbstractBeaconEvent> getEvents() {
    return events;
  }
}
