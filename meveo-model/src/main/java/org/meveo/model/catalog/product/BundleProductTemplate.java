package org.meveo.model.catalog.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.ExportIdentifier;

/**
 * @author Edward P. Legaspi
 */
@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "CAT_BUNDLE_PRODUCT_TEMPLATE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_BUNDLE_PRODUCT_TEMPLATE_SEQ")
public class BundleProductTemplate {

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
