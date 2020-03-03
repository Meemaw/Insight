package com.meemaw.rec.model.beacon.event;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BeaconEventType {

  NAVIGATE(BeaconEventTypeConstants.NAVIGATE), UNLOAD(BeaconEventTypeConstants.UNLOAD), RESIZE(
      BeaconEventTypeConstants.RESIZE), PERFORMANCE(
      BeaconEventTypeConstants.PERFORMANCE);

  String value;

}
