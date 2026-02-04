package org.lorislab.lorisgate.domain.services;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.lorislab.lorisgate.domain.utils.JwtHelper.generateKeyPair;

import java.nio.file.NoSuchFileException;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;
import org.lorislab.lorisgate.domain.utils.Base64Utils;
import org.lorislab.lorisgate.domain.utils.JwtHelper;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class CommonTest {

    @Test
    void testBase64Url() {
        Base64Utils.base64Url(new byte[] { 1, 1 });
        Base64Utils.base64Url(new byte[] { 0 });
        Base64Utils.base64Url(new byte[] { 0, 1 });
    }

    @Test
    void testJwtHelperKeyPairException() {

        assertThatNoException().isThrownBy(() -> JwtHelper.generateKeyPair(JwtHelper.ALGORITHM));

        assertThatThrownBy(() -> JwtHelper.generateKeyPair("NONE"))
                .isInstanceOf(NoSuchAlgorithmException.class);

        assertThatThrownBy(() -> JwtHelper.fromFiles("src/test/resources/keys/test_wrong_key_1.pem", null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid key PEM format");
        assertThatThrownBy(() -> JwtHelper.fromFiles("src/test/resources/keys/test_wrong_key_2.pem", null))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> JwtHelper.fromFiles("src/test/resources/keys/test_wrong_key_3.pem", null))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> JwtHelper.fromFiles("src/test/resources/keys/does_not_exists_key.pem", null))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    void testJwtHelperGenerateChallenge() {

        assertThatThrownBy(() -> JwtHelper.generateChallenge("NONE", "12345"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No SHA-256 algorithm found.");

    }
}
