package io.quarkus.ts.jaxrs.reactive;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import io.quarkus.test.scenarios.QuarkusScenario;
import io.restassured.http.ContentType;

@QuarkusScenario
public class BookResourceIT {

    @Test
    public void testUsingJson() {
        given()
                .accept(ContentType.JSON)
                .get("/custom-book")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(is("{\"title\":\"Title!\"}"));
    }

    @Test
    public void testUsingXml() {
        Book actual = given()
                .accept(ContentType.XML)
                .get("/custom-book")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(Book.class);

        assertEquals("Title!", actual.title);
    }
}
