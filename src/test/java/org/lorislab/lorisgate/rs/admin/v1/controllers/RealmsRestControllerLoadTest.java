package org.lorislab.lorisgate.rs.admin.v1.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import gen.org.lorislab.lorisgate.rs.admin.v1.model.RealmSearchResultDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(RealmsRestController.class)
public class RealmsRestControllerLoadTest {

    @Test
    public void getAllRealmsTest() {

        var result = given()
                .when().contentType(ContentType.JSON)
                .get()
                .then()
                .extract().as(RealmSearchResultDTO.class);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(2);
    }
}
