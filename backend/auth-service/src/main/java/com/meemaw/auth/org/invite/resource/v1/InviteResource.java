package com.meemaw.auth.org.resource.v1;

import com.meemaw.auth.org.resource.model.dto.TeamInviteAcceptDTO;
import com.meemaw.auth.org.resource.model.dto.TeamInviteCreateDTO;
import com.meemaw.auth.sso.core.CookieAuth;
import java.util.concurrent.CompletionStage;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(OrganizationResource.PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface OrganizationResource {

  String PATH = "v1/org";

  @POST
  @Path("invites")
  @CookieAuth
  CompletionStage<Response> createInvite(
      @NotNull(message = "Payload is required") @Valid TeamInviteCreateDTO teamInviteCreate);

  @GET
  @Path("invites")
  @CookieAuth
  CompletionStage<Response> listInvites();

  @POST
  @Path("invite/accept")
  CompletionStage<Response> acceptInvite(
      @NotNull(message = "Payload is required") @Valid TeamInviteAcceptDTO teamInviteAccept);

}
