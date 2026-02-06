package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.core.HttpHeaders;

import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;
import org.lorislab.lorisgate.domain.model.HttpAuth;

import gen.org.lorislab.lorisgate.rs.oidc.model.ErrorTokenDTO;
import gen.org.lorislab.lorisgate.rs.oidc.model.UserInfoDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OidcUserRestController.class)
class OidcUserRestControllerTest extends AbstractOidcTest {

    @Test
    void testUserInfoWrongRealm() {

        var tokens = createUserTokens();

        given()
                .when()
                .header(HttpHeaders.AUTHORIZATION, HttpAuth.BEARER_PREFIX + tokens.getAccessToken())
                .when()
                .pathParam("realm", "does-not-exists")
                .get()
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }

    @Test
    void testUserInfo() {

        var error = given()
                .when()
                .pathParam("realm", REALM)
                .get()
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED)
                .extract().as(ErrorTokenDTO.class);

        assertThat(error).isNotNull();
        assertThat(error.getError()).isEqualTo(ErrorTokenDTO.ErrorEnum.MISSING_BEARER_TOKEN);

        error = given()
                .when()
                .header(HttpHeaders.AUTHORIZATION, "None 1")
                .pathParam("realm", "test")
                .get()
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED)
                .extract().as(ErrorTokenDTO.class);

        assertThat(error).isNotNull();
        assertThat(error.getError()).isEqualTo(ErrorTokenDTO.ErrorEnum.MISSING_BEARER_TOKEN);

        error = given()
                .when()
                .header(HttpHeaders.AUTHORIZATION, HttpAuth.bearerValue("12345"))
                .pathParam("realm", REALM)
                .get()
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED)
                .extract().as(ErrorTokenDTO.class);

        assertThat(error).isNotNull();
        assertThat(error.getError()).isEqualTo(ErrorTokenDTO.ErrorEnum.INVALID_TOKEN);

        var tokens = createUserTokens();

        var result = given()
                .when()
                .header(HttpHeaders.AUTHORIZATION, HttpAuth.bearerValue(tokens.getAccessToken()))
                .pathParam("realm", REALM)
                .get()
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(UserInfoDTO.class);

        assertThat(result).isNotNull();
        assertThat(result.getPreferredUsername()).isEqualTo("alice");
        assertThat(result.getEmail()).isEqualTo("alice@localhost");
    }

}
