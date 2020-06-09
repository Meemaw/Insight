package com.meemas.shared;

import lombok.Data;

@Data
public class TermExpression<T> extends Expression {
  public String field;
  public TermOperation op;
  public T target;

  TermExpression(String field, TermOperation op, T target) {
    this.field = field;
    this.op = op;
    this.target = target;
  }
}
