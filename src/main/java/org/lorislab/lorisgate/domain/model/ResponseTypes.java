package org.lorislab.lorisgate.domain.model;

import java.util.*;

public interface ResponseTypes {

    String NONE = "none";
    String CODE = "code";
    String ID_TOKEN = "id_token";
    String TOKEN = "token";

    static List<String> supportedResponseTypes() {
        return List.of(NONE, CODE, ID_TOKEN, TOKEN);
    }

    static Set<String> toTypes(String value) {
        if (value == null || value.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(Arrays.asList(value.split(" ")));
    }
}
