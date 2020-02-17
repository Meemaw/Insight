package com.meemaw.auth.password.datasource;

import com.meemaw.auth.user.model.UserRole;
import com.meemaw.auth.user.model.UserWithHashedPasswordDTO;
import com.meemaw.shared.rest.exception.DatabaseException;
import io.vertx.axle.pgclient.PgPool;
import io.vertx.axle.sqlclient.Row;
import io.vertx.axle.sqlclient.Transaction;
import io.vertx.axle.sqlclient.Tuple;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class PgPasswordDatasource implements PasswordDatasource {

  @Inject
  PgPool pgPool;

  private static final String CREATE_PASSWORD_RAW_SQL = "INSERT INTO auth.password(user_id, hash) VALUES($1, $2) RETURNING *";

  @Override
  public CompletionStage<Boolean> create(Transaction transaction, UUID userId,
      String hashedPassword) {
    Tuple values = Tuple.of(userId, hashedPassword);
    return transaction.preparedQuery(CREATE_PASSWORD_RAW_SQL, values)
        .thenApply(pgRowSet -> {
          boolean hasNext = pgRowSet.iterator().hasNext();
          if (!hasNext) {
            log.error("Failed to store password userId={}", userId);
            throw new DatabaseException();
          }
          return true;
        });
  }

  private static final String FIND_USER_WITH_PASSWORD_RAW_SQL =
      "SELECT auth.user.id, auth.user.email, auth.user.org, auth.user.role, auth.password.hash" +
          " FROM auth.user LEFT JOIN auth.password ON auth.user.id = auth.password.user_id" +
          " WHERE email = $1";


  @Override
  public CompletionStage<Optional<UserWithHashedPasswordDTO>> findUserWithPassword(String email) {
    Tuple values = Tuple.of(email);
    return pgPool.preparedQuery(FIND_USER_WITH_PASSWORD_RAW_SQL, values)
        .thenApply(pgRowSet -> {
          if (!pgRowSet.iterator().hasNext()) {
            return Optional.empty();
          }
          Row row = pgRowSet.iterator().next();
          return Optional.of(new UserWithHashedPasswordDTO(
              row.getUUID("id"),
              row.getString("email"),
              UserRole.valueOf(row.getString("role")),
              row.getString("org"),
              row.getString("hash")));
        });
  }
}
