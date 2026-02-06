package org.lorislab.lorisgate.rs.oidc.controllers;

import java.net.URI;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.reactive.RestResponse;
import org.lorislab.lorisgate.domain.model.GrantTypes;
import org.lorislab.lorisgate.domain.model.ResponseTypes;
import org.lorislab.lorisgate.domain.model.Scopes;
import org.lorislab.lorisgate.domain.services.IssuerService;
import org.lorislab.lorisgate.domain.services.KeyManager;
import org.lorislab.lorisgate.domain.services.RealmService;
import org.lorislab.lorisgate.domain.utils.Base64Utils;

import gen.org.lorislab.lorisgate.rs.oidc.ConfigApi;
import gen.org.lorislab.lorisgate.rs.oidc.model.JwkDTO;
import gen.org.lorislab.lorisgate.rs.oidc.model.JwksDTO;
import gen.org.lorislab.lorisgate.rs.oidc.model.OpenIdConfigurationDTO;
import io.smallrye.jwt.algorithm.SignatureAlgorithm;

public class OidcConfigRestController implements ConfigApi {

    @Inject
    UriInfo uriInfo;

    @Inject
    KeyManager keyManager;

    @Inject
    IssuerService issuerService;

    @Inject
    RealmService realmService;

    @Override
    public RestResponse<JwksDTO> getJwks(String realm) {

        var store = realmService.getRealm(realm);
        if (store == null) {
            return RestResponse.status(Response.Status.BAD_REQUEST);
        }

        RSAPublicKey pub = keyManager.getPublicKey();
        var kid = keyManager.getKid();
        var dto = new JwksDTO()
                .addKeysItem(new JwkDTO()
                        .kty("RSA").use(JwkDTO.UseEnum.SIG).kid(kid).alg(SignatureAlgorithm.RS256.name())
                        .n(Base64Utils.base64Url(pub.getModulus().toByteArray()))
                        .e(Base64Utils.base64Url(pub.getPublicExponent().toByteArray())));
        return RestResponse.ok(dto);
    }

    @Override
    public RestResponse<OpenIdConfigurationDTO> getOpenIdConfiguration(String realm) {

        var store = realmService.getRealm(realm);
        if (store == null) {
            return RestResponse.status(Response.Status.BAD_REQUEST);
        }

        var base = issuerService.issuer(uriInfo, store);

        var result = new OpenIdConfigurationDTO()
                .issuer(URI.create(base))
                .authorizationEndpoint(URI.create(base + "/protocol/openid-connect/auth"))
                .tokenEndpoint(URI.create(base + "/protocol/openid-connect/token"))
                .jwksUri(URI.create(base + "/protocol/openid-connect/certs"))
                .userinfoEndpoint(URI.create(base + "/protocol/openid-connect/userinfo"))
                .endSessionEndpoint(URI.create(base + "/protocol/openid-connect/logout"))
                .checkSessionIframe(URI.create(base + "/protocol/openid-connect/login-status-iframe.html"))
                .scopesSupported(Scopes.supportedScopes())
                .responseTypesSupported(ResponseTypes.supportedResponseTypes())
                .grantTypesSupported(GrantTypes.supportedGrantTypes())
                .subjectTypesSupported(List.of(OpenIdConfigurationDTO.SubjectTypesSupportedEnum.PUBLIC))
                .idTokenEncryptionAlgValuesSupported(List.of(SignatureAlgorithm.RS256.name()))
                .codeChallengeMethodsSupported(List.of(OpenIdConfigurationDTO.CodeChallengeMethodsSupportedEnum.PLAIN,
                        OpenIdConfigurationDTO.CodeChallengeMethodsSupportedEnum.S256))
                .tokenEndpointAuthMethodsSupported(
                        List.of(OpenIdConfigurationDTO.TokenEndpointAuthMethodsSupportedEnum.CLIENT_SECRET_BASIC,
                                OpenIdConfigurationDTO.TokenEndpointAuthMethodsSupportedEnum.CLIENT_SECRET_POST));
        return RestResponse.ok(result);
    }

}
