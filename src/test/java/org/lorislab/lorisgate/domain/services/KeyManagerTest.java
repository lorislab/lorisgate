package org.lorislab.lorisgate.domain.services;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.component.QuarkusComponentTest;
import io.quarkus.test.component.TestConfigProperty;

@QuarkusComponentTest
@TestConfigProperty(key = "lorisgate.key.id", value = "12345")
class KeyManagerTest {

    @Inject
    KeyManager keyManager;

    @Test
    @TestConfigProperty(key = "lorisgate.key.private-key-file", value = "")
    @TestConfigProperty(key = "lorisgate.key.public-key-file", value = "")
    void testGenerateKeyPairEmpty() {
        keyManager.init();
    }

    @Test
    @TestConfigProperty(key = "lorisgate.key.private-key-file", value = "")
    void testGenerateKeyPairPrivateEmpty() {
        keyManager.init();
    }

    @Test
    @TestConfigProperty(key = "lorisgate.key.public-key-file", value = "")
    void testGenerateKeyPairPublicEmpty() {
        keyManager.init();
    }

    @Test
    void testGenerateKeyPairException() {
        assertThatThrownBy(() -> keyManager.generateKeyPair("NONE"))
                .isInstanceOf(KeyManager.KeyManagerException.class)
                .hasMessage("Generate key pair error");

        KeyManager.KeyManagerException ex = null;
        try {
            keyManager.generateKeyPair("NONE");
        } catch (KeyManager.KeyManagerException e) {
            ex = e;
        }
        assertThat(ex).isNotNull().hasMessage("Generate key pair error");
    }

    @Test
    void testWrongKeys() {
        assertThatThrownBy(() -> keyManager.loadKeyPair("src/test/resources/keys/test_wrong_key_1.pem", null))
                .isInstanceOf(KeyManager.KeyManagerException.class).hasMessage("Error loading key pair from PEM");
        assertThatThrownBy(() -> keyManager.loadKeyPair("src/test/resources/keys/test_wrong_key_2.pem", null))
                .isInstanceOf(KeyManager.KeyManagerException.class);
        assertThatThrownBy(() -> keyManager.loadKeyPair("src/test/resources/keys/test_wrong_key_3.pem", null))
                .isInstanceOf(KeyManager.KeyManagerException.class);
        assertThatThrownBy(() -> keyManager.loadKeyPair("src/test/resources/keys/does_not_exists_key.pem", null))
                .isInstanceOf(KeyManager.KeyManagerException.class);
    }
}
