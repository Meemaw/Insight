package com.meemaw.auth.sso.core;

import com.meemaw.auth.sso.model.SsoUser;
import java.util.UUID;
import javax.enterprise.context.RequestScoped;

@RequestScoped
public class InsightPrincipal {

  private UUID userId;
  private String org;

  public InsightPrincipal as(SsoUser ssoUser) {
    userId = ssoUser.getId();
    org = ssoUser.getOrg();
    return this;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getOrg() {
    return org;
  }
}
