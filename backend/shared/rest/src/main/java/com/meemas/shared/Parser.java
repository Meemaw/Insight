package com.meemas.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Parser {
  static Query buildFromParams(Map<String, List<String>> params) {
    List<Expression> expressions = new ArrayList();
    List<Pair<String, SortDirection>> sorts = new ArrayList();
    for (Entry<String, List<String>> pair : params.entrySet()) {
      String name = pair.getKey();
      System.out.println(name);
      if (name.equals("sort_by")) {
        sorts = parseSorts(pair.getValue().get(0));
      } else {
        String value = pair.getValue().get(0);
        Pair<TermOperation, String> p = extractOperationAndValue(value);
        expressions.add(new TermExpression(name, p.getElement0(), p.getElement1()));
      }
    }
    return new Query(new BooleanExpression(BooleanOp.AND, expressions), new Sort(sorts));
  }

  static List<Pair<String, SortDirection>> parseSorts(String text) {
    List<Pair<String, SortDirection>> sorts = new ArrayList();
    String[] fields = text.split(",");

    for (String string : fields) {
      SortDirection direction = SortDirection.ASC;
      if (string.startsWith("+")) {
        direction = SortDirection.ASC;
        string = string.substring(1);
      } else if (string.startsWith("-")) {
        direction = SortDirection.DESC;
        string = string.substring(1);
      }
      sorts.add(new Pair(direction, string));
    }
    return sorts;
  }

  static Pair<TermOperation, String> extractOperationAndValue(String text) {
    int colon = text.indexOf(":");
    TermOperation op = TermOperation.valueOf(text.substring(0, colon).toUpperCase());
    return new Pair(op, text.substring(colon + 1, text.length()));
  }
}
