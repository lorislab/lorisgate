package org.lorislab.lorisgate.rs.admin.v1.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import gen.org.lorislab.lorisgate.rs.admin.v1.model.RealmDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(RealmsRestController.class)
public class RealmsRestControllerTest extends AbstractAdminTest {

    @Test
    public void createRealmTest() {

        var req = createRealm("testCreate");

        given()
                .when().contentType(ContentType.JSON)
                .get("not-found")
                .then()
                .statusCode(RestResponse.StatusCode.NOT_FOUND);

        var result = given()
                .when().contentType(ContentType.JSON)
                .get(req.getName())
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(RealmDTO.class);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(req.getName());
        assertThat(result.getDisplayName()).isEqualTo(req.getDisplayName());

        given()
                .when().contentType(ContentType.JSON)
                .body(req)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.CONFLICT);
    }

    @Test
    public void deleteRealmTest() {

        var req = createRealm("testDelete");

        given()
                .when().contentType(ContentType.JSON)
                .delete("not-found")
                .then()
                .statusCode(RestResponse.StatusCode.NOT_FOUND);

        given()
                .when().contentType(ContentType.JSON)
                .delete(req.getName())
                .then()
                .statusCode(RestResponse.StatusCode.OK);
    }

    @Test
    public void updateRealmTest() {

        var req = createRealm("testUpdate");

        var result = given()
                .when().contentType(ContentType.JSON)
                .get(req.getName())
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(RealmDTO.class);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(req.getName());
        assertThat(result.getDisplayName()).isEqualTo(req.getDisplayName());

        given()
                .when().contentType(ContentType.JSON)
                .body(req)
                .put("not-found")
                .then()
                .statusCode(RestResponse.StatusCode.NOT_FOUND);

        req.setDisplayName("UPDATE1");

        given()
                .when().contentType(ContentType.JSON)
                .body(req)
                .put(req.getName())
                .then()
                .statusCode(RestResponse.StatusCode.OK);

        result = given()
                .when().contentType(ContentType.JSON)
                .get(req.getName())
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(RealmDTO.class);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(req.getName());
        assertThat(result.getDisplayName()).isEqualTo(req.getDisplayName());

    }
}
