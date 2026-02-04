package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;
import org.jose4j.jwt.ReservedClaimNames;
import org.junit.jupiter.api.Test;
import org.lorislab.lorisgate.domain.model.ClaimNames;
import org.lorislab.lorisgate.domain.model.GrantTypes;
import org.lorislab.lorisgate.domain.model.Scopes;
import org.lorislab.lorisgate.domain.utils.JwtHelper;

import gen.org.lorislab.lorisgate.rs.oidc.model.TokenSuccessDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.build.Jwt;

@QuarkusTest
@TestHTTPEndpoint(OidcTokenRestController.class)
class OidcTokenRestControllerRefreshTokenTest extends AbstractOidcTest {

    @Test
    void testGrantTypeRefreshTokenMissingRefreshToken() {
        // missing refresh token
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.REFRESH_TOKEN)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }

    @Test
    void testGrantTypeRefreshTokenEmptyRefreshToken() {
        // missing refresh token
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.REFRESH_TOKEN)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("scope", Scopes.OPENID)
                .formParam("refresh_token", " ")
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }

    @Test
    void testGrantTypeRefreshTokenWrongClientId() {

        var tokens = createUserTokens();

        // wrong client id
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.REFRESH_TOKEN)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("scope", Scopes.OPENID)
                .formParam("refresh_token", tokens.getRefreshToken())
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }

    @Test
    void testGrantTypeRefreshToken() {

        var tokens = createUserTokens();

        // wrong client id
        var response = given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.REFRESH_TOKEN)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("scope", Scopes.OPENID)
                .formParam("refresh_token", tokens.getRefreshToken())
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
    void testGrantTypeRefreshTokenNoUsername() throws Exception {
        var tokens = createUserTokens();

        var keys = JwtHelper.fromFiles(privateKeyFile, publicKeyFile);

        var claims = JwtHelper.parse(ISSUER, tokens.getRefreshToken(), keys.getPublic(), 3600);

        var map = claims.getClaimsMap();
        map.remove(ClaimNames.PREFERRED_USERNAME);
        var keyId = (String) map.get(ReservedClaimNames.JWT_ID);

        var newToken = Jwt.claims(map);

        var refreshToken = JwtHelper.sign(newToken, keyId, keys.getPrivate());

        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.REFRESH_TOKEN)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("scope", Scopes.OPENID)
                .formParam("refresh_token", refreshToken)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.OK);
    }

    @Test
    void testGrantTypeRefreshTokenWrongUsername() throws Exception {
        var tokens = createUserTokens();

        var keys = JwtHelper.fromFiles(privateKeyFile, publicKeyFile);

        var claims = JwtHelper.parse(ISSUER, tokens.getRefreshToken(), keys.getPublic(), 3600);

        var map = claims.getClaimsMap();
        var keyId = (String) map.get(ReservedClaimNames.JWT_ID);

        var newToken = Jwt.claims(map)
                .preferredUserName("does-not-exists");
        var refreshToken = JwtHelper.sign(newToken, keyId, keys.getPrivate());

        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.REFRESH_TOKEN)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("scope", Scopes.OPENID)
                .formParam("refresh_token", refreshToken)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED);
    }

    @Test
    void testGrantTypeRefreshTokenDisabledUsername() throws Exception {
        var tokens = createUserTokens();

        var keys = JwtHelper.fromFiles(privateKeyFile, publicKeyFile);
        var claims = JwtHelper.parse(ISSUER, tokens.getRefreshToken(), keys.getPublic(), 3600);

        var map = claims.getClaimsMap();
        var keyId = (String) map.get(ReservedClaimNames.JWT_ID);

        var newToken = Jwt.claims(map).preferredUserName("disabled");

        var refreshToken = JwtHelper.sign(newToken, keyId, keys.getPrivate());

        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.REFRESH_TOKEN)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("scope", Scopes.OPENID)
                .formParam("refresh_token", refreshToken)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED);
    }

    @Test
    void testGrantTypeRefreshTokenParseException() {
        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.REFRESH_TOKEN)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("scope", Scopes.OPENID)
                .formParam("refresh_token", "parse-Refresh-token-exception")
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }

    @Test
    void testGrantTypeRefreshTokenExpired() throws Exception {

        var tokens = createUserTokens();

        var keys = JwtHelper.fromFiles(privateKeyFile, publicKeyFile);

        var claims = JwtHelper.parse(ISSUER, tokens.getRefreshToken(), keys.getPublic(), 3600);

        var map = claims.getClaimsMap();
        var keyId = (String) map.get(ReservedClaimNames.JWT_ID);

        var newToken = Jwt.claims(map)
                .issuedAt(Instant.now().minusSeconds(15))
                .expiresAt(Instant.now().minusSeconds(10));

        var refreshToken = JwtHelper.sign(newToken, keyId, keys.getPrivate());

        given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", GrantTypes.REFRESH_TOKEN)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("scope", Scopes.OPENID)
                .formParam("refresh_token", refreshToken)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }
}
