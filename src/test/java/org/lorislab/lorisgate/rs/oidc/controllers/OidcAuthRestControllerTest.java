package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;
import org.lorislab.lorisgate.domain.model.ResponseTypes;
import org.lorislab.lorisgate.domain.model.Scopes;

import gen.org.lorislab.lorisgate.rs.oidc.model.OAuthErrorDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OidcAuthRestController.class)
class OidcAuthRestControllerTest extends AbstractOidcTest {

    @Test
    void testAuthWrongResponseType() {
        var response = given().redirects().follow(false)
                .when().contentType(MediaType.APPLICATION_JSON)
                .pathParam("realm", REALM)
                .queryParam("response_type", "wrong-response-type")
                .queryParam("client_id", "redirect-test")
                .queryParam("redirect_uri", "http://localhost/callback/?foo=bar")
                .queryParam("scope", Scopes.OPENID)
                .queryParam("state", "foo")
                .queryParam("nonce", "n1")
                .queryParam("code_challenge_method", "S256")
                .queryParam("code_challenge", "12345")
                .queryParam("as_user", USERNAME)
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST)
                .extract().as(OAuthErrorDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isEqualTo(OAuthErrorDTO.ErrorEnum.INVALID_REQUEST);
    }

    @Test
    void testAuthResponseTypeTokenIdToken() {
        given().redirects().follow(false)
                .when().contentType(MediaType.APPLICATION_JSON)
                .pathParam("realm", REALM)
                .queryParam("response_type", ResponseTypes.ID_TOKEN + " " + ResponseTypes.TOKEN)
                .queryParam("client_id", "redirect-test")
                .queryParam("redirect_uri", "http://localhost/callback/?foo=bar")
                .queryParam("scope", Scopes.OPENID)
                .queryParam("state", "foo")
                .queryParam("nonce", "n1")
                .queryParam("code_challenge_method", "S256")
                .queryParam("code_challenge", "12345")
                .queryParam("as_user", USERNAME)
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.SEE_OTHER);
    }

    @Test
    void testAuthResponseTypeIdToken() {
        given().redirects().follow(false)
                .when().contentType(MediaType.APPLICATION_JSON)
                .pathParam("realm", REALM)
                .queryParam("response_type", ResponseTypes.ID_TOKEN)
                .queryParam("client_id", "redirect-test")
                .queryParam("redirect_uri", "http://localhost/callback/?foo=bar")
                .queryParam("scope", Scopes.OPENID)
                .queryParam("state", "foo")
                .queryParam("nonce", "n1")
                .queryParam("code_challenge_method", "S256")
                .queryParam("code_challenge", "12345")
                .queryParam("as_user", USERNAME)
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.SEE_OTHER);
    }

    @Test
    void testAuthResponseTypeTokenStateNull() {
        given().redirects().follow(false)
                .when().contentType(MediaType.APPLICATION_JSON)
                .pathParam("realm", REALM)
                .queryParam("response_type", ResponseTypes.TOKEN)
                .queryParam("client_id", "redirect-test")
                .queryParam("redirect_uri", "http://localhost/callback/?foo=bar")
                .queryParam("scope", Scopes.OPENID)
                .queryParam("nonce", "n1")
                .queryParam("code_challenge_method", "S256")
                .queryParam("code_challenge", "12345")
                .queryParam("as_user", USERNAME)
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.SEE_OTHER);
    }

    @Test
    void testAuthResponseTypeToken() {
        given().redirects().follow(false)
                .when().contentType(MediaType.APPLICATION_JSON)
                .pathParam("realm", REALM)
                .queryParam("response_type", ResponseTypes.TOKEN)
                .queryParam("client_id", "redirect-test")
                .queryParam("redirect_uri", "http://localhost/callback/?foo=bar")
                .queryParam("scope", Scopes.OPENID)
                .queryParam("state", "foo")
                .queryParam("nonce", "n1")
                .queryParam("code_challenge_method", "S256")
                .queryParam("code_challenge", "12345")
                .queryParam("as_user", USERNAME)
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.SEE_OTHER);
    }

    @Test
    void testAuthUser() {
        given().redirects().follow(false)
                .when().contentType(MediaType.APPLICATION_JSON)
                .pathParam("realm", REALM)
                .queryParam("response_type", ResponseTypes.CODE)
                .queryParam("client_id", "redirect-test")
                .queryParam("redirect_uri", "http://localhost/callback/?foo=bar")
                .queryParam("scope", Scopes.OPENID)
                .queryParam("state", "foo")
                .queryParam("nonce", "n1")
                .queryParam("code_challenge_method", "S256")
                .queryParam("code_challenge", "12345")
                .queryParam("as_user", USERNAME)
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.SEE_OTHER);
    }

    @Test
    void testAuthUserNoState() {
        given().redirects().follow(false)
                .when().contentType(MediaType.APPLICATION_JSON)
                .pathParam("realm", REALM)
                .queryParam("response_type", ResponseTypes.CODE)
                .queryParam("client_id", "redirect-test")
                .queryParam("redirect_uri", "http://localhost/callback/?foo=bar")
                .queryParam("scope", Scopes.OPENID)
                .queryParam("nonce", "n1")
                .queryParam("code_challenge_method", "S256")
                .queryParam("code_challenge", "12345")
                .queryParam("as_user", USERNAME)
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.SEE_OTHER);
    }

    @Test
    void testAuthUserDisabled() {
        var response = given().redirects().follow(false)
                .when().contentType(MediaType.APPLICATION_JSON)
                .pathParam("realm", REALM)
                .queryParam("response_type", ResponseTypes.CODE)
                .queryParam("client_id", "redirect-test")
                .queryParam("redirect_uri", "http://localhost/callback/?foo=bar")
                .queryParam("scope", Scopes.OPENID)
                .queryParam("state", "foo")
                .queryParam("nonce", "n1")
                .queryParam("code_challenge_method", "S256")
                .queryParam("code_challenge", "12345")
                .queryParam("as_user", USERNAME_DISABLED)
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST)
                .extract().as(OAuthErrorDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isEqualTo(OAuthErrorDTO.ErrorEnum.ACCESS_DENIED);
    }

    @Test
    void testAuthNoUserNoCodeChallenge() {
        given().redirects().follow(false)
                .when().contentType(MediaType.APPLICATION_JSON)
                .pathParam("realm", REALM)
                .queryParam("response_type", ResponseTypes.CODE)
                .queryParam("client_id", CLIENT_ID_WEB)
                .queryParam("redirect_uri", "http://localhost/callback/?foo=bar")
                .queryParam("scope", Scopes.OPENID)
                .queryParam("nonce", "n1")
                .queryParam("code_challenge_method", "S256")
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.SEE_OTHER);
    }

    @Test
    void testAuthWrongUser() {
        given().redirects().follow(false)
                .when().contentType(MediaType.APPLICATION_JSON)
                .pathParam("realm", REALM)
                .queryParam("response_type", ResponseTypes.CODE)
                .queryParam("client_id", "redirect-test")
                .queryParam("redirect_uri", "http://localhost/callback/?foo=bar")
                .queryParam("scope", Scopes.OPENID)
                .queryParam("nonce", "n1")
                .queryParam("code_challenge_method", "S256")
                .queryParam("code_challenge", "12345")
                .queryParam("as_user", "does-not-exist")
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }

    @Test
    void testAuthWrongClientRedirect() {
        var response = given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .pathParam("realm", REALM)
                .queryParam("response_type", ResponseTypes.CODE)
                .queryParam("client_id", "redirect-test")
                .queryParam("redirect_uri", "http://localhost:8081/")
                .queryParam("scope", Scopes.OPENID)
                .queryParam("state", "foo")
                .queryParam("nonce", "n1")
                .queryParam("code_challenge_method", "S256")
                .queryParam("code_challenge", "12345")
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST)
                .extract().as(OAuthErrorDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isEqualTo(OAuthErrorDTO.ErrorEnum.INVALID_REQUEST);
    }

    @Test
    void testAuthClientRedirect() {
        given().redirects().follow(false)
                .when().contentType(MediaType.APPLICATION_JSON)
                .pathParam("realm", REALM)
                .queryParam("response_type", ResponseTypes.CODE)
                .queryParam("client_id", "redirect-test")
                .queryParam("redirect_uri", "http://localhost/callback/?foo=bar")
                .queryParam("scope", Scopes.OPENID)
                .queryParam("state", "foo")
                .queryParam("nonce", "n1")
                .queryParam("code_challenge_method", "S256")
                .queryParam("code_challenge", "12345")
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.SEE_OTHER);
    }

    @Test
    void testAuthWrongClient() {
        var response = given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .pathParam("realm", REALM)
                .queryParam("response_type", ResponseTypes.CODE)
                .queryParam("client_id", "wrong-client")
                .queryParam("redirect_uri", "http://localhost:8081/")
                .queryParam("scope", Scopes.OPENID)
                .queryParam("state", "foo")
                .queryParam("nonce", "n1")
                .queryParam("code_challenge_method", "S256")
                .queryParam("code_challenge", "12345")
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST)
                .extract().as(OAuthErrorDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isEqualTo(OAuthErrorDTO.ErrorEnum.UNAUTHORIZED_CLIENT);
    }

    @Test
    void testAuthWrongRealm() {
        var response = given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .pathParam("realm", "does-not-exist")
                .queryParam("response_type", ResponseTypes.CODE)
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", "http://localhost:8081/")
                .queryParam("scope", Scopes.OPENID)
                .queryParam("state", "foo")
                .queryParam("nonce", "n1")
                .queryParam("code_challenge_method", "S256")
                .queryParam("code_challenge", "12345")
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST)
                .extract().as(OAuthErrorDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isEqualTo(OAuthErrorDTO.ErrorEnum.REALM_NOT_FOUND);
    }

    @Test
    void testAuthNoResponseType() {
        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .pathParam("realm", REALM)
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", "http://localhost:8081/")
                .queryParam("scope", Scopes.OPENID)
                .queryParam("state", "foo")
                .queryParam("nonce", "n1")
                .queryParam("code_challenge_method", "S256")
                .queryParam("code_challenge", "12345")
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }

    @Test
    void testAuthNoClientId() throws Exception {
        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .pathParam("realm", REALM)
                .queryParam("response_type", ResponseTypes.CODE)
                .queryParam("redirect_uri", "http://localhost:8081/")
                .queryParam("scope", Scopes.OPENID)
                .queryParam("state", "foo")
                .queryParam("nonce", "n1")
                .queryParam("code_challenge_method", "S256")
                .queryParam("code_challenge", "12345")
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }

    @Test
    void testAuthNoRedirectUri() throws Exception {
        given()
                .when().contentType(MediaType.APPLICATION_JSON)
                .pathParam("realm", REALM)
                .queryParam("response_type", ResponseTypes.CODE)
                .queryParam("client_id", CLIENT_ID)
                .queryParam("scope", Scopes.OPENID)
                .queryParam("state", "foo")
                .queryParam("nonce", "n1")
                .queryParam("code_challenge_method", "S256")
                .queryParam("code_challenge", "12345")
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }
}
