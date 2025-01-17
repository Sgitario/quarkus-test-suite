package io.quarkus.ts.http.minimum.reactive;

import static org.hamcrest.CoreMatchers.is;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.bootstrap.DevModeQuarkusService;
import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.services.DevModeQuarkusApplication;
import io.quarkus.test.utils.AwaitilityUtils;

// TODO: mvavrik enable and adapt to new continuous testing page
@Disabled("Disabled as DEV UI continuous testing is currently re-worked")
@Tag("QUARKUS-1026")
@QuarkusScenario
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DevModeHttpMinimumReactiveIT {

    static final String HELLO_IN_ENGLISH = "Hello, %s!";
    static final String HELLO_IN_SPANISH = "Hola, %s!";
    static final String WORLD = "World";
    static final String HELLO_RESOURCE_JAVA = "src/main/java/io/quarkus/ts/http/minimum/reactive/HelloResource.java";

    @DevModeQuarkusApplication
    static DevModeQuarkusService app = new DevModeQuarkusService();

    @BeforeEach
    public void setup() {
        // Replace hello resource java with the original content
        app.copyFile(HELLO_RESOURCE_JAVA, HELLO_RESOURCE_JAVA);
    }

    @Test
    @Order(1)
    public void shouldDetectNewTests() {
        // At first, there are no tests annotated with @QuarkusTest
        app.logs().assertContains("Tests paused");
        // Now, we enable continuous testing via DEV UI
        app.enableContinuousTesting();
        // We add a new test
        app.copyFile("src/test/resources/HelloResourceTest.java.template", "src/test/java/HelloResourceTest.java");
        app.copyFile("src/test/resources/NoFunctionalTest.java.template", "src/test/java/NoFunctionalTest.java");
        // So good so far!
        app.logs().assertContains("All 2 tests are passing");
        // Modify Hello to Hola
        app.modifyFile(HELLO_RESOURCE_JAVA, content -> content.replace(HELLO_IN_ENGLISH, HELLO_IN_SPANISH));
        // Then the NoFunctionalTest should not have been executed as it's not affected, only HelloResourceTest
        app.logs().assertContains("Running 1/1. Running: io.quarkus.ts.http.minimum.HelloResourceTest#HelloResourceTest");
    }

    @Test
    @Order(2)
    public void shouldDetectChanges() {
        // Should say first Hello (the default name)
        app.given().get("/hello").then().statusCode(HttpStatus.SC_OK)
                .body("content", is(String.format(HELLO_IN_ENGLISH, WORLD)));

        // Modify Hello to Hola
        app.modifyFile(HELLO_RESOURCE_JAVA, content -> content.replace(HELLO_IN_ENGLISH, HELLO_IN_SPANISH));

        // Now, the app should say Manuel
        AwaitilityUtils.untilAsserted(
                () -> app.given().get("/hello").then().statusCode(HttpStatus.SC_OK)
                        .body("content", is(String.format(HELLO_IN_SPANISH, WORLD))));
    }
}
