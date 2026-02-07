package org.lorislab.lorisgate.domain.model;

import org.eclipse.microprofile.jwt.Claims;
import org.jose4j.jwt.ReservedClaimNames;

public final class ClaimNames {

    public static final String ISS = Claims.iss.name();

    public static final String AZP = Claims.azp.name();

    public static final String JTI = ReservedClaimNames.JWT_ID;

    public static final String SCOPE = "scope";

    public static final String NONCE = Claims.nonce.name();

    public static final String CLIENT_ID = "client_id";

    public static final String PREFERRED_USERNAME = Claims.preferred_username.name();

    public static final String EMAIL = Claims.email.name();

    public static final String FAMILY_NAME = Claims.family_name.name();

    public static final String GIVEN_NAME = Claims.given_name.name();

    public static final String NAME = "name";

    public static final String EMAIL_VERIFIED = "email_verified";

    public static final String REALM_ROLES = "realm_roles";

    public static final String ROLES = "roles";

    public static final String TYP = "typ";

    private ClaimNames() {
    }
}
