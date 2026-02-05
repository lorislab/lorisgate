package org.lorislab.lorisgate.domain.model;

import org.eclipse.microprofile.jwt.Claims;
import org.jose4j.jwt.ReservedClaimNames;

public interface ClaimNames {

    String ISS = Claims.iss.name();

    String AZP = Claims.azp.name();

    String JTI = ReservedClaimNames.JWT_ID;

    String SCOPE = "scope";

    String NONCE = Claims.nonce.name();

    String CLIENT_ID = "client_id";

    String PREFERRED_USERNAME = Claims.preferred_username.name();

    String EMAIL = Claims.email.name();

    String FAMILY_NAME = Claims.family_name.name();

    String GIVEN_NAME = Claims.given_name.name();

    String NAME = "name";

    String EMAIL_VERIFIED = "email_verified";

    String REALM_ROLES = "realm_roles";

    String ROLES = "roles";

    String TYP = "typ";

}
