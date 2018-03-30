package com.hiekn.boot.autoconfigure.web.exception.handler;

import com.google.gson.JsonParseException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * describe about this class
 *
 * @author DingHao
 * @date 2019/1/3 14:41
 */
@Provider
public final class JsonParseExceptionHandler extends AbstractExceptionHandler implements ExceptionMapper<JsonParseException> {

    @Override
    public Response toResponse(JsonParseException exception) {
        dealStackTraceElement(exception);
        logger.error("ErrorMsg = {}",getErrorMsg(JSON_PARSE_ERROR),exception);
        return Response.ok(buildErrorMessage(JSON_PARSE_ERROR)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

}
