package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.core.MediaType;

import org.htmlunit.SilentCssErrorHandler;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;
import org.lorislab.lorisgate.domain.utils.JwtHelper;

import gen.org.lorislab.lorisgate.rs.oidc.model.TokenSuccessDTO;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.specification.SpecificationQuerier;

@QuarkusTest
@TestHTTPEndpoint(OidcTokenRestController.class)
class OidcLoginFlowTest extends AbstractOidcTest {

    @Test
    void testLogin() throws IOException {

        var verifier = "123456";
        var codeChallenge = JwtHelper.generateChallenge(verifier);
        var homeUrl = SpecificationQuerier.query(given().basePath("/")).getURI();
        String code;

        var url = SpecificationQuerier.query(
                given().basePath("/realms/{realm}/protocol/openid-connect/auth")
                        .pathParam("realm", REALM)
                        .queryParam("response_type", "code")
                        .queryParam("client_id", CLIENT_ID_WEB)
                        .queryParam("redirect_uri", homeUrl)
                        .queryParam("scope", "openid email")
                        .queryParam("state", "foo")
                        .queryParam("nonce", "n1")
                        .queryParam("code_challenge_method", "S256")
                        .queryParam("code_challenge", codeChallenge))
                .getURI();

        try (final WebClient webClient = createWebClient()) {
            HtmlPage page = webClient.getPage(url);
            assertEquals("Sign in to test", page.getTitleText());

            HtmlForm loginForm = page.getForms().getFirst();

            loginForm.getInputByName("username").setValueAttribute(USERNAME);
            loginForm.getInputByName("password").setValueAttribute(PASSWORD);

            page = loginForm.getButtonByName("login").click();

            QueryStringDecoder decoder = new QueryStringDecoder(page.getBaseURL().toString());
            Map<String, List<String>> parameters = decoder.parameters();

            List<String> codes = parameters.get("code");
            code = (codes != null && !codes.isEmpty()) ? codes.getFirst() : null;
            webClient.getCookieManager().clearCookies();
        }

        assertThat(code).isNotNull();

        var response = given()
                .when().contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .pathParam("realm", REALM)
                .formParam("grant_type", "authorization_code")
                .formParam("client_id", CLIENT_ID_WEB)
                .formParam("code", code)
                .formParam("redirect_uri", homeUrl)
                .formParam("code_verifier", verifier)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(TokenSuccessDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getIdToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
    }

    private WebClient createWebClient() {
        WebClient webClient = new WebClient();
        webClient.setCssErrorHandler(new SilentCssErrorHandler());
        return webClient;
    }

}
