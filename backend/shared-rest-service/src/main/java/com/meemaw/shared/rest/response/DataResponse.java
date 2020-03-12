package com.meemaw.shared.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class DataResponse<T> {

  public DataResponse(T data, Boom error) {
    this.data = data;
    this.error = error;
  }

  public static <T> DataResponse<T> data(T data) {
    return new DataResponse<T>(Objects.requireNonNull(data), null);
  }

  public static <T> Response ok(T data) {
    return DataResponse.okBuilder(data).build();
  }

  public static <T> Response created(T data) {
    return DataResponse.data(data).builder(201).build();
  }

  public static <T> Response.ResponseBuilder okBuilder(T data) {
    return DataResponse.data(data).builder(200);
  }

  public static <T> DataResponse<T> error(Boom error) {
    return new DataResponse<T>(null, Objects.requireNonNull(error));
  }

  @JsonProperty("data")
  private T data;

  @JsonProperty("error")
  private Boom error;

  public T getData() {
    return data;
  }

  public Boom getError() {
    return error;
  }

  public Response.ResponseBuilder builder(int statusCode) {
    return Response.status(statusCode).entity(this).type(MediaType.APPLICATION_JSON);
  }

  public Response.ResponseBuilder builder() {
    return Response.status(Objects.requireNonNull(error).getStatusCode()).entity(this)
        .type(MediaType.APPLICATION_JSON);
  }

  public Response response(int statusCode) {
    return builder(statusCode).build();
  }

  public Response response(Response.Status status) {
    return response(status.getStatusCode());
  }

}
