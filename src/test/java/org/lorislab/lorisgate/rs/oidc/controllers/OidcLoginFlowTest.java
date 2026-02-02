package org.lorislab.lorisgate.rs.oidc.controllers;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.core.MediaType;

import org.htmlunit.SilentCssErrorHandler;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

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

        var realm = "test";
        var username = "alice";
        var password = "alice";
        var clientId = "web-portal";
        var verifier = "123456";
        var code_challenge = generateChallenge(verifier);
        var homeUrl = SpecificationQuerier.query(given().basePath("/")).getURI();
        String code;

        var url = SpecificationQuerier.query(
                given().basePath("/realms/{realm}/protocol/openid-connect/auth")
                        .pathParam("realm", realm)
                        .queryParam("response_type", "code")
                        .queryParam("client_id", clientId)
                        .queryParam("redirect_uri", homeUrl)
                        .queryParam("scope", "openid email")
                        .queryParam("state", "foo")
                        .queryParam("nonce", "n1")
                        .queryParam("code_challenge_method", "S256")
                        .queryParam("code_challenge", code_challenge))
                .getURI();

        try (final WebClient webClient = createWebClient()) {
            HtmlPage page = webClient.getPage(url);
            assertEquals("Sign in to test", page.getTitleText());

            HtmlForm loginForm = page.getForms().getFirst();

            loginForm.getInputByName("username").setValueAttribute(username);
            loginForm.getInputByName("password").setValueAttribute(password);

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
                .pathParam("realm", realm)
                .formParam("grant_type", "authorization_code")
                .formParam("client_id", clientId)
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

    private static String generateChallenge(String verifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(verifier.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    private WebClient createWebClient() {
        WebClient webClient = new WebClient();
        webClient.setCssErrorHandler(new SilentCssErrorHandler());
        return webClient;
    }

}
