package org.meveo.model.cpq.commercial;

import org.meveo.model.cpq.ProductVersion;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Embeddable
public class OrderInfo {
    
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private CommercialOrder order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_version_id")
	private ProductVersion productVersion;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_lot_id")
	private OrderLot orderLot;

	
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

	
}
