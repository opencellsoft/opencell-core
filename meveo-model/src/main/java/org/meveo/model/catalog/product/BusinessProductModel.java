package org.meveo.model.catalog.product;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.meveo.model.module.MeveoModule;

/**
 * @author Edward P. Legaspi
 */
@Entity
@Table(name = "CAT_BUSINESS_PROD_MODEL")
public class BusinessProductModel extends MeveoModule {

	private static final long serialVersionUID = 4010282288751376225L;

	@OneToMany(mappedBy = "businessProductModel")
	private ProductTemplate productTemplate;

	public ProductTemplate getProductTemplate() {
		return productTemplate;
	}

	public void setProductTemplate(ProductTemplate productTemplate) {
		this.productTemplate = productTemplate;
	}

}
