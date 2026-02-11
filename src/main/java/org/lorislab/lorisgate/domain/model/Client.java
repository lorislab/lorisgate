package org.lorislab.lorisgate.domain.model;

import java.util.HashSet;
import java.util.Set;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Client {

    private String clientId;
    private String clientSecret;
    private boolean confidential = true;
    private Set<String> scopes = new HashSet<>();
    private Set<String> roles = new HashSet<>();
    private Set<String> redirectUris = new HashSet<>();

    public Set<String> getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(Set<String> redirectUris) {
        this.redirectUris = redirectUris;
    }

    public boolean isConfidential() {
        return confidential;
    }

    public void setConfidential(boolean confidential) {
        this.confidential = confidential;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

}
