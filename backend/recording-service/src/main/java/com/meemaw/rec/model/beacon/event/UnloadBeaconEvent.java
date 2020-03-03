package com.meemaw.rec.model.beacon.event;

public class UnloadBeaconEvent extends AbstractBeaconEvent {

  public String getLocation() {
    return (String) args.get(0);
  }

}
