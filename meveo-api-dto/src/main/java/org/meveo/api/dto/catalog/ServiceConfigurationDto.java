package org.meveo.api.dto.catalog;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ServiceConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceConfigurationDto {

	@NotNull
	@XmlAttribute
	private String code;

	@XmlAttribute
	private String description;

	@XmlElementWrapper(name = "parameters")
	@XmlElement(name = "parameter")
	private CustomFieldsDto serviceCustomFields;

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

	@Override
	public String toString() {
		return "ServiceCodeDto [code=" + code + ", description=" + description + ", serviceCustomFields=" + serviceCustomFields + "]";
	}

	public CustomFieldsDto getServiceCustomFields() {
		return serviceCustomFields;
	}

	public void setServiceCustomFields(CustomFieldsDto serviceCustomFields) {
		this.serviceCustomFields = serviceCustomFields;
	}

}
