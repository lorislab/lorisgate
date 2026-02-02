package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.given;

import org.jboss.resteasy.reactive.RestResponse;
import org.lorislab.lorisgate.AbstractTest;

import gen.org.lorislab.lorisgate.rs.oidc.model.TokenSuccessDTO;

class AbstractAdminTest extends AbstractTest {

    protected TokenSuccessDTO createUserTokens() {
        return createUserTokens("test", "alice", "alice", "web-portal", "openid");
    }

    protected TokenSuccessDTO createUserTokens(String realm, String user, String password, String clientId, String scopes) {
        return given().basePath("/realms/{realm}/protocol/openid-connect/token")
                .when().contentType("application/x-www-form-urlencoded")
                .pathParam("realm", realm)
                .formParam("username", user)
                .formParam("password", password)
                .formParam("grant_type", "password")
                .formParam("client_id", clientId)
                .formParam("scope", scopes)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(TokenSuccessDTO.class);
    }
}
