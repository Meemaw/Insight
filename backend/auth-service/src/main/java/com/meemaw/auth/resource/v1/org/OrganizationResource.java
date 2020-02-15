package com.meemaw.auth.resource.v1.org;

import com.meemaw.auth.model.signup.dto.TeamInviteCreateDTO;
import com.meemaw.auth.sso.CookieAuth;
import java.util.concurrent.CompletionStage;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(OrganizationResource.PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@CookieAuth
public interface OrganizationResource {

  String PATH = "v1/org";

  @POST
  @Path("invite")
  CompletionStage<Response> invite(
      @NotNull(message = "Payload is required") @Valid TeamInviteCreateDTO teamInvite);

}
