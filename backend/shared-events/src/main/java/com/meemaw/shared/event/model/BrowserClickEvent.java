package com.meemaw.shared.event.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BrowserClickEvent extends AbstractBrowserEvent {

  public int getClientX() {
    return (int) args.get(0);
  }

  public int getClientY() {
    return (int) args.get(1);
  }

  public List<Object> getAttributes() {
    int size = args.size();
    if (size <= 3) {
      return Collections.emptyList();
    }
    return args.subList(3, size);
  }

  public List<Object> getNodeWithAttributes() {
    int size = args.size();
    if (size <= 2) {
      return Collections.emptyList();
    }
    return args.subList(2, size);
  }

  public Optional<String> getNode() {
    if (args.size() <= 2) {
      return Optional.empty();
    }
    String node = (String) args.get(2);
    return Optional.of(node.substring(1));
  }

}