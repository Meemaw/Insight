package com.meemas.shared;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Query {
  public final Expression expression;
  public final Sort order;

  Query(Expression e, Sort o) {
    this.expression = e;
    this.order = o;
  }
}
