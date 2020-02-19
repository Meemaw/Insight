package com.meemaw.auth.signup.service;

import com.meemaw.auth.password.service.PasswordService;
import com.meemaw.auth.signup.datasource.SignupDatasource;
import com.meemaw.auth.signup.model.SignupRequest;
import com.meemaw.auth.signup.model.dto.SignupRequestCompleteDTO;
import com.meemaw.auth.signup.model.dto.SignupRequestDTO;
import com.meemaw.auth.user.datasource.UserDatasource;
import com.meemaw.shared.rest.exception.DatabaseException;
import com.meemaw.shared.rest.response.Boom;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.ReactiveMailer;
import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;
import io.vertx.axle.pgclient.PgPool;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class SignupServiceImpl implements SignupService {

  @ResourcePath("signup/welcome")
  Template welcomeTemplate;

  @Inject
  ReactiveMailer mailer;

  @Inject
  PgPool pgPool;

  @Inject
  UserDatasource userDatasource;

  @Inject
  SignupDatasource signupDatasource;

  @Inject
  PasswordService passwordService;


  private static final String FROM_SUPPORT = "Insight Support <support@insight.com>";

  private CompletionStage<Void> sendWelcomeEmail(SignupRequestDTO signupRequest) {
    String email = signupRequest.getEmail();
    String subject = "Welcome to Insight";

    return welcomeTemplate
        .data("email", email)
        .data("orgId", signupRequest.getOrg())
        .data("token", signupRequest.getToken())
        .renderAsync()
        .thenCompose(
            html -> mailer.send(Mail.withHtml(email, subject, html).setFrom(FROM_SUPPORT)));
  }

  public CompletionStage<Boolean> exists(String email, String org, UUID token) {
    return signupDatasource.exists(email, org, token);
  }

  public CompletionStage<SignupRequestDTO> create(final String email) {
    log.info("signup request email={}", email);

    return pgPool.begin().thenCompose(transaction -> userDatasource
        .createOrganization(transaction, new SignupRequest(email))
        .thenCompose(org -> userDatasource.createUser(transaction, org))
        .thenCompose(req -> signupDatasource.create(transaction, req))
        .thenCompose(signupRequest -> sendWelcomeEmail(signupRequest)
            .exceptionally(throwable -> {
              transaction.rollback();
              log.error("Failed to send signup email={}", email, throwable);
              throw Boom.serverError().message("Failed to send signup email").exception();
            })
            .thenCompose(x -> transaction.commit())
            .thenApply(x -> {
              log.info("signup complete email={} userId={} org={}", email,
                  signupRequest.getUserId(),
                  signupRequest.getOrg());
              ;
              return signupRequest;
            })
            .exceptionally(throwable -> {
              log.error("Failed to commit signup transaction email={}", email, throwable);
              throw new DatabaseException();
            })));
  }

  public CompletionStage<Boolean> complete(SignupRequestCompleteDTO completeSignup) {
    String email = completeSignup.getEmail();
    String org = completeSignup.getOrg();
    UUID token = completeSignup.getToken();

    return pgPool.begin().thenCompose(transaction -> {
      log.info("signupComplete starting transaction email={} token={}", email, token);

      return signupDatasource.find(transaction, email, org, token)
          .thenApply(maybeSignup -> {
            SignupRequestDTO signup = maybeSignup.orElseThrow(() -> {
              log.info("Signup request does not exist email={} org={} token={}", email, org, token);
              throw Boom.badRequest().message("Signup request does not exist.").exception();
            });

            Instant lastActive = signup.getCreatedAt().plusDays(1).toInstant();
            if (Instant.now().isAfter(lastActive)) {
              log.info("Signup request expired email={} org={} token={}", email, org, token);
              throw Boom.badRequest().message("Signup request expired").exception();
            }

            return signup;
          })
          .thenCompose(signup -> {
            log.info("Deleting existing signup requests email={} org={}", email, org);
            return signupDatasource.delete(transaction, signup)
                .thenApply(isDeleted -> {
                  if (!isDeleted) {
                    log.info("Failed to delete signup requests email={} org={}", email, org);
                    throw new DatabaseException();
                  }
                  return signup;
                });
          })
          .thenCompose(signup -> {
            UUID userId = signup.getUserId();
            String password = completeSignup.getPassword();
            return passwordService.create(transaction, userId, email, org, password);
          })
          .thenCompose(created -> transaction.commit().thenApply(x -> created));
    });
  }

}
