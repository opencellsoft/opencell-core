package org.meveo.api;

import java.util.Properties;

import javax.ejb.Stateless;

import org.meveo.commons.utils.ParamBean;

import com.google.gson.Gson;

/**
 * @author Wassim Drira
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
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
    
	public String getPropertiesAsJsonString() {
		ParamBean paramBean = paramBeanFactory.getInstance();
		Properties props = paramBean.getProperties();
		Gson gsonObj = new Gson();
		return gsonObj.toJson(props);
	}
    
}