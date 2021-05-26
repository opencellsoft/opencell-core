package org.meveo.api.dto.cpq.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.cpq.enums.AttributeTypeEnum;

@XmlAccessorType(XmlAccessType.FIELD)
public class Attribute {

    @XmlAttribute
	private String code;
    @XmlAttribute
	private String stringValue;
    private Date dateValue;
    private Double doubleValue;
    private AttributeTypeEnum attributeType;
    private CustomFieldsDto customFields;
    
    
    public Attribute(org.meveo.model.cpq.QuoteAttribute attribute,CustomFieldsDto customFields) {
    	this.code = attribute.getAttribute().getCode();
    	this.stringValue = attribute.getStringValue();
    	this.dateValue=attribute.getDateValue();
    	this.doubleValue=attribute.getDoubleValue();
    	this.attributeType=attribute.getAttribute().getAttributeType();
    	this.customFields=customFields;
    }
    
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	
	

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public AttributeTypeEnum getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(AttributeTypeEnum attributeType) {
		this.attributeType = attributeType;
	}

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

	
    
}
