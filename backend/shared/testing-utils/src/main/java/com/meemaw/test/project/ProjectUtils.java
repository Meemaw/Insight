package com.meemaw.test.project;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProjectUtils {

  private static final String BACKEND = "backend";

  private static String getUserDirectory() {
    return System.getProperty("user.dir");
  }

  public static File rootFile() {
    return new File(getUserDirectory().split(BACKEND)[0]);
  }

  private static Path backend() {
    String projectRoot = rootFile().toString();
    return Paths.get(projectRoot, BACKEND).toAbsolutePath();
  }

  public static Path getFromBackend(String... args) {
    return Paths.get(ProjectUtils.backend().toString(), args);
  }

  public static Path getFromModule(String... args) {
    return Paths.get(getUserDirectory(), args);
  }

}
