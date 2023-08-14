package org.meveo.model.cpq;

import static javax.persistence.EnumType.STRING;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.OfferTemplate;

@Entity
@Table(name = "offer_template_attribute")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "offer_template_attribute_seq"), })
@NamedQueries({
		@NamedQuery(name = "OfferTemplateAttribute.findByAttributeAndOfferTemplate", query = "FROM OfferTemplateAttribute ota where ota.attribute.id =:attributeId and ota.offerTemplate.id =:offerTemplateId")
})
public class OfferTemplateAttribute extends AttributeBaseEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -5934892816847168643L;
   
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "offer_template_id", nullable = false)
   @NotNull
   private OfferTemplate  offerTemplate;

   /**
    * sequence for Offer Template and attribute
    */
   @Column(name = "sequence")
   protected Integer sequence = 0;
   /**
    *
    */
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "attribute_id", nullable = false)
   @NotNull
   private Attribute attribute;
   
   
   @Column(name = "mandatorwith_el",length = 255)
   private String mandatoryWithEl;
   
   /**
	  * Mandatory
	  */
	 @Type(type = "numeric_boolean")
	 @Column(name = "mandatory")
	 @NotNull
	 private boolean mandatory=Boolean.FALSE;
	 /**
	 * Display
	 */
	@Type(type = "numeric_boolean")
	@Column(name = "display")
	@NotNull
	private boolean display;
	
	@Type(type = "numeric_boolean")
	@Column(name = "read_only")
	private Boolean readOnly = Boolean.FALSE;
	
	@Column(name = "default_value")
	private String defaultValue;
	
	@Column(name = "validation_type", length = 10)
	@Enumerated(STRING)
	private AttributeValidationType validationType;
	
	@Column(name = "validation_pattern", length = 2000)
	private String validationPattern;
	
	@Column(name = "validation_label")
	private String validationLabel;
   
   
   
   public OfferTemplateAttribute(){
   }

   /**
    * @return the attribute
    */
   public Attribute getAttribute() {
       return attribute;
   }
   /**
    * @param attribute the attribute to set
    */
   public void setAttribute(Attribute attribute) {
       this.attribute = attribute;
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
	/**
	 * @return the offerTemplate
	 */
	public OfferTemplate getOfferTemplate() {
		return offerTemplate;
	}
	/**
	 * @param offerTemplate the offerTemplate to set
	 */
	public void setOfferTemplate(OfferTemplate offerTemplate) {
		this.offerTemplate = offerTemplate;
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
	/**
	 * @return the readOnly
	 */
	public Boolean getReadOnly() {
		return readOnly;
	}
	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(Boolean readOnly) {
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

}
