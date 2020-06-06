package com.meemaw.search.core.resource.search.resource;

import com.meemaw.search.events.resource.Event;
import com.meemaw.search.search.resource.ElasticsearchProvider;
import com.meemaw.test.testconainers.elasticsearch.Elasticsearch;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.context.ThreadContext;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.util.UUID;

import static com.meemaw.test.matchers.SameJSON.sameJson;
import static io.restassured.RestAssured.given;

@QuarkusTest
@Elasticsearch
public class SearchResourceTest {
    @Inject ElasticsearchProvider es;

    @BeforeEach
    public void setup() throws IOException {
        RestHighLevelClient client = es.getInstance();
        //client.indices().delete(new DeleteIndexRequest("events"), RequestOptions.DEFAULT);
        // Why is start needed here?
        client.index(new IndexRequest("events").source("name", "abc"), RequestOptions.DEFAULT);
    }

    @Test
    public void random_path_should_fail() throws IOException {
        given()
                .when().get("/v1/search")
                .then()
                .statusCode(200)
                .body(sameJson("{\"hits\":1,\"events\":[{\"name\":\"abcdef\"}]}"));
    }
}
