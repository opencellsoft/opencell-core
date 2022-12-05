package org.meveo.model.cpq.commercial;

import java.util.Objects;
import java.util.stream.Collectors;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.security.MeveoUser;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;

/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@Entity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "OrderAttribute")
@Table(name = "cpq_order_attribute", uniqueConstraints = @UniqueConstraint(columnNames = { "id" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_order_item_seq")})
public class OrderAttribute extends AttributeValue<OrderAttribute> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private CommercialOrder commercialOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_lot_id")
	private OrderLot orderLot;
	
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_product_id")
	private OrderProduct orderProduct;
	
	
	@Column(name = "access_point", length = 20)
	@Size(max = 20)
	private String accessPoint;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
	@JoinColumn(name = "cpq_order_offer_id",referencedColumnName = "id")
	private OrderOffer  orderOffer;

	public OrderAttribute() {
	}
	
	

	public OrderAttribute(QuoteAttribute quoteAttribute, MeveoUser currentUser) {
		attribute=quoteAttribute.getAttribute();
		stringValue=quoteAttribute.getStringValue();
		dateValue=quoteAttribute.getDateValue();
		doubleValue=quoteAttribute.getDoubleValue();
		booleanValue=quoteAttribute.getBooleanValue();
		updateAudit(currentUser);
		assignedAttributeValue = quoteAttribute.getAssignedAttributeValue()
				.stream()
				.map(nestedQuoteAttribute -> {
					OrderAttribute orderAttribute = new OrderAttribute(nestedQuoteAttribute, currentUser);
					orderAttribute.setParentAttributeValue(this);
					return orderAttribute;
				})
				.collect(Collectors.toList());
	}

 

	public void update(OrderAttribute other) {
		this.orderOffer = other.orderOffer;
		this.stringValue = other.stringValue;
		this.doubleValue = other.doubleValue;
		this.dateValue = other.dateValue;
		this.accessPoint=other.accessPoint;
		this.commercialOrder=other.commercialOrder;
		this.orderLot=other.orderLot;
		this.attribute = other.attribute;
		this.auditable = other.auditable;
		this.orderProduct = other.orderProduct;  
		this.version = other.version;
		this.id=other.id;
		this.booleanValue = other.booleanValue;
		
	} 
	
	/**
	 * @return the commercialOrder
	 */
	public CommercialOrder getCommercialOrder() {
		return commercialOrder;
	}

	/**
	 * @param commercialOrder the commercialOrder to set
	 */
	public void setCommercialOrder(CommercialOrder commercialOrder) {
		this.commercialOrder = commercialOrder;
	}

	/**
	 * @return the orderProduct
	 */
	public OrderProduct getOrderProduct() {
		return orderProduct;
	}

	/**
	 * @param orderProduct the orderProduct to set
	 */
	public void setOrderProduct(OrderProduct orderProduct) {
		this.orderProduct = orderProduct;
	}


	/**
	 * @return the accessPoint
	 */
	public String getAccessPoint() {
		return accessPoint;
	}

	/**
	 * @param accessPoint the accessPoint to set
	 */
	public void setAccessPoint(String accessPoint) {
		this.accessPoint = accessPoint;
	}

	/**
	 * @return the orderLot
	 */
	public OrderLot getOrderLot() {
		return orderLot;
	}

	/**
	 * @param orderLot the orderLot to set
	 */
	public void setOrderLot(OrderLot orderLot) {
		this.orderLot = orderLot;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		OrderAttribute that = (OrderAttribute) o;
		return Objects.equals(commercialOrder, that.commercialOrder) &&
				Objects.equals(orderLot, that.orderLot) &&
				Objects.equals(orderProduct, that.orderProduct) &&
				Objects.equals(accessPoint, that.accessPoint) && 
				Objects.equals(orderOffer, that.orderOffer);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), commercialOrder, orderLot, orderProduct, accessPoint);
	}
}
