package org.lorislab.lorisgate.rs.oidc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.lorislab.lorisgate.domain.model.ClaimNames;
import org.lorislab.lorisgate.domain.services.IssuerService;
import org.lorislab.lorisgate.domain.services.TokenService;

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

    @Override
    public Response getUserinfo(String realm) {
        var auth = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorTokenDTO().error(ErrorTokenDTO.ErrorEnum.MISSING_BEARER_TOKEN)).build();
        }
        String token = auth.substring("Bearer ".length());

        try {
            var claims = refreshTokenService.parse(issuerService.issuer(uriInfo, realm), token);

            var dto = new UserInfoDTO().sub(claims.getSubject())
                    .name(claims.getClaimValueAsString(ClaimNames.NAME))
                    .preferredUsername(claims.getClaimValueAsString(ClaimNames.PREFERRED_USERNAME))
                    .givenName(claims.getClaimValueAsString(ClaimNames.GIVEN_NAME))
                    .familyName(claims.getClaimValueAsString(ClaimNames.FAMILY_NAME))
                    .email(claims.getClaimValueAsString(ClaimNames.EMAIL))
                    .emailVerified(Boolean.parseBoolean(claims.getClaimValueAsString(ClaimNames.EMAIL_VERIFIED)));
            return Response.ok(dto).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorTokenDTO().error(ErrorTokenDTO.ErrorEnum.INVALID_TOKEN).errorDescription(e.getMessage()))
                    .build();
        }
    }
}
