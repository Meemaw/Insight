package com.meemaw.auth.datasource.user;

import com.meemaw.auth.model.signup.Signup;
import com.meemaw.auth.model.signup.SignupRequest;
import com.meemaw.auth.model.signup.TeamInvite;
import com.meemaw.auth.model.signup.TeamInviteCreateIdentified;
import com.meemaw.auth.model.signup.dto.SignupVerifyRequestDTO;
import com.meemaw.auth.model.signup.dto.TeamInviteAcceptDTO;
import com.meemaw.auth.model.signup.dto.TeamInviteDTO;
import com.meemaw.auth.model.user.UserRole;
import com.meemaw.auth.model.user.UserWithPasswordHashDTO;
import com.meemaw.shared.pg.PgError;
import com.meemaw.shared.rest.exception.DatabaseException;
import com.meemaw.shared.rest.response.Boom;
import com.meemaw.shared.string.RandomString;
import io.vertx.axle.pgclient.PgPool;
import io.vertx.axle.sqlclient.Row;
import io.vertx.axle.sqlclient.Transaction;
import io.vertx.axle.sqlclient.Tuple;
import io.vertx.pgclient.PgException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class PgUserDatasource implements UserDatasource {

  private static final Logger log = LoggerFactory.getLogger(PgUserDatasource.class);

  @Inject
  PgPool pgPool;

  private static final String SELECT_USER_WITH_PASSWORD_HASH_RAW_SQL =
      "SELECT auth.user.id, auth.user.email, auth.user.org, auth.user.role, auth.password.hash" +
          " FROM auth.user LEFT JOIN auth.password ON auth.user.id = auth.password.user_id" +
          " WHERE email = $1";


  public CompletionStage<Optional<UserWithPasswordHashDTO>> findUserWithPasswordHash(String email) {
    Tuple values = Tuple.of(email);
    return pgPool.preparedQuery(SELECT_USER_WITH_PASSWORD_HASH_RAW_SQL, values)
        .thenApply(pgRowSet -> {
          if (!pgRowSet.iterator().hasNext()) {
            return Optional.empty();
          }
          Row row = pgRowSet.iterator().next();
          return Optional.of(new UserWithPasswordHashDTO(
              row.getUUID("id"),
              row.getString("email"),
              UserRole.valueOf(row.getString("role")),
              row.getString("org"),
              row.getString("hash")));
        });
  }

  private static final String INSERT_USER_RAW_SQL = "INSERT INTO auth.user(email, org, role) VALUES($1, $2, $3) RETURNING id";

  public CompletionStage<UUID> createUser(
      Transaction transaction,
      String email,
      String org,
      UserRole role) {
    Tuple values = Tuple.of(email, org, role.toString());
    return transaction.preparedQuery(INSERT_USER_RAW_SQL, values)
        .thenApply(pgRowSet -> pgRowSet.iterator().next().getUUID("id"))
        .exceptionally(throwable -> {
          Throwable cause = throwable.getCause();
          if (cause instanceof PgException) {
            PgException pgException = (PgException) cause;
            if (pgException.getCode().equals(PgError.UNIQUE_VIOLATION.getCode())) {
              log.error("Email already exists email={} org={}", email, org);
              throw Boom.status(Response.Status.CONFLICT).message("Email already exists")
                  .exception();
            }
          }
          log.error("Failed to create user email={} org={}", email, org, throwable);
          throw new DatabaseException();
        });
  }

  public CompletionStage<SignupRequest> createUser(Transaction transaction,
      SignupRequest signupRequest) {
    String email = signupRequest.getEmail();
    String org = signupRequest.getOrg();

    return createUser(transaction, email, org, UserRole.ADMIN).thenApply(signupRequest::userId);
  }

  private static final String INSERT_ORG_RAW_SQL = "INSERT INTO auth.org(id) VALUES($1)";

  public CompletionStage<SignupRequest> createOrganization(Transaction transaction,
      SignupRequest signupRequest) {
    String org = RandomString.alphanumeric(6);
    Tuple values = Tuple.of(org);

    return transaction.preparedQuery(INSERT_ORG_RAW_SQL, values)
        .thenApply(pgRowSet -> signupRequest.org(org))
        .exceptionally(throwable -> {
          Throwable cause = throwable.getCause();
          if (cause instanceof PgException) {
            PgException pgException = (PgException) cause;
            if (pgException.getCode().equals(PgError.UNIQUE_VIOLATION.getCode())) {
              log.error("Organization already exists org={}", org);
              throw Boom.status(Response.Status.CONFLICT).message("Organization already exists")
                  .exception();
            }
          }
          log.error("Failed to create organization org={}", org, throwable);
          throw new DatabaseException();
        });
  }

  private static final String INSERT_SIGNUP_RAW_SQL = "INSERT INTO auth.signup(user_email, org, user_id) VALUES($1, $2, $3) RETURNING token";

  public CompletionStage<SignupRequest> createSignupRequest(Transaction transaction,
      SignupRequest signupRequest) {
    String email = signupRequest.getEmail();
    String org = signupRequest.getOrg();
    UUID userId = signupRequest.getUserId();
    Tuple values = Tuple.of(email, org, userId);

    return transaction.preparedQuery(INSERT_SIGNUP_RAW_SQL, values)
        .thenApply(pgRowSet -> {
          UUID signupToken = pgRowSet.iterator().next().getUUID("token");
          return signupRequest.token(signupToken);
        })
        .exceptionally(throwable -> {
          log.error("Failed to create signup email={} userId={} org={}", email, userId, org,
              throwable);
          throw new DatabaseException();
        });
  }

  public CompletionStage<Boolean> verifySignupExists(SignupVerifyRequestDTO verifySignup) {
    return pgPool.begin()
        .thenCompose(
            transaction -> findSignup(transaction, verifySignup).thenApply(Optional::isPresent))
        .exceptionally(throwable -> {
          log.error("Failed to verify signup exists email={} org={} token={}",
              verifySignup.getEmail(), verifySignup.getOrg(), verifySignup.getToken(), throwable);
          throw new DatabaseException();
        });
  }

  private static final String SELECT_SIGNUP_RAW_SQL = "SELECT * FROM auth.signup WHERE user_email = $1 AND org = $2 AND token = $3";

  public CompletionStage<Optional<Signup>> findSignup(
      Transaction transaction,
      SignupVerifyRequestDTO verifySignup) {
    Tuple values = Tuple
        .of(verifySignup.getEmail(), verifySignup.getOrg(), verifySignup.getToken());

    return transaction.preparedQuery(SELECT_SIGNUP_RAW_SQL, values)
        .thenApply(pgRowSet -> {
          if (!pgRowSet.iterator().hasNext()) {
            return Optional.empty();
          }
          Row row = pgRowSet.iterator().next();
          return Optional.of(new Signup(
              row.getString("user_email"),
              row.getString("org"),
              row.getUUID("token"),
              row.getUUID("user_id"),
              row.getOffsetDateTime("created_at")));
        });
  }

  private static final String SELECT_INVITE_RAW_SQL = "SELECT * FROM auth.invite WHERE user_email = $1 AND org = $2 AND token = $3";


  @Override
  public CompletionStage<Optional<TeamInvite>> findInvite(Transaction transaction,
      TeamInviteAcceptDTO teamInviteAccept) {
    Tuple values = Tuple
        .of(teamInviteAccept.getEmail(), teamInviteAccept.getOrg(), teamInviteAccept.getToken());

    return transaction.preparedQuery(SELECT_INVITE_RAW_SQL, values)
        .thenApply(pgRowSet -> {
          if (!pgRowSet.iterator().hasNext()) {
            return Optional.empty();
          }
          Row row = pgRowSet.iterator().next();
          return Optional.of(new TeamInvite(
              row.getString("user_email"),
              row.getString("org"),
              row.getUUID("token"),
              UserRole.valueOf(row.getString("role")),
              row.getOffsetDateTime("created_at")));
        });
  }

  private static final String DELETE_USER_SIGNUP_RAW_SQL = "DELETE FROM auth.signup WHERE user_email = $1 AND org = $2 AND user_id = $3";

  public CompletionStage<Boolean> deleteSignupRequests(Transaction transaction,
      Signup activationRequest) {
    Tuple values = Tuple.of(activationRequest.getEmail(), activationRequest.getOrg(),
        activationRequest.getUserId());

    return transaction.preparedQuery(DELETE_USER_SIGNUP_RAW_SQL, values)
        .thenApply(pgRowSet -> true);
  }

  private static final String DELETE_INVITE_RAW_SQL = "DELETE FROM auth.invite WHERE user_email = $1 AND org = $2";

  @Override
  public CompletionStage<Boolean> deleteTeamInvite(Transaction transaction,
      TeamInviteAcceptDTO teamInviteAccept) {
    Tuple values = Tuple.of(teamInviteAccept.getEmail(), teamInviteAccept.getOrg());
    return transaction.preparedQuery(DELETE_INVITE_RAW_SQL, values).thenApply(pgRowSet -> true);
  }


  private static final String INSERT_PASSWORD_RAW_SQL = "INSERT INTO auth.password(user_id, hash) VALUES($1, $2) RETURNING *";

  public CompletionStage<Void> storePassword(Transaction transaction, UUID userId,
      String hashedPassword) {
    Tuple values = Tuple.of(userId, hashedPassword);
    return transaction.preparedQuery(INSERT_PASSWORD_RAW_SQL, values)
        .thenAccept(pgRowSet -> {
          boolean hasNext = pgRowSet.iterator().hasNext();
          if (!hasNext) {
            log.error("Failed to store password userId={}", userId);
            throw new DatabaseException();
          }
        });
  }

  private static final String INSERT_INVITATION_RAW_SQL = "INSERT INTO auth.invite(creator, user_email, org, role) VALUES($1, $2, $3, $4) RETURNING token, created_at";

  @Override
  public CompletionStage<TeamInviteDTO> storeInvite(Transaction transaction,
      TeamInviteCreateIdentified teamInvite) {
    UUID creator = teamInvite.getCreator();
    String email = teamInvite.getEmail();
    String org = teamInvite.getOrg();
    UserRole role = teamInvite.getRole();

    Tuple values = Tuple.of(creator, email, org, role.toString());

    return transaction.preparedQuery(INSERT_INVITATION_RAW_SQL, values)
        .thenApply(pgRowSet -> {
          Row row = pgRowSet.iterator().next();
          UUID token = row.getUUID("token");
          OffsetDateTime createdAt = row.getOffsetDateTime("created_at");
          return new TeamInviteDTO(email, role, token, createdAt);
        }).exceptionally(throwable -> {
          Throwable cause = throwable.getCause();
          if (cause instanceof PgException) {
            PgException pgException = (PgException) cause;
            if (pgException.getCode().equals(PgError.UNIQUE_VIOLATION.getCode())) {
              log.error("User has already been invited email={} org={}", email, org);
              throw Boom.status(Response.Status.CONFLICT).message("User has already been invited")
                  .exception();
            }
          }
          log.error("Failed to store invite email={} org={} creator={} role={}", email, org,
              creator, role,
              throwable);
          throw new DatabaseException();
        });
  }
}
