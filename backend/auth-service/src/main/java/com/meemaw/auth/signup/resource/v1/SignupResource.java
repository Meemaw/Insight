package com.meemaw.auth.resource.v1.signup;

import com.meemaw.auth.model.signup.dto.SignupCompleteRequestDTO;
import com.meemaw.auth.model.signup.dto.SignupVerifyRequestDTO;
import java.util.concurrent.CompletionStage;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(SignupResource.PATH)
public interface SignupResource {

  String PATH = "v1/signup";

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  CompletionStage<Response> signup(
      @NotBlank(message = "Email is required") @Email @FormParam("email") String email);

  @POST
  @Path("verify")
  @Consumes(MediaType.APPLICATION_JSON)
  CompletionStage<Response> signupVerify(
      @NotNull(message = "Payload is required") @Valid SignupVerifyRequestDTO req);

  @POST
  @Path("complete")
  @Consumes(MediaType.APPLICATION_JSON)
  CompletionStage<Response> signupComplete(
      @NotNull(message = "Payload is required") @Valid SignupCompleteRequestDTO req);

}
