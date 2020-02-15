package com.meemaw.auth.service.sso;

import com.meemaw.auth.model.user.UserDTO;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface SsoService {

  CompletionStage<String> createSession(UserDTO user);

  CompletionStage<Optional<UserDTO>> findSession(String sessionId);

  CompletionStage<Boolean> logout(String sessionId);

  CompletionStage<String> login(String email, String password);
}
