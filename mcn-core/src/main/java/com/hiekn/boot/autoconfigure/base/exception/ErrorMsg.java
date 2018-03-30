package com.hiekn.boot.autoconfigure.base.exception;

import com.hiekn.boot.autoconfigure.base.model.result.RestResp;
import com.hiekn.boot.autoconfigure.base.util.McnUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class ErrorMsg implements ExceptionKeys {

    private static List<Properties> errMsgProp;

    static {
        errMsgProp = new ArrayList<>();
        errMsgProp.add(McnUtils.loadProperties("mcn-error-msg.properties"));
        errMsgProp.add(McnUtils.loadProperties("error-msg.properties"));
    }

    public static String getErrorMsg(Integer code){
        for (Properties prop : errMsgProp) {
            String propertyValue = prop.getProperty(code.toString());
            if(!McnUtils.isNullOrEmpty(propertyValue)){
                return propertyValue;
            }
        }
        return null;
    }

    public static RestResp invalidCertificate(){
        return buildErrorMessage(ExceptionKeys.INVALID_CERTIFICATE_ERROR);
    }

    public static RestResp buildErrorMessage(int code){
        return buildErrorMessage(code,getErrorMsg(code));
    }

    public static RestResp buildErrorMessage(int code,String msg){
        return new RestResp(code, msg);
    }

}
