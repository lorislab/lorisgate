package org.lorislab.lorisgate.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.UriInfo;

@ApplicationScoped
public class IssuerService {

    public String issuer(UriInfo uriInfo, String realm) {
        return "http://localhost:8080/realms/" + realm;

        //        return uriInfo.getBaseUri() + "realms/" + realm;
    }
}
