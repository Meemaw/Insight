package com.meemaw.test.testconainers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;


@Slf4j
public class PostgresExtension implements BeforeAllCallback {

  private static PostgresSQLTestContainer POSTGRES = PostgresSQLTestContainer.newInstance();

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    if (!POSTGRES.isRunning()) {
      POSTGRES.start();
      System.setProperty("quarkus.datasource.url", POSTGRES.getDatasourceURL());
    }

    String projectPath = System.getProperty("user.dir");
    Path migrationsSqlPath = Paths.get(projectPath, "migrations", "sql");
    System.out.println("Applying migrations from: " + migrationsSqlPath.toAbsolutePath());

    Files.walk(migrationsSqlPath).filter(path -> !Files.isDirectory(path)).forEach(path -> {
      System.out.println("Applying: " + path);
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
