package com.meemaw.auth.org.invite.resource.v1;

import com.meemaw.auth.org.invite.datasource.TeamInviteDatasource;
import com.meemaw.auth.org.invite.model.TeamInviteCreateIdentifiedDTO;
import com.meemaw.auth.org.invite.model.TeamInviteAcceptDTO;
import com.meemaw.auth.org.invite.model.TeamInviteCreateDTO;
import com.meemaw.auth.signup.service.SignupService;
import com.meemaw.auth.sso.core.InsightPrincipal;
import com.meemaw.shared.rest.response.DataResponse;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class OrganizationResourceImpl implements InviteResource {

  @Inject
  InsightPrincipal principal;

  @Inject
  SignupService signupService;

  @Inject
  TeamInviteDatasource teamInviteDatasource;

  @Override
  public CompletionStage<Response> listInvites() {
    return teamInviteDatasource.findAll(principal.getOrg()).thenApply(DataResponse::ok);
  }

  @Override
  public CompletionStage<Response> createInvite(TeamInviteCreateDTO teamInviteCreate) {
    UUID creator = principal.getUserId();
    String org = principal.getOrg();

    TeamInviteCreateIdentifiedDTO identified = new TeamInviteCreateIdentifiedDTO(
        teamInviteCreate.getEmail(), org, teamInviteCreate.getRole(), creator);
    return signupService.invite(identified).thenApply(DataResponse::created);
  }

  @Override
  public CompletionStage<Response> acceptInvite(TeamInviteAcceptDTO teamInviteAccept) {
    return signupService.acceptInvite(teamInviteAccept).thenApply(DataResponse::created);
  }
}
