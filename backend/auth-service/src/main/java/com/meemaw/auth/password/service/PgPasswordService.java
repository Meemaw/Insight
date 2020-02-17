package com.meemaw.auth.service.password;

import com.meemaw.auth.datasource.user.UserDatasource;
import com.meemaw.auth.model.user.UserDTO;
import com.meemaw.auth.model.user.UserWithPasswordHashDTO;
import com.meemaw.shared.rest.exception.BoomException;
import com.meemaw.shared.rest.exception.DatabaseException;
import com.meemaw.shared.rest.response.Boom;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class PgPasswordService implements PasswordService {

  private static final Logger log = LoggerFactory.getLogger(PgPasswordService.class);

  @Inject
  UserDatasource userDatasource;

  public CompletionStage<UserDTO> verifyPassword(String email, String password) {
    return userDatasource.findUserWithPasswordHash(email)
        .thenApply(maybeUserWithPasswordHash -> {
          UserWithPasswordHashDTO userWithPasswordHash = maybeUserWithPasswordHash
              .orElseThrow(() -> {
                log.info("User {} not found", email);
                throw new BoomException(Boom.badRequest().message("Invalid email or password"));
              });

          String hashedPassword = userWithPasswordHash.getPassword();
          if (hashedPassword == null) {
            log.info("User {} unfinished sign up", email);
            throw new BoomException(Boom.badRequest().message("Invalid email or password"));
          }

          if (!BCrypt.checkpw(password, hashedPassword)) {
            throw Boom.badRequest().message("Invalid email or password").exception();
          }

          return userWithPasswordHash.user();
        }).exceptionally(throwable -> {
          Throwable cause = throwable.getCause();
          if (cause instanceof BoomException) {
            throw (BoomException) cause;
          }
          log.error("Failed to retrieve user with password hash", throwable);
          throw new DatabaseException();
        });
  }
}
