package org.lorislab.lorisgate.rs.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.htmlunit.SilentCssErrorHandler;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlTable;
import org.junit.jupiter.api.Test;
import org.lorislab.lorisgate.AbstractTest;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.specification.SpecificationQuerier;

@QuarkusTest
@TestHTTPEndpoint(RealmsUIController.class)
class RealmsUIControllerTest extends AbstractTest {

    @Test
    void testGetRealms() throws IOException {

        var homeUrl = SpecificationQuerier.query(given()).getURI();
        try (final WebClient webClient = createWebClient()) {
            HtmlPage page = webClient.getPage(homeUrl);
            assertEquals("Lorisgate - Realms", page.getTitleText());
        }
    }

    @Test
    void testGetRealmTest() throws IOException {

        var homeUrl = SpecificationQuerier.query(given()).getURI() + "/test";
        try (final WebClient webClient = createWebClient()) {
            HtmlPage page = webClient.getPage(homeUrl);
            assertEquals("Lorisgate - Realm: test", page.getTitleText());

            HtmlTable table = page.getHtmlElementById("table-general-info");
            assertThat(table).isNotNull();
            assertThat(table.getRows()).hasSize(4);

            table = page.getHtmlElementById("table-roles");
            assertThat(table).isNotNull();
            assertThat(table.getRows()).hasSize(4);

            table = page.getHtmlElementById("table-clients");
            assertThat(table).isNotNull();
            assertThat(table.getRows()).hasSize(7);

            table = page.getHtmlElementById("table-users");
            assertThat(table).isNotNull();
            assertThat(table.getRows()).hasSize(5);
        }
    }

    private WebClient createWebClient() {
        WebClient webClient = new WebClient();
        webClient.setCssErrorHandler(new SilentCssErrorHandler());
        return webClient;
    }
}
