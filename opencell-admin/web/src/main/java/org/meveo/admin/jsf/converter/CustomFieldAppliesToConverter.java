package org.meveo.admin.jsf.converter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.custom.CustomizedEntity;
import org.meveo.service.custom.CustomizedEntityService;
import org.meveo.util.EntityCustomizationUtils;

/**
 * Converter to convert customFieldTemplate.appliesTo value to a humanized form and back
 * 
 * @author Andrius Karpavicius
 **/
@FacesConverter("customFieldAppliesToConverter")
@ViewScoped
public class CustomFieldAppliesToConverter implements Converter, Serializable {

    private static final long serialVersionUID = -7175173363564310863L;

    @Inject
    private CustomizedEntityService customizedEntityService;

    private Map<String, String> appliesToMap = new HashMap<String, String>();

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

        String stringValue = appliesToMap.get(obj.toString());
        // if (stringValue == null) {
        // } else {
        // }
        return stringValue;
    }

    private void loadAppliesToDefinitions() {

        appliesToMap = new HashMap<String, String>();

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