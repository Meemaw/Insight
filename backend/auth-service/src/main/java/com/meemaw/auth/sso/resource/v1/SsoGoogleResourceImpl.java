package com.meemaw.auth.sso.resource.v1;

import com.meemaw.auth.sso.model.SsoSession;
import com.meemaw.auth.sso.service.SsoGoogleService;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SsoGoogleResourceImpl implements SsoGoogleResource {

  @Inject
  SsoGoogleService ssoGoogleService;

  @Context
  UriInfo info;

  private String getRedirectUri() {
    // info.getBaseUri() will fail for tests
    return "http://localhost:8080/" + SsoGoogleResource.PATH + "/oauth2callback";
  }

  @Override
  public Response signin(String dest) {
    String Location = ssoGoogleService.signin(dest, getRedirectUri());
    return Response.status(Status.FOUND).header("Location", Location).build();
  }

  @Override
  public CompletionStage<Response> oauth2callback(String state, String code) {
    return ssoGoogleService.oauth2callback(state, code, getRedirectUri())
        .thenApply(ssoSocialLogin -> {
          String Location = ssoSocialLogin.getLocation();
          String SessionId = ssoSocialLogin.getSessionId();
          return Response.status(Status.FOUND).header("Location", Location)
              .cookie(SsoSession.cookie(SessionId)).build();
        });
  }
}
