package org.lorislab.lorisgate.domain.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.lorislab.lorisgate.rs.admin.v1.model.RealmDTO;

@ApplicationScoped
public class StoreService {

    private static final Logger log = LoggerFactory.getLogger(StoreService.class.getName());

    @Inject
    RealmService realmService;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    StoreServiceMapper storeServiceMapper;

    public void importFiles(String dir) throws IOException {
        try (Stream<Path> paths = Files.list(Paths.get(dir))) {
            var list = paths.filter(Files::isRegularFile).toList();
            for (Path path : list) {
                try {
                    log.info("Loading realm file '{}'", path);
                    var data = Files.readAllBytes(path);
                    var realm = objectMapper.readValue(data, RealmDTO.class);
                    if (realm.getName() != null) {
                        realmService.addRealm(storeServiceMapper.create(realm));
                        log.info("Realm '{}' added to store.", realm.getName());
                    }
                } catch (Exception e) {
                    log.error("Error loading realm file '{}'. Error: {}", path, e.getMessage());
                }
            }
        }
    }
}
