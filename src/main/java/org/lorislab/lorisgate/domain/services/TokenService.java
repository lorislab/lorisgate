package org.lorislab.lorisgate.domain.services;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jose4j.jwt.JwtClaims;
import org.lorislab.lorisgate.config.LorisGateConfig;
import org.lorislab.lorisgate.domain.model.*;
import org.lorislab.lorisgate.domain.utils.JwtHelper;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;

@ApplicationScoped
public class TokenService {

    @Inject
    LorisGateConfig config;

    @Inject
    KeyManager keyManager;

    public JwtClaims parse(String issuer, String token) throws TokenValidationException {
        try {
            return JwtHelper.parse(issuer, token, keyManager.getPublicKey(), config.oidc().tokenSkew());
        } catch (Exception e) {
            throw new TokenValidationException(e);
        }
    }

    public RefreshToken parseRefreshToken(String issuer, String token) throws TokenValidationException {
        var tmp = parse(issuer, token);
        return new RefreshToken(tmp);
    }

    public String createIdToken(String issuer, User user, Client client, String nonce) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(config.oidc().tokenLifetime());

        JwtClaimsBuilder id = Jwt.claims();
        id.issuer(issuer)
                .subject(user.getId())
                .issuedAt(now.getEpochSecond())
                .expiresAt(exp.getEpochSecond())
                .audience(client.getClientId())
                .claim(ClaimNames.TYP, TokenTypes.ID)
                .claim(ClaimNames.PREFERRED_USERNAME, user.getUsername());
        if (nonce != null) {
            id.claim(ClaimNames.NONCE, nonce);
        }
        return JwtHelper.sign(id, keyManager.getKid(), keyManager.getPrivateKey());
    }

    public String createAccessToken(String issuer, User user, Client client, Set<String> scopes) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(config.oidc().tokenLifetime());

        JwtClaimsBuilder access = Jwt.claims();
        access.issuer(issuer)
                .issuedAt(now.getEpochSecond())
                .expiresAt(exp.getEpochSecond())
                .claim(ClaimNames.JTI, UUID.randomUUID().toString())
                .audience(client.getClientId())
                .claim(ClaimNames.TYP, TokenTypes.ACCESS);

        if (user != null) {
            access.groups(user.getGroups());
            access.subject(user.getId())
                    .claim(ClaimNames.PREFERRED_USERNAME, user.getUsername())
                    .claim(ClaimNames.EMAIL, user.getEmail())
                    .claim(ClaimNames.FAMILY_NAME, user.getFamilyName())
                    .claim(ClaimNames.GIVEN_NAME, user.getGivenName())
                    .claim(ClaimNames.NAME, user.getName())
                    .claim(ClaimNames.EMAIL_VERIFIED, user.isEmailVerified())
                    .claim(ClaimNames.REALM_ROLES, Map.of(ClaimNames.ROLES, user.getRoles()));
        } else {
            access.subject("client:" + client.getClientId()).claim(ClaimNames.CLIENT_ID, client.getClientId());
        }

        if (!scopes.isEmpty()) {
            access.scope(scopes);
        }

        return JwtHelper.sign(access, keyManager.getKid(), keyManager.getPrivateKey());
    }

    public String createRefreshToken(String issuer, String clientId, String username, Set<String> scopes) {
        var result = Jwt.claims()
                .subject(UUID.randomUUID().toString())
                .claim(ClaimNames.JTI, UUID.randomUUID().toString())
                .issuedAt(Instant.now().getEpochSecond())
                .expiresAt(Instant.now().plusSeconds(config.oidc().refreshLifetime()).getEpochSecond())
                .issuer(issuer)
                .audience(issuer)
                .preferredUserName(username)
                .claim(ClaimNames.TYP, TokenTypes.REFRESH)
                .claim(ClaimNames.AZP, clientId);

        if (!scopes.isEmpty()) {
            result.claim(ClaimNames.SCOPE, scopes);
        }

        return JwtHelper.sign(result, keyManager.getKid(), keyManager.getPrivateKey());
    }

    public String rotateRefreshToken(RefreshToken token) {
        return createRefreshToken(token.getIssuer(), token.getClientId(), token.getUsername(), token.getScopes());
    }

    public static class TokenValidationException extends RuntimeException {
        public TokenValidationException(Throwable t) {
            super(t);
        }
    }

}
