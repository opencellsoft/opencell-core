package org.meveo.model.catalog;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.IEntity;

/**
 * @author Edward P. Legaspi
 */
@Entity
@Table(name = "CAT_OFFER_PRODUCT_TEMPLATE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_OFFER_PRODUCT_TEMPLATE_SEQ")
public class OfferProductTemplate implements IEntity {

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	@Column(name = "ID")
	@Access(AccessType.PROPERTY)
	protected Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "OFFER_TEMPLATE_ID")
	private OfferTemplate offerTemplate;

	@ManyToOne(optional = false)
	@JoinColumn(name = "PRODUCT_TEMPLATE_ID")
	private ProductTemplate productTemplate;

	@Column(name = "MANDATORY")
	private boolean mandatory;

	@Override
	public Serializable getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean isTransient() {
		return id == null;
	}

	public OfferTemplate getOfferTemplate() {
		return offerTemplate;
	}

	public void setOfferTemplate(OfferTemplate offerTemplate) {
		this.offerTemplate = offerTemplate;
	}

	public ProductTemplate getProductTemplate() {
		return productTemplate;
	}

	public void setProductTemplate(ProductTemplate productTemplate) {
		this.productTemplate = productTemplate;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	@Override
	public int hashCode() {
		if (id != null){
			return id.intValue();
		}
		final int prime = 31;
		int result = prime * 1; // super.hashCode();
		result = prime * result + ((offerTemplate == null) ? 0 : offerTemplate.getId().hashCode());
		result = prime * result + ((productTemplate == null) ? 0 : productTemplate.getId().hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		} else if (!(obj instanceof OfferProductTemplate)) {
			return false;
		}

		OfferProductTemplate that = (OfferProductTemplate) obj;

		ProductTemplate thatProductTemplate = that.getProductTemplate();
		if (productTemplate == null) {
			if (thatProductTemplate != null) {
				return false;
			}
		} else if (!productTemplate.equals(thatProductTemplate)) {
			return false;
		}

		OfferTemplate thatOfferTemplate = that.getOfferTemplate();
		if (offerTemplate == null) {
			if (thatOfferTemplate != null) {
				return false;
			}
		} else if (!offerTemplate.equals(thatOfferTemplate)) {
			return false;
		}

		return true;
	}

}
