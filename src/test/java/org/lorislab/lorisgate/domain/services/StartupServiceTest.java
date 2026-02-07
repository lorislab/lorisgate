package org.lorislab.lorisgate.domain.services;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.test.InjectMock;
import io.quarkus.test.component.QuarkusComponentTest;
import io.quarkus.test.component.TestConfigProperty;

@QuarkusComponentTest
@TestConfigProperty(key = "lorisgate.key.id", value = "12345")
class StartupServiceTest {

    @Inject
    StartupService service;

    @InjectMock
    StoreService storeService;

    @Test
    void testImportFilesException() throws Exception {
        Mockito.doThrow(new RuntimeException("Error")).when(storeService).importFiles(any());
        assertThatNoException().isThrownBy(() -> service.onStart(new StartupEvent()));
    }

    @Test
    @TestConfigProperty(key = "lorisgate.store.directory", value = "")
    void testEmptyDir() {
        assertThatNoException().isThrownBy(() -> service.onStart(new StartupEvent()));
    }

}
