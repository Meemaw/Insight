package com.meemaw.rec.page.resource.v1;

import com.meemaw.rec.page.page.PageDTO;
import java.util.concurrent.CompletionStage;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(PageResource.PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PageResource {

  String PATH = "v1/page";

  @POST
  CompletionStage<Response> page(
      @NotNull(message = "Payload may not be blank") @Valid PageDTO payload);
}
