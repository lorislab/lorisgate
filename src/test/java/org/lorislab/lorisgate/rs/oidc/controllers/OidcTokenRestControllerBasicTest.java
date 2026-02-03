package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;
import org.lorislab.lorisgate.domain.model.GrantTypes;
import org.lorislab.lorisgate.domain.model.Scopes;

import gen.org.lorislab.lorisgate.rs.oidc.model.TokenSuccessDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OidcTokenRestController.class)
class OidcTokenRestControllerBasicTest extends AbstractOidcTest {

    @Test
    void testTokenWithBasic() {

        var basic = Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes(StandardCharsets.UTF_8));
        var response = given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Basic " + basic)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("grant_type", "password")
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(TokenSuccessDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getIdToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();

        response = given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Basic " + basic)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("client_id", CLIENT_ID)
                .formParam("grant_type", "password")
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(TokenSuccessDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getIdToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();

        response = given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Basic " + basic)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("grant_type", "password")
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(TokenSuccessDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getIdToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();

        response = given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Basic " + basic)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("grant_type", "password")
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(TokenSuccessDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getIdToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();

    }

    @Test
    void testTokenWithBasicBadRequestMissingBasicAuthSeparator() {
        // missing :
        var basic = Base64.getEncoder().encodeToString((CLIENT_ID + CLIENT_SECRET).getBytes(StandardCharsets.UTF_8));
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Basic " + basic)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("grant_type", GrantTypes.PASSWORD)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);

    }

    @Test
    void testTokenWithBasicBadRequestNoBasicPrefix() {
        // no Basic prefix
        var basic = Base64.getEncoder().encodeToString((CLIENT_ID + CLIENT_SECRET).getBytes(StandardCharsets.UTF_8));
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "None " + basic)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }

    @Test
    void testTokenWithBasicBadRequestNoClientId() {
        // no clientId
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("grant_type", GrantTypes.PASSWORD)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }

    @Test
    void testTokenWithBasicBadRequestNoGrantType() {
        // no grant_type
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("client_id", CLIENT_ID)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);

    }

    @Test
    void testTokenWithBasicBadRequestNoRealm() {
        // wrong realm
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", "wrong-realm")
                .formParam("username", USERNAME)
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
    void testTokenWithBasicBadRequestWrongClientId() {
        // clientId does not exist
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("grant_type", GrantTypes.PASSWORD)
                .formParam("client_id", "no-client-id")
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED);

    }

    @Test
    void testTokenWithBasicBadRequestWrongClientSecret() {
        // wrong client secret
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("grant_type", GrantTypes.PASSWORD)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", "wrong_secret")
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED);

    }

    @Test
    void testTokenWithBasicBadRequestNullClientSecret() {
        // null client secret
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("grant_type", GrantTypes.PASSWORD)
                .formParam("client_id", CLIENT_ID)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED);
    }

}
