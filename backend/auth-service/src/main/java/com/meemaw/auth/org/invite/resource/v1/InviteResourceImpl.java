package com.meemaw.auth.org.invite.resource.v1;

import com.meemaw.auth.org.invite.datasource.InviteDatasource;
import com.meemaw.auth.org.invite.model.dto.InviteAcceptDTO;
import com.meemaw.auth.org.invite.model.dto.InviteCreateDTO;
import com.meemaw.auth.org.invite.model.dto.InviteCreateIdentifiedDTO;
import com.meemaw.auth.org.invite.model.dto.InviteSendDTO;
import com.meemaw.auth.org.invite.service.InviteService;
import com.meemaw.auth.sso.core.InsightPrincipal;
import com.meemaw.auth.user.model.UserRole;
import com.meemaw.shared.rest.response.Boom;
import com.meemaw.shared.rest.response.DataResponse;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InviteResourceImpl implements InviteResource {

  @Inject
  InsightPrincipal principal;

  @Inject
  InviteService inviteService;

  @Inject
  InviteDatasource inviteDatasource;

  @Override
  public CompletionStage<Response> create(InviteCreateDTO teamInviteCreate) {
    UUID creator = principal.getUserId();
    String org = principal.getOrg();

    InviteCreateIdentifiedDTO identified = new InviteCreateIdentifiedDTO(
        teamInviteCreate.getEmail(), org, teamInviteCreate.getRole(), creator);
    return inviteService.create(identified).thenApply(DataResponse::created);
  }

  @Override
  public CompletionStage<Response> delete(UUID token) {
    String org = principal.getOrg();
    return inviteDatasource.delete(token, org).thenApply(deleted -> {
      if (!deleted) {
        throw Boom.status(Status.NOT_FOUND).exception();
      }
      return DataResponse.ok(true);
    });
  }

  @Override
  public CompletionStage<Response> list() {
    return inviteDatasource.findAll(principal.getOrg()).thenApply(DataResponse::ok);
  }

  @Override
  public CompletionStage<Response> accept(InviteAcceptDTO teamInviteAccept) {
    return inviteService.accept(teamInviteAccept).thenApply(DataResponse::created);
  }

  @Override
  public CompletionStage<Response> send(InviteSendDTO inviteSend) {
    String org = principal.getOrg();
    String email = inviteSend.getEmail();
    UUID token = inviteSend.getToken();

    return inviteDatasource.find(email, org, token)
        .thenApply(maybeInvite -> maybeInvite.orElseThrow(() -> {
          log.error("Failed to find invite for user={} org={}", email, org);
          throw Boom.status(Status.NOT_FOUND).exception();
        }))
        .thenCompose(invite -> {
          UserRole role = invite.getRole();
          log.info("Sending invite email to user={} org={} role={} token={}", email, org, role,
              token);
          return inviteService.send(token, invite);
        })
        .thenApply(x -> DataResponse.ok(true));
  }
}
