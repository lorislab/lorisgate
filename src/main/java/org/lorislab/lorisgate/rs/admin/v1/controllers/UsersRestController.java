package org.lorislab.lorisgate.rs.admin.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;
import org.lorislab.lorisgate.domain.services.RealmService;
import org.lorislab.lorisgate.rs.admin.v1.mappers.RealmMapper;

import gen.org.lorislab.lorisgate.rs.admin.v1.UsersApi;
import gen.org.lorislab.lorisgate.rs.admin.v1.model.UserDTO;
import gen.org.lorislab.lorisgate.rs.admin.v1.model.UserSearchResultDTO;

@ApplicationScoped
public class UsersRestController implements UsersApi {

    @Inject
    RealmService realmService;

    @Inject
    RealmMapper mapper;

    @Override
    public RestResponse<Void> createUser(String realm, UserDTO userDTO) {
        var r = realmService.getRealm(realm);
        if (r == null) {
            return RestResponse.notFound();
        }
        var u = r.getUser(userDTO.getUsername());
        if (u != null) {
            return RestResponse.status(RestResponse.Status.CONFLICT);
        }
        r.addUser(mapper.create(userDTO.getUsername(), userDTO));
        return RestResponse.status(RestResponse.Status.CREATED);
    }

    @Override
    public RestResponse<Void> deleteUser(String realm, String username) {
        var r = realmService.getRealm(realm);
        if (r == null) {
            return RestResponse.notFound();
        }
        if (r.deleteUser(username)) {
            return RestResponse.ok();
        }
        return RestResponse.notFound();
    }

    @Override
    public RestResponse<UserDTO> getUser(String realm, String username) {
        var r = realmService.getRealm(realm);
        if (r == null) {
            return RestResponse.notFound();
        }
        var u = r.getUser(username);
        if (u == null) {
            return RestResponse.notFound();
        }
        return RestResponse.ok(mapper.map(u));
    }

    @Override
    public RestResponse<UserSearchResultDTO> getUsers(String realm) {
        var r = realmService.getRealm(realm);
        if (r == null) {
            return RestResponse.notFound();
        }
        return RestResponse.ok(mapper.mapResultUsers(r.getUsers().values()));
    }

    @Override
    public RestResponse<Void> updateUser(String realm, String username, UserDTO userDTO) {
        var r = realmService.getRealm(realm);
        if (r == null) {
            return RestResponse.notFound();
        }
        var u = r.getUser(username);
        if (u == null) {
            return RestResponse.notFound();
        }
        r.addUser(mapper.create(username, userDTO));
        return RestResponse.ok();
    }
}
