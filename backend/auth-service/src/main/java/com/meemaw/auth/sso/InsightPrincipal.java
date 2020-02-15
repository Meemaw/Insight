package com.meemaw.auth.sso;

import com.meemaw.auth.model.sso.SsoUser;
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
