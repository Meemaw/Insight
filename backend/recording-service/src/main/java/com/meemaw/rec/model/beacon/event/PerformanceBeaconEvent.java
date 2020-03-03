package com.meemaw.rec.model.beacon.event;

public class PerformanceBeaconEvent extends AbstractBeaconEvent {

  public String getName() {
    return (String) args.get(0);
  }

  public String getEntryType() {
    return (String) args.get(1);
  }

  public int getStartTime() {
    return (int) args.get(2);
  }

  public int getDuration() {
    return (int) args.get(3);
  }

}
