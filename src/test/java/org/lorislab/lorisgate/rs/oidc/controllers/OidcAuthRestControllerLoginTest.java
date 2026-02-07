package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OidcAuthRestController.class)
class OidcAuthRestControllerLoginTest extends AbstractOidcTest {

    @Test
    void testLoginPage() {
        given()
                .when()
                .pathParam("realm", "test")
                .get("/login-actions/authenticate")
                .then()
                .statusCode(RestResponse.StatusCode.OK);
    }

    @Test
    void testLoginPageWrongRealm() {
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", "wrong-realm")
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("return_to", "http://localhost:8081/")
                .post("/login-actions/authenticate")
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }

    @Test
    void testLoginPageWrongUser() {
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", "does-not-exist")
                .formParam("password", PASSWORD)
                .formParam("return_to", "http://localhost:8081/")
                .post("/login-actions/authenticate")
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED);
    }

    @Test
    void testLoginPageDisabledUser() {
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME_DISABLED)
                .formParam("password", PASSWORD)
                .formParam("return_to", "http://localhost:8081/")
                .post("/login-actions/authenticate")
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED);
    }

    @Test
    void testLoginPageWrongPassword() {
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", "wrong-password")
                .formParam("return_to", "http://localhost:8081/")
                .post("/login-actions/authenticate")
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED);
    }

    @Test
    void testLoginPageNoReturnTo() {
        given()
                .redirects().follow(false)
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .post("/login-actions/authenticate")
                .then()
                .statusCode(RestResponse.StatusCode.SEE_OTHER);
    }

    @Test
    void testLoginPageReturnToQuery() {
        given().redirects().follow(false)
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("return_to", "http://localhost:8081/?foo=bar")
                .post("/login-actions/authenticate")
                .then()
                .statusCode(RestResponse.StatusCode.SEE_OTHER);

        given().redirects().follow(false)
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("return_to", "http://localhost:8081/")
                .post("/login-actions/authenticate")
                .then()
                .statusCode(RestResponse.StatusCode.SEE_OTHER);
    }
}
