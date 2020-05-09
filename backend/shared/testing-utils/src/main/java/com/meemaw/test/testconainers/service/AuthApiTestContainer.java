package com.meemaw.test.testconainers.service;

import com.github.dockerjava.api.DockerClient;
import com.meemaw.test.project.ProjectUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.Duration;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.Base58;

@Slf4j
public class AuthApiTestContainer extends GenericContainer<AuthApiTestContainer> {

  private static final int PORT = 3435;

  private AuthApiTestContainer() {
    super(dockerImage());
  }

  public static AuthApiTestContainer newInstance() {
    return new AuthApiTestContainer().withEnv("AUTH_API_PORT", "" + PORT).withExposedPorts(PORT)
        .withLogConsumer(new Slf4jLogConsumer(log)).waitingFor(Wait.forHttp("/health").forStatusCode(200));
  }

  private static String dockerImage() {
    Path dockerfile = ProjectUtils.getFromBackend("auth", "api", "docker", "Dockerfile.jvm");
    Path context = ProjectUtils.backendPath();

    System.out.println("Dockerfile: " + dockerfile.toString());
    System.out.println("Context: " + context.toAbsolutePath());

    ProcessBuilder builder = new ProcessBuilder("docker", "build", "-f", dockerfile.toString(), "-t", "authapi-test", context.toAbsolutePath().toString());
    builder.redirectErrorStream(true);
    Process p;
    try {
      p = builder.start();

    BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String line;
    while (true) {
        line = r.readLine();
        if (line == null) { break; }
        System.out.println(line);
    }

    if (p.waitFor() > 0) {
      throw new Exception("Build fail");
    }

    return "authapi-test";    
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return "";
    }
  }

  public int getPort() {
    return getMappedPort(PORT);
  }
}
