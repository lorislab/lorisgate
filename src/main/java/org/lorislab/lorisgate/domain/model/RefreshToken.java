package org.lorislab.lorisgate.domain.model;

import java.time.Instant;
import java.util.Set;

import org.jose4j.jwt.JwtClaims;

public class RefreshToken {

    private final JwtClaims claims;

    public RefreshToken(JwtClaims claims) {
        this.claims = claims;
    }

    public String getIssuer() {
        return claims.getClaimValueAsString(ClaimNames.ISS);
    }

    public String getClientId() {
        return claims.getClaimValueAsString(ClaimNames.AZP);
    }

    public Set<String> getScopes() {
        return Scopes.toScopes(claims.getClaimValueAsString(ClaimNames.SCOPE));
    }

    public String getUsername() {
        return claims.getClaimValueAsString(ClaimNames.PREFERRED_USERNAME);
    }

    public boolean isExpired() {
        try {
            var tmp = claims.getExpirationTime();
            return Instant.now().isAfter(Instant.ofEpochSecond(tmp.getValue()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
