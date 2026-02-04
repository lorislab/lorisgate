package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestResponse;
import org.lorislab.lorisgate.AbstractTest;
import org.lorislab.lorisgate.domain.model.GrantTypes;
import org.lorislab.lorisgate.domain.model.Scopes;

import gen.org.lorislab.lorisgate.rs.oidc.model.TokenSuccessDTO;

class AbstractOidcTest extends AbstractTest {

    @ConfigProperty(name = "%test.lorisgate.key.public-key-file")
    protected String publicKeyFile;

    @ConfigProperty(name = "%test.lorisgate.key.private-key-file")
    protected String privateKeyFile;

    protected static final String ISSUER = "http://localhost:8080/realms/test";

    protected static final String REALM = "test";

    protected static final String USERNAME = "alice";

    protected static final String PASSWORD = "alice";

    protected static final String CLIENT_ID = "app-backend";

    protected static final String CLIENT_ID_WEB = "web-portal";

    protected static final String CLIENT_SECRET = "s3cr3t-value";

    protected TokenSuccessDTO createUserTokens() {
        return createUserTokens(REALM, USERNAME, PASSWORD, CLIENT_ID_WEB, Scopes.OPENID);
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
