package org.lorislab.lorisgate.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.UriInfo;

import org.lorislab.lorisgate.config.LorisGateConfig;
import org.lorislab.lorisgate.domain.model.Realm;

@ApplicationScoped
public class IssuerService {

    @Inject
    LorisGateConfig config;

    public String issuer(UriInfo uriInfo, Realm realm) {
        var host = config.hostname().orElseGet(() -> uriInfo.getBaseUri().toString());
        if (realm.getFrontendUrl() != null) {
            host = realm.getFrontendUrl();
        }
        return host + "realms/" + realm.getName();
    }
}
