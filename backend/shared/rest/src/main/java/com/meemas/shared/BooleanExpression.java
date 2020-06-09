package com.meemas.shared;

import lombok.Data;

import java.util.List;

@Data
public class BooleanExpression extends Expression {
  public BooleanOp op;
  public List<Expression> childNodes;

  BooleanExpression(BooleanOp op, List<Expression> childNodes) {
    this.op = op;
    this.childNodes = childNodes;
  }
}
