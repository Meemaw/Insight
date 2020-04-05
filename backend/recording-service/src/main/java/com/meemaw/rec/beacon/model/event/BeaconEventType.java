package com.meemaw.rec.beacon.model.event;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BeaconEventType {

  NAVIGATE(BeaconEventTypeConstants.NAVIGATE), UNLOAD(BeaconEventTypeConstants.UNLOAD), RESIZE(
      BeaconEventTypeConstants.RESIZE), PERFORMANCE(
      BeaconEventTypeConstants.PERFORMANCE), CLICK(BeaconEventTypeConstants.CLICK), MOUSEMOVE(
      BeaconEventTypeConstants.MOUSEMOVE), MOUSEDOWN(BeaconEventTypeConstants.MOUSEDOWN), MOUSEUP(
      BeaconEventTypeConstants.MOUSEUP);

  String value;

}
