package com.meemaw.auth.signup.resource.v1;

import com.meemaw.auth.signup.model.dto.SignupRequestCompleteDTO;
import com.meemaw.auth.signup.service.SignupService;
import com.meemaw.shared.rest.response.DataResponse;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class SignupResourceImpl implements SignupResource {

  @Inject
  SignupService signupService;

  @Override
  public CompletionStage<Response> signup(String email) {
    return signupService.create(email).thenApply(DataResponse::ok);
  }

  @Override
  public CompletionStage<Response> signupExists(String email, String org, UUID token) {
    return signupService.exists(email, org, token).thenApply(DataResponse::ok);
  }

  @Override
  public CompletionStage<Response> signupComplete(SignupRequestCompleteDTO req) {
    return signupService.complete(req).thenApply(DataResponse::ok);
  }
}
