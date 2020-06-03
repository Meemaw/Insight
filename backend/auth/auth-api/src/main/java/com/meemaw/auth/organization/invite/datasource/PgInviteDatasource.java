package com.meemaw.auth.organization.invite.datasource;

import com.meemaw.auth.organization.datasource.PgOrganizationDatasource;
import com.meemaw.auth.organization.invite.model.TeamInvite;
import com.meemaw.auth.organization.invite.model.TeamInviteTemplateData;
import com.meemaw.auth.organization.model.Organization;
import com.meemaw.auth.user.model.UserRole;
import com.meemaw.shared.pg.PgError;
import com.meemaw.shared.rest.exception.DatabaseException;
import com.meemaw.shared.rest.response.Boom;
import io.vertx.axle.pgclient.PgPool;
import io.vertx.axle.sqlclient.Row;
import io.vertx.axle.sqlclient.RowSet;
import io.vertx.axle.sqlclient.Transaction;
import io.vertx.axle.sqlclient.Tuple;
import io.vertx.pgclient.PgException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

@ApplicationScoped
@Slf4j
public class PgInviteDatasource implements InviteDatasource {

  @Inject PgPool pgPool;

  private static final String FIND_INVITE_RAW_SQL =
      "SELECT * FROM auth.team_invite WHERE token = $1";

  private static final String FIND_TEAM_INVITE_WITH_ORGANIZATION_RAW_SQL =
      "SELECT * FROM auth.team_invite LEFT JOIN auth.organization ON auth.team_invite.org_id = auth.organization.id WHERE token = $1";

  private static final String FIND_ALL_INVITES_RAW_SQL =
      "SELECT * FROM auth.team_invite WHERE org_id = $1";

  private static final String DELETE_INVITE_RAW_SQL =
      "DELETE FROM auth.team_invite WHERE token = $1";

  private static final String DELETE_ALL_INVITES_RAW_SQL =
      "DELETE FROM auth.team_invite WHERE email = $1 AND org_id = $2";

  private static final String CREATE_INVITE_RAW_SQL =
      "INSERT INTO auth.team_invite(creator_id, email, org_id, role) VALUES($1, $2, $3, $4) RETURNING token, created_at";

  @Override
  public CompletionStage<Optional<TeamInvite>> findTeamInvite(UUID token) {
    return pgPool
        .preparedQuery(FIND_INVITE_RAW_SQL, Tuple.of(token))
        .thenApply(this::inviteFromRowSet);
  }

  @Override
  public CompletionStage<Optional<TeamInvite>> findTeamInvite(UUID token, Transaction transaction) {
    return transaction
        .preparedQuery(FIND_INVITE_RAW_SQL, Tuple.of(token))
        .thenApply(this::inviteFromRowSet);
  }

  @Override
  public CompletionStage<Optional<Pair<TeamInvite, Organization>>> findTeamInviteWithOrganization(
      UUID token) {
    return pgPool
        .preparedQuery(FIND_TEAM_INVITE_WITH_ORGANIZATION_RAW_SQL, Tuple.of(token))
        .thenApply(
            pgRowSet -> {
              if (!pgRowSet.iterator().hasNext()) {
                return Optional.empty();
              }
              Row row = pgRowSet.iterator().next();
              TeamInvite teamInvite = mapInviteDTO(row);
              Organization organization = PgOrganizationDatasource.mapOrganization(row);
              return Optional.of(Pair.of(teamInvite, organization));
            });
  }

  @Override
  public CompletionStage<List<TeamInvite>> findTeamInvites(String orgId) {
    return pgPool
        .preparedQuery(FIND_ALL_INVITES_RAW_SQL, Tuple.of(orgId))
        .thenApply(
            pgRowSet ->
                StreamSupport.stream(pgRowSet.spliterator(), false)
                    .map(PgInviteDatasource::mapInviteDTO)
                    .collect(Collectors.toList()));
  }

  @Override
  public CompletionStage<Boolean> deleteTeamInvite(UUID token) {
    return pgPool.preparedQuery(DELETE_INVITE_RAW_SQL, Tuple.of(token)).thenApply(pgRowSet -> true);
  }

  @Override
  public CompletionStage<Boolean> deleteTeamInvites(
      String email, String org, Transaction transaction) {
    Tuple values = Tuple.of(email, org);
    return transaction
        .preparedQuery(DELETE_ALL_INVITES_RAW_SQL, values)
        .thenApply(pgRowSet -> true);
  }

  @Override
  public CompletionStage<TeamInvite> createTeamInvite(
      String orgId, UUID creatorId, TeamInviteTemplateData teamInvite, Transaction transaction) {
    String email = teamInvite.getRecipientEmail();
    UserRole role = teamInvite.getRecipientRole();

    Tuple values = Tuple.of(creatorId, email, orgId, role.toString());
    return transaction
        .preparedQuery(CREATE_INVITE_RAW_SQL, values)
        .thenApply(
            pgRowSet -> {
              Row row = pgRowSet.iterator().next();
              UUID token = row.getUUID("token");
              OffsetDateTime createdAt = row.getOffsetDateTime("created_at");
              return new TeamInvite(token, email, orgId, role, creatorId, createdAt);
            })
        .exceptionally(
            throwable -> {
              Throwable cause = throwable.getCause();
              if (cause instanceof PgException) {
                PgException pgException = (PgException) cause;
                if (pgException.getCode().equals(PgError.UNIQUE_VIOLATION.getCode())) {
                  log.error("User has already been invited user={} org={}", email, orgId);
                  throw Boom.status(Response.Status.CONFLICT)
                      .message("User has already been invited")
                      .exception();
                }
              }
              log.error(
                  "Failed to create invite user={} org={} creator={} role={}",
                  email,
                  orgId,
                  creatorId,
                  role,
                  throwable);
              throw new DatabaseException(throwable);
            });
  }

  private Optional<TeamInvite> inviteFromRowSet(RowSet<Row> rowSet) {
    if (!rowSet.iterator().hasNext()) {
      return Optional.empty();
    }
    return Optional.of(mapInviteDTO(rowSet.iterator().next()));
  }

  public static TeamInvite mapInviteDTO(Row row) {
    return new TeamInvite(
        row.getUUID("token"),
        row.getString("email"),
        row.getString("org_id"),
        UserRole.valueOf(row.getString("role")),
        row.getUUID("creator_id"),
        row.getOffsetDateTime("created_at"));
  }
}
