package com.meemaw.auth.signup.datasource;

import com.meemaw.auth.signup.model.dto.SignupRequestDTO;
import com.meemaw.auth.signup.model.SignupRequest;
import io.vertx.axle.sqlclient.Transaction;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public interface SignupDatasource {

  CompletionStage<SignupRequestDTO> create(Transaction transaction,
      SignupRequest signupRequest);

  CompletionStage<Boolean> exists(String email, String org, UUID token);

  CompletionStage<Optional<SignupRequestDTO>> find(Transaction transaction, String email,
      String org,
      UUID token);

  CompletionStage<Boolean> delete(Transaction transaction, SignupRequestDTO signupRequest);
}
