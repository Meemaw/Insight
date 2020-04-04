package com.meemaw.rec.resource.v1.beacon;

import java.util.concurrent.CompletionStage;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(BeaconResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface BeaconResource {

  String PATH = "v1/beacon";

  @POST
  @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
  CompletionStage<Response> beacon(String payload);

}
