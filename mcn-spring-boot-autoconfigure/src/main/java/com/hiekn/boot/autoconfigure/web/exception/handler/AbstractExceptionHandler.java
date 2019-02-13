package com.hiekn.boot.autoconfigure.web.exception.handler;

import com.hiekn.boot.autoconfigure.base.exception.ErrorMsg;
import com.hiekn.boot.autoconfigure.jersey.JerseySwaggerProperties;
import com.hiekn.boot.autoconfigure.web.util.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * common
 *
 * @author DingHao
 * @date 2019/1/2 19:04
 */
public abstract class AbstractExceptionHandler extends ErrorMsg {

    protected String basePackage = SpringBeanUtils.getBean(JerseySwaggerProperties.class).getBasePackage();
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected void dealStackTraceElement(Exception exception){
        List<StackTraceElement> elements = Arrays.asList(exception.getStackTrace()).stream().filter(s -> s.getClassName().contains(basePackage)).collect(Collectors.toList());
        exception.setStackTrace(elements.toArray(new StackTraceElement[elements.size()]));
    }

}
