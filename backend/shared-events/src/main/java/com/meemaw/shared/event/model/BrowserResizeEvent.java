package com.meemaw.shared.event.model;

public class BrowserResizeEvent extends AbstractBrowserEvent {

  public int getInnerWidth() {
    return (int) args.get(0);
  }

  public int getInnerHeight() {
    return (int) args.get(1);
  }
}
