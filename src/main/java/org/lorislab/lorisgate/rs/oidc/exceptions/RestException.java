package org.lorislab.lorisgate.rs.oidc.exceptions;

import org.jboss.resteasy.reactive.RestResponse;

public class RestException extends RuntimeException {

    private final RestResponse.Status status;

    private final transient Object error;

    public static RestException unauthorized(Object error) {
        return new RestException(RestResponse.Status.UNAUTHORIZED, error);
    }

    public static RestException unauthorized(Object error, String message) {
        return new RestException(RestResponse.Status.UNAUTHORIZED, error, message);
    }

    public static RestException unauthorized(Object error, Throwable cause) {
        return new RestException(RestResponse.Status.UNAUTHORIZED, error, cause);
    }

    public static RestException badRequest(Object error) {
        return new RestException(RestResponse.Status.BAD_REQUEST, error);
    }

    public static RestException badRequest(Object error, String message) {
        return new RestException(RestResponse.Status.BAD_REQUEST, error, message);
    }

    public RestException(RestResponse.Status status, Object error) {
        this.status = status;
        this.error = error;
    }

    public RestException(RestResponse.Status status, Object error, String message) {
        super(message);
        this.status = status;
        this.error = error;
    }

    public RestException(RestResponse.Status status, Object error, Throwable cause) {
        super(cause.getMessage(), cause);
        this.status = status;
        this.error = error;
    }

    public RestResponse.Status getStatus() {
        return status;
    }

    @SuppressWarnings("unchecked")
    public <T> T getError() {
        return (T) error;
    }

}
