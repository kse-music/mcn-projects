package com.hiekn.boot.autoconfigure.web.exception.handler;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public final class ExceptionHandler extends AbstractExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(final Exception exception) {
        dealStackTraceElement(exception);
        logger.error("ErrorMsg = {}",getErrorMsg(SERVICE_ERROR),exception);
        return Response.ok(buildErrorMessage(SERVICE_ERROR)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

}
