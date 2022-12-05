/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api;

import java.util.List;
import java.util.Properties;

import jakarta.ejb.Stateless;

import org.meveo.api.dto.PropertiesDto;
import org.meveo.api.dto.PropertyDto;
import org.meveo.commons.utils.ParamBean;

import com.google.gson.Gson;
import org.meveo.service.crm.impl.CustomFieldTemplateService;

/**
 * @author Wassim Drira
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Stateless
public class ConfigurationApi extends BaseApi {

    /**
     * Set configuration/settings property
     *
     * @param property Property key
     * @param value    Property value as string
     */
    public void setProperty(String property, String value) {
        ParamBean paramBean = paramBeanFactory.getInstance();
        paramBean.setProperty(property, value);
        paramBean.saveProperties();

        if (property.equals("cache.cacheCFT")) {
            if (value.equals("false")) {
                CustomFieldTemplateService.setCacheCFTAsFalse();
            }
            else if (value.equals("true")) {
                CustomFieldTemplateService.setCacheCFTAsTrue();
            }
        }
    }

    public void setProperties(List<PropertyDto> properties) {
        ParamBean paramBean = paramBeanFactory.getInstance();
        for (PropertyDto property : properties) {
            paramBean.setProperty(property.getKey(), property.getValue());
        }
        paramBean.saveProperties();
    }

    public String getPropertiesAsJsonString() {
        ParamBean paramBean = paramBeanFactory.getInstance();
        Properties props = paramBean.getProperties();
        Gson gsonObj = new Gson();
        return gsonObj.toJson(props);
    }

    public List<PropertyDto> getProperties() {
        ParamBean paramBean = paramBeanFactory.getInstance();
        PropertiesDto properties = new PropertiesDto(paramBean.getProperties());
        return properties.getProperties();
    }

}