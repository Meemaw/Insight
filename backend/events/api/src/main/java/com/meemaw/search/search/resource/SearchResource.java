package com.meemaw.search.search.resource;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CompletionStage;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.meemaw.search.events.resource.Event;
import lombok.Value;

@Value
class SearchResponse {
  long hits;
  Iterable<Event> events;

  SearchResponse(long hits, Iterable<Event> events) {
    this.hits = hits;
    this.events = events;
  }
}

@Path(SearchResource.PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SearchResource {

  String PATH = "v1/search";

  @GET
  CompletionStage<SearchResponse> search(@Context UriInfo uriInfo) throws IOException;

}
