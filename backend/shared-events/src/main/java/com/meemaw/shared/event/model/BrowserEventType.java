package com.meemaw.shared.event.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BrowserEventType {

  NAVIGATE(BrowserEventTypeConstants.NAVIGATE), UNLOAD(BrowserEventTypeConstants.UNLOAD), RESIZE(
      BrowserEventTypeConstants.RESIZE), PERFORMANCE(
      BrowserEventTypeConstants.PERFORMANCE), CLICK(BrowserEventTypeConstants.CLICK), MOUSEMOVE(
      BrowserEventTypeConstants.MOUSEMOVE), MOUSEDOWN(BrowserEventTypeConstants.MOUSEDOWN), MOUSEUP(
      BrowserEventTypeConstants.MOUSEUP);

  String value;

}
