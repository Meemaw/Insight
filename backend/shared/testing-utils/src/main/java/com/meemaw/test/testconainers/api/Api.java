package com.meemaw.test.testconainers.api;

import com.meemaw.test.project.ProjectUtils;
import java.nio.file.Path;
import java.util.Optional;

public enum Api {
  AUTH {
    @Override
    public Path dockerfile() {
      return ProjectUtils.getFromBackend("auth", "api", "docker", "Dockerfile.jvm");
    }

    @Override
    public Optional<Path> migrations() {
      return Optional.of(ProjectUtils.getFromBackend("auth", "api", "migrations"));
    }
  };

  public abstract Path dockerfile();

  public abstract Optional<Path> migrations();
}
