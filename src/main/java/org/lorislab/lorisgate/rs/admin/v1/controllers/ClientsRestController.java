package org.lorislab.lorisgate.rs.admin.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;
import org.lorislab.lorisgate.domain.services.RealmService;
import org.lorislab.lorisgate.rs.admin.v1.mappers.RealmMapper;

import gen.org.lorislab.lorisgate.rs.admin.v1.ClientsApi;
import gen.org.lorislab.lorisgate.rs.admin.v1.model.ClientDTO;
import gen.org.lorislab.lorisgate.rs.admin.v1.model.ClientSearchResultDTO;

@ApplicationScoped
public class ClientsRestController implements ClientsApi {

    @Inject
    RealmService realmService;

    @Inject
    RealmMapper mapper;

    @Override
    public RestResponse<Void> createClient(String realm, ClientDTO clientDTO) {
        var r = realmService.getRealm(realm);
        if (r == null) {
            return RestResponse.notFound();
        }
        var c = r.getClient(clientDTO.getClientId());
        if (c != null) {
            return RestResponse.status(RestResponse.Status.CONFLICT);
        }
        r.addClient(mapper.create(clientDTO.getClientId(), clientDTO));
        return RestResponse.status(RestResponse.Status.CREATED);
    }

    @Override
    public RestResponse<Void> deleteClient(String realm, String clientId) {
        var r = realmService.getRealm(realm);
        if (r == null) {
            return RestResponse.notFound();
        }
        if (r.deleteClient(clientId)) {
            return RestResponse.ok();
        }
        return RestResponse.notFound();
    }

    @Override
    public RestResponse<ClientDTO> getClient(String realm, String clientId) {
        var r = realmService.getRealm(realm);
        if (r == null) {
            return RestResponse.notFound();
        }
        var c = r.getClient(clientId);
        if (c == null) {
            return RestResponse.notFound();
        }
        return RestResponse.ok(mapper.map(c));
    }

    @Override
    public RestResponse<ClientSearchResultDTO> getClients(String realm) {
        var r = realmService.getRealm(realm);
        if (r == null) {
            return RestResponse.notFound();
        }
        return RestResponse.ok(mapper.mapResultClients(r.getClients().values()));
    }

    @Override
    public RestResponse<Void> updateClient(String realm, String clientId, ClientDTO clientDTO) {
        var r = realmService.getRealm(realm);
        if (r == null) {
            return RestResponse.notFound();
        }
        var u = r.getClient(clientId);
        if (u == null) {
            return RestResponse.notFound();
        }
        r.addClient(mapper.create(clientId, clientDTO));
        return RestResponse.status(RestResponse.Status.OK);
    }
}
