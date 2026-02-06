package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;
import org.lorislab.lorisgate.domain.model.GrantTypes;
import org.lorislab.lorisgate.domain.model.Scopes;

import gen.org.lorislab.lorisgate.rs.oidc.model.TokenErrorDTO;
import gen.org.lorislab.lorisgate.rs.oidc.model.TokenSuccessDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OidcTokenRestController.class)
class OidcTokenRestControllerTest extends AbstractOidcTest {

    @Test
    void testWrongGrantType() {
        var response = given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", "wrong-grant-type")
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST)
                .extract().as(TokenErrorDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getError()).isEqualTo(TokenErrorDTO.ErrorEnum.UNSUPPORTED_GRANT_TYPE);
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
    }

    @Test
    void testTokenAppUserNoScopes() {

        var response = given()
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
    }

    @Test
    void testTokenAppUserNoOpenId() {
        var response = given()
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
