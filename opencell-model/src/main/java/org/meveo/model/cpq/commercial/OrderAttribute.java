package org.meveo.model.cpq.commercial;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeValue;

/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@Entity
@Table(name = "cpq_order_item", uniqueConstraints = @UniqueConstraint(columnNames = { "id" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_order_item_seq")})
public class OrderAttribute extends AttributeValue<OrderAttribute> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	@NotNull
	private CommercialOrder orderCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_lot_id")
	private OrderLot orderLot;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_product_id")
	private OrderProduct orderProduct;
	
	
	@Column(name = "access_point", length = 20)
	@Size(max = 20)
	private String accessPoint;
	
	/**
	 * @return the orderCode
	 */
	public CommercialOrder getOrderCode() {
		return orderCode;
	}

	/**
	 * @param orderCode the orderCode to set
	 */
	public void setOrderCode(CommercialOrder orderCode) {
		this.orderCode = orderCode;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		OrderAttribute that = (OrderAttribute) o;
		return Objects.equals(orderCode, that.orderCode) &&
				Objects.equals(orderLot, that.orderLot) &&
				Objects.equals(orderProduct, that.orderProduct) &&
				Objects.equals(accessPoint, that.accessPoint);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), orderCode, orderLot, orderProduct, accessPoint);
	}
}
