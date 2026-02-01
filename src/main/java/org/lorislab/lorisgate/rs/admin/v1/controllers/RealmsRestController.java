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
}
