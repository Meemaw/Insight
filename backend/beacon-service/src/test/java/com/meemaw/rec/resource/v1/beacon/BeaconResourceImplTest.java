package com.meemaw.rec.resource.v1.beacon;

import static com.meemaw.test.matchers.SameJSON.sameJson;
import static io.restassured.RestAssured.given;

import com.meemaw.rec.beacon.resource.v1.BeaconResource;
import com.meemaw.shared.rest.exception.DatabaseException;
import com.meemaw.test.testconainers.kafka.KafkaResource;
import com.meemaw.test.testconainers.pg.Postgres;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


@Postgres
@QuarkusTestResource(KafkaResource.class)
@QuarkusTest
@Tag("integration")
public class BeaconResourceImplTest {

  @Inject
  PgPool pgPool;

  private static final String INSERT_PAGE_RAW_SQL = "INSERT INTO rec.page (id, uid, session_id, organization, doctype, url, referrer, height, width, screen_height, screen_width, compiled_timestamp) VALUES($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12);";

  private Uni<Void> insertPage(UUID pageId, UUID uid, UUID sessionId) {
    Tuple values = Tuple.newInstance(io.vertx.sqlclient.Tuple.of(
        pageId,
        uid,
        sessionId,
        "testOrganization",
        "testDocType",
        "testURL",
        "testReferrer",
        200,
        200,
        200,
        200,
        200
    ));

    return pgPool.preparedQuery(INSERT_PAGE_RAW_SQL, values)
        .onItem().ignore().andContinueWithNull()
        .onFailure().invoke(throwable -> {
          throw new DatabaseException();
        });
  }


  @Test
  public void postBeacon_shouldThrowError_whenEmptyTextPlainPayload() {
    given()
        .when().contentType(MediaType.TEXT_PLAIN).post(BeaconResource.PATH)
        .then()
        .statusCode(422)
        .body(sameJson(
            "{\"error\":{\"statusCode\":422,\"reason\":\"Unprocessable Entity\",\"message\":\"No content to map due to end-of-input\"}}"));
  }

  @Test
  public void postBeacon_shouldThrowError_whenEmptyJsonPayload() {
    given()
        .when().contentType(MediaType.APPLICATION_JSON).post(BeaconResource.PATH)
        .then()
        .statusCode(422)
        .body(sameJson(
            "{\"error\":{\"statusCode\":422,\"reason\":\"Unprocessable Entity\",\"message\":\"No content to map due to end-of-input\"}}"));
  }

  @Test
  public void postBeacon_shouldThrowError_whenEmptyJson() {
    given()
        .when().contentType(MediaType.APPLICATION_JSON).body("{}").post(BeaconResource.PATH)
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"sequence\":\"s must be greater than 0\",\"events\":\"e may not be empty\"}}}"));
  }

  @Test
  public void postBeaconAsJson_shouldThrowError_whenUnlinkedBeacon()
      throws IOException, URISyntaxException {
    String payload = Files.readString(Path.of(getClass().getResource(
        "/beacon/initial.json").toURI()));

    given()
        .when().contentType(ContentType.JSON).body(payload).post(BeaconResource.PATH)
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Unlinked beacon\"}}"));
  }

  @Test
  public void postBeaconAsJson_shouldStore_whenValidPayload()
      throws IOException, URISyntaxException {
    UUID sessionID = UUID.randomUUID();
    UUID uid = UUID.randomUUID();
    UUID pageID = UUID.randomUUID();

    insertPage(pageID, uid, sessionID).await().indefinitely();

    String payload = Files.readString(Path.of(getClass().getResource(
        "/beacon/initial.json").toURI()));

    given()
        .when().contentType(ContentType.JSON)
        .queryParam("SessionID", sessionID)
        .queryParam("UserID", uid)
        .queryParam("PageID", pageID)
        .body(payload)
        .post(BeaconResource.PATH)
        .then()
        .statusCode(500);
  }

  @Test
  public void postBeaconAsText_shouldStore_whenValidPayload()
      throws IOException, URISyntaxException {
    UUID sessionID = UUID.randomUUID();
    UUID uid = UUID.randomUUID();
    UUID pageID = UUID.randomUUID();

    insertPage(pageID, uid, sessionID).await().indefinitely();

    String payload = Files.readString(Path.of(getClass().getResource(
        "/beacon/initial.json").toURI()));

    given()
        .when()
        .contentType(ContentType.TEXT)
        .queryParam("SessionID", sessionID)
        .queryParam("UserID", uid)
        .queryParam("PageID", pageID)
        .body(payload)
        .post(BeaconResource.PATH)
        .then()
        .statusCode(204);
  }


  @Test
  public void postBeaconSmall_shouldStore_whenValidPayload()
      throws IOException, URISyntaxException {
    UUID sessionID = UUID.randomUUID();
    UUID uid = UUID.randomUUID();
    UUID pageID = UUID.randomUUID();

    insertPage(pageID, uid, sessionID).await().indefinitely();

    String payload = Files.readString(Path.of(getClass().getResource(
        "/beacon/small.json").toURI()));

    given()
        .when()
        .contentType(ContentType.TEXT)
        .queryParam("SessionID", sessionID)
        .queryParam("UserID", uid)
        .queryParam("PageID", pageID)
        .body(payload)
        .post(BeaconResource.PATH)
        .then()
        .statusCode(204);
  }

  @Test
  public void postBeacon_shouldThrowError_whenNoEvents()
      throws IOException, URISyntaxException {
    String payload = Files.readString(Path.of(getClass().getResource(
        "/beacon/noEvents.json").toURI()));

    given()
        .when().contentType(ContentType.TEXT).body(payload).post(BeaconResource.PATH)
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"events\":\"e may not be empty\"}}}"));
  }

}
