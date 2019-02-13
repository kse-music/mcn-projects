package com.hiekn.boot.autoconfigure.web.exception.handler;

import com.hiekn.boot.autoconfigure.base.model.ValidationErrorBean;
import com.hiekn.boot.autoconfigure.base.model.result.RestResp;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.validation.ValidationError;
import org.glassfish.jersey.server.validation.internal.ValidationHelper;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;

@Provider
public final class ValidationExceptionMapper extends AbstractExceptionHandler implements ExceptionMapper<ValidationException> {

    @Context
    private Configuration config;

    @Override
    public Response toResponse(final ValidationException exception) {
        RestResp<List<ValidationErrorBean>> objectRestResp = buildErrorMessage(PARAM_PARSE_ERROR);
        if (exception instanceof ConstraintViolationException) {

            final ConstraintViolationException cve = (ConstraintViolationException) exception;
            final Response.ResponseBuilder response = Response.status(ValidationHelper.getResponseStatus(cve));

            final Object property = config.getProperty(ServerProperties.BV_SEND_ERROR_IN_RESPONSE);

            if (property != null && Boolean.valueOf(property.toString())) {
                response.type(MediaType.APPLICATION_JSON_TYPE);
                List<ValidationError> errors = ValidationHelper.constraintViolationToValidationErrors(cve);
                List<ValidationErrorBean> list = new ArrayList();
                for (ValidationError error : errors) {
                    ValidationErrorBean validationErrorBean = new ValidationErrorBean();
                    validationErrorBean.setMessage(error.getMessage());
                    validationErrorBean.setInvalidValue(error.getInvalidValue());
                    validationErrorBean.setPath(error.getPath());
                    list.add(validationErrorBean);
                }
                objectRestResp.setData(list);
            }
        }
        return Response.ok(objectRestResp).build();
    }
}
