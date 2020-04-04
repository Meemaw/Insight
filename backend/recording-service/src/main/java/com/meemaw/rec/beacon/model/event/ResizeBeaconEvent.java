package com.meemaw.rec.beacon.model.event;

public class ResizeBeaconEvent extends AbstractBeaconEvent {

  public int getInnerWidth() {
    return (int) args.get(0);
  }

  public int getInnerHeight() {
    return (int) args.get(1);
  }
}
