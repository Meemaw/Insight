package com.meemas.shared;

public enum TermOperation {
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
