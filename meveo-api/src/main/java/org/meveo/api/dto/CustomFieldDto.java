package org.meveo.api.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CustomField")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomFieldDto {

	@XmlAttribute(required = true)
	protected String code;

	@XmlAttribute
	protected String description;

	@XmlElement
	protected String stringValue;

	@XmlElement
	protected Date dateValue;

	@XmlElement
	protected Long longValue;

	@XmlElement
	protected Double doubleValue;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Long getLongValue() {
		return longValue;
	}

	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	@Override
	public String toString() {
		return "CustomFieldDto [code=" + code + ", description=" + description + ", stringValue=" + stringValue
				+ ", dateValue=" + dateValue + ", longValue=" + longValue + ", doubleValue=" + doubleValue + "]";
	}

}
