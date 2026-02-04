package org.lorislab.lorisgate.rs.oidc.controllers;

import java.net.URI;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.lorislab.lorisgate.config.LorisGateConfig;
import org.lorislab.lorisgate.domain.model.AuthorizationCode;
import org.lorislab.lorisgate.domain.model.ResponseTypes;
import org.lorislab.lorisgate.domain.model.Scopes;
import org.lorislab.lorisgate.domain.services.IssuerService;
import org.lorislab.lorisgate.domain.services.RealmService;
import org.lorislab.lorisgate.domain.services.TokenService;

import gen.org.lorislab.lorisgate.rs.oidc.AuthApi;
import gen.org.lorislab.lorisgate.rs.oidc.model.OAuthErrorDTO;
import io.quarkus.qute.Template;
import io.quarkus.runtime.annotations.RegisterForReflection;

@ApplicationScoped
public class OidcAuthRestController implements AuthApi {

    @Inject
    LorisGateConfig config;

    @Inject
    RealmService realmService;

    @Inject
    TokenService tokenService;

    @Inject
    UriInfo uriInfo;

    @Inject
    IssuerService issuerService;

    @Inject
    Template login;

    @Override
    public Response authorize(String realm, String responseType, String clientId, URI redirectUri, String scope, String state,
            String nonce, String codeChallenge, String codeChallengeMethod, String asUser) {

        var store = realmService.getRealm(realm);

        if (store == null) {
            return bad(OAuthErrorDTO.ErrorEnum.REALM_NOT_FOUND);
        }

        var client = store.getClient(clientId);
        if (client == null) {
            return bad(OAuthErrorDTO.ErrorEnum.UNAUTHORIZED_CLIENT);
        }

        if (!client.getRedirectUris().contains("*")) {
            if (!client.getRedirectUris().contains(redirectUri.toString())) {
                return bad(OAuthErrorDTO.ErrorEnum.INVALID_REQUEST);
            }
        }

        if (asUser == null) {
            String returnTo = String.format(
                    "/realms/%s/protocol/openid-connect/auth?response_type=%s&client_id=%s&redirect_uri=%s&scope=%s&state=%s&nonce=%s&code_challenge=%s&code_challenge_method=%s",
                    realm,
                    enc(responseType), enc(clientId), enc(redirectUri.toString()), enc(scope), enc(state), enc(nonce),
                    enc(codeChallenge),
                    enc(codeChallengeMethod));
            return Response
                    .seeOther(URI
                            .create(String.format("/realms/%s/login-actions/authenticate?return_to=%s", realm, enc(returnTo))))
                    .build();
        }

        var user = store.getUser(asUser);
        if (user == null || !user.isEnabled()) {
            return bad(OAuthErrorDTO.ErrorEnum.ACCESS_DENIED);
        }

        Set<String> scopes = Scopes.toScopes(scope);

        responseType = responseType.toLowerCase();

        if (ResponseTypes.CODE.equals(responseType)) {
            AuthorizationCode ac = new AuthorizationCode();
            ac.setCode(UUID.randomUUID().toString());
            ac.setClientId(clientId);
            ac.setUsername(user.getUsername());
            ac.setRedirectUri(redirectUri.toString());
            ac.setNonce(nonce);
            ac.setCodeChallenge(codeChallenge);
            ac.setCodeChallengeMethod(codeChallengeMethod);
            ac.setScopes(scopes);
            ac.setExpiresAt(Instant.now().plusSeconds(config.oidc().authCodeLifetime()));
            store.saveAuthCode(ac);

            var tmp = URI.create(redirectUri + (redirectUri.toString().contains("?") ? "&" : "?") + "code=" + ac.getCode()
                    + (state != null ? "&state=" + enc(state) : ""));
            return Response.seeOther(tmp).build();
        }

        Set<String> types = ResponseTypes.toTypes(responseType);
        if (types.contains(ResponseTypes.TOKEN) || types.contains(ResponseTypes.ID_TOKEN)) {

            String fragment = "";

            var issuer = issuerService.issuer(uriInfo, realm);

            if (types.contains(ResponseTypes.TOKEN)) {
                String at = tokenService.createAccessToken(issuer, user, client, scopes);
                fragment += "access_token=" + at + "&token_type=bearer&expires_in=" + config.oidc().tokenLifetime();
            }

            if (types.contains(ResponseTypes.ID_TOKEN)) {
                String idt = tokenService.createIdToken(issuer, user, client, nonce);
                fragment += (fragment.isEmpty() ? "" : "&") + "id_token=" + idt;
            }

            if (state != null) {
                fragment += "&state=" + enc(state);
            }
            return Response.seeOther(URI.create(redirectUri + "#" + fragment)).build();
        }

        return bad(OAuthErrorDTO.ErrorEnum.INVALID_REQUEST);
    }

    private static String enc(String s) {
        return s == null ? "" : java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    }

    private static Response bad(OAuthErrorDTO.ErrorEnum error) {
        return Response.status(Response.Status.BAD_REQUEST).entity(new OAuthErrorDTO().error(error)).build();
    }

    @Override
    public Response doLogin(String realm, String username, String password, String returnTo) {

        var store = realmService.getRealm(realm);

        if (store == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("realm not found").build();
        }

        var user = store.getUser(username);
        if (user == null || !user.isEnabled() || !user.getPassword().equals(password)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }

        if (returnTo == null) {
            return Response.seeOther(URI.create("/")).build();
        }
        String sep = returnTo.contains("?") ? "&" : "?";
        return Response.seeOther(URI.create(returnTo + sep + "as_user=" + username)).build();
    }

    @Override
    public Response loginPage(String realm, String returnTo) {
        return Response.ok(
                login.data("container", new Container(realm, returnTo)).render()).build();
    }

    @RegisterForReflection
    public record Container(String realm, String returnTo) {
    }
}
