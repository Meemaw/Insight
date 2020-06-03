package com.meemaw.auth.password.model.dto;

import static com.meemaw.test.matchers.SameJSON.sameJson;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.meemaw.test.rest.mappers.JacksonMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class PasswordResetRequestSerializationTest {

  @Test
  public void jacksonSerialization() throws JsonProcessingException {
    PasswordResetRequestDTO signUpCompleteRequest =
        new PasswordResetRequestDTO(
            UUID.fromString("bc2a1cc5-62ed-45a2-b7a6-70520dadc33b"), "superPassword");

    String payload = JacksonMapper.get().writeValueAsString(signUpCompleteRequest);
    assertThat(
        payload,
        sameJson(
            "{\"token\":\"bc2a1cc5-62ed-45a2-b7a6-70520dadc33b\",\"password\":\"superPassword\"}"));

    PasswordResetRequestDTO deserialized =
        JacksonMapper.get().readValue(payload, PasswordResetRequestDTO.class);
    assertEquals(signUpCompleteRequest, deserialized);
    assertEquals(payload, JacksonMapper.get().writeValueAsString(deserialized));
  }
}
