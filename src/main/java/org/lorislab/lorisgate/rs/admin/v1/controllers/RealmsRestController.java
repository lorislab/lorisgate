package org.lorislab.lorisgate.rs.admin.v1.controllers;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;
import org.lorislab.lorisgate.domain.services.RealmService;
import org.lorislab.lorisgate.rs.admin.v1.mappers.RealmMapper;

import gen.org.lorislab.lorisgate.rs.admin.v1.RealmsApi;
import gen.org.lorislab.lorisgate.rs.admin.v1.model.RealmDTO;
import gen.org.lorislab.lorisgate.rs.admin.v1.model.RealmItemDTO;

@ApplicationScoped
public class RealmsRestController implements RealmsApi {

    @Inject
    RealmService realmService;

    @Inject
    RealmMapper mapper;

    @Override
    public RestResponse<Void> createRealm(RealmDTO realmDTO) {
        if (realmService.getRealm(realmDTO.getName()) != null) {
            return RestResponse.status(RestResponse.Status.CONFLICT);
        }
        realmService.addRealm(mapper.create(realmDTO));
        return RestResponse.status(RestResponse.Status.CREATED);
    }

    @Override
    public RestResponse<Void> deleteRealm(String realm) {
        if (realmService.deleteRealm(realm)) {
            return RestResponse.ok();
        }
        return RestResponse.notFound();
    }

    @Override
    public RestResponse<RealmDTO> getRealm(String realm) {
        var item = realmService.getRealm(realm);
        if (item == null) {
            return RestResponse.notFound();
        }
        return RestResponse.ok(mapper.map(item));
    }

    @Override
    public RestResponse<List<RealmItemDTO>> getRealms() {
        return RestResponse.ok(mapper.mapItems(realmService.realms()));
    }

    @Override
    public RestResponse<Void> updateRealm(String realm, RealmDTO realmDTO) {
        if (realmService.getRealm(realmDTO.getName()) == null) {
            return RestResponse.notFound();
        }
        realmService.addRealm(mapper.create(realmDTO));
        return RestResponse.ok();
    }
}
