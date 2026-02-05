package org.lorislab.lorisgate.rs.admin.v1.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import gen.org.lorislab.lorisgate.rs.admin.v1.model.ClientDTO;
import gen.org.lorislab.lorisgate.rs.admin.v1.model.ClientSearchResultDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(ClientsRestController.class)
class ClientsRestControllerTest extends AbstractAdminTest {

    @Test
    void getClientsTest() {

        var realm = createRealm("testClientsSearch");

        var req = new ClientDTO().clientId("testClient1").clientSecret("secret1");

        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .body(req)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.CREATED);

        req = new ClientDTO().clientId("testClient2").clientSecret("secret2");

        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .body(req)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.CREATED);

        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", "not-existing-realm")
                .get()
                .then()
                .statusCode(RestResponse.StatusCode.NOT_FOUND);

        var result = given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .get()
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(ClientSearchResultDTO.class);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(2);
    }

    @Test
    void createClientTest() {
        var realm = createRealm("testClientCreate");

        var req = new ClientDTO().clientId("testClient1")
                .clientSecret("secret-1");

        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .body(req)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.CREATED);

        // create user realm not found
        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", "not-found")
                .body(req)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.NOT_FOUND);

        // conflict
        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .body(req)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.CONFLICT);

        // get user realm not found
        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", "not-found")
                .get(req.getClientId())
                .then()
                .statusCode(RestResponse.StatusCode.NOT_FOUND);

        // get user not found
        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .get("not-found")
                .then()
                .statusCode(RestResponse.StatusCode.NOT_FOUND);

        var result = given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .get(req.getClientId())
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(ClientDTO.class);

        assertThat(result).isNotNull();
        assertThat(result.getClientId()).isEqualTo(req.getClientId());
        assertThat(result.getClientSecret()).isEqualTo(req.getClientSecret());
    }

    @Test
    void updateClientTest() {
        var realm = createRealm("testClientUpdate");

        var req = new ClientDTO().clientId("testClient1").clientSecret("testUser1");

        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .body(req)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.CREATED);

        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", "not-existing-realm")
                .body(req)
                .put(req.getClientId())
                .then()
                .statusCode(RestResponse.StatusCode.NOT_FOUND);

        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .body(req)
                .put("not-existing-user")
                .then()
                .statusCode(RestResponse.StatusCode.NOT_FOUND);

        req.setClientSecret("UPDATE1");

        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .body(req)
                .put(req.getClientId())
                .then()
                .statusCode(RestResponse.StatusCode.OK);

        var result = given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .get(req.getClientId())
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(ClientDTO.class);

        assertThat(result).isNotNull();
        assertThat(result.getClientSecret()).isEqualTo(req.getClientSecret());
    }

    @Test
    void deleteClientTest() {
        var realm = createRealm("testClientDelete");

        var req = new ClientDTO().clientId("testClientDelete1")
                .clientSecret("testUser1");

        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .body(req)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.CREATED);

        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", "not-found")
                .delete(req.getClientId())
                .then()
                .statusCode(RestResponse.StatusCode.NOT_FOUND);

        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .delete("not-found")
                .then()
                .statusCode(RestResponse.StatusCode.NOT_FOUND);

        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .delete(req.getClientId())
                .then()
                .statusCode(RestResponse.StatusCode.OK);
    }
}
