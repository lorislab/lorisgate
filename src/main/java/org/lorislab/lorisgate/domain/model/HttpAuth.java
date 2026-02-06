package org.lorislab.lorisgate.domain.model;

public interface HttpAuth {

    String BEARER = "Bearer";

    String BEARER_PREFIX = BEARER + " ";

    int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    static String bearerValue(String token) {
        return BEARER_PREFIX + token;
    }
}
