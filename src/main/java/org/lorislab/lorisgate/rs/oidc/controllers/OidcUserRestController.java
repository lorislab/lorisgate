package org.lorislab.lorisgate.rs.oidc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.lorislab.lorisgate.domain.model.ClaimNames;
import org.lorislab.lorisgate.domain.model.HttpAuth;
import org.lorislab.lorisgate.domain.services.IssuerService;
import org.lorislab.lorisgate.domain.services.RealmService;
import org.lorislab.lorisgate.domain.services.TokenService;
import org.lorislab.lorisgate.rs.oidc.exceptions.RestException;

import gen.org.lorislab.lorisgate.rs.oidc.UserApi;
import gen.org.lorislab.lorisgate.rs.oidc.model.ErrorTokenDTO;
import gen.org.lorislab.lorisgate.rs.oidc.model.UserInfoDTO;

@ApplicationScoped
public class OidcUserRestController implements UserApi {

    @Inject
    UriInfo uriInfo;

    @Context
    HttpHeaders headers;

    @Inject
    TokenService refreshTokenService;

    @Inject
    IssuerService issuerService;

    @Inject
    RealmService realmService;

    @Override
    public RestResponse<UserInfoDTO> getUserinfo(String realm) {
        var auth = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith(HttpAuth.BEARER_PREFIX)) {
            throw RestException.unauthorized(ErrorTokenDTO.ErrorEnum.MISSING_BEARER_TOKEN);
        }
        String token = auth.substring(HttpAuth.BEARER_PREFIX_LENGTH);

        var store = realmService.getRealm(realm);
        if (store == null) {
            return RestResponse.status(Response.Status.BAD_REQUEST);
        }

        try {
            var claims = refreshTokenService.parse(issuerService.issuer(uriInfo, store), token);

            var dto = new UserInfoDTO().sub(claims.getSubject())
                    .name(claims.getClaimValueAsString(ClaimNames.NAME))
                    .preferredUsername(claims.getClaimValueAsString(ClaimNames.PREFERRED_USERNAME))
                    .givenName(claims.getClaimValueAsString(ClaimNames.GIVEN_NAME))
                    .familyName(claims.getClaimValueAsString(ClaimNames.FAMILY_NAME))
                    .email(claims.getClaimValueAsString(ClaimNames.EMAIL))
                    .emailVerified(Boolean.parseBoolean(claims.getClaimValueAsString(ClaimNames.EMAIL_VERIFIED)));
            return RestResponse.ok(dto);

        } catch (Exception e) {
            throw RestException.unauthorized(ErrorTokenDTO.ErrorEnum.INVALID_TOKEN, e);
        }
    }

    @ServerExceptionMapper
    public RestResponse<ErrorTokenDTO> mapException(RestException e) {
        return RestResponse
                .status(e.getStatus(), new ErrorTokenDTO().error(e.getError()).errorDescription(e.getMessage()));
    }

}
