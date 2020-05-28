package com.meemaw.shared.context;

import com.meemaw.shared.rest.response.Boom;
import io.vertx.core.http.HttpServerRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public final class RequestUtils {

  private RequestUtils() {}

  public static Optional<String> parseRefererBaseURL(HttpServerRequest request) {
    return Optional.ofNullable(request.getHeader("referer"))
        .map(
            referer -> {
              try {
                return new URL(referer);
              } catch (MalformedURLException e) {
                throw Boom.badRequest().message(e.getMessage()).exception(e);
              }
            })
        .map(RequestUtils::parseBaseURL);
  }

  public static String parseBaseURL(URL url) {
    String base = url.getProtocol() + "://" + url.getHost();
    if (url.getPort() == -1) {
      return base;
    }
    return base + ":" + url.getPort();
  }
}
