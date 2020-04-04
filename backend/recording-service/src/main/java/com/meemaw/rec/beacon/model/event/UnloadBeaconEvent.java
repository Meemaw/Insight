package com.meemaw.rec.beacon.model.event;

public class UnloadBeaconEvent extends AbstractBeaconEvent {

  public String getLocation() {
    return (String) args.get(0);
  }

}
