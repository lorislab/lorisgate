package org.lorislab.lorisgate.domain.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import org.lorislab.lorisgate.config.LorisGateConfig;
import org.lorislab.lorisgate.domain.model.Client;
import org.lorislab.lorisgate.domain.model.Realm;
import org.lorislab.lorisgate.domain.model.Role;
import org.lorislab.lorisgate.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.lorislab.lorisgate.rs.admin.v1.model.ClientDTO;
import gen.org.lorislab.lorisgate.rs.admin.v1.model.RealmDTO;
import gen.org.lorislab.lorisgate.rs.admin.v1.model.RoleDTO;
import gen.org.lorislab.lorisgate.rs.admin.v1.model.UserDTO;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class StoreService {

    private static final Logger log = LoggerFactory.getLogger(StoreService.class.getName());

    @Inject
    RealmService realmService;

    @Inject
    LorisGateConfig config;

    @Inject
    ObjectMapper objectMapper;

    void onStart(@Observes StartupEvent ev) {
        var dir = config.store().directory();
        if (dir.isEmpty()) {
            log.info("Store directory is empty!");
            return;
        }

        try (Stream<Path> paths = Files.list(Paths.get(dir.get()))) {
            var list = paths.filter(Files::isRegularFile).toList();
            for (Path path : list) {
                try {
                    log.info("Loading realm file '{}'", path);
                    var data = loadData(path);
                    if (data != null) {
                        var realm = objectMapper.readValue(data, RealmDTO.class);
                        if (realm != null) {
                            realmService.addRealm(map(realm));
                            log.info("Realm '{}' added to store.", realm.getName());
                        }
                    }
                } catch (Exception e) {
                    log.info("Error loading realm file '{}'.", path, e);
                }
            }
        } catch (IOException e) {
            log.info("Error searching store directory '{}'.", dir, e);
        }

    }

    private Realm map(RealmDTO dto) {
        var realm = new Realm();
        realm.setName(dto.getName());
        realm.setDisplayName(dto.getDisplayName());
        realm.setEnabled(dto.getEnabled() == null || dto.getEnabled());
        if (dto.getRoles() != null) {
            dto.getRoles().entrySet().forEach(entry -> realm.addRole(mapRole(entry)));
        }
        if (dto.getClients() != null) {
            dto.getClients().entrySet().forEach(entry -> realm.addClient(mapClient(entry)));
        }
        if (dto.getUsers() != null) {
            dto.getUsers().entrySet().forEach(entry -> realm.addUser(mapUser(entry)));
        }
        return realm;
    }

    private Role mapRole(Map.Entry<String, RoleDTO> dto) {
        var result = new Role();
        result.setName(dto.getKey());
        var role = dto.getValue();
        result.setDescription(role.getDescription());
        result.setEnabled(role.getEnabled());
        return result;
    }

    private Client mapClient(Map.Entry<String, ClientDTO> dto) {
        var result = new Client();
        result.setClientId(dto.getKey());

        var client = dto.getValue();
        result.setClientSecret(client.getClientSecret());
        result.setConfidential(client.getConfidential() != null && client.getConfidential());
        result.setRoles(client.getRoles());
        result.setScopes(client.getScopes());
        result.setRedirectUris(client.getRedirectUris());
        return result;
    }

    private User mapUser(Map.Entry<String, UserDTO> dto) {
        var result = new User();
        result.setUsername(dto.getKey());

        var user = dto.getValue();
        result.setPassword(user.getPassword());
        result.setName(user.getName());
        result.setGivenName(user.getGivenName());
        result.setFamilyName(user.getFamilyName());
        result.setRoles(user.getRoles());
        result.setEmail(user.getEmail());
        result.setEnabled(user.getEnabled());
        result.setId(user.getId());
        return result;

    }

    private static byte[] loadData(Path path) throws RuntimeException {
        if (!Files.exists(path)) {
            return null;
        }
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Error loading data from " + path, e);
        }
    }
}
