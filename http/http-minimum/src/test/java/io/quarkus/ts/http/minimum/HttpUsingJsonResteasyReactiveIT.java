package io.quarkus.ts.http.minimum;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.quarkus.test.bootstrap.RestService;
import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.services.Dependency;
import io.quarkus.test.services.QuarkusApplication;
import io.restassured.http.ContentType;

@Tag("reproducer-20859")
@QuarkusScenario
public class HttpUsingJsonResteasyReactiveIT {

    @QuarkusApplication(classes = { Hello.class, HelloReactiveResource.class }, dependencies = {
            @Dependency(artifactId = "quarkus-resteasy-reactive-jackson")
    })
    static final RestService app = new RestService();

    @Test
    public void validateDefaultMediaType() {
        given().get("/reactive/validate-no-produces/boom")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                // if JSON library is present, default is JSON
                .contentType(ContentType.JSON)
                .body("parameterViolations[0].message", containsString("numeric value out of bounds"));
    }

    @Test
    public void validateMultipleMediaTypesUsingAcceptJson() {
        given().accept(ContentType.JSON)
                .get("/reactive/validate-multiple-produces/boom")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.JSON)
                .body("violations[0].message", containsString("numeric value out of bounds"));
    }

    @Test
    public void validateMultipleMediaTypesUsingAcceptText() {
        given().accept(ContentType.TEXT)
                .get("/reactive/validate-multiple-produces/boom")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.TEXT)
                .body(containsString("numeric value out of bounds"));
    }
}
