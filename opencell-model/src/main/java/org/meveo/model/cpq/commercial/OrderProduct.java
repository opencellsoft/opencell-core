package org.meveo.model.cpq.commercial;

import static javax.persistence.FetchType.LAZY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.quote.QuoteProduct;

/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@Entity
@Table(name = "cpq_order_product")
@CustomFieldEntity(cftCodePrefix = "OrderProduct",inheritCFValuesFrom = "quoteProduct")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_order_product_seq")})
@NamedQueries({
	@NamedQuery(name = "OrderProduct.findOrderProductByOrder", query = "FROM OrderProduct op WHERE op.order.id = :commercialOrderId")
})
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
	private List<OrderAttribute> orderAttributes=new ArrayList<>();

	/**
	 * discountPlan attached to this orderProduct
	 */
    @ManyToOne(fetch = LAZY)
	@JoinColumn(name = "discount_plan_id", referencedColumnName = "id")
	private DiscountPlan discountPlan;
    
    
    /**
   	 * quote product attached to this OrderProduct
   	 */
       
   	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_product_id")
   	private QuoteProduct quoteProduct;
   	
    /** Delivery timestamp. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "delivery_date")
    private Date deliveryDate;
    
    /**production action type */
    @Enumerated(EnumType.STRING)
    @Column(name = "production_action_type", length = 10)
   	private ProductActionTypeEnum productActionType = ProductActionTypeEnum.MODIFY;
    
    /** termination timestamp. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "termination_date")
    private Date terminationDate;
    
    /** Termination reason. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_termin_reason_id")
    private SubscriptionTerminationReason terminationReason;
    
    /** Current product status */
    @Enumerated(EnumType.STRING)
    @Column(name = "instance_status", length = 10)
   	private InstanceStatusEnum status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_instance_id")
	private ServiceInstance serviceInstance;

	@OneToMany(mappedBy = "orderProduct", fetch = FetchType.LAZY)
	private List<OrderArticleLine> orderArticleLines;

	public void update(OrderProduct other) {
    	this.orderOffer = other.orderOffer;
    	this.order = other.order;
		this.orderServiceCommercial = other.orderServiceCommercial;
		this.productVersion = other.productVersion;
		this.quantity = other.quantity;
		this.orderAttributes.clear();
		this.orderAttributes.addAll(other.orderAttributes);
        this.discountPlan=other.getDiscountPlan();
        this.quoteProduct=other.getQuoteProduct();
        this.deliveryDate=other.deliveryDate;
        this.productActionType=other.productActionType;
        this.terminationDate=other.terminationDate;
        this.terminationReason=other.terminationReason;
		this.serviceInstance = other.serviceInstance;
    }

	@Override
	public ICustomFieldEntity[] getParentCFEntities() {
		if (quoteProduct != null) {
			return new ICustomFieldEntity[] { quoteProduct };
		}
		return null;
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



	public DiscountPlan getDiscountPlan() {
		return discountPlan;
	}

	public void setDiscountPlan(DiscountPlan discountPlan) {
		this.discountPlan = discountPlan;
	}



	public QuoteProduct getQuoteProduct() {
		return quoteProduct;
	}

	public void setQuoteProduct(QuoteProduct quoteProduct) {
		this.quoteProduct = quoteProduct;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(productVersion, quantity, order, orderOffer, orderServiceCommercial,quantity,discountPlan, serviceInstance);
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
				&& Objects.equals(orderOffer, other.orderOffer) && Objects.equals(orderServiceCommercial, other.orderServiceCommercial)
				&& Objects.equals(discountPlan, other.discountPlan) && Objects.equals(serviceInstance, other.serviceInstance);
	}


	public Date getDeliveryDate() {
		return deliveryDate;
	}


	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public ProductActionTypeEnum getProductActionType() {
		return productActionType;
	}

	public void setProductActionType(ProductActionTypeEnum productActionType) {
		this.productActionType = productActionType;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public SubscriptionTerminationReason getTerminationReason() {
		return terminationReason;
	}

	public void setTerminationReason(SubscriptionTerminationReason terminationReason) {
		this.terminationReason = terminationReason;
	}

	public InstanceStatusEnum getStatus() {
		return status;
	}

	public void setStatus(InstanceStatusEnum status) {
		this.status = status;
	}

	/**
	 * @return Service instance that order product is associated to
	 */
	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	/**
	 * @param serviceInstance Service instance that order product is associated to
	 */
	public void setServiceInstance(ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	public List<OrderArticleLine> getOrderArticleLines() {
		return orderArticleLines;
	}

	public void setOrderArticleLines(List<OrderArticleLine> orderArticleLines) {
		this.orderArticleLines = orderArticleLines;
	}
}