package com.meemaw.rec.page.page;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PageDTO {

  @NotNull(message = "may not be null")
  String orgId;

  UUID uid;

  @NotNull(message = "may not be null")
  String url;

  @NotNull(message = "may not be null")
  String referrer;

  @NotNull(message = "may not be null")
  String doctype;

  @NotNull(message = "may not be null")
  @Min(message = "must be non negative", value = 0)
  int screenWidth;

  @NotNull(message = "may not be null")
  @Min(message = "must be non negative", value = 0)
  int screenHeight;

  @NotNull(message = "may not be null")
  @Min(message = "must be non negative", value = 0)
  int width;

  @JsonProperty("height")
  @NotNull(message = "may not be null")
  @Min(message = "must be non negative", value = 0)
  private int height;

  @NotNull(message = "may not be null")
  @Min(message = "must be non negative", value = 0)
  long compiledTs;

}
