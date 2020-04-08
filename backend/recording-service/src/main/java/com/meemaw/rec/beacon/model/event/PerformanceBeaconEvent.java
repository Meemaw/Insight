package com.meemaw.rec.beacon.model.event;

public class PerformanceBeaconEvent extends AbstractBeaconEvent {

  public String getName() {
    return (String) args.get(0);
  }

  public String getEntryType() {
    return (String) args.get(1);
  }

  public double getStartTime() {
    Object startTime = args.get(2);
    if (startTime instanceof Integer) {
      return (int) startTime;
    } else {
      return (Double) startTime;
    }
  }

  public double getDuration() {
    Object duration = args.get(3);
    if (duration instanceof Integer) {
      return (int) duration;
    } else {
      return (Double) duration;
    }
  }

}
