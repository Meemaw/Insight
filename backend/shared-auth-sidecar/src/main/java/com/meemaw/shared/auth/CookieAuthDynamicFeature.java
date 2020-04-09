package com.meemaw.shared.auth;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CookieAuthDynamicFeature extends AbstractCookieAuthDynamicFeature {


  @Override
  protected ContainerRequestFilter cookieAuthFilter() {
    return new CookieAuthFilter();
  }

  private class CookieAuthFilter extends AbstractCookieAuthFilter<AuthUser> {

    @Override
    protected CompletionStage<Optional<AuthUser>> findSession(String sessionId) {
      return CompletableFuture.completedFuture(Optional.empty());
    }
  }


}
