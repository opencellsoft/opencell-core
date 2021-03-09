package org.meveo.model.cpq.commercial;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteProduct;

/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@Entity
@Table(name = "cpq_order_product")
@CustomFieldEntity(cftCodePrefix = "OrderProduct")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_order_product_seq")})
public class OrderProduct extends AuditableCFEntity {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1316379006709425156L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	@NotNull
	private CommercialOrder order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_service_commercial_id")
	private OrderLot orderServiceCommercial;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_offer_id",referencedColumnName = "id") 
	private OrderOffer orderOffer;
	 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_version_id")
	private ProductVersion productVersion;

    
	@Column(name = "quantity", nullable = false, scale = NB_DECIMALS, precision = NB_PRECISION)
	@NotNull
	private BigDecimal quantity;

	@OneToMany(mappedBy = "orderProduct", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderAttribute> orderAttributes=new ArrayList<OrderAttribute>();


	
	
	public void update(OrderProduct other) {
    	this.orderOffer = other.orderOffer;
    	this.order = other.order;
		this.orderServiceCommercial = other.orderServiceCommercial;
		this.productVersion = other.productVersion;
		this.quantity = other.quantity;
		this.productVersion = other.productVersion;
		this.orderAttributes = other.orderAttributes;
    }
	
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
	 * @return the orderServiceCommercial
	 */
	public OrderLot getOrderServiceCommercial() {
		return orderServiceCommercial;
	}


	/**
	 * @param orderServiceCommercial the orderServiceCommercial to set
	 */
	public void setOrderServiceCommercial(OrderLot orderServiceCommercial) {
		this.orderServiceCommercial = orderServiceCommercial;
	}


	/**
	 * @return the orderOffer
	 */
	public OrderOffer getOrderOffer() {
		return orderOffer;
	}


	/**
	 * @param orderOffer the orderOffer to set
	 */
	public void setOrderOffer(OrderOffer orderOffer) {
		this.orderOffer = orderOffer;
	}

	/**
	 * @return the quantity
	 */
	public BigDecimal getQuantity() {
		return quantity;
	}


	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
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

	public List<OrderAttribute> getOrderAttributes() {
		return orderAttributes;
	}

	public void setOrderAttributes(List<OrderAttribute> orderAttributes) {
		this.orderAttributes = orderAttributes;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(productVersion, quantity, order, orderOffer, orderServiceCommercial,quantity);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		OrderProduct other = (OrderProduct) obj;
		return  Objects.equals(productVersion, other.productVersion) && Objects.equals(quantity, other.quantity)
				&& Objects.equals(order, other.order)
				&& Objects.equals(orderOffer, other.orderOffer) && Objects.equals(orderServiceCommercial, other.orderServiceCommercial);
	} 
	
	
	
}
