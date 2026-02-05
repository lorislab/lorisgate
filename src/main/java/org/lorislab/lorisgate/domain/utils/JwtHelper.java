package org.lorislab.lorisgate.domain.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.jwt.build.JwtClaimsBuilder;

public class JwtHelper {

    private static final Logger log = LoggerFactory.getLogger(JwtHelper.class);

    public static final String ALGORITHM = "RSA";

    private static final String PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----";
    private static final String PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----";
    private static final String PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----";

    private JwtHelper() {
    }

    public static String sign(JwtClaimsBuilder builder, String keyId, PrivateKey privateKey) {
        return builder.jws().keyId(keyId).sign(privateKey);
    }

    public static JwtClaims parse(String issuer, String token, Key publicKey, int secondsOfAllowedClockSkew) throws Exception {
        var consumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setExpectedIssuer(issuer)
                .setVerificationKey(publicKey)
                .setSkipDefaultAudienceValidation()
                .setAllowedClockSkewInSeconds(secondsOfAllowedClockSkew)
                .build();

        return consumer.processToClaims(token);
    }

    public static KeyPair generateKeyPair(String algorithm) throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm);
        kpg.initialize(2048);
        return kpg.generateKeyPair();
    }

    public static KeyPair fromFiles(String privateKeyFile, String publicKeyFile)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        RSAPrivateKey privateKey = loadPrivateKeyFromPem(privateKeyFile);
        RSAPublicKey publicKey = loadPublicKeyFromPem(publicKeyFile);
        return new KeyPair(publicKey, privateKey);
    }

    public static RSAPrivateKey loadPrivateKeyFromPem(String filePath)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        byte[] keyBytes = getKey(PRIVATE_KEY_HEADER, PRIVATE_KEY_FOOTER, filePath);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return (RSAPrivateKey) keyFactory().generatePrivate(spec);
    }

    public static RSAPublicKey loadPublicKeyFromPem(String filePath)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        byte[] keyBytes = getKey(PUBLIC_KEY_HEADER, PUBLIC_KEY_FOOTER, filePath);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return (RSAPublicKey) keyFactory().generatePublic(spec);
    }

    private static KeyFactory keyFactory() throws NoSuchAlgorithmException {
        return KeyFactory.getInstance(ALGORITHM);
    }

    private static byte[] getKey(String header, String footer, String filePath) throws IOException {
        String pem = Files.readString(java.nio.file.Path.of(filePath));
        int start = pem.indexOf(header);
        int end = pem.indexOf(footer);
        if (start < 0 || end < 0) {
            if (log.isErrorEnabled()) {
                log.error("Invalid {} key PEM format in file: {}", header.split(" ")[1], filePath);
            }
            throw new RuntimeException("Invalid key PEM format");
        }
        String base64 = pem.substring(start + header.length(), end).replaceAll("\\s", "");
        return Base64.getDecoder().decode(base64);
    }

    public static String generateChallenge(String verifier) {
        return generateChallenge("SHA-256", verifier);
    }

    public static String generateChallenge(String algorithm, String verifier) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] digest = md.digest(verifier.getBytes(StandardCharsets.US_ASCII));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No SHA-256 algorithm found.", e);
        }
    }
}
