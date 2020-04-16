package com.meemaw.search.indexer;

import org.apache.http.HttpHost;

public class SearchIndexerRunner {

  public static void main(String[] args) {
    HttpHost elasticsearchHost = new HttpHost("localhost", 9200, "http");
    String kafkaBootstrapServers = "localhost:9092";

    SearchIndexer searchIndexer = new SearchIndexer(kafkaBootstrapServers, elasticsearchHost);
    searchIndexer.start();
  }
}
