package org.meveo.model.catalog;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.meveo.model.CustomFieldEntity;

/**
 * @author Edward P. Legaspi
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "BUNDLE")
@DiscriminatorValue("BUNDLE")
@NamedQueries({ @NamedQuery(name = "BundleTemplate.countActive", query = "SELECT COUNT(*) FROM BundleTemplate WHERE disabled=false"),
		@NamedQuery(name = "BundleTemplate.countDisabled", query = "SELECT COUNT(*) FROM BundleTemplate WHERE disabled=true"),
		@NamedQuery(name = "BundleTemplate.countExpiring", query = "SELECT COUNT(*) FROM BundleTemplate WHERE :nowMinus1Day<validTo and validTo > NOW()") })
public class BundleTemplate extends ProductTemplate {

	private static final long serialVersionUID = -4295608354238684804L;

	@OneToMany(mappedBy = "bundleTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<BundleProductTemplate> bundleProducts = new HashSet<BundleProductTemplate>();

	public Set<BundleProductTemplate> getBundleProducts() {
		return bundleProducts;
	}

	public void setBundleProducts(Set<BundleProductTemplate> bundleProducts) {
		this.bundleProducts = bundleProducts;
	}

	public void addBundleProductTemplate(BundleProductTemplate bundleProductTemplate) {
		if (getBundleProducts() == null) {
			bundleProducts = new HashSet<BundleProductTemplate>();
		}
		bundleProductTemplate.setBundleTemplate(this);
		
		bundleProducts.add(bundleProductTemplate);
	}

}
