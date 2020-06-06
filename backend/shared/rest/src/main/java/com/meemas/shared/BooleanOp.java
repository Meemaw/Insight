package com.meemas.shared;

public enum BooleanOp {
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
