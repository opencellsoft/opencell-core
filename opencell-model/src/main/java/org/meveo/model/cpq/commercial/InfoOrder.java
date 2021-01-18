package org.meveo.model.cpq.commercial;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;

@Embeddable
public class InfoOrder {


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private CommercialOrder order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_version_id")
	private ProductVersion productVersion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attribute_id")
	private Attribute attribute;
	
	@Column(name = "prestation")
	private String prestation;

	
	/**
	 * @return the order
	 */
	public CommercialOrder getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(CommercialOrder order) {
		this.order = order;
	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @param product the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	/**
	 * @return the productVersion
	 */
	public ProductVersion getProductVersion() {
		return productVersion;
	}

	/**
	 * @param productVersion the productVersion to set
	 */
	public void setProductVersion(ProductVersion productVersion) {
		this.productVersion = productVersion;
	}


	/**
	 * @return the prestation
	 */
	public String getPrestation() {
		return prestation;
	}

	/**
	 * @param prestation the prestation to set
	 */
	public void setPrestation(String prestation) {
		this.prestation = prestation;
	}

	/**
	 * @return the attribute
	 */
	public Attribute getAttribute() {
		return attribute;
	}

	/**
	 * @param attribute the attribute to set
	 */
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
	
	
	
}
