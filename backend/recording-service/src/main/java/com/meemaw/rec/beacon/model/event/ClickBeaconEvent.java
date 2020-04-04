package com.meemaw.rec.beacon.model.event;

import java.util.List;

public class ClickBeaconEvent extends AbstractBeaconEvent {

  public int getClientX() {
    return (int) args.get(0);
  }

  public int getClientY() {
    return (int) args.get(1);
  }

  public List<Object> getAttributes() {
    return args.subList(3, args.size());
  }

  public List<Object> getNodeWithAttributes() {
    return args.subList(2, args.size());
  }

  public String getNode() {
    String node = (String) args.get(2);
    return node.substring(1);
  }


}