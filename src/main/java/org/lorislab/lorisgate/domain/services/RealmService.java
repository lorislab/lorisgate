package org.lorislab.lorisgate.domain.services;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.inject.Singleton;

import org.lorislab.lorisgate.domain.model.Realm;

@Singleton
public class RealmService {

    private static final Map<String, Realm> REALMS = new ConcurrentHashMap<>();

    public void addRealm(Realm realm) {
        REALMS.put(realm.getName(), realm);
    }

    public Realm getRealm(String realm) {
        return REALMS.get(realm);
    }

    public Collection<Realm> realms() {
        return REALMS.values();
    }
}
