package org.meveo.model.catalog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.meveo.model.module.MeveoModule;

/**
 * Business product model used for Product customization
 * 
 * @author Edward P. Legaspi
 */
@Entity
@Table(name = "cat_business_product_model")
public class BusinessProductModel extends MeveoModule {

    private static final long serialVersionUID = 4010282288751376225L;

    /**
     * Product template
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_template_id")
    private ProductTemplate productTemplate;

    public ProductTemplate getProductTemplate() {
        return productTemplate;
    }

    public void setProductTemplate(ProductTemplate productTemplate) {
        this.productTemplate = productTemplate;
    }

}
