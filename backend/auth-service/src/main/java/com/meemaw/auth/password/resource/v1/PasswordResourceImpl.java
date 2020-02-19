package com.meemaw.auth.password.resource.v1;

import com.meemaw.auth.password.model.dto.PasswordForgotRequestDTO;
import com.meemaw.auth.password.model.dto.PasswordResetRequestDTO;
import com.meemaw.auth.password.service.PasswordService;
import com.meemaw.shared.rest.response.DataResponse;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class PasswordResourceImpl implements PasswordResource {

  @Inject
  PasswordService passwordService;

  @Override
  public CompletionStage<Response> forgot(PasswordForgotRequestDTO passwordForgotRequestDTO) {
    return passwordService.forgot(passwordForgotRequestDTO.getEmail())
        .thenApply(DataResponse::created);
  }

  @Override
  public CompletionStage<Response> reset(PasswordResetRequestDTO passwordResetRequestDTO) {
    return passwordService.reset(passwordResetRequestDTO).thenApply(DataResponse::ok);
  }
}
