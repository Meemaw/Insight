package com.meemaw.rec.beacon.model.event;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BeaconEventType {

  NAVIGATE(BeaconEventTypeConstants.NAVIGATE), UNLOAD(BeaconEventTypeConstants.UNLOAD), RESIZE(
      BeaconEventTypeConstants.RESIZE), PERFORMANCE(
      BeaconEventTypeConstants.PERFORMANCE);

  String value;

}
