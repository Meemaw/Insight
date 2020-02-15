package com.meemaw.auth.service.signup;

import com.meemaw.auth.model.signup.SignupRequest;
import com.meemaw.auth.model.signup.TeamInviteCreateIdentified;
import com.meemaw.auth.model.signup.dto.SignupCompleteRequestDTO;
import com.meemaw.auth.model.signup.dto.SignupVerifyRequestDTO;
import com.meemaw.auth.model.signup.dto.TeamInviteDTO;
import java.util.concurrent.CompletionStage;

public interface SignupService {

  CompletionStage<Boolean> verifySignupRequestExists(SignupVerifyRequestDTO verifySignup);

  CompletionStage<SignupRequest> signup(final String email);

  CompletionStage<Boolean> completeSignup(SignupCompleteRequestDTO completeSignup);

  CompletionStage<TeamInviteDTO> invite(TeamInviteCreateIdentified teamInvite);

}
