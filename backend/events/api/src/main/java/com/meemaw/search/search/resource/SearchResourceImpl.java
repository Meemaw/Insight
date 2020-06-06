package com.meemaw.search.search.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.meemas.shared.*;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import com.meemaw.search.events.resource.Event;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

public class SearchResourceImpl implements SearchResource {
  @Inject ElasticsearchProvider esProvider;

  private List<Event> events = new ArrayList<>();

  @Override
  public CompletionStage<com.meemaw.search.search.resource.SearchResponse> search(@Context UriInfo uriInfo) throws IOException {
    System.out.println(uriInfo.getRequestUri().getQuery());
    Map<String, List<String>> map = new HashMap<>();
    for (java.util.Map.Entry<String, List<String>> field : uriInfo.getQueryParameters().entrySet()) {
      map.put(field.getKey(), field.getValue());
    }
    Query q = Parser.buildFromParams(map);
    SearchSourceBuilder req = EventsQueryBuilder.fromQuery(q);
    System.out.println(req.toString());

    SearchResponse resp = esProvider.getInstance().search(new SearchRequest().source(req), RequestOptions.DEFAULT);
    long hits = resp.getHits().getTotalHits().value;
    System.out.println("Found " + hits + " events");

    System.out.println(q.toString());
    events.add(new Event("abcdef"));
    return CompletableFuture.completedStage(new com.meemaw.search.search.resource.SearchResponse(hits, events));
  }
}


class EventsQueryBuilder {
  static SearchSourceBuilder fromQuery(Query q) {
    QueryBuilder terms = toQueryBuilder(q.expression);
    return addSort(new SearchSourceBuilder().query(terms), q.order);
  }

  static SearchSourceBuilder addSort(SearchSourceBuilder builder, Sort sort) {
    for (Pair<String, SortDirection> p: sort.order) {
      builder.sort(p.getElement0(), p.getElement1() == SortDirection.ASC ? SortOrder.ASC : SortOrder.DESC);
    }
    return builder;
  }

  static QueryBuilder toQueryBuilder(Expression exp) {
    if (exp instanceof TermExpression) {
      TermExpression term = (TermExpression) exp;
      return toQueryBuilder(term);
    } else if (exp instanceof BooleanExpression) {
      BooleanExpression bexp = (BooleanExpression) exp;
      return toQueryBuilder(bexp);
    }
    return null;
  }

  static QueryBuilder toQueryBuilder(BooleanExpression term) {
    BoolQueryBuilder builder = QueryBuilders.boolQuery();
    switch (term.op) {
      case OR: {
        for(Expression e : term.childNodes) {
          builder = builder.should(toQueryBuilder(e));
        }
      }
      case AND: {
        for(Expression e : term.childNodes) {
          builder = builder.must(toQueryBuilder(e));
        }
      }
    }
    return builder;
  }

  static QueryBuilder toQueryBuilder(TermExpression term) {
    switch (term.op) {
      case GT: {
        return QueryBuilders.rangeQuery(term.field).gt(term.target);
      }
      case GTE: {
        return QueryBuilders.rangeQuery(term.field).gte(term.target);
      }
      case LT: {
        return QueryBuilders.rangeQuery(term.field).lt(term.target);
      }
      case LTE: {
        return QueryBuilders.rangeQuery(term.field).lte(term.target);
      }
      case EQ: {
        return QueryBuilders.termQuery(term.field, term.target);
      }
    }
    return null;
  }
}
