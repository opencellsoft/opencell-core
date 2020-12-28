package org.meveo.model.cpq.commercial;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.cpq.Product;

/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@Entity
@Table(name = "cpq_order_product", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_order_product_seq")})
public class OrderProduct extends BusinessEntity {


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
	private OrderCustomerService orderServiceCommercial;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_offer_id", nullable = false)
	@NotNull
	private OrderOffer orderOffer;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

    
	@Column(name = "quantity", nullable = false, scale = NB_DECIMALS, precision = NB_PRECISION)
	@NotNull
	private BigDecimal quantity;


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
	public OrderCustomerService getOrderServiceCommercial() {
		return orderServiceCommercial;
	}


	/**
	 * @param orderServiceCommercial the orderServiceCommercial to set
	 */
	public void setOrderServiceCommercial(OrderCustomerService orderServiceCommercial) {
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

	
	
	
}
