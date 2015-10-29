package org.meveo.model.jaxb.customer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.model.crm.CustomFieldFields;
import org.meveo.model.crm.CustomFieldInstance;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}customerField" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "customField" })
@XmlRootElement(name = "customFields")
public class CustomFields {
    protected List<CustomField> customField;

    public CustomFields() {

    }

    public static CustomFields toDTO(CustomFieldFields cff) {
        if (cff == null || cff.getCustomFields() == null || cff.getCustomFields().isEmpty()) {
            return null;
        }
        CustomFields dto = new CustomFields();
        for (CustomFieldInstance cfi : cff.getCustomFields().values()) {
            dto.getCustomField().addAll(CustomField.toDTO(cfi));
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
