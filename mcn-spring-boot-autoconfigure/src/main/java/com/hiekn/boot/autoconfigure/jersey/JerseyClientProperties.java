package com.hiekn.boot.autoconfigure.jersey;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.ws.rs.core.MediaType;

@ConfigurationProperties(
        prefix = "jersey.client"
)
public class JerseyClientProperties {

    private String acceptContentType = MediaType.APPLICATION_JSON+";"+MediaType.CHARSET_PARAMETER+"=utf-8";
    private String requestContentEncode = MediaType.APPLICATION_FORM_URLENCODED+";"+MediaType.CHARSET_PARAMETER+"=utf-8";

    private Integer connectTimeout = 2*60000;//default 60000ms 2min
    private Integer readTimeout = 2*60000;//default 60000ms 2min

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String getAcceptContentType() {
        return acceptContentType;
    }

    public void setAcceptContentType(String acceptContentType) {
        this.acceptContentType = acceptContentType;
    }

    public String getRequestContentEncode() {
        return requestContentEncode;
    }

    public void setRequestContentEncode(String requestContentEncode) {
        this.requestContentEncode = requestContentEncode;
    }
}
