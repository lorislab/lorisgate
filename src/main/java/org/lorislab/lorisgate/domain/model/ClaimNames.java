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

    String PREFERRED_USERNAME = "preferred_username";

    String EMAIL = "email";

    String FAMILY_NAME = "family_name";

    String GIVEN_NAME = "given_name";

    String NAME = "name";

    String EMAIL_VERIFIED = "email_verified";

    String REALM_ROLES = "realm_roles";

    String ROLES = "roles";

    String TYP = "typ";

}
