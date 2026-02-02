package org.lorislab.lorisgate.rs.admin.v1.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import gen.org.lorislab.lorisgate.rs.admin.v1.model.UserDTO;
import gen.org.lorislab.lorisgate.rs.admin.v1.model.UserSearchResultDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(UsersRestController.class)
class UsersRestControllerTest extends AbstractAdminTest {

    @Test
    void getUsersTest() throws Exception {

        var realm = createRealm("testUserSearch");

        var req = new UserDTO().username("testUser1").name("testUser1").emailVerified(true).familyName("familyName1");

        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .body(req)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.CREATED);

        req = new UserDTO().username("testUser2").name("testUser2").emailVerified(true).familyName("familyName2");

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
                .extract().as(UserSearchResultDTO.class);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(2);
    }

    @Test
    void createUserTest() {
        var realm = createRealm("testUserCreate");

        var req = new UserDTO().username("testUser1")
                .name("testUser1")
                .emailVerified(true)
                .familyName("familyName1");

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
                .get(req.getUsername())
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
                .get(req.getUsername())
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(UserDTO.class);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(req.getName());
        assertThat(result.getUsername()).isEqualTo(req.getUsername());
    }

    @Test
    void updateUserTest() {
        var realm = createRealm("testUserUpdate");

        var req = new UserDTO().username("testUser1").name("testUser1").emailVerified(true).familyName("familyName1");

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
                .put(req.getUsername())
                .then()
                .statusCode(RestResponse.StatusCode.NOT_FOUND);

        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .body(req)
                .put("not-existing-user")
                .then()
                .statusCode(RestResponse.StatusCode.NOT_FOUND);

        req.name("UPDATE1").familyName("updated");

        given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .body(req)
                .put(req.getUsername())
                .then()
                .statusCode(RestResponse.StatusCode.OK);

        var result = given()
                .when().contentType(ContentType.JSON)
                .pathParam("realm", realm.getName())
                .get(req.getUsername())
                .then()
                .statusCode(RestResponse.StatusCode.OK)
                .extract().as(UserDTO.class);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(req.getName());
        assertThat(result.getFamilyName()).isEqualTo(req.getFamilyName());

    }

    @Test
    void deleteUserTest() {
        var realm = createRealm("testUserDelete");

        var req = new UserDTO().username("testUserDelete1")
                .name("testUser1")
                .emailVerified(true)
                .familyName("familyName1");

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
                .delete(req.getUsername())
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
                .delete(req.getUsername())
                .then()
                .statusCode(RestResponse.StatusCode.OK);
    }
}
