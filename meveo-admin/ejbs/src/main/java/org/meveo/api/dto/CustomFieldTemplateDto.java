package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.crm.CustomFieldTemplate;

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
	protected boolean valueRequired;

	@XmlElement
	protected boolean versionable;

	@XmlElement
	protected boolean triggerEndPeriodEvent;

	@XmlElement
	protected String calendar;
	
	@XmlElement
	protected String entityClazz;
	
	public CustomFieldTemplateDto() {
		
	}

	public CustomFieldTemplateDto(CustomFieldTemplate cf) {
		code = cf.getCode();
		description = cf.getDescription();
		fieldType = cf.getFieldType().name();
		accountLevel = cf.getAccountLevel().name();
		defaultValue = cf.getDefaultValue();
		storageType = cf.getStorageType().name();
		valueRequired = cf.isValueRequired();
		versionable = cf.isVersionable();
		triggerEndPeriodEvent = cf.isTriggerEndPeriodEvent();
		entityClazz = cf.getEntityClazz();
		if (cf.getCalendar() != null) {
			calendar = cf.getCalendar().getCode();
		}
	}

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

	public boolean isValueRequired() {
		return valueRequired;
	}

	public void setValueRequired(boolean valueRequired) {
		this.valueRequired = valueRequired;
	}

	@Override
	public String toString() {
		return "CustomFieldTemplateDto [code=" + code + ", description=" + description + ", fieldType=" + fieldType
				+ ", accountLevel=" + accountLevel + ", defaultValue=" + defaultValue + ", storageType=" + storageType
				+ ", valueRequired=" + valueRequired + ", versionable=" + versionable + ", triggerEndPeriodEvent="
				+ triggerEndPeriodEvent + ", calendar=" + calendar + ", entityClazz=" + entityClazz + "]";
	}

	public String getEntityClazz() {
		return entityClazz;
	}

	public void setEntityClazz(String entityClazz) {
		this.entityClazz = entityClazz;
	}

}
