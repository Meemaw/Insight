package com.meemaw.auth.sso.service.google;

import com.meemaw.auth.sso.model.SsoSocialLogin;
import java.net.URI;
import java.util.concurrent.CompletionStage;

public interface SsoGoogleService {

  URI buildAuthorizationURI(String dest, String redirectURI);

  CompletionStage<SsoSocialLogin> oauth2callback(String dest, String code, String redirectURI);
}
