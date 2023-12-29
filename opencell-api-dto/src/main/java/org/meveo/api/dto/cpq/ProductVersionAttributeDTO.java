package org.meveo.api.dto.cpq;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeBaseEntity;
import org.meveo.model.cpq.AttributeValidationType;
import org.meveo.model.cpq.GroupedAttributes;
import org.meveo.model.cpq.ProductVersionAttribute;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.cpq.trade.CommercialRuleHeader;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Tarik FAKHOURI
 *
 */
public class ProductVersionAttributeDTO {
	
	@Schema(description = "attribute id")
	private Long id;
	
	@Deprecated(since = "V12")
	@Schema(description = "Code of attribute")
	private String code;
	
	@Schema(description = "description")
	private String description;
	
	@Schema(description = "description")
	private Boolean disabled;

	@Schema(description = "Corresponding to minimum one shot charge template code",
			example = "possible value are : INFO, LIST_TEXT, LIST_MULTIPLE_TEXT, LIST_NUMERIC, "
					+ "LIST_MULTIPLE_NUMERIC, TEXT, NUMERIC, INTEGER, DATE, CALENDAR, EMAIL, PHONE, TOTAL, COUNT, EXPRESSION_LANGUAGE")
	private AttributeTypeEnum attributeType;

	@Schema(description = "Corresponding to predefined allowed values")
	private List<String> allowedValues;

	@Schema(description = "diplay the attribute")
	private boolean display;

	@NotNull
	@Schema(description = "indicate if the attribute is mandatory")
	private boolean mandatory=Boolean.FALSE;

	@Schema(description = "indicate if the attribute is selectable")
	private boolean selectable=Boolean.TRUE;

	@Schema(description = "indicate if the attribute is ruled")
	private boolean ruled=Boolean.FALSE;

	@Schema(description = "number of decimal for attribute if the type of attribute is a NUMBER")
	private Integer unitNbDecimal = BaseEntity.NB_DECIMALS;

	@Schema(description = "indicate if the attribute is read only")
	private boolean readOnly = Boolean.FALSE;

	@Schema(description = "Code of attribute", required = true)
	@NotNull
	private String attributeCode;

    @Schema(description = "attribute order in the GUI")
    private Integer sequence = 0;

    @Schema(description = "Indicate if the attribute has a mandatory EL")
    private String mandatoryWithEl;

    @Schema(description = "default value for attribute")
    private String defaultValue;

	@Schema(description = "Validation type", example = "Possible value are: EL, REGEX")
	private AttributeValidationType validationType;

	@Schema(description = "Validation pattern")
	private String validationPattern;

	@Schema(description = "Validation label")
	private String validationLabel;
	@Schema(description = "list of commercial rule code", example = "commercialRuleCodes : [CODE_1, CODE_2,..]")
	private List<String> commercialRuleCodes=new ArrayList<>();

	@Schema(description = "replaced value")
	private Object assignedValue;

	 
    @Schema(description = "list of charge template code", example = "chargeTemplateCodes : [CODE_1, CODE_2,..]")
    private List<String> chargeTemplateCodes = new ArrayList<>();
	 /**
     * The lower number, the higher the priority is
     */
    @Schema(description = "The lower number, the higher the priority is")
    private Integer priority ;
    
    
    /** The media codes. */ 
    @Schema(description = "list of media code", example = "mediaCodes : [CODE_1, CODE_2,..]")
    private Set<String> mediaCodes = new HashSet<>();
    
    /** The tags */ 
    @Schema(description = "list of tag code", example = "tags : [CODE_1, CODE_2,..]")
    private List<String> tagCodes=new ArrayList<>();
     
    @Schema(description = "list of assigned attribute code", example = "assignedAttributeCodes : [CODE_1, CODE_2,..]")
    private List<String> assignedAttributeCodes=new ArrayList<>();

    @Schema(description = "list of custom field associated to attribute")
    private CustomFieldsDto customFields;

    @Schema(description = "grouped attributes")
	private List<GroupedAttributeDto> groupedAttributes;
    
    public ProductVersionAttributeDTO() {
        super();
    }
    public ProductVersionAttributeDTO(AttributeBaseEntity attributebaseEntity) {
    	new ProductVersionAttributeDTO(attributebaseEntity,true);
    	
    }
    public ProductVersionAttributeDTO(AttributeBaseEntity attributebaseEntity, boolean loadNestedEntities) {
    	Attribute attribute=attributebaseEntity.getAttribute();
        if(attribute != null) {
        this.id=attribute.getId();
        this.code=attribute.getCode();
        this.description=attribute.getDescription();
        this.disabled=attribute.isDisabled();
        this.attributeType=attribute.getAttributeType(); 
		this.allowedValues = attribute.getAllowedValues();
		this.priority=attribute.getPriority();
		
        this.display=attributebaseEntity.isDisplay();
        this.mandatory = attributebaseEntity.isMandatory();
        this.unitNbDecimal=attribute.getUnitNbDecimal();
        this.readOnly=attributebaseEntity.getReadOnly();
        this.validationType = attributebaseEntity.getValidationType();
        this.validationPattern = attributebaseEntity.getValidationPattern();
        this.validationLabel = attributebaseEntity.getValidationLabel();
        if(loadNestedEntities) {
        	if (!attribute.getChargeTemplates().isEmpty()) {
            	for (ChargeTemplate charge:attribute.getChargeTemplates()) {
            		this.chargeTemplateCodes.add(charge.getCode());
            	}
            }
            if (!attribute.getCommercialRules().isEmpty()) {
            	for (CommercialRuleHeader rule:attribute.getCommercialRules()) {
            		this.commercialRuleCodes.add(rule.getCode());
            	}
            }   
            if(!attribute.getMedias().isEmpty()){
    			this.mediaCodes = attribute.getMedias().stream()
    								.map(tag -> tag.getCode())
    								.collect(Collectors.toSet());
    		} 
            if(!attribute.getTags().isEmpty()){
    			this.tagCodes = attribute.getTags().stream()
    								.map(tag -> tag.getCode())
    								.collect(Collectors.toList());
    		}
            if (!attribute.getAssignedAttributes().isEmpty()) {
            	for (Attribute attr:attribute.getAssignedAttributes()) {
            		assignedAttributeCodes.add(attr.getCode());
            	}
            }
        	if(attribute.getGroupedAttributes()!=null && !attribute.getGroupedAttributes().isEmpty()){
    			this.groupedAttributes = attribute.getGroupedAttributes().stream()
    					.map(ga -> new GroupedAttributeDto(ga))
    					.collect(Collectors.toList());
    		}
        }
        
        
        this.attributeCode = attributebaseEntity.getAttribute().getCode();
        this.sequence = attributebaseEntity.getSequence();
        this.mandatoryWithEl = attributebaseEntity.getMandatoryWithEl(); 
        this.defaultValue = attributebaseEntity.getDefaultValue();  
        }
    }
    
    public ProductVersionAttributeDTO(Attribute attribute,List<GroupedAttributeDto> groupedAttributes) {
    	this.groupedAttributes=groupedAttributes;
        if(attribute != null) {
        this.id=attribute.getId();
        this.code=attribute.getCode();
        this.description=attribute.getDescription();
        this.disabled=attribute.isDisabled();
        this.attributeType=attribute.getAttributeType(); 
		this.allowedValues = attribute.getAllowedValues();
        if (!attribute.getChargeTemplates().isEmpty()) {
        	for (ChargeTemplate charge:attribute.getChargeTemplates()) {
        		this.chargeTemplateCodes.add(charge.getCode());
        	}
        }
        if (!attribute.getCommercialRules().isEmpty()) {
        	for (CommercialRuleHeader rule:attribute.getCommercialRules()) {
        		this.commercialRuleCodes.add(rule.getCode());
        	}
        }   
        if(!attribute.getMedias().isEmpty()){
			this.mediaCodes = attribute.getMedias().stream()
								.map(tag -> tag.getCode())
								.collect(Collectors.toSet());
		} 
        if(!attribute.getTags().isEmpty()){
			this.tagCodes = attribute.getTags().stream()
								.map(tag -> tag.getCode())
								.collect(Collectors.toList());
		}
        if (!attribute.getAssignedAttributes().isEmpty()) {
        	for (Attribute attr:attribute.getAssignedAttributes()) {
        		assignedAttributeCodes.add(attr.getCode());
        	}
        }
    	if(attribute.getGroupedAttributes()!=null && !attribute.getGroupedAttributes().isEmpty()){
			this.groupedAttributes = attribute.getGroupedAttributes().stream()
					.map(ga -> new GroupedAttributeDto(ga))
					.collect(Collectors.toList());
		}
        
        this.attributeCode = attribute.getCode();  
        }
    }
    /**
     * @return the sequence
     */
    public Integer getSequence() {
        return sequence;
    }
    /**
     * @param sequence the sequence to set
     */
    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
	/**
	 * @return the mandatoryWithEl
	 */
	public String getMandatoryWithEl() {
		return mandatoryWithEl;
	}
	/**
	 * @param mandatoryWithEl the mandatoryWithEl to set
	 */
	public void setMandatoryWithEl(String mandatoryWithEl) {
		this.mandatoryWithEl = mandatoryWithEl;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductVersionAttributeDTO other = (ProductVersionAttributeDTO) obj;
		return Objects.equals(attributeCode, other.attributeCode)
				&& Objects.equals(mandatoryWithEl, other.mandatoryWithEl) && Objects.equals(sequence, other.sequence);
	}

    @Override
	public int hashCode() {
		return Objects.hash(attributeCode, mandatoryWithEl, sequence);
	}
	/**
	 * @return the attributeCode
	 */
	public String getAttributeCode() {
		return attributeCode;
	}
	/**
	 * @param attributeCode the attributeCode to set
	 */
	public void setAttributeCode(String attributeCode) {
		this.attributeCode = attributeCode;
	}
	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}
	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	/**
	 * @return the validationType
	 */
	public AttributeValidationType getValidationType() {
		return validationType;
	}
	/**
	 * @param validationType the validationType to set
	 */
	public void setValidationType(AttributeValidationType validationType) {
		this.validationType = validationType;
	}
	/**
	 * @return the validationPattern
	 */
	public String getValidationPattern() {
		return validationPattern;
	}
	/**
	 * @param validationPattern the validationPattern to set
	 */
	public void setValidationPattern(String validationPattern) {
		this.validationPattern = validationPattern;
	}
	/**
	 * @return the validationLabel
	 */
	public String getValidationLabel() {
		return validationLabel;
	}
	/**
	 * @param validationLabel the validationLabel to set
	 */
	public void setValidationLabel(String validationLabel) {
		this.validationLabel = validationLabel;
	}
	/**
	 * @return the mandatory
	 */
	public boolean isMandatory() {
		return mandatory;
	}
	/**
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	/**
	 * @return the display
	 */
	public boolean isDisplay() {
		return display;
	}
	/**
	 * @param display the display to set
	 */
	public void setDisplay(boolean display) {
		this.display = display;
	}
	public boolean isRuled() {
		return ruled;
	}
	public void setRuled(boolean ruled) {
		this.ruled = ruled;
	}
	public List<String> getCommercialRuleCodes() {
		return commercialRuleCodes;
	}
	public void setCommercialRuleCodes(List<String> commercialRuleCodes) {
		this.commercialRuleCodes = commercialRuleCodes;
	}
	public Object getAssignedValue() {
		return assignedValue;
	}
	public void setAssignedValue(Object assignedValue) {
		this.assignedValue = assignedValue;
	}
	public boolean isSelectable() {
		return selectable;
	}
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public Boolean getDisabled() {
		return disabled;
	}
	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
	public AttributeTypeEnum getAttributeType() {
		return attributeType;
	}
	public void setAttributeType(AttributeTypeEnum attributeType) {
		this.attributeType = attributeType;
	}
	public List<String> getAllowedValues() {
		return allowedValues;
	}
	public void setAllowedValues(List<String> allowedValues) {
		this.allowedValues = allowedValues;
	}
	public Integer getUnitNbDecimal() {
		return unitNbDecimal;
	}
	public void setUnitNbDecimal(Integer unitNbDecimal) {
		this.unitNbDecimal = unitNbDecimal;
	}
	public List<String> getChargeTemplateCodes() {
		return chargeTemplateCodes;
	}
	public void setChargeTemplateCodes(List<String> chargeTemplateCodes) {
		this.chargeTemplateCodes = chargeTemplateCodes;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public Set<String> getMediaCodes() {
		return mediaCodes;
	}
	public void setMediaCodes(Set<String> mediaCodes) {
		this.mediaCodes = mediaCodes;
	}
	public List<String> getTagCodes() {
		return tagCodes;
	}
	public void setTagCodes(List<String> tagCodes) {
		this.tagCodes = tagCodes;
	}
	public List<String> getAssignedAttributeCodes() {
		return assignedAttributeCodes;
	}
	public void setAssignedAttributeCodes(List<String> assignedAttributeCodes) {
		this.assignedAttributeCodes = assignedAttributeCodes;
	}
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}
	public List<GroupedAttributeDto> getGroupedAttributes() {
		return groupedAttributes;
	}
	public void setGroupedAttributes(List<GroupedAttributeDto> groupedAttributes) {
		this.groupedAttributes = groupedAttributes;
	}
	
	
	
}