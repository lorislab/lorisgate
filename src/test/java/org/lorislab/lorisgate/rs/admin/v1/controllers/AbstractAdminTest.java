package org.lorislab.lorisgate.rs.admin.v1.controllers;

import static io.restassured.RestAssured.given;

import java.util.UUID;

import org.jboss.resteasy.reactive.RestResponse;
import org.lorislab.lorisgate.AbstractTest;

import gen.org.lorislab.lorisgate.rs.admin.v1.model.RealmDTO;
import io.restassured.http.ContentType;

public class AbstractAdminTest extends AbstractTest {

    protected RealmDTO createRealm(String name) {
        return createRealm(new RealmDTO().name(name).displayName(UUID.randomUUID().toString()).enabled(true));
    }

    protected RealmDTO createRealm(RealmDTO req) {
        given().basePath("/admin/realms")
                .when().contentType(ContentType.JSON)
                .body(req)
                .post()
                .then()
                .statusCode(RestResponse.StatusCode.CREATED);
        return req;
    }
}
