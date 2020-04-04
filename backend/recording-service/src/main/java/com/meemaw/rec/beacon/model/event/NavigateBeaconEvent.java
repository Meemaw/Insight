package com.meemaw.rec.beacon.model.event;

public class NavigateBeaconEvent extends AbstractBeaconEvent {

  public String getLocation() {
    return (String) args.get(0);
  }

  public String getTitle() {
    return (String) args.get(1);
  }
}
