package com.meemaw.shared.event.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BrowserUnloadEvent extends AbstractBrowserEvent {

  @JsonIgnore
  public String getLocation() {
    return (String) args.get(0);
  }

}
