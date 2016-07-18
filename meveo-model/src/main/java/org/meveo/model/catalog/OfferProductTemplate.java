package org.meveo.model.catalog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;

/**
 * @author Edward P. Legaspi
 */
@Entity
@Table(name = "CAT_OFFER_PRODUCT_TEMPLATE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_OFFER_PRODUCT_TEMPLATE_SEQ")
public class OfferProductTemplate extends BaseEntity {

	private static final long serialVersionUID = 5819613762779252418L;

	@ManyToOne(optional = false)
	@JoinColumn(name = "OFFER_TEMPLATE_ID")
	private OfferTemplate offerTemplate;

	@ManyToOne(optional = false)
	@JoinColumn(name = "PRODUCT_TEMPLATE_ID")
	private ProductTemplate productTemplate;

	@Column(name = "MANDATORY")
	private boolean mandatory;

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
