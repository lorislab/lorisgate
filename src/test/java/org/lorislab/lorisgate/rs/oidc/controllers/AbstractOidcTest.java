package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;
import org.lorislab.lorisgate.AbstractTest;
import org.lorislab.lorisgate.domain.model.GrantTypes;
import org.lorislab.lorisgate.domain.model.Scopes;

import gen.org.lorislab.lorisgate.rs.oidc.model.TokenSuccessDTO;

abstract class AbstractOidcTest extends AbstractTest {

    protected static final String PUBLIC_KEY_FILE = "src/test/resources/keys/test_public.example";

    protected static final String PRIVATE_KEY_FILE = "src/test/resources/keys/test_private.example";

    protected static final String ISSUER = "http://localhost:8080/realms/test";

    protected static final String REALM = "test";

    protected static final String USERNAME = "alice";

    protected static final String USERNAME_DISABLED = "disabled";

    protected static final String PASSWORD = "alice";

    protected static final String CLIENT_ID = "app-backend";

    protected static final String CLIENT_ID_WEB = "web-portal";

    protected static final String CLIENT_SECRET = "s3cr3t-value";

    protected TokenSuccessDTO createUserTokens() {
        return given().basePath("/realms/{realm}/protocol/openid-connect/token")
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("username", USERNAME)
                .formParam("password", PASSWORD)
                .formParam("grant_type", GrantTypes.PASSWORD)
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("scope", Scopes.OPENID)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(TokenSuccessDTO.class);
    }
}
