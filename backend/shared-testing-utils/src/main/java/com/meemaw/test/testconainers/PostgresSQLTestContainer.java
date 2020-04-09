package com.meemaw.test.testconainers;

import io.vertx.axle.pgclient.PgPool;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresSQLTestContainer extends PostgreSQLContainer<PostgresSQLTestContainer> {

  private static final String DOCKER_TAG = "postgres:11.6";
  private static final String HOST = "localhost";
  private static final String DATABASE_NAME = "postgres";
  private static final String USERNAME = "postgres";
  private static final String PASSWORD = "postgres";
  private static final int PORT = PostgreSQLContainer.POSTGRESQL_PORT;

  private PostgresSQLTestContainer() {
    super(DOCKER_TAG);
  }

  public static PostgresSQLTestContainer newInstance() {
    return new PostgresSQLTestContainer()
        .withDatabaseName(DATABASE_NAME)
        .withUsername(USERNAME)
        .withPassword(PASSWORD)
        .withExposedPorts(PORT);
  }

  public PgPool client() {
    return PostgresSQLTestContainer.client(this);
  }

  public static PgPool client(PostgreSQLContainer<PostgresSQLTestContainer> container) {
    PgConnectOptions connectOptions = new PgConnectOptions()
        .setPort(container.getMappedPort(PORT))
        .setHost(HOST)
        .setDatabase(container.getDatabaseName())
        .setUser(container.getUsername())
        .setPassword(container.getPassword());

    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
    return PgPool.pool(connectOptions, poolOptions);
  }


  public String getDatasourceURL() {
    return String
        .format("vertx-reactive:postgresql://%s:%d/%s", HOST, getMappedPort(PORT), DATABASE_NAME);
  }
}
