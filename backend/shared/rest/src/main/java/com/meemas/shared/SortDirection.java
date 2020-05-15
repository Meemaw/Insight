package com.meemas.shared;

enum SortDirection {
  ASC("ASC"), DESC("DESC");
  
  private final String text;
  SortDirection(final String text) {
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
