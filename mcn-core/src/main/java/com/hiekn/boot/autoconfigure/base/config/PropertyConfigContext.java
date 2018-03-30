package com.hiekn.boot.autoconfigure.base.config;

import java.util.Properties;

/**
 * describe about this class
 *
 * @author: DingHao
 * @date: 2019/1/7 20:45
 */
public class PropertyConfigContext {

    private PropertyConfig propertyConfig;

    public PropertyConfigContext(PropertyConfig propertyConfig) {
        this.propertyConfig = propertyConfig;
    }

    public void setPropertyConfig(PropertyConfig propertyConfig) {
        this.propertyConfig = propertyConfig;
    }

    public Properties getProperties(){
        return propertyConfig.getProperties();
    }

}
