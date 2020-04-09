package com.meemaw.shared.event.model;

public class BrowserNavigateEvent extends AbstractBrowserEvent {

  public String getLocation() {
    return (String) args.get(0);
  }

  public String getTitle() {
    return (String) args.get(1);
  }
}
