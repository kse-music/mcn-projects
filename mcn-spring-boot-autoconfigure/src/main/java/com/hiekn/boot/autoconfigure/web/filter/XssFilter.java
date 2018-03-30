package com.hiekn.boot.autoconfigure.web.filter;

import com.hiekn.boot.autoconfigure.base.util.McnUtils;
import com.hiekn.boot.autoconfigure.jersey.FilterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * XssFilter
 *
 * @author DingHao
 * @date 2019/1/9 11:11
 */
public class XssFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(XssFilter.class);

    private FilterProperties filterProperties;

    public XssFilter(FilterProperties filterProperties) {
        this.filterProperties = filterProperties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        logger.debug("xss filter is open");

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (handleExcludeURL(req, resp)) {
            filterChain.doFilter(request, response);
            return;
        }

        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request, filterProperties.isIncludeRichText());

        filterChain.doFilter(xssRequest, response);
    }

    private boolean handleExcludeURL(HttpServletRequest request, HttpServletResponse response) {

        if (McnUtils.isNullOrEmpty(filterProperties.getExcludes())) {
            return false;
        }

        String url = request.getServletPath();
        for (String pattern : filterProperties.getExcludes()) {
            Pattern p = Pattern.compile("^" + pattern);
            Matcher m = p.matcher(url);
            if (m.find()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

}
