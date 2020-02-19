package com.meemaw.auth.password.resource.v1;

import com.meemaw.auth.password.model.dto.PasswordForgotRequestDTO;
import com.meemaw.auth.password.model.dto.PasswordResetRequestDTO;
import java.util.concurrent.CompletionStage;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(PasswordResource.PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PasswordResource {

  String PATH = "v1/password";

  @POST
  @Path("forgot")
  CompletionStage<Response> forgot(
      @NotNull(message = "Payload is required") @Valid PasswordForgotRequestDTO passwordForgotRequestDTO);

  @POST
  @Path("reset")
  CompletionStage<Response> reset(
      @NotNull(message = "Payload is required") @Valid PasswordResetRequestDTO passwordResetRequestDTO);

}
