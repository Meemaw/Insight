package com.meemaw.page.model.beacon;

public enum BeaconEventType {

  LOAD(0), UNLOAD(1), RESIZE(2), PERFORMANCE(3);

  private final int value;

  BeaconEventType(final int newValue) {
    value = newValue;
  }

  public int getValue() {
    return value;
  }
}
