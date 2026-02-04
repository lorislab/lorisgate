package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.given;

import java.util.UUID;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;
import org.lorislab.lorisgate.domain.model.GrantTypes;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OidcTokenRestController.class)
class OidcTokenRestControllerAuthCodeTest extends AbstractOidcTest {

    @Test
    void testCodeWrongClient() {
        //        var response = given()
        //                .when().contentType(MediaType.APPLICATION_JSON)
        //                .pathParam("realm", "does-not-exist")
        //                .queryParam("response_type", ResponseTypes.CODE)
        //                .queryParam("client_id", CLIENT_ID)
        //                .queryParam("redirect_uri", "http://localhost:8081/")
        //                .queryParam("scope", Scopes.OPENID)
        //                .queryParam("state", "foo")
        //                .queryParam("nonce", "n1")
        //                .queryParam("code_challenge_method", "S256")
        //                .queryParam("code_challenge", "12345")
        //                .get("/protocol/openid-connect/auth")
        //                .then()
        //                .statusCode(RestResponse.StatusCode.BAD_REQUEST)
        //                .extract().as(OAuthErrorDTO.class);
    }

    @Test
    void testWrongCode() {
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.AUTHORIZATION_CODE)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("code", UUID.randomUUID().toString())
                .formParam("redirect_uri", "http://localhost:8081/")
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }

    @Test
    void testCodeNull() {
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.AUTHORIZATION_CODE)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("redirect_uri", "http://localhost:8081/")
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }

    @Test
    void testRedirectUriNull() {
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.AUTHORIZATION_CODE)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("code", "12345")
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }

    @Test
    void testCodeAndRedirectUriNull() {
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.AUTHORIZATION_CODE)
                .formParam("client_id", CLIENT_ID_WEB)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }
}
