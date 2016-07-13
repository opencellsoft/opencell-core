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
@Table(name = "CAT_BUNDLE_PRODUCT_TEMPLATE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_BUNDLE_PRODUCT_TEMPLATE_SEQ")
public class BundleProductTemplate extends BaseEntity {

	private static final long serialVersionUID = -7043079148076022783L;

	@ManyToOne
	@JoinColumn(name = "PRODUCT_TEMPLATE_ID")
	private ProductTemplate productTemplate;

	@ManyToOne
	@JoinColumn(name = "BUNDLE_TEMPLATE_ID")
	private BundleTemplate bundleTemplate;

	@Column(name = "QUANTITY")
	private int quantity;

	public ProductTemplate getProductTemplate() {
		return productTemplate;
	}

	public void setProductTemplate(ProductTemplate productTemplate) {
		this.productTemplate = productTemplate;
	}

	public BundleTemplate getBundleTemplate() {
		return bundleTemplate;
	}

	public void setBundleTemplate(BundleTemplate bundleTemplate) {
		this.bundleTemplate = bundleTemplate;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
