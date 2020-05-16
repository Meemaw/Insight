package com.meemas.shared;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Parser {
  public static Query buildFromParams(Map<String, List<String>> params) {
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
      sorts.add(new Pair<>(string, direction));
    }
    return sorts;
  }

  static Pair<TermOperation, String> extractOperationAndValue(String text) {
    int colon = text.indexOf(":");
    TermOperation op = TermOperation.valueOf(text.substring(0, colon).toUpperCase());
    return new Pair(op, text.substring(colon + 1, text.length()));
  }

  public static Map<String, List<String>> splitQuery(URL url) throws UnsupportedEncodingException {
    final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
    final String[] pairs = url.getQuery().split("&");
    for (String pair : pairs) {
      final int idx = pair.indexOf("=");
      final String key = idx > 0 ? pair.substring(0, idx) : pair;
      if (!query_pairs.containsKey(key)) {
        query_pairs.put(key, new LinkedList<String>());
      }
      final String value = idx > 0 && pair.length() > idx + 1 ? pair.substring(idx + 1) : null;
      query_pairs.get(key).add(value);
    }
    return query_pairs;
  }
}
