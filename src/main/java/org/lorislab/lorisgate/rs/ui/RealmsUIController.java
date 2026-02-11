package org.lorislab.lorisgate.rs.ui;

import java.util.Collection;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import org.lorislab.lorisgate.domain.model.Realm;
import org.lorislab.lorisgate.domain.services.RealmService;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.runtime.annotations.RegisterForReflection;

@Path("/admin/ui/realms")
public class RealmsUIController {

    @Inject
    Template realms;

    @Inject
    Template realm;

    @Inject
    RealmService realmService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        var tmp = realmService.realms();
        return realms.data("container", new ContainerRealms(tmp));
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get(@PathParam("name") String name) {
        var tmp = realmService.getRealm(name);
        return realm.data("container", new ContainerRealm(tmp));
    }

    @RegisterForReflection
    record ContainerRealms(Collection<Realm> realms) {
    }

    @RegisterForReflection
    record ContainerRealm(Realm realm) {
    }
}
