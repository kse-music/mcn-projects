package com.hiekn.boot.autoconfigure.web.filter;

import com.hiekn.boot.autoconfigure.base.exception.ErrorMsg;
import com.hiekn.boot.autoconfigure.base.util.JsonUtils;
import com.hiekn.licence.verify.VerifyLicense;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CheckCertificateFilter implements Filter {

    private static volatile VerifyLicense vLicense;

    private Environment environment;

    public CheckCertificateFilter(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        String url = request.getRequestURI().replaceFirst(request.getContextPath(), "");
        String path = environment.getProperty("spring.jersey.application-path")+"/h/lic";
        if(!path.equalsIgnoreCase(url)){
            try {
                initLic();
                vLicense.verify();
            } catch (Exception e) {
                vLicense = null;
                response.getWriter().write(JsonUtils.toJson(ErrorMsg.invalidCertificate()));
                return;
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }

    private void initLic(){
        if(vLicense == null){
            synchronized (CheckCertificateFilter.class){
                if(vLicense == null) {
                    vLicense = new VerifyLicense("lic-verify.properties");
                }
            }
        }
    }

}
