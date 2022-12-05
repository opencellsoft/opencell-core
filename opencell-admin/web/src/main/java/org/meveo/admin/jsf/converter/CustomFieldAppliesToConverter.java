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

package org.meveo.admin.jsf.converter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;

import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.custom.CustomizedEntity;
import org.meveo.service.custom.CustomizedEntityService;
import org.meveo.util.EntityCustomizationUtils;

/**
 * Converter to convert customFieldTemplate.appliesTo value to a humanized form and back
 * 
 * @author Andrius Karpavicius
 **/
@FacesConverter(value = "customFieldAppliesToConverter", managed = true)
@ViewScoped
public class CustomFieldAppliesToConverter implements Converter, Serializable {

    private static final long serialVersionUID = -7175173363564310863L;

    @Inject
    private CustomizedEntityService customizedEntityService;

    private Map<String, String> appliesToMap = new HashMap<>();

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        if (!appliesToMap.containsValue(value)) {
            loadAppliesToDefinitions();
        }

        for (Entry<String, String> entry : appliesToMap.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }

        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object obj) {

        if (obj == null || obj.toString().length() == 0) {
            return "";
        }
        // Misunderstanding in value conversion - we already have a converted value to String
        if (appliesToMap.containsValue(obj.toString())) {
            return obj.toString();
        }

        if (!appliesToMap.containsKey(obj.toString())) {
            loadAppliesToDefinitions();
        }

        return appliesToMap.get(obj.toString());
    }

    private void loadAppliesToDefinitions() {

        appliesToMap = new HashMap<>();

        List<CustomizedEntity> entities = customizedEntityService.getCustomizedEntities(null, false, true, true, null, null);

        for (CustomizedEntity customizedEntity : entities) {

            if (customizedEntity.isStandardEntity()) {
                appliesToMap.put(EntityCustomizationUtils.getAppliesTo(customizedEntity.getEntityClass(), null), customizedEntity.getClassnameToDisplayHuman());
            } else {
                appliesToMap.put(EntityCustomizationUtils.getAppliesTo(CustomEntityTemplate.class, customizedEntity.getEntityCode()),
                    customizedEntity.getClassnameToDisplayHuman());
            }
        }
    }
}