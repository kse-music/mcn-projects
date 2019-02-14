package com.hiekn.boot.autoconfigure.jersey;

import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.ReaderListener;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.HeaderParameter;

import java.util.Map;

/**
 * global generate header parameter
 *
 * @author DingHao
 * @date 2019/2/14 18:06
 */
public class SwaggerReaderListener implements ReaderListener {

    @Override
    public void beforeScan(Reader reader, Swagger swagger) {

    }

    @Override
    public void afterScan(Reader reader, Swagger swagger) {
        HeaderParameter headerParameter = new HeaderParameter();
        headerParameter.setName("Authorization");
        headerParameter.setType("string");
//        headerParameter.setRequired(true);
        Map<String, Path> paths = swagger.getPaths();
        paths.forEach((k,v) -> {
            v.addParameter(headerParameter);
        });
    }

}
