package org.meveo.model.catalog;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

/**
 * @author Edward P. Legaspi
 */
@Entity
@DiscriminatorValue("BUNDLE")
public class BundleTemplate extends ProductTemplate {

	private static final long serialVersionUID = -4295608354238684804L;

	@OneToMany(mappedBy = "bundleTemplate", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<BundleProductTemplate> bundleProducts;

	public List<BundleProductTemplate> getBundleProducts() {
		return bundleProducts;
	}

	public void setBundleProducts(List<BundleProductTemplate> bundleProducts) {
		this.bundleProducts = bundleProducts;
	}

}
