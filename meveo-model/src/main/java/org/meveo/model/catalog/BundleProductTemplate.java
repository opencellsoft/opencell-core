package org.meveo.model.catalog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.meveo.model.BaseEntity;

/**
 * @author Edward P. Legaspi
 */
@Entity
@Table(name = "CAT_BUNDLE_PRODUCT_TEMPLATE", uniqueConstraints = @UniqueConstraint(columnNames = { "PRODUCT_TEMPLATE_ID", "BUNDLE_TEMPLATE_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_BUNDLE_PRODUCT_TEMPLATE_SEQ")
public class BundleProductTemplate extends BaseEntity {

	private static final long serialVersionUID = -7043079148076022783L;

	@ManyToOne(optional = false)
	@JoinColumn(name = "PRODUCT_TEMPLATE_ID")
	@NotNull
	private ProductTemplate productTemplate;

	@ManyToOne(optional = false)
	@JoinColumn(name = "BUNDLE_TEMPLATE_ID")
	@NotNull
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		} else if (!(obj instanceof BundleProductTemplate)) {
			return false;
		}

		BundleProductTemplate that = (BundleProductTemplate) obj;

		ProductTemplate thatProductTemplate = that.getProductTemplate();
		if (productTemplate == null) {
			if (thatProductTemplate != null) {
				return false;
			}
		} else if (!productTemplate.equals(thatProductTemplate)) {
			return false;
		}

		BundleTemplate thatBundleTemplate = that.getBundleTemplate();
		if (bundleTemplate == null && thatBundleTemplate != null) {
			return false;
		} else if (!bundleTemplate.equals(thatBundleTemplate)) {
			return false;
		}

		return true;
	}

}
