package org.lorislab.lorisgate.domain.model;

import java.util.*;

public interface Scopes {

    String OPENID = "openid";
    String PROFILE = "profile";
    String EMAIL = "email";
    String OFFLINE_ACCESS = "offline_access";

    static List<String> supportedScopes() {
        return List.of(OPENID, PROFILE, EMAIL, OFFLINE_ACCESS);
    }

    static Set<String> toScopes(String s) {
        if (s == null || s.isBlank()) {
            return Collections.emptySet();
        }
        return new HashSet<>(Arrays.asList(s.split(" ")));
    }

    static String fromScopes(Set<String> scopes) {
        return String.join(" ", scopes);
    }
}
