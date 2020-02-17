package com.meemaw.auth.resource.v1.signup;

import com.meemaw.auth.model.signup.dto.SignupCompleteRequestDTO;
import com.meemaw.auth.model.signup.dto.SignupVerifyRequestDTO;
import com.meemaw.auth.service.signup.SignupService;
import com.meemaw.shared.rest.response.DataResponse;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class SignupResourceImpl implements SignupResource {

  @Inject
  SignupService signupService;

  @Override
  public CompletionStage<Response> signup(String email) {
    return signupService.signup(email).thenApply(DataResponse::ok);
  }

  @Override
  public CompletionStage<Response> signupVerify(SignupVerifyRequestDTO req) {
    return signupService.verifySignupRequestExists(req).thenApply(DataResponse::ok);
  }

  @Override
  public CompletionStage<Response> signupComplete(SignupCompleteRequestDTO req) {
    return signupService.completeSignup(req).thenApply(DataResponse::ok);
  }
}
