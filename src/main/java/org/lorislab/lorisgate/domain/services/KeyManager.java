package org.lorislab.lorisgate.domain.services;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

import io.quarkus.runtime.Startup;

@Startup
@Singleton
public class KeyManager {

    private KeyPair keyPair;
    private String kid;

    @PostConstruct
    void init() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            keyPair = kpg.generateKeyPair();
            kid = UUID.randomUUID().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public RSAPrivateKey getPrivateKey() {
        return (RSAPrivateKey) keyPair.getPrivate();
    }

    public RSAPublicKey getPublicKey() {
        return (RSAPublicKey) keyPair.getPublic();
    }

    public String getKid() {
        return kid;
    }

}
