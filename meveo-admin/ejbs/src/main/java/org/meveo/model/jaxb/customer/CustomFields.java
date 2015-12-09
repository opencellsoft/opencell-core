package org.meveo.model.jaxb.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.model.crm.CustomFieldInstance;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "customField" })
@XmlRootElement(name = "customFields")
public class CustomFields {
    protected List<CustomField> customField;

    public CustomFields() {

    }

    public static CustomFields toDTO(Map<String, List<CustomFieldInstance>> customFields) {
        if (customFields == null || customFields.isEmpty()) {
            return null;
        }
        CustomFields dto = new CustomFields();
        for (List<CustomFieldInstance> cfis : customFields.values()) {
            for (CustomFieldInstance cfi : cfis) {
                dto.getCustomField().add(CustomField.toDTO(cfi));
            }
        }
        return dto;
    }

    public List<CustomField> getCustomField() {
        if (customField == null) {
            customField = new ArrayList<CustomField>();
        }

        return customField;
    }

    public void setCustomField(List<CustomField> customField) {
        this.customField = customField;
    }

    public CustomField getCF(String code) {
        for (CustomField cf : getCustomField()) {
            if (cf.getCode().equals(code)) {
                return cf;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "CustomFields [customField=" + customField + "]";
    }
}
