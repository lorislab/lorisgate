package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.given;

import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OidcAuthRestController.class)
class OidcAuthRestControllerTest extends AbstractAdminTest {
    @Test
    void testAuth() throws Exception {

    }

    @Test
    void testLoginPage() throws Exception {
        given()
                .when()
                .pathParam("realm", "test")
                .get("/login-actions/authenticate")
                .then()
                .statusCode(RestResponse.StatusCode.OK);
    }
}
