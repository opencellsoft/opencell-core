package org.meveo.model.cpq;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.catalog.OfferTemplate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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

   public OfferTemplateAttribute(){
	   super();
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
