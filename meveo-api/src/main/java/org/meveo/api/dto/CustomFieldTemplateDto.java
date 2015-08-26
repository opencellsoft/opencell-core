package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CustomFieldTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomFieldTemplateDto {

	@XmlAttribute(required = true)
	protected String code;

	@XmlAttribute(required = true)
	protected String description;

	@XmlElement(required = true)
	protected String fieldType;

	@XmlElement(required = true)
	protected String accountLevel;

	@XmlElement
	protected String defaultValue;

	@XmlElement(required = true)
	protected String storageType;

	@XmlElement
	protected String valueRequired;

	@XmlElement
	protected boolean versionable;

	@XmlElement
	protected boolean triggerEndPeriodEvent;

	@XmlElement
	protected String calendar;

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

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getAccountLevel() {
		return accountLevel;
	}

	public void setAccountLevel(String accountLevel) {
		this.accountLevel = accountLevel;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getStorageType() {
		return storageType;
	}

	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}

	public String getValueRequired() {
		return valueRequired;
	}

	public void setValueRequired(String valueRequired) {
		this.valueRequired = valueRequired;
	}

	public boolean isVersionable() {
		return versionable;
	}

	public void setVersionable(boolean versionable) {
		this.versionable = versionable;
	}

	public boolean isTriggerEndPeriodEvent() {
		return triggerEndPeriodEvent;
	}

	public void setTriggerEndPeriodEvent(boolean triggerEndPeriodEvent) {
		this.triggerEndPeriodEvent = triggerEndPeriodEvent;
	}

	public String getCalendar() {
		return calendar;
	}

	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}

}
