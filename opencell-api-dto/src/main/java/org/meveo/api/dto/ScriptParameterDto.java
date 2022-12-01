package org.meveo.api.dto;

import static org.meveo.api.dto.LanguageDescriptionDto.convertMultiLanguageFromMapOfValues;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.scripts.ScriptParameter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "ScriptParameter")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScriptParameterDto extends BaseEntityDto {
	
	private static final long serialVersionUID = 1L;
	
	@Schema(description = "The code parameter")
    private String code;
 
    @Schema(description = "The class name")
    private String className;
    
    @Schema(description = "The default value")
    private String defaultValue;
    
    @Schema(description = "Field mandatory")
    private Boolean mandatory;
	
    @Schema(description = "The allowed values")
    private String allowedValues;
    
    @Schema(description = "The values separator")
    private String valuesSeparator;
    
    @Schema(description = "Field collection")
    private Boolean collection;
	
	private List<LanguageDescriptionDto> languageDescriptions;

	public ScriptParameterDto() {
	}
	
	 /**
     * Instantiates a new script parameter dto from scriptParameter entity
     */
	public ScriptParameterDto (ScriptParameter scriptParameter) {
        super();
        code = scriptParameter.getCode();
        className = scriptParameter.getClassName();
        defaultValue = scriptParameter.getDefaultValue();
        mandatory = scriptParameter.isMandatory();
        allowedValues = scriptParameter.getAllowedValues();
        valuesSeparator = scriptParameter.getValuesSeparator();
        collection = scriptParameter.isCollection();
        languageDescriptions = convertMultiLanguageFromMapOfValues(scriptParameter.getDescriptionI18n());
    }
	
	public ScriptParameter mapToEntity() {
		ScriptParameter scriptParameter = new ScriptParameter();
		scriptParameter.setCode(code);
		scriptParameter.setClassName(className);
		scriptParameter.setAllowedValues(allowedValues);
		scriptParameter.setDefaultValue(defaultValue);
		scriptParameter.setMandatory(mandatory);
		scriptParameter.setValuesSeparator(StringUtils.isBlank(valuesSeparator)? "\\|" : valuesSeparator);
		scriptParameter.setCollection(collection);
		scriptParameter.setDescriptionI18n(languageDescriptions.stream().collect(Collectors.toMap(LanguageDescriptionDto::getLanguageCode, LanguageDescriptionDto::getDescription)));
		return scriptParameter;
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

	public Boolean getMandatory() {
		return mandatory;
	}

	public void setMandatory(Boolean mandatory) {
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

	public Boolean getCollection() {
		return collection;
	}

	public void setCollection(Boolean collection) {
		this.collection = collection;
	}

	public List<LanguageDescriptionDto> getLanguageDescriptions() {
		return languageDescriptions;
	}

	public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
		this.languageDescriptions = languageDescriptions;
	}
	
}
