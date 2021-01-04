package org.meveo.model.cpq.commercial;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.Product;

/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@Entity
@Table(name = "cpq_order_item", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_order_item_seq")})
public class OrderCommercialItem extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	@NotNull
	private CommercialOrder orderCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_service_commercial_id")
	private OrderCustomerService serviceCommercila;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_product_id")
	private OrderProduct orderProduct;
	
	//TODO: adding order article item id => not yet created

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "offer_template_id", nullable = false)
	@NotNull
	private OfferTemplate offerTemplate;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	@NotNull
	private Product product;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_template_id", nullable = false)
	@NotNull
	private ServiceTemplate serviceTemplate;
	
	@Column(name = "access_point", length = 20)
	@Size(max = 20)
	private String accessPoint;
	
	@Column(name = "service_type", nullable = false)
	private int serviceType;
	
	@Column(name = "value")
	@Lob
	private String value;

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
	 * @return the serviceCommercila
	 */
	public OrderCustomerService getServiceCommercila() {
		return serviceCommercila;
	}

	/**
	 * @param serviceCommercila the serviceCommercila to set
	 */
	public void setServiceCommercila(OrderCustomerService serviceCommercila) {
		this.serviceCommercila = serviceCommercila;
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
	 * @return the offerTemplate
	 */
	public OfferTemplate getOfferTemplate() {
		return offerTemplate;
	}

	/**
	 * @param offerTemplate the offerTemplate to set
	 */
	public void setOfferTemplate(OfferTemplate offerTemplate) {
		this.offerTemplate = offerTemplate;
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
	 * @return the serviceTemplate
	 */
	public ServiceTemplate getServiceTemplate() {
		return serviceTemplate;
	}

	/**
	 * @param serviceTemplate the serviceTemplate to set
	 */
	public void setServiceTemplate(ServiceTemplate serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
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
	 * @return the serviceType
	 */
	public int getServiceType() {
		return serviceType;
	}

	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
	
}
