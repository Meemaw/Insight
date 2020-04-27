package com.meemaw.test.testconainers.service;

import com.meemaw.test.project.ProjectUtils;
import java.nio.file.Path;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

public class AuthApiTestContainer extends GenericContainer<AuthApiTestContainer> {

  private static final int PORT = 8080;

  private AuthApiTestContainer() {
    super(dockerImage());
  }

  public static AuthApiTestContainer newInstance() {
    return new AuthApiTestContainer()
        .withExposedPorts(PORT)
        .waitingFor(Wait.forHttp("/health"));
  }

  private static ImageFromDockerfile dockerImage() {
    Path dockerfile = ProjectUtils.getFromBackend("auth", "api", "docker", "Dockerfile.jvm");
    return new ImageFromDockerfile()
        .withDockerfile(dockerfile)
        .withFileFromFile(".", ProjectUtils.rootFile());
  }

  public static int getPort() {
    return PORT;
  }
}
