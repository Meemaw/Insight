package com.meemaw.auth.password.service;

import com.meemaw.auth.core.MailingConstants;
import com.meemaw.auth.password.datasource.PasswordDatasource;
import com.meemaw.auth.password.datasource.PasswordResetDatasource;
import com.meemaw.auth.password.model.PasswordResetRequest;
import com.meemaw.auth.password.model.dto.PasswordResetRequestDTO;
import com.meemaw.auth.user.datasource.UserDatasource;
import com.meemaw.auth.user.model.AuthUser;
import com.meemaw.auth.user.model.UserWithHashedPassword;
import com.meemaw.shared.rest.exception.BoomException;
import com.meemaw.shared.rest.exception.DatabaseException;
import com.meemaw.shared.rest.response.Boom;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;
import io.vertx.axle.pgclient.PgPool;
import io.vertx.axle.sqlclient.Transaction;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
@Slf4j
public class PasswordServiceImpl implements PasswordService {

  @Inject PasswordDatasource passwordDatasource;
  @Inject PasswordResetDatasource passwordResetDatasource;
  @Inject UserDatasource userDatasource;
  @Inject ReactiveMailer mailer;
  @Inject PgPool pgPool;

  @ResourcePath("password/password_reset")
  Template passwordResetTemplate;

  @Override
  public CompletionStage<AuthUser> verifyPassword(String email, String password) {
    return passwordDatasource
        .findUserWithPassword(email)
        .thenApply(
            maybeUserWithPasswordHash -> {
              UserWithHashedPassword withPassword =
                  maybeUserWithPasswordHash.orElseThrow(
                      () -> {
                        log.info("User {} not found", email);
                        throw new BoomException(
                            Boom.badRequest().message("Invalid email or password"));
                      });

              String hashedPassword = withPassword.getPassword();
              if (hashedPassword == null) {
                log.info("Could not associate password with user={}", email);
                throw Boom.badRequest().message("Invalid email or password").exception();
              }

              if (!BCrypt.checkpw(password, hashedPassword)) {
                throw Boom.badRequest().message("Invalid email or password").exception();
              }

              return withPassword.user();
            })
        .exceptionally(
            throwable -> {
              Throwable cause = throwable.getCause();
              if (cause instanceof BoomException) {
                throw (BoomException) cause;
              }
              log.error("Failed to retrieve user with password hash", throwable);
              throw new DatabaseException(throwable);
            });
  }

  @Override
  public CompletionStage<AuthUser> forgotPassword(String email, String passwordResetBaseURL) {
    String passwordResetURL = String.join("/", passwordResetBaseURL, "password-reset");

    return userDatasource
        .findUser(email)
        .thenApply(
            maybeUser ->
                maybeUser.orElseThrow(
                    () -> {
                      log.info("User {} not found", email);
                      throw new BoomException(Boom.notFound().message("User not found"));
                    }))
        .thenCompose(user -> this.forgotPassword(user, passwordResetURL));
  }

  private CompletionStage<AuthUser> forgotPassword(AuthUser authUser, String passwordResetURL) {
    return pgPool
        .begin()
        .thenCompose(transaction -> forgotPassword(authUser, passwordResetURL, transaction));
  }

  private CompletionStage<AuthUser> forgotPassword(
      AuthUser authUser, String passwordResetURL, Transaction transaction) {
    String email = authUser.getEmail();
    String org = authUser.getOrg();
    UUID userId = authUser.getId();

    return passwordResetDatasource
        .createPasswordResetRequest(email, userId, transaction)
        .thenApply(
            passwordResetRequest ->
                sendPasswordResetEmail(passwordResetRequest, passwordResetURL)
                    .exceptionally(
                        throwable -> {
                          transaction.rollback();
                          log.error(
                              "Failed to send password reset email={} org={}",
                              email,
                              org,
                              throwable);
                          throw Boom.serverError()
                              .message("Failed to send password reset email")
                              .exception();
                        }))
        .thenApply(nothing -> transaction.commit())
        .thenApply(nothing -> authUser);
  }

  private CompletionStage<Void> sendPasswordResetEmail(
      PasswordResetRequest passwordResetRequest, String passwordResetURL) {
    String email = passwordResetRequest.getEmail();
    UUID token = passwordResetRequest.getToken();
    String subject = "Reset your Insight password";

    return passwordResetTemplate
        .data("email", email)
        .data("token", token)
        .data("passwordResetURL", passwordResetURL)
        .renderAsync()
        .thenCompose(
            html ->
                mailer
                    .send(
                        Mail.withHtml(email, subject, html).setFrom(MailingConstants.FROM_SUPPORT))
                    .subscribeAsCompletionStage());
  }

  @Override
  public CompletionStage<PasswordResetRequest> resetPassword(
      PasswordResetRequestDTO passwordResetRequestDTO) {
    UUID token = passwordResetRequestDTO.getToken();
    String password = passwordResetRequestDTO.getPassword();
    return passwordResetDatasource
        .findPasswordResetRequest(token)
        .thenApply(
            maybePasswordRequest ->
                maybePasswordRequest.orElseThrow(
                    () -> {
                      throw new BoomException(
                          Boom.notFound().message("Password reset request not found"));
                    }))
        .thenCompose(passwordResetRequest -> reset(passwordResetRequest, password));
  }

  private CompletionStage<PasswordResetRequest> reset(
      PasswordResetRequest passwordResetRequest, String newPassword) {
    UUID token = passwordResetRequest.getToken();
    String email = passwordResetRequest.getEmail();

    if (passwordResetRequest.hasExpired()) {
      throw Boom.badRequest().message("Password reset request expired").exception();
    }

    return pgPool
        .begin()
        .thenCompose(
            transaction ->
                passwordResetDatasource
                    .deletePasswordResetRequest(token, transaction)
                    .thenCompose(
                        deleted ->
                            createPassword(
                                passwordResetRequest.getUserId(), email, newPassword, transaction))
                    .thenCompose(created -> transaction.commit())
                    .thenApply(nothing -> passwordResetRequest));
  }

  @Override
  public CompletionStage<Boolean> createPassword(
      UUID userId, String email, String password, Transaction transaction) {
    return passwordDatasource
        .storePassword(userId, hashPassword(password), transaction)
        .thenApply(x -> true);
  }

  @Override
  public CompletionStage<Boolean> passwordResetRequestExists(UUID token) {
    return passwordResetDatasource.findPasswordResetRequest(token).thenApply(Optional::isPresent);
  }
}
