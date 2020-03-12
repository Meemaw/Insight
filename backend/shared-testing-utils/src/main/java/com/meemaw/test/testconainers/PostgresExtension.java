package com.meemaw.test.testconainers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.junit.jupiter.Container;


public class PostgresExtension implements BeforeAllCallback {

  @Container
  private static PostgresSQLTestContainer POSTGRES = PostgresSQLTestContainer.newInstance();

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    if (!POSTGRES.isRunning()) {
      POSTGRES.start();
    }

    String projectPath = System.getProperty("user.dir");
    String projectName = new File(projectPath).getName();
    String migrationsProjectName = String.format("%s-migrations", projectName);
    Path migrationsSqlPath = Paths.get(projectPath, "..", migrationsProjectName, "sql");
    System.out.println("Applying migrations from: " + migrationsSqlPath.toAbsolutePath());

    Files.walk(migrationsSqlPath).filter(path -> !Files.isDirectory(path)).forEach(path -> {
      try {
        POSTGRES.client()
            .query(Files.readString(path))
            .toCompletableFuture()
            .join();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    });
  }


}
