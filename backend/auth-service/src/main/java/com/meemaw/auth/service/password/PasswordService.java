package com.meemaw.auth.service.password;

import com.meemaw.auth.model.user.UserDTO;
import java.util.concurrent.CompletionStage;

public interface PasswordService {

  CompletionStage<UserDTO> verifyPassword(String email, String password);

}
