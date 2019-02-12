package com.hiekn.boot.autoconfigure.web.filter;

import com.google.common.collect.Lists;
import com.hiekn.boot.autoconfigure.base.exception.ErrorMsg;
import com.hiekn.boot.autoconfigure.base.util.JsonUtils;
import com.hiekn.licence.verify.VerifyLicense;
import org.springframework.http.MediaType;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CheckCertificateFilter implements Filter {

    private static volatile VerifyLicense vLicense;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        String url = request.getRequestURI().replaceFirst(request.getContextPath(), "");
        if(!isWhite(url)){
            try {
                initLic();
                vLicense.verify();
            } catch (Exception e) {
                vLicense = null;
                HttpServletResponse response = (HttpServletResponse)servletResponse;
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                response.getWriter().write(JsonUtils.toJson(ErrorMsg.invalidCertificate()));
                return;
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }

    private boolean isWhite(String url){
        if(url.contains("Swagger")){
            return true;
        }
        for (String s : Lists.newArrayList("h/lic","swagger.json")) {
            if(url.endsWith(s)){
                return true;
            }
        }
        return false;
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
