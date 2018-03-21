package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CustomFields")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomFieldsDto implements Serializable {

    private static final long serialVersionUID = 7751924530575980282L;

    private List<CustomFieldDto> customField;
    private List<CustomFieldDto> inheritedCustomField;

    public CustomFieldsDto() {

    }

    public List<CustomFieldDto> getCustomField() {
        if (customField == null) {
            customField = new ArrayList<CustomFieldDto>();
        }

        return customField;
    }

    public void setCustomField(List<CustomFieldDto> customField) {
        this.customField = customField;
    }

    public CustomFieldDto getCF(String code) {
        for (CustomFieldDto cf : getCustomField()) {
            if (cf.getCode().equals(code)) {
                return cf;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "CustomFieldsDto [customField=" + customField + "]";
    }

    public boolean isEmpty() {
        return (customField == null || customField.isEmpty()) && (inheritedCustomField == null || inheritedCustomField.isEmpty());
    }

	public List<CustomFieldDto> getInheritedCustomField() {
		if (inheritedCustomField == null) {
			inheritedCustomField = new ArrayList<CustomFieldDto>();
		}

		return inheritedCustomField;
	}

	public void setInheritedCustomField(List<CustomFieldDto> inheritedCustomField) {
		this.inheritedCustomField = inheritedCustomField;
	}

}
