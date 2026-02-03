package org.lorislab.lorisgate.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "lorisgate")
public interface LorisGateConfig {

    /**
     * OIDC configuration.
     */
    @WithName("oidc")
    OidcConfig oidc();

    /**
     * Store configuration.
     */
    @WithName("store")
    StoreConfig store();

    /**
     * Key configuration.
     */
    @WithName("key")
    KeyConfig key();

    /**
     * Key configuration.
     */
    interface KeyConfig {

        /**
         * Key id.
         */
        @WithName("id")
        @WithDefault("${quarkus.uuid}")
        String id();

        /**
         * Private key file.
         */
        @WithName("private-key-file")
        Optional<String> privateKeyFile();

        /**
         * Public key file.
         */
        @WithName("public-key-file")
        Optional<String> publicKeyFile();
    }

    /**
     * OIDC configuration.
     */
    interface OidcConfig {

        /**
         * Token lifetime.
         */
        @WithName("token-lifetime")
        @WithDefault("3600")
        long tokenLifetime();

        /**
         * Token skew.
         */
        @WithName("token-skew")
        @WithDefault("30")
        int tokenSkew();

        /**
         * Refresh token lifetime.
         */
        @WithName("refresh-token-lifetime")
        @WithDefault("2592000")
        long refreshLifetime();

        /**
         * Authorization code lifetime.
         */
        @WithName("auth-code-lifetime")
        @WithDefault("3000")
        long authCodeLifetime();
    }

    interface StoreConfig {

        /**
         * Store directory.
         */
        @WithName("directory")
        Optional<String> directory();
    }
}
