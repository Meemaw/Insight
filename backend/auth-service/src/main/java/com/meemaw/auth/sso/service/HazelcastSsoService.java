package com.meemaw.auth.service.sso;

import com.meemaw.auth.datasource.sso.SsoDatasource;
import com.meemaw.auth.model.sso.SsoUser;
import com.meemaw.auth.model.user.UserDTO;
import com.meemaw.auth.service.password.PasswordService;
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
