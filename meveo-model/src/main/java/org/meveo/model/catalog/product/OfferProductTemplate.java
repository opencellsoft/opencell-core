package org.meveo.model.catalog.product;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.catalog.OfferTemplate;

/**
 * @author Edward P. Legaspi
 */
@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "CAT_OFFER_PRODUCT_TEMPLATE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_OFFER_PRODUCT_TEMPLATE_SEQ")
public class OfferProductTemplate extends BaseEntity {

	private static final long serialVersionUID = 5819613762779252418L;

	@ManyToOne(optional = false)
	@JoinColumn(name = "OFFER_TEMPLATE_ID")
	private OfferTemplate offerTemplate;

	@ManyToOne(optional = false)
	@JoinColumn(name = "PRODUCT_TEMPLATE_ID")
	private ProductTemplate productTemplate;

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

}
