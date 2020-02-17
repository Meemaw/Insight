package com.meemaw.auth.password.service;

import com.meemaw.auth.password.datasource.PasswordDatasource;
import com.meemaw.auth.user.model.UserDTO;
import com.meemaw.auth.user.model.UserWithHashedPasswordDTO;
import com.meemaw.shared.rest.exception.BoomException;
import com.meemaw.shared.rest.exception.DatabaseException;
import com.meemaw.shared.rest.response.Boom;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
@Slf4j
public class PgPasswordService implements PasswordService {

  @Inject
  PasswordDatasource passwordDatasource;

  public CompletionStage<UserDTO> verifyPassword(String email, String password) {
    return passwordDatasource.findUserWithPassword(email)
        .thenApply(maybeUserWithPasswordHash -> {
          UserWithHashedPasswordDTO userWithPasswordHash = maybeUserWithPasswordHash
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
