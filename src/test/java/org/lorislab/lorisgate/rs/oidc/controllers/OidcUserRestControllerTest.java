package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import gen.org.lorislab.lorisgate.rs.oidc.model.ErrorTokenDTO;
import gen.org.lorislab.lorisgate.rs.oidc.model.UserInfoDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OidcUserRestController.class)
class OidcUserRestControllerTest extends AbstractAdminTest {

    @Test
    void testUserInfo() {

        var error = given()
                .when()
                .pathParam("realm", "test")
                .get()
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED)
                .extract().as(ErrorTokenDTO.class);

        assertThat(error).isNotNull();
        assertThat(error.getError()).isEqualTo(ErrorTokenDTO.ErrorEnum.MISSING_BEARER_TOKEN);

        error = given()
                .when()
                .header("Authorization", "None 1")
                .pathParam("realm", "test")
                .get()
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED)
                .extract().as(ErrorTokenDTO.class);

        assertThat(error).isNotNull();
        assertThat(error.getError()).isEqualTo(ErrorTokenDTO.ErrorEnum.MISSING_BEARER_TOKEN);

        error = given()
                .when()
                .header("Authorization", "Bearer 12345")
                .pathParam("realm", "test")
                .get()
                .then()
                .statusCode(RestResponse.StatusCode.UNAUTHORIZED)
                .extract().as(ErrorTokenDTO.class);

        assertThat(error).isNotNull();
        assertThat(error.getError()).isEqualTo(ErrorTokenDTO.ErrorEnum.INVALID_TOKEN);

        var tokens = createUserTokens();

        var result = given()
                .when()
                .header("Authorization", "Bearer " + tokens.getAccessToken())
                .pathParam("realm", "test")
                .get()
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(UserInfoDTO.class);

        assertThat(result).isNotNull();
        assertThat(result.getPreferredUsername()).isEqualTo("alice");
        assertThat(result.getEmail()).isEqualTo("alice@localhost");
    }

}
