package com.meemaw.auth.sso.service;

import com.meemaw.auth.sso.datasource.SsoDatasource;
import com.meemaw.auth.sso.model.SsoUser;
import com.meemaw.auth.user.model.UserDTO;
import com.meemaw.auth.password.service.PasswordService;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class HazelcastSsoService implements SsoService {

  @Inject
  SsoDatasource ssoDatasource;

  @Inject
  PasswordService passwordService;

  public CompletionStage<String> createSession(UserDTO user) {
    return ssoDatasource.createSession(user);
  }

  public CompletionStage<Optional<UserDTO>> findSession(String sessionId) {
    return ssoDatasource.findSession(sessionId)
        .thenApply(maybeSsoUser -> maybeSsoUser.map(SsoUser::dto));
  }

  public CompletionStage<Boolean> logout(String sessionId) {
    return ssoDatasource.deleteSession(sessionId);
  }

  public CompletionStage<String> login(String email, String password) {
    return passwordService.verifyPassword(email, password).thenCompose(this::createSession);
  }
}
