package org.lorislab.lorisgate.domain.services;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.lorislab.lorisgate.config.LorisGateConfig;
import org.lorislab.lorisgate.domain.utils.JwtHelper;

import io.quarkus.runtime.Startup;

@Startup
@Singleton
@SuppressWarnings("java:S6813")
public class KeyManager {

    @Inject
    LorisGateConfig config;

    private KeyPair keyPair;
    private String kid;

    @PostConstruct
    void init() {
        var key = config.key();
        kid = key.id();

        var pr = key.privateKeyFile();
        var pu = key.publicKeyFile();
        if (pr.isPresent() && pu.isPresent()) {
            keyPair = loadKeyPair(pr.get(), pu.get());
        } else {
            keyPair = generateKeyPair(JwtHelper.ALGORITHM);
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

    public KeyPair loadKeyPair(String privateKeyFile, String publicKeyFile) {
        try {
            return JwtHelper.fromFiles(privateKeyFile, publicKeyFile);
        } catch (Exception ex) {
            throw new Error("Load private and public key from file. Error: " + ex.getMessage(), ex);
        }
    }

    public KeyPair generateKeyPair(String algorithm) {
        try {
            return JwtHelper.generateKeyPair(algorithm);
        } catch (Exception ex) {
            throw new Error("Generate key pair error", ex);
        }
    }

    static class Error extends RuntimeException {

        Error(String message, Exception e) {
            super(message, e);
        }
    }
}
