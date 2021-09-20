package org.meveo.model.cpq;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.OfferTemplate;

@Entity
@Table(name = "offer_template_attribute")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "offer_template_attribute_seq"), })
public class OfferTemplateAttribute extends BaseEntity {    

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
   
   
   
   
   public OfferTemplateAttribute(){
   }
   public OfferTemplateAttribute(OfferTemplate offerTemplate, Attribute attribute, String mandatoryWithEl, Integer sequence) {
       super();
       this.offerTemplate = offerTemplate;
       this.attribute = attribute;
       this.mandatoryWithEl = mandatoryWithEl;
       this.sequence = sequence;
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

}
