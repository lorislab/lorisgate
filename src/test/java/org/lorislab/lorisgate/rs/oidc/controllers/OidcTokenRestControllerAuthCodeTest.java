package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;
import org.lorislab.lorisgate.domain.model.GrantTypes;
import org.lorislab.lorisgate.domain.model.ResponseTypes;
import org.lorislab.lorisgate.domain.model.Scopes;
import org.lorislab.lorisgate.domain.utils.JwtHelper;

import gen.org.lorislab.lorisgate.rs.admin.v1.model.UserDTO;
import gen.org.lorislab.lorisgate.rs.oidc.model.TokenSuccessDTO;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(OidcTokenRestController.class)
class OidcTokenRestControllerAuthCodeTest extends AbstractOidcTest {

    private String createCode(String codeChallenge, String clientId, String codeChallengeMethod) {
        return createCode(codeChallenge, clientId, codeChallengeMethod, USERNAME);
    }

    private String createCode(String codeChallenge, String clientId, String codeChallengeMethod, String username) {
        var req = given().redirects().follow(false)
                .when().contentType(MediaType.APPLICATION_JSON)
                .queryParam("response_type", ResponseTypes.CODE)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", "http://localhost:8081/")
                .queryParam("scope", Scopes.OPENID)
                .queryParam("state", "foo")
                .queryParam("nonce", "n1")
                .queryParam("as_user", username);

        if (codeChallenge != null) {
            req.queryParam("code_challenge", codeChallenge);
        }
        if (codeChallengeMethod != null) {
            req.queryParam("code_challenge_method", codeChallengeMethod);
        }

        var location = req.basePath("/realms/" + REALM)
                .get("/protocol/openid-connect/auth")
                .then()
                .statusCode(RestResponse.StatusCode.SEE_OTHER)
                .header(HttpHeaders.LOCATION, org.hamcrest.Matchers.containsString("code="))
                .extract().header(HttpHeaders.LOCATION);

        QueryStringDecoder decoder = new QueryStringDecoder(location);
        Map<String, List<String>> parameters = decoder.parameters();

        return parameters.get("code").getFirst();
    }

    @Test
    void testCodeCodeChallengePlainUserDisable() {

        var userToDelete = "auth-plain-disable";
        var verifier = "123456";
        var code = createCode(verifier, CLIENT_ID_WEB, null, userToDelete);

        // get user
        var user = given().basePath("/admin/realms/{realm}/users")
                .when().contentType(ContentType.JSON)
                .pathParam("realm", REALM)
                .get(userToDelete)
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(UserDTO.class);

        // disable user
        user.setEnabled(false);
        given().basePath("/admin/realms/{realm}/users")
                .when().contentType(ContentType.JSON)
                .pathParam("realm", REALM)
                .body(user)
                .put(userToDelete)
                .then()
                .statusCode(RestResponse.StatusCode.OK);

        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.AUTHORIZATION_CODE)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("code", code)
                .formParam("code_verifier", verifier)
                .formParam("redirect_uri", "http://localhost:8081/")
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED);

    }

    @Test
    void testCodeCodeChallengePlainUserDelete() {

        var userToDelete = "auth-plain-delete";
        var verifier = "123456";
        var code = createCode(verifier, CLIENT_ID_WEB, null, userToDelete);

        // delete user if exists
        given().basePath("/admin/realms/{realm}/users")
                .when().contentType(ContentType.JSON)
                .pathParam("realm", REALM)
                .delete(userToDelete)
                .then()
                .statusCode(RestResponse.StatusCode.OK);

        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.AUTHORIZATION_CODE)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("code", code)
                .formParam("code_verifier", verifier)
                .formParam("redirect_uri", "http://localhost:8081/")
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED);

    }

    @Test
    void testCodeCodeChallengePlain() {

        var verifier = "123456";
        var code = createCode(verifier, CLIENT_ID_WEB, null);

        // empty verifier
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.AUTHORIZATION_CODE)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("code", code)
                .formParam("code_verifier", " ")
                .formParam("redirect_uri", "http://localhost:8081/")
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);

        // no verifier
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.AUTHORIZATION_CODE)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("code", code)
                .formParam("redirect_uri", "http://localhost:8081/")
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);

        // wrong verifier
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.AUTHORIZATION_CODE)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("code", code)
                .formParam("code_verifier", "wrong-verifier")
                .formParam("redirect_uri", "http://localhost:8081/")
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);

        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.AUTHORIZATION_CODE)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("code", code)
                .formParam("code_verifier", verifier)
                .formParam("redirect_uri", "http://localhost:8081/")
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.OK);

    }

    @Test
    void testCodeCodeChallengeS256() {

        var verifier = "123456";
        var code_challenge = JwtHelper.generateChallenge(verifier);

        var code = createCode(code_challenge, CLIENT_ID_WEB, "S256");

        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.AUTHORIZATION_CODE)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("code", code)
                .formParam("code_verifier", verifier)
                .formParam("redirect_uri", "http://localhost:8081/")
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.OK);

    }

    @Test
    void testCodeNoCodeChallengeNoConfidential() {
        var code = createCode(null, "web-portal3", null);
        var response = given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.AUTHORIZATION_CODE)
                .formParam("client_id", "web-portal3")
                .formParam("client_secret", "web-portal3")
                .formParam("code", code)
                .formParam("redirect_uri", "http://localhost:8081/")
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
    void testCodeNoCodeChallenge() {
        var code = createCode(null, CLIENT_ID_WEB, null);

        // wrong client
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.AUTHORIZATION_CODE)
                .formParam("client_id", "web-portal2")
                .formParam("code", code)
                .formParam("redirect_uri", "http://localhost:8081/")
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);

        // wrong redirect
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.AUTHORIZATION_CODE)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("code", code)
                .formParam("redirect_uri", "http://localhost:8080/")
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);

        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.AUTHORIZATION_CODE)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("code", code)
                .formParam("redirect_uri", "http://localhost:8081/")
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
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
