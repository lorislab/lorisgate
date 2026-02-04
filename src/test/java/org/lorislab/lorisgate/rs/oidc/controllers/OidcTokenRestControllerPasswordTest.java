package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;
import org.lorislab.lorisgate.domain.model.GrantTypes;
import org.lorislab.lorisgate.domain.model.Scopes;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OidcTokenRestController.class)
class OidcTokenRestControllerPasswordTest extends AbstractOidcTest {

    @Test
    void testGrantPasswordBadRequestNoUser() {
        // no username
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("password", PASSWORD)
                .formParam("grant_type", GrantTypes.PASSWORD)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);

    }

    @Test
    void testGrantPasswordBadRequestNoUserAndPassword() {
        // no username and password
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("password", PASSWORD)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);

    }

    @Test
    void testGrantPasswordBadRequestNoPassword() {
        // no password
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("grant_type", GrantTypes.PASSWORD)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);

    }

    @Test
    void testGrantPasswordBadRequestWrongUser() {
        // wrong user
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", "wrong-user")
                .formParam("password", PASSWORD)
                .formParam("grant_type", GrantTypes.PASSWORD)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED);

    }

    @Test
    void testGrantPasswordBadRequestWrongPassword() {
        // wrong password
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", "wrong-password")
                .formParam("grant_type", GrantTypes.PASSWORD)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED);
    }

    @Test
    void testGrantPasswordBadRequestDisabledUser() {
        // wrong password
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", "disabled")
                .formParam("password", PASSWORD)
                .formParam("grant_type", GrantTypes.PASSWORD)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED);
    }

}
