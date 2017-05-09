package org.meveo.model.catalog;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.meveo.model.module.MeveoModule;

/**
 * @author Edward P. Legaspi
 */
@Entity
@Table(name = "CAT_BUSINESS_PRODUCT_MODEL")
public class BusinessProductModel extends MeveoModule {

	private static final long serialVersionUID = 4010282288751376225L;
	
	@ManyToOne
    @JoinColumn(name = "PRODUCT_TEMPLATE_ID")
    private ProductTemplate productTemplate;

	public ProductTemplate getProductTemplate() {
		return productTemplate;
	}

	public void setProductTemplate(ProductTemplate productTemplate) {
		this.productTemplate = productTemplate;
	}

}
