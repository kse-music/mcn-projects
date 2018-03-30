package com.hiekn.boot.autoconfigure.web.exception.handler;


import org.glassfish.jersey.server.ParamException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public final class ParamExceptionHandler extends AbstractExceptionHandler implements ExceptionMapper<ParamException> {

    @Override
    public Response toResponse(final ParamException exception) {
        dealStackTraceElement(exception);
        logger.error("ErrorMsg = {}",getErrorMsg(PARAM_PARSE_ERROR),exception);
        return Response.ok(buildErrorMessage(PARAM_PARSE_ERROR)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

}
