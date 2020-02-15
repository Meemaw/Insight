package com.meemaw.auth.datasource.sso;

import com.meemaw.auth.model.sso.SsoUser;
import com.meemaw.auth.model.user.UserDTO;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface SsoDatasource {

  CompletionStage<String> createSession(UserDTO user);

  CompletionStage<Optional<SsoUser>> findSession(String sessionId);

  CompletionStage<Boolean> deleteSession(String sessionId);

}
