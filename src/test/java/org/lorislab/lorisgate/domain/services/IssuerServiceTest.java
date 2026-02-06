package org.lorislab.lorisgate.domain.services;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.UriInfo;

import org.junit.jupiter.api.Test;
import org.lorislab.lorisgate.domain.model.Realm;
import org.mockito.Mockito;

import io.quarkus.test.component.QuarkusComponentTest;
import io.quarkus.test.component.TestConfigProperty;

@QuarkusComponentTest
@TestConfigProperty(key = "lorisgate.key.id", value = "12345")
public class IssuerServiceTest {

    @Inject
    IssuerService service;

    @Test
    void testIssuer() {

        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getBaseUri()).thenReturn(java.net.URI.create("http://localhost:8080/"));

        var realm = new Realm();
        service.issuer(uriInfo, realm);
    }
}
