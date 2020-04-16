package com.meemaw.search.indexer;

import com.meemaw.shared.event.model.AbstractBrowserEvent;
import com.meemaw.shared.event.model.AbstractBrowserEventBatch;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Cancellable;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

@Slf4j
public class EventBatchProcessor {

  private final RestHighLevelClient client;

  public EventBatchProcessor(HttpHost... hosts) {
    this.client = new RestHighLevelClient(RestClient.builder(hosts));
  }

  private IndexRequest eventIndex(AbstractBrowserEvent event) {
    Map<String, Object> index = event.index();
    return new IndexRequest(EventIndex.NAME).source(index);
  }

  public Cancellable process(AbstractBrowserEventBatch batch) {
    log.info("Processing batch {}", batch);

    BulkRequest request = new BulkRequest();
    for (AbstractBrowserEvent event : batch.getEvents()) {
      request.add(eventIndex(event));
    }

    ActionListener<BulkResponse> listener = new ActionListener<>() {
      @Override
      public void onResponse(BulkResponse bulkItemResponses) {
        long tookMillis = bulkItemResponses.getIngestTookInMillis();
        long itemCount = bulkItemResponses.getItems().length;
        log.info("Successfully indexed {} events in {}ms", itemCount, tookMillis);
      }

      @Override
      public void onFailure(Exception ex) {
        log.error("Failed to index events", ex);
      }
    };

    return client.bulkAsync(request, RequestOptions.DEFAULT, listener);
  }
}
