package org.meveo.api.dto.cpq;


import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.meveo.model.cpq.OfferTemplateAttribute;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Tarik FAKHOURI
 *
 */
public class OfferTemplateAttributeDTO {
	
    @Schema(description = "Code of attribute", required = true)
    @NotNull
    private String attributeCode;
    
    @Schema(description = "attribute order in the GUI")
    private Integer sequence = 0;

    @Schema(description = "Indicate if the attribute has a mandatory EL")
    private String mandatoryWithEl;
    
    
    public OfferTemplateAttributeDTO() {
        super();
    }
    public OfferTemplateAttributeDTO(OfferTemplateAttribute offerTemplateAttribute) {
        if(offerTemplateAttribute.getAttribute() != null)
        	this.attributeCode = offerTemplateAttribute.getAttribute().getCode();
        this.sequence = offerTemplateAttribute.getSequence();
        this.mandatoryWithEl = offerTemplateAttribute.getMandatoryWithEl();
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
		OfferTemplateAttributeDTO other = (OfferTemplateAttributeDTO) obj;
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
}