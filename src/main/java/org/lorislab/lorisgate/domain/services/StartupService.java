package org.lorislab.lorisgate.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import org.lorislab.lorisgate.config.LorisGateConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class StartupService {

    private static final Logger log = LoggerFactory.getLogger(StartupService.class.getName());

    @Inject
    LorisGateConfig config;

    @Inject
    StoreService storeService;

    void onStart(@Observes StartupEvent ev) {
        var dir = config.store().directory();
        if (dir.isEmpty()) {
            log.info("Store directory is empty!");
            return;
        }

        try {
            storeService.importFiles(dir.get());
        } catch (Exception e) {
            log.error("Error searching store directory '{}'.", dir, e);
        }
    }

}
