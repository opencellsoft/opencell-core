package org.meveo.model.scripts;

import java.util.Map;
import java.util.Objects;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BaseEntity;

@Entity
@Table(name = "meveo_script_parameter")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "meveo_script_parameter_seq"), })
@Cacheable
public class ScriptParameter extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "code", length = 255, nullable = false)
    @Size(max = 255)
    private String code;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "script_instance_id", nullable = false)
	@NotNull
	private ScriptInstance scriptInstance;
	
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "jsonb")
    private Map<String, String> descriptionI18n;
    
    @Column(name = "class_name", length = 255, nullable = false)
    @Size(max = 255)
    @NotNull
    private String className = "java.lang.String";
    
    @Column(name = "default_value", length = 255)
    @Size(max = 255)
    private String defaultValue;
    
	@Type(type = "numeric_boolean")
	@Column(name = "mandatory")
	private boolean mandatory = Boolean.FALSE;
	
    @Column(name = "allowed_values", length = 255)
    @Size(max = 255)
    private String allowedValues;
    
    @Column(name = "values_separator", length = 20)
    @Size(max = 20)
    private String valuesSeparator = "\\|";
    
    @Type(type = "numeric_boolean")
	@Column(name = "collection")
	private boolean collection = Boolean.FALSE;
    
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ScriptInstance getScriptInstance() {
		return scriptInstance;
	}

	public void setScriptInstance(ScriptInstance scriptInstance) {
		this.scriptInstance = scriptInstance;
	}

	public Map<String, String> getDescriptionI18n() {
		return descriptionI18n;
	}

	public void setDescriptionI18n(Map<String, String> descriptionI18n) {
		this.descriptionI18n = descriptionI18n;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getAllowedValues() {
		return allowedValues;
	}

	public void setAllowedValues(String allowedValues) {
		this.allowedValues = allowedValues;
	}

	public String getValuesSeparator() {
		return valuesSeparator;
	}

	public void setValuesSeparator(String valuesSeparator) {
		this.valuesSeparator = valuesSeparator;
	}

	public boolean isCollection() {
		return collection;
	}

	public void setCollection(boolean collection) {
		this.collection = collection;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(id, allowedValues, className, code, collection, defaultValue,
				descriptionI18n, mandatory, scriptInstance, valuesSeparator);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScriptParameter other = (ScriptParameter) obj;
		return Objects.equals(id, other.id) && Objects.equals(allowedValues, other.allowedValues) 
				&& Objects.equals(className, other.className)
				&& Objects.equals(code, other.code) && collection == other.collection
				&& Objects.equals(defaultValue, other.defaultValue)
				&& Objects.equals(descriptionI18n, other.descriptionI18n) && mandatory == other.mandatory
				&& Objects.equals(scriptInstance, other.scriptInstance)
				&& Objects.equals(valuesSeparator, other.valuesSeparator);
	}

}
