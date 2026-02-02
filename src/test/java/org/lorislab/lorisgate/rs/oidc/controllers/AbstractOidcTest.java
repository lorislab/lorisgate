package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;
import org.lorislab.lorisgate.AbstractTest;
import org.lorislab.lorisgate.domain.model.GrantTypes;

import gen.org.lorislab.lorisgate.rs.oidc.model.TokenSuccessDTO;

class AbstractOidcTest extends AbstractTest {

    protected TokenSuccessDTO createUserTokens() {
        return createUserTokens("test", "alice", "alice", "web-portal", "openid");
    }

    protected TokenSuccessDTO createUserTokens(String realm, String user, String password, String clientId, String scopes) {
        return given().basePath("/realms/{realm}/protocol/openid-connect/token")
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", realm)
                .formParam("username", user)
                .formParam("password", password)
                .formParam("grant_type", GrantTypes.PASSWORD)
                .formParam("client_id", clientId)
                .formParam("scope", scopes)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(TokenSuccessDTO.class);
    }
}
