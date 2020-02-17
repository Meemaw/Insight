package com.meemaw.auth.password.service;

import com.meemaw.auth.user.model.UserDTO;
import java.util.concurrent.CompletionStage;

public interface PasswordService {

  CompletionStage<UserDTO> verifyPassword(String email, String password);

}
