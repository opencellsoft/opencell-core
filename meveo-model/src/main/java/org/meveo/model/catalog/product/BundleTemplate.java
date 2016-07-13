package org.meveo.model.catalog.product;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * @author Edward P. Legaspi
 */
@Entity
@DiscriminatorValue("BUNDLE")
public class BundleTemplate extends ProductTemplate {

	private static final long serialVersionUID = -4295608354238684804L;

	@OneToMany(mappedBy = "bundleTemplate")
	private List<BundleProductTemplate> bundleProducts;

	public List<BundleProductTemplate> getBundleProducts() {
		return bundleProducts;
	}

	public void setBundleProducts(List<BundleProductTemplate> bundleProducts) {
		this.bundleProducts = bundleProducts;
	}

}
