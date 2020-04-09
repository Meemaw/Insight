package com.meemaw.shared.event.model;

public class BrowserUnloadEvent extends AbstractBrowserEvent {

  public String getLocation() {
    return (String) args.get(0);
  }

}
