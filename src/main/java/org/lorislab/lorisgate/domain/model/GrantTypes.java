package org.lorislab.lorisgate.domain.model;

import java.util.List;

public interface GrantTypes {

    String AUTHORIZATION_CODE = "authorization_code";
    String REFRESH_TOKEN = "refresh_token";
    String PASSWORD = "password";
    String IMPLICIT = "implicit";
    String CLIENT_CREDENTIALS = "client_credentials";

    static List<String> supportedGrantTypes() {
        return List.of(AUTHORIZATION_CODE, REFRESH_TOKEN, IMPLICIT, PASSWORD, CLIENT_CREDENTIALS);
    }
}
