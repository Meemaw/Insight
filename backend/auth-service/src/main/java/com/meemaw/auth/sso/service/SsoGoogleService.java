package com.meemaw.auth.sso.service;

import com.meemaw.auth.sso.model.SsoSocialLogin;
import java.util.concurrent.CompletionStage;

public interface SsoGoogleService {

  String signin(String dest, String redirectURI);

  CompletionStage<SsoSocialLogin> oauth2callback(String dest, String code, String redirectURI);
}
