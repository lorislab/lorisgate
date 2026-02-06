package org.lorislab.lorisgate.rs.oidc.controllers;

import java.util.Base64;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.lorislab.lorisgate.config.LorisGateConfig;
import org.lorislab.lorisgate.domain.model.*;
import org.lorislab.lorisgate.domain.services.IssuerService;
import org.lorislab.lorisgate.domain.services.RealmService;
import org.lorislab.lorisgate.domain.services.TokenService;
import org.lorislab.lorisgate.domain.utils.JwtHelper;

import gen.org.lorislab.lorisgate.rs.oidc.TokenApi;
import gen.org.lorislab.lorisgate.rs.oidc.model.TokenSuccessDTO;

@ApplicationScoped
public class OidcTokenRestController implements TokenApi {

    @Inject
    LorisGateConfig config;

    @Inject
    UriInfo uriInfo;

    @Inject
    RealmService realmService;

    @Inject
    TokenService tokenService;

    @Inject
    IssuerService issuerService;

    @Override
    public Response getToken(String realm, String grantType, String clientId, String authorization, String clientSecret,
            String username, String password, String scope, String code, String redirectUri, String codeVerifier,
            String refreshToken) {

        if ((clientId == null || clientSecret == null) && authorization != null && authorization.startsWith("Basic ")) {
            String decoded = new String(Base64.getDecoder().decode(authorization.substring(6)));
            int idx = decoded.indexOf(':');
            if (idx > 0) {
                if (clientId == null) {
                    clientId = decoded.substring(0, idx);
                }
                if (clientSecret == null) {
                    clientSecret = decoded.substring(idx + 1);
                }
            }
        }

        if (clientId == null || grantType == null) {
            return error(Response.Status.BAD_REQUEST, "invalid_request", "client_id and grant_type are required");
        }

        var store = realmService.getRealm(realm);

        if (store == null) {
            return error(Response.Status.BAD_REQUEST, "invalid_request", "realm does not exist");
        }

        if (!store.hasClient(clientId)) {
            return error(Response.Status.UNAUTHORIZED, "invalid_client", "Unknown client");
        }
        var client = store.getClient(clientId);
        if (client.isConfidential()) {
            if (clientSecret == null || !clientSecret.equals(client.getClientSecret())) {
                return error(Response.Status.UNAUTHORIZED, "invalid_client", "Invalid client credentials");
            }
        }

        var issuer = issuerService.issuer(uriInfo, store);
        var scopes = Scopes.toScopes(scope);

        return switch (grantType) {
            case GrantTypes.CLIENT_CREDENTIALS -> grandTypeClientCredentials(issuer, store, client, scopes);
            case GrantTypes.PASSWORD -> grandTypePassword(issuer, store, client, scopes, username, password);
            case GrantTypes.AUTHORIZATION_CODE ->
                grandTypeAuthorizationCode(issuer, store, client, code, redirectUri, codeVerifier);
            case GrantTypes.REFRESH_TOKEN -> grantTypeRefreshToken(issuer, store, client, refreshToken, scopes);
            default -> grandTypeDefault();
        };
    }

    private Response grandTypeDefault() {
        return error(Response.Status.BAD_REQUEST, "unsupported_grant_type",
                "Supported: authorization_code, password, client_credentials");
    }

    private Response grantTypeRefreshToken(String issuer, Realm store, Client client, String refreshToken, Set<String> scope) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return error(Response.Status.BAD_REQUEST, "invalid_request", "refresh_token required");
        }

        try {
            var rToken = tokenService.parseRefreshToken(issuerService.issuer(uriInfo, store),
                    refreshToken);

            if (!client.getClientId().equals(rToken.getClientId())) {
                return error(Response.Status.BAD_REQUEST, "invalid_grant", "refresh_token not for this client");
            }

            if (rToken.isExpired()) {
                return error(Response.Status.BAD_REQUEST, "invalid_grant", "refresh_token expired");
            }

            var tmp = new HashSet<>(client.getScopes());
            tmp.retainAll(scope);

            User user = null;
            var username = rToken.getUsername();
            if (username != null) {
                user = store.getUser(username);
                if (user == null || !user.isEnabled()) {
                    return error(Response.Status.UNAUTHORIZED, "invalid_grant", "Invalid user");
                }
            }

            return issueTokens(issuer, client, user, tmp, null, true, rToken);

        } catch (Exception e) {
            return error(Response.Status.BAD_REQUEST, "invalid_grant", "Invalid refresh_token");
        }
    }

    private Response grandTypePassword(String issuer, Realm store, Client client, Set<String> scope, String username,
            String password) {

        if (username == null || password == null) {
            return error(Response.Status.BAD_REQUEST, "invalid_request", "username and password required");
        }

        var user = store.getUser(username);
        if (user == null || !user.isEnabled()) {
            return error(Response.Status.UNAUTHORIZED, "invalid_grant", "Invalid user");
        }

        if (!password.equals(user.getPassword())) {
            return error(Response.Status.UNAUTHORIZED, "invalid_grant", "Invalid credentials");
        }

        var tmp = new HashSet<>(client.getScopes());
        tmp.retainAll(scope);

        return issueTokens(issuer, client, user, tmp, null, true, null);
    }

    private Response grandTypeAuthorizationCode(String issuer, Realm store, Client client, String code, String redirectUri,
            String codeVerifier) {

        if (code == null || redirectUri == null) {
            return error(Response.Status.BAD_REQUEST, "invalid_request", "code and redirect_uri required");
        }

        var tmp = store.getAuthCode(code);

        if (tmp.isEmpty()) {
            return error(Response.Status.BAD_REQUEST, "invalid_grant", "Invalid or expired code");
        }

        var ac = tmp.get();
        if (!ac.getClientId().equals(client.getClientId())) {
            return error(Response.Status.BAD_REQUEST, "invalid_grant", "Code not for this client");
        }
        if (!ac.getRedirectUri().equals(redirectUri)) {
            return error(Response.Status.BAD_REQUEST, "invalid_grant", "redirect_uri mismatch");
        }

        if (ac.getCodeChallenge() != null) {

            if (codeVerifier == null || codeVerifier.isBlank()) {
                return error(Response.Status.BAD_REQUEST, "invalid_grant", "code_verifier required");
            }

            String method = ac.getCodeChallengeMethod();
            String derived = codeVerifier;

            if ("S256".equalsIgnoreCase(method)) {
                derived = JwtHelper.generateChallenge(codeVerifier);
            }

            if (!derived.equals(ac.getCodeChallenge())) {
                return error(Response.Status.BAD_REQUEST, "invalid_grant", "PKCE verification failed");
            }
        } else if (!client.isConfidential()) {
            return error(Response.Status.BAD_REQUEST, "invalid_grant", "PKCE required for public clients");
        }

        store.consumeAuthCode(code);

        var user = store.getUser(ac.getUsername());
        if (user == null || !user.isEnabled()) {
            return error(Response.Status.UNAUTHORIZED, "invalid_grant", "User not found");
        }
        return issueTokens(issuer, client, user, ac.getScopes(), ac.getNonce(), true, null);
    }

    private Response grandTypeClientCredentials(String issuer, Realm store, Client client, Set<String> scope) {

        var tmp = new HashSet<>(client.getScopes());
        tmp.retainAll(scope);

        return issueTokens(issuer, client, null, tmp, null, false, null);
    }

    private Response error(Response.Status s, String code, String desc) {
        return Response.status(s).entity(Map.of("error", code, "error_description", desc)).build();
    }

    private Response issueTokens(String issuer, Client client, User user, Set<String> scopes, String nonce,
            boolean issueRefresh, RefreshToken fromRefresh) {

        var accessToken = tokenService.createAccessToken(issuer, user, client, scopes);

        var dto = new TokenSuccessDTO()
                .accessToken(accessToken)
                .expiresIn(config.oidc().tokenLifetime())
                .tokenType(TokenSuccessDTO.TokenTypeEnum.BEARER);

        if (!scopes.isEmpty()) {
            dto.scope(Scopes.fromScopes(scopes));
        }

        if (user != null && !scopes.isEmpty() && scopes.contains(Scopes.OPENID)) {
            dto.idToken(tokenService.createIdToken(issuer, user, client, nonce));
        }

        if (issueRefresh) {
            if (fromRefresh != null) {
                dto.setRefreshToken(tokenService.rotateRefreshToken(user, client, fromRefresh));
            } else {
                dto.setRefreshToken(tokenService.createRefreshToken(issuer, user, client, scopes));
            }
            dto.setRefreshExpiresIn(config.oidc().refreshLifetime());
        }
        return Response.ok(dto).build();
    }

}
