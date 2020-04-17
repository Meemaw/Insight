package com.meemaw.search.indexer;

import com.meemaw.search.indexer.exception.ElasticsearchRecoverableException;
import com.meemaw.shared.event.model.AbstractBrowserEvent;
import com.meemaw.shared.event.model.AbstractBrowserEventBatch;
import java.io.IOException;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;

@Log4j2
public class EventBatchProcessor {

  private static final String INTERNAL_SERVER_ERROR = RestStatus.INTERNAL_SERVER_ERROR.name();
  private static final String SERVICE_UNAVAILABLE = RestStatus.SERVICE_UNAVAILABLE.name();
  private static final int RECONNECT_TIMEOUT_MILLIS = 10000;

  private final RestHighLevelClient client;
  private final RequestOptions requestOptions;

  public EventBatchProcessor(HttpHost... hosts) {
    this.client = new RestHighLevelClient(RestClient.builder(hosts));
    this.requestOptions = RequestOptions.DEFAULT;
  }

  private IndexRequest eventIndex(AbstractBrowserEvent event) {
    Map<String, Object> index = event.index();
    return new IndexRequest(EventIndex.NAME).source(index);
  }

  public void shutdown() throws IOException {
    client.close();
  }


  public boolean process(AbstractBrowserEventBatch batch)
      throws InterruptedException, ElasticsearchRecoverableException {
    boolean commitOffset = true;
    boolean isServerError = false;
    boolean isServiceUnavailable = false;

    log.info("Processing batch {}", batch);

    BulkRequest request = new BulkRequest();
    for (AbstractBrowserEvent event : batch.getEvents()) {
      request.add(eventIndex(event));
    }

    try {
      BulkResponse bulkResponse = client.bulk(request, requestOptions);
      long tookMillis = bulkResponse.getIngestTookInMillis();
      int itemCount = bulkResponse.getItems().length;
      log.info("Time to POST index requests to ElasticSearch: {}ms {} items", tookMillis,
          itemCount);

      if (bulkResponse.hasFailures()) {
        String failure = bulkResponse.buildFailureMessage();
        log.error("Bulk POST index request to ElasticSearch has errors: {}", failure);
        int failedCount = 0;
        for (BulkItemResponse bulkItemResponse : bulkResponse) {
          if (bulkItemResponse.isFailed()) {
            failedCount++;
            String errorMessage = bulkItemResponse.getFailure().getMessage();
            String restResponse = bulkItemResponse.getFailure().getStatus().name();
            log.error("Failed Message #{}, REST response:{}; errorMessage:{}", failedCount,
                restResponse, errorMessage);

            if (SERVICE_UNAVAILABLE.equals(restResponse)) {
              isServiceUnavailable = true;
            } else if (INTERNAL_SERVER_ERROR.equals(restResponse)) {
              isServerError = true;
            }

            // TODO: write to dead latter queue
          }
        }

        if (isServiceUnavailable || isServerError) {
          log.error(
              "ElasticSearch cluster unavailable, thread is sleeping for {} ms, after this current batch will be reprocessed",
              RECONNECT_TIMEOUT_MILLIS);
          Thread.sleep(RECONNECT_TIMEOUT_MILLIS);
          throw new ElasticsearchRecoverableException(
              "Recovering after an SERVICE_UNAVAILABLE response from ElasticSearch - will re-try processing current batch");
        }

        log.error("FAILURES: # of failed to POST index requests to ElasticSearch: {} ",
            failedCount);
      }
    } catch (IOException ex) {
      log.error("Something went wrong while indexing bulk request", ex);
      commitOffset = false;
    }

    return commitOffset;
  }


}
