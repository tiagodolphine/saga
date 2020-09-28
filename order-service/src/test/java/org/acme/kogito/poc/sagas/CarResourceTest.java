package org.acme.kogito.poc.sagas;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class CarResourceTest {

    @Test
    public void testEndpoint() {
        given()
          .when().get("/cars/reservations")
          .then()
             .statusCode(200);
    }

}