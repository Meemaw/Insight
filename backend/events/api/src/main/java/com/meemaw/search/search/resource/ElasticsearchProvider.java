package com.meemaw.search.search.resource;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ElasticsearchProvider {

  protected static RestHighLevelClient esInstance;

  public RestHighLevelClient getInstance() {
    if (esInstance == null) {
      esInstance = new RestHighLevelClient(
              RestClient.builder(new HttpHost("localhost", 9200, "http")));
    }
    return esInstance;
  }

}
