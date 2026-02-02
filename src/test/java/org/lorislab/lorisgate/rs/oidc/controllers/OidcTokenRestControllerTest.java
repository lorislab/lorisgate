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
class OidcTokenRestControllerTest extends AbstractOidcTest {

    private static final String REALM = "test";

    private static final String USERNAME = "alice";

    private static final String PASSWORD = "alice";

    private static final String CLIENT_ID = "app-backend";

    private static final String CLIENT_SECRET = "s3cr3t-value";

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
    void testTokenWithBasicBadRequest() {
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

        // no Basic prefix
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

    @Test
    void testGrantPasswordBadRequest() {
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

        // wrong password
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", "wrong-password")
                .formParam("grant_type", "password")
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED);
    }

    @Test
    void testGrantClientCredentials() {
        var response = given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.CLIENT_CREDENTIALS)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(TokenSuccessDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getIdToken()).isNull();
        assertThat(response.getRefreshToken()).isNull();

        // no scope
        response = given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.CLIENT_CREDENTIALS)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(TokenSuccessDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getIdToken()).isNull();
        assertThat(response.getRefreshToken()).isNull();

        // no openid scope
        response = given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.CLIENT_CREDENTIALS)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("scope", Scopes.EMAIL)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(TokenSuccessDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getIdToken()).isNull();
        assertThat(response.getRefreshToken()).isNull();
    }

    @Test
    void testTokenAppUser() {

        var response = given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("grant_type", GrantTypes.PASSWORD)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(TokenSuccessDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getIdToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();

        // no scopes
        response = given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("grant_type", GrantTypes.PASSWORD)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(TokenSuccessDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getIdToken()).isNull();
        assertThat(response.getRefreshToken()).isNotNull();

        // no openid scope
        response = given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("grant_type", GrantTypes.PASSWORD)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("scope", Scopes.EMAIL)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(TokenSuccessDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getIdToken()).isNull();
        assertThat(response.getRefreshToken()).isNotNull();
    }
}
