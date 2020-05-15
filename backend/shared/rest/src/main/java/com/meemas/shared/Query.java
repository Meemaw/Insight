package com.meemas.shared;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

class Expression {
  protected List<Expression> childNodes;
}

enum BooleanOp {
  AND("AND"), OR("OR");

  private final String text;
  BooleanOp(final String text) {
    this.text = text;
  }

  /* (non-Javadoc)
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return text;
  }
}

enum TermOperation {
  EQ("EQ"), GT("GT"), GTE("GTE"), LT("LT"), LTE("LTE");

  private final String text;
  TermOperation(final String text) {
    this.text = text;
  }

  /* (non-Javadoc)
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return text;
  }
}

@Data
class TermExpression<T> extends Expression {
  String field;
  TermOperation op;
  T target;

  TermExpression(String field, TermOperation op, T target) {
    this.field = field;
    this.op = op;
    this.target = target;
  }
}

@Data
class BooleanExpression extends Expression {
  BooleanOp op;
  protected List<Expression> childNodes;

  BooleanExpression(BooleanOp op, List<Expression> childNodes) {
    this.op = op;
    this.childNodes = childNodes; 
  }
}

@Data
class Query {
  Expression expression;
  Sort order;

  Query(Expression e, Sort o) {
    this.expression = e;
    this.order = o;
  }
}
