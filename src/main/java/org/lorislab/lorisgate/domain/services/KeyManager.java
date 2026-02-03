package org.lorislab.lorisgate.domain.services;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.lorislab.lorisgate.config.LorisGateConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.Startup;

@Startup
@Singleton
public class KeyManager {

    private static final Logger log = LoggerFactory.getLogger(KeyManager.class);

    private static final String ALGORITHM = "RSA";

    private static final String PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----";
    private static final String PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----";
    private static final String PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----";

    @Inject
    LorisGateConfig config;

    private KeyPair keyPair;
    private String kid;

    @PostConstruct
    void init() {
        var key = config.key();
        kid = key.id();
        if (key.privateKeyFile().isPresent() && key.publicKeyFile().isPresent()) {
            keyPair = loadKeyPair(key.privateKeyFile().get(), key.publicKeyFile().get());
        } else {
            keyPair = generateKeyPair();
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
        return fromFiles(privateKeyFile, publicKeyFile);
    }

    public KeyPair generateKeyPair() {
        return generateKeyPair(ALGORITHM);
    }

    public KeyPair generateKeyPair(String algorithm) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm);
            kpg.initialize(2048);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new KeyManagerException("Generate key pair error", e);
        }
    }

    public static KeyPair fromFiles(String privateKeyFile, String publicKeyFile) {
        try {
            RSAPrivateKey privateKey = loadPrivateKeyFromPem(privateKeyFile);
            RSAPublicKey publicKey = loadPublicKeyFromPem(publicKeyFile);
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new KeyManagerException("Error loading key pair from PEM", e);
        }
    }

    public static RSAPrivateKey loadPrivateKeyFromPem(String filePath) throws Exception {
        byte[] keyBytes = getKey(PRIVATE_KEY_HEADER, PRIVATE_KEY_FOOTER, filePath);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return (RSAPrivateKey) keyFactory().generatePrivate(spec);
    }

    public static RSAPublicKey loadPublicKeyFromPem(String filePath) throws Exception {
        byte[] keyBytes = getKey(PUBLIC_KEY_HEADER, PUBLIC_KEY_FOOTER, filePath);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return (RSAPublicKey) keyFactory().generatePublic(spec);
    }

    private static KeyFactory keyFactory() throws NoSuchAlgorithmException {
        return KeyFactory.getInstance(ALGORITHM);
    }

    private static byte[] getKey(String header, String footer, String filePath) throws Exception {
        String pem = Files.readString(java.nio.file.Path.of(filePath));
        int start = pem.indexOf(header);
        int end = pem.indexOf(footer);
        if (start < 0 || end < 0) {
            log.error("Invalid {} key PEM format in file: {}", header.split(" ")[1], filePath);
            throw new KeyManagerException("Invalid key PEM format");
        }
        String base64 = pem.substring(start + header.length(), end).replaceAll("\\s", "");
        return Base64.getDecoder().decode(base64);
    }

    public static class KeyManagerException extends RuntimeException {
        public KeyManagerException(String message, Throwable cause) {
            super(message, cause);
        }

        public KeyManagerException(String message) {
            super(message);
        }
    }

}
