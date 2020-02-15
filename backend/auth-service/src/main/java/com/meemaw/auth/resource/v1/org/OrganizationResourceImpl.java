package com.meemaw.auth.resource.v1.org;

import com.meemaw.auth.model.signup.TeamInviteCreateIdentified;
import com.meemaw.auth.model.signup.dto.TeamInviteCreateDTO;
import com.meemaw.auth.service.signup.SignupService;
import com.meemaw.auth.sso.InsightPrincipal;
import com.meemaw.shared.rest.response.DataResponse;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class OrganizationResourceImpl implements OrganizationResource {

  @Inject
  InsightPrincipal principal;

  @Inject
  SignupService signupService;

  @Override
  public CompletionStage<Response> invite(TeamInviteCreateDTO teamInvite) {
    UUID creator = principal.getUserId();
    String org = principal.getOrg();

    TeamInviteCreateIdentified identified = new TeamInviteCreateIdentified(creator, org,
        teamInvite.getEmail(), teamInvite.getRole());
    return signupService.invite(identified).thenApply(DataResponse::created);
  }
}
