package org.lorislab.lorisgate.domain.model;

import java.time.Instant;
import java.util.*;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Realm {

    private String name;

    private String displayName;

    private boolean enabled;

    private String frontendUrl;

    private Map<String, Role> roles = new HashMap<>();

    private Map<String, Client> clients = new HashMap<>();

    private Map<String, User> users = new HashMap<>();

    private final Map<String, AuthorizationCode> codes = new HashMap<>();

    public void addRole(Role role) {
        roles.put(role.getName(), role);
    }

    public Role getRoles(String name) {
        return roles.get(name);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void saveAuthCode(AuthorizationCode code) {
        codes.put(code.getCode(), code);
    }

    public Optional<AuthorizationCode> getAuthCode(String code) {
        AuthorizationCode ac = codes.get(code);
        if (ac == null)
            return Optional.empty();
        if (ac.getExpiresAt().isBefore(Instant.now())) {
            codes.remove(code);
            return Optional.empty();
        }
        return Optional.of(ac);
    }

    public void consumeAuthCode(String code) {
        codes.remove(code);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public boolean hasClient(String clientId) {
        return clients.containsKey(clientId);
    }

    public Client getClient(String id) {
        return clients.get(id);
    }

    public void addClient(Client client) {
        clients.put(client.getClientId(), client);
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public Map<String, Role> getRoles() {
        return roles;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public boolean deleteUser(String username) {
        return users.remove(username) != null;
    }

    public Map<String, Client> getClients() {
        return clients;
    }

    public boolean deleteClient(String clientId) {
        return clients.remove(clientId) != null;
    }

    public String getFrontendUrl() {
        return frontendUrl;
    }

    public void setFrontendUrl(String frontendUrl) {
        this.frontendUrl = frontendUrl;
    }
}
