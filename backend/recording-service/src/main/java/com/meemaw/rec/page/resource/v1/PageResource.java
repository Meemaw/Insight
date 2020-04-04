package com.meemaw.rec.page.resource.v1;

import java.util.concurrent.CompletionStage;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(PageResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface PageResource {

  String PATH = "v1/page";

  @POST
  @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
  CompletionStage<Response> page(String payload);
}
