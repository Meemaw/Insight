package com.meemaw.auth.sso.service.google;

import com.meemaw.auth.sso.model.SsoSocialLogin;
import java.net.URI;
import java.util.concurrent.CompletionStage;

public interface SsoGoogleService {

  URI buildAuthorizationUri(String state, String redirectUri);

  String secureState(String destination);

  CompletionStage<SsoSocialLogin> oauth2callback(
      String state, String sessionState, String code, String redirectUri);
}
