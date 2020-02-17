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
import io.vertx.axle.sqlclient.Transaction;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public interface UserDatasource {

  CompletionStage<Optional<UserWithPasswordHashDTO>> findUserWithPasswordHash(String email);

  CompletionStage<UUID> createUser(Transaction transaction, String email, String org,
      UserRole role);

  CompletionStage<SignupRequest> createUser(Transaction transaction, SignupRequest signupRequest);

  CompletionStage<SignupRequest> createOrganization(Transaction transaction,
      SignupRequest signupRequest);

  CompletionStage<SignupRequest> createSignupRequest(Transaction transaction,
      SignupRequest signupRequest);

  CompletionStage<Boolean> verifySignupExists(SignupVerifyRequestDTO verifySignup);

  CompletionStage<Optional<Signup>> findSignup(Transaction transaction,
      SignupVerifyRequestDTO verifySignup);

  CompletionStage<Optional<TeamInvite>> findInvite(Transaction transaction,
      TeamInviteAcceptDTO teamInviteAccept);

  CompletionStage<Boolean> deleteSignupRequests(Transaction transaction, Signup activationRequest);

  CompletionStage<Boolean> deleteTeamInvite(Transaction transaction,
      TeamInviteAcceptDTO teamInviteAccept);

  CompletionStage<Void> storePassword(Transaction transaction, UUID userId, String hashedPassword);

  CompletionStage<TeamInviteDTO> storeInvite(Transaction transaction,
      TeamInviteCreateIdentified teamInvite);

}
