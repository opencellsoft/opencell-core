package org.meveo.api;

import javax.ejb.Stateless;

import org.meveo.commons.utils.ParamBean;

@Stateless
public class ConfigurationApi extends BaseApi {

    private ParamBean paramBean = ParamBean.getInstance();

    /**
     * Set configuration/settings property
     * 
     * @param property Property key
     * @param value Property value as string
     */
    public void setProperty(String property, String value) {

        paramBean.setProperty(property, value);
        paramBean.saveProperties();
    }
}