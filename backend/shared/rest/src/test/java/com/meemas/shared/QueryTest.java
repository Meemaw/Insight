package com.meemas.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class QueryTest {

  @Test
  public void testParseSorts() {
    String input = "+field1,-age,name";
    Sort s = new Sort(Parser.parseSorts(input));

    Pair[] arr = { new Pair(SortDirection.ASC, "field1"), new Pair(SortDirection.DESC, "age"),
        new Pair(SortDirection.ASC, "name") };
    Sort expected = new Sort(Arrays.asList(arr));
    assertEquals(s, expected);
  }

  @Test
  public void testExtractOperationAndValue() {
    String text = "lte:123";
    Pair<TermOperation, String> expected = new Pair(TermOperation.LTE, "123");

    Pair<TermOperation, String> res = Parser.extractOperationAndValue(text);
    assertEquals(expected, res);
  }

  @Test
  public void testParse() throws UnsupportedEncodingException, MalformedURLException {
    String input = "http://www.abc.com?field1=lte:123&sort_by=+field2,-age&field2=gte:matej";
    Map<String, List<String>> params = Parser.splitQuery(new URL(input));
    Query res = Parser.buildFromParams(params);

    Expression[] ex = { new TermExpression("field1", TermOperation.LTE, "123"),
        new TermExpression("field2", TermOperation.GTE, "matej") };
    Pair[] orders = { new Pair(SortDirection.ASC, "field2"), new Pair(SortDirection.DESC, "age") };
    Query expected = new Query(new BooleanExpression(BooleanOp.AND, Arrays.asList(ex)),
        new Sort(Arrays.asList(orders)));

    assertEquals(expected, res);
  }
}
