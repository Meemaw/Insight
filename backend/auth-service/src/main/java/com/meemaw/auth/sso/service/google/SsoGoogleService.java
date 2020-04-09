package com.meemaw.auth.sso.service.google;

import com.meemaw.auth.sso.model.SsoSocialLogin;
import io.smallrye.mutiny.Uni;
import java.net.URI;

public interface SsoGoogleService {

  URI buildAuthorizationURI(String state, String redirectURI);

  String secureState(String destination);

  Uni<SsoSocialLogin> oauth2callback(String state, String sessionState, String code,
      String redirectURI);
}
