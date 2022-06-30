package org.meveo.model.ordering;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.cpq.Product;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "open_order_product")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "open_order_product_seq"),})
public class OpenOrderProduct extends BusinessEntity {

	@OneToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
	
	@OneToOne(fetch = LAZY)
    @JoinColumn(name = "open_order_template_id")
    private OpenOrderTemplate openOrderTemplate;
	
	@Type(type = "numeric_boolean")
    @Column(name = "active")
    private Boolean active;

	public OpenOrderTemplate getOpenOrderTemplate() {
		return openOrderTemplate;
	}

	public void setOpenOrderTemplate(OpenOrderTemplate openOrderTemplate) {
		this.openOrderTemplate = openOrderTemplate;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}    

}
