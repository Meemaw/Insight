package com.meemaw.search.events.resource;

import lombok.Value;

@Value
public class Event {
  String name;

  public Event(String name) {
    this.name = name;
  }
}
