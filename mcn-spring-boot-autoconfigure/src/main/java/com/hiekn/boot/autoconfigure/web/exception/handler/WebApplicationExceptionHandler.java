package com.hiekn.boot.autoconfigure.web.exception.handler;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public final class WebApplicationExceptionHandler extends AbstractExceptionHandler implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(final WebApplicationException exception) {
        Integer code = HTTP_ERROR_500;
        Response.Status statusCode = Response.Status.OK;
        if (exception instanceof NotFoundException) {
            statusCode = Response.Status.NOT_FOUND;
            code = HTTP_ERROR_404;
        } else if (exception instanceof NotAllowedException) {
            statusCode = Response.Status.METHOD_NOT_ALLOWED;
            code = HTTP_ERROR_405;
        } else if (exception instanceof NotAcceptableException) {
            statusCode = Response.Status.NOT_ACCEPTABLE;
            code = HTTP_ERROR_406;
        } else if (exception instanceof InternalServerErrorException) {
            statusCode = Response.Status.INTERNAL_SERVER_ERROR;
        }
        String errMsg = getErrorMsg(code);
        logger.error("ErrorMsg = {}",errMsg,exception);
        return Response.ok(buildErrorMessage(code, errMsg)).status(statusCode).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

}
