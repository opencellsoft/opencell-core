package org.meveo.model.cpq.commercial;

import java.util.Date;

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

/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@Entity
@Table(name = "cpq_order_item", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_order_item_seq")})
public class CommercialOrderAttribute extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	@NotNull
	private CommercialOrder orderCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_customer_service_id")
	private OrderCustomerService orderCustomerService;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_product_id")
	private OrderProduct orderProduct;
	
	
	@Column(name = "access_point", length = 20)
	@Size(max = 20)
	private String accessPoint;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attribute_id")
	private Attribute attribute;

	@Column(name = "string_value", length = 255)
	@Size(max = 255)
	private String stringValue;
	
	@Column(name = "date_value")
	private Date dateValue;
	
	@Column(name = "double_value")
	private Double doubleValue;
	
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
	 * @return the orderCustomerService
	 */
	public OrderCustomerService getOrderCustomerService() {
		return orderCustomerService;
	}

	/**
	 * @param orderCustomerService the orderCustomerService to set
	 */
	public void setOrderCustomerService(OrderCustomerService orderCustomerService) {
		this.orderCustomerService = orderCustomerService;
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

	/**
	 * @return the stringValue
	 */
	public String getStringValue() {
		return stringValue;
	}

	/**
	 * @param stringValue the stringValue to set
	 */
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	/**
	 * @return the dateValue
	 */
	public Date getDateValue() {
		return dateValue;
	}

	/**
	 * @param dateValue the dateValue to set
	 */
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	/**
	 * @return the doubleValue
	 */
	public Double getDoubleValue() {
		return doubleValue;
	}

	/**
	 * @param doubleValue the doubleValue to set
	 */
	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}
	
}
