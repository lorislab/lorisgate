package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import gen.org.lorislab.lorisgate.rs.oidc.model.JwksDTO;
import gen.org.lorislab.lorisgate.rs.oidc.model.OpenIdConfigurationDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OidcConfigRestController.class)
class OidcConfigRestControllerTest extends AbstractOidcTest {

    @Test
    void testGetOidcConfig() {

        var result = given()
                .when()
                .pathParam("realm", REALM)
                .get("/.well-known/openid-configuration")
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(OpenIdConfigurationDTO.class);

        assertThat(result).isNotNull();
        assertThat(result.getIssuer()).hasPath("/realms/test");
    }

    @Test
    void testGetOidcConfigNoRealm() {

        given()
                .when()
                .pathParam("realm", "does-not-exists")
                .get("/.well-known/openid-configuration")
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }

    @Test
    void testJwks() {

        var result = given()
                .when()
                .pathParam("realm", REALM)
                .get("/protocol/openid-connect/certs")
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(JwksDTO.class);

        assertThat(result).isNotNull();
        assertThat(result.getKeys()).hasSize(1);
        var key = result.getKeys().getFirst();
        assertThat(key).isNotNull();
        assertThat(key.getKty()).isEqualTo("RSA");
        assertThat(key.getAlg()).isEqualTo("RS256");
    }

    @Test
    void testJwksNoRealm() {

        given()
                .when()
                .pathParam("realm", "does-not-exists")
                .get("/protocol/openid-connect/certs")
                .then()
                .statusCode(RestResponse.StatusCode.BAD_REQUEST);
    }
}
