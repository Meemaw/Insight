package com.meemas.shared;

import java.util.List;

import lombok.Data;

@Data
public class Sort {
  public List<Pair<String, SortDirection>> order;

  Sort(List<Pair<String, SortDirection>> order) {
    this.order = order;
  }
}
