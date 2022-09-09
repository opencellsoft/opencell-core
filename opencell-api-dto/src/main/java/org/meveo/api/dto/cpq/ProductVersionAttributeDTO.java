package org.meveo.api.dto.cpq;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.meveo.model.cpq.AttributeValidationType;
import org.meveo.model.cpq.ProductVersionAttribute;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Tarik FAKHOURI
 *
 */
public class ProductVersionAttributeDTO {
	
    @Schema(description = "Code of attribute", required = true)
    @NotNull
    private String attributeCode;
    
    @Schema(description = "attribute order in the GUI")
    private Integer sequence = 0;

    @Schema(description = "Indicate if the attribute has a mandatory EL")
    private String mandatoryWithEl;

    @Schema(description = "indicate if the attribute is read only")
    protected boolean readOnly = Boolean.FALSE;
    
    @Schema(description = "default value for attribute")
    protected String defaultValue;

	@Schema(description = "Validation type", example = "Possible value are: EL, REGEX")
	protected AttributeValidationType validationType;

	@Schema(description = "Validation pattern")
	protected String validationPattern;

	@Schema(description = "Validation label")
	protected String validationLabel;
    /**
     * Mandatory
     */
    @NotNull
    @Schema(description = "indicate if the attribute is mandatory")
    protected boolean mandatory=Boolean.FALSE;
	  /**
     * Display
     */
    @Schema(description = "diplay the attribute")
    protected boolean display;
    
    @Schema(description = "indicate if the attribute is ruled")
    private boolean ruled=Boolean.FALSE;
  
    @Schema(description = "list of commercial rule header codes", example = "commercialRuleCodes : [CODE_1, CODE_2,..]")
    protected List<String> commercialRuleCodes=new ArrayList<>();
    
    @Schema(description = "replaced value")
    protected Object assignedValue;
    
    @Schema(description = "indicate if the attribute is selectable")
    protected boolean selectable=Boolean.TRUE;
    
    public ProductVersionAttributeDTO() {
        super();
    }
    public ProductVersionAttributeDTO(ProductVersionAttribute pva) {
        
        if(pva.getAttribute() != null)
        	this.attributeCode = pva.getAttribute().getCode();
        this.sequence = pva.getSequence();
        this.mandatoryWithEl = pva.getMandatoryWithEl();
        mandatory=pva.isMandatory();
        display=pva.isDisplay();
        readOnly = pva.getReadOnly();
        defaultValue = pva.getDefaultValue();
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
	
	
}