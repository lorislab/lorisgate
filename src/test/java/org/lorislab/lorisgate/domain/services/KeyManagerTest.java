package org.lorislab.lorisgate.domain.services;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.lorislab.lorisgate.domain.utils.JwtHelper;

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
        assertThatNoException().isThrownBy(() -> keyManager.init());
    }

    @Test
    @TestConfigProperty(key = "lorisgate.key.private-key-file", value = "")
    void testGenerateKeyPairPrivateEmpty() {
        assertThatNoException().isThrownBy(() -> keyManager.init());
    }

    @Test
    @TestConfigProperty(key = "lorisgate.key.public-key-file", value = "")
    void testGenerateKeyPairPublicEmpty() {
        assertThatNoException().isThrownBy(() -> keyManager.init());
    }

    @Test
    void testGenerateKeyPairException() {
        assertThatNoException().isThrownBy(() -> keyManager.generateKeyPair(JwtHelper.ALGORITHM));
        assertThatThrownBy(() -> keyManager.generateKeyPair("NONE"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Generate key pair error");
    }

    @Test
    void testWrongKeys() {
        assertThatThrownBy(() -> keyManager.loadKeyPair("src/test/resources/keys/test_wrong_1.example", null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Load private and public key from file. Error: Invalid key PEM format");
        assertThatThrownBy(() -> keyManager.loadKeyPair("src/test/resources/keys/test_wrong_2.example", null))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> keyManager.loadKeyPair("src/test/resources/keys/test_wrong_3.example", null))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> keyManager.loadKeyPair("src/test/resources/keys/does_not_exists_key.pem", null))
                .isInstanceOf(RuntimeException.class);
    }

}
