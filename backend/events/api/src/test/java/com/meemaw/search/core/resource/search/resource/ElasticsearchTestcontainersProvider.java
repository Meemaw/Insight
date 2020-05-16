package com.meemaw.search.core.resource.search.resource;

import com.meemaw.search.search.resource.ElasticsearchProvider;
import com.meemaw.test.testconainers.elasticsearch.ElasticsearchTestExtension;
import io.quarkus.test.Mock;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import javax.enterprise.context.ApplicationScoped;

@Mock
@ApplicationScoped
public class ElasticsearchTestcontainersProvider extends ElasticsearchProvider {

    public RestHighLevelClient getInstance() {
        if (esInstance == null) {
            ElasticsearchTestExtension.start();
            esInstance = ElasticsearchTestExtension.getInstance().restHighLevelClient();
        }
        return esInstance;
    }

}

