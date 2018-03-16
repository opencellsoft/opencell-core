package org.meveo.api;

import javax.ejb.Stateless;

import org.meveo.commons.utils.ParamBean;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 *
 */
@Stateless
public class ConfigurationApi extends BaseApi {

    /**
     * Set configuration/settings property
     * 
     * @param property Property key
     * @param value Property value as string
     */
    public void setProperty(String property, String value) {
        ParamBean paramBean = paramBeanFactory.getInstance();
        paramBean.setProperty(property, value);
        paramBean.saveProperties();
    }
}