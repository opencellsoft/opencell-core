package org.meveo.model.cpq.commercial;

import static javax.persistence.FetchType.LAZY;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.offer.QuoteOffer;

/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "OrderOffer",inheritCFValuesFrom = "quoteOffer")
@Table(name = "cpq_order_offer", uniqueConstraints = @UniqueConstraint(columnNames = {"code", "order.code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_order_offer_seq")})
public class OrderOffer extends BusinessCFEntity {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1362016936635761040L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	@NotNull
	private CommercialOrder order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "offer_template_id", nullable = false)
	@NotNull
	private OfferTemplate offerTemplate;

	@OneToMany(mappedBy = "orderOffer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderProduct> products=new ArrayList<OrderProduct>();

	@OneToMany(mappedBy = "orderOffer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
	private List<OrderAttribute> orderAttributes = new ArrayList<OrderAttribute>();
	
	
	/**
	 * discountPlan attached to this orderOffer
	 */
    @ManyToOne(fetch = LAZY)
	@JoinColumn(name = "discount_plan_id", referencedColumnName = "id")
	private DiscountPlan discountPlan;
    
    
    /**
	 * quote offer attached to this orderOffer
	 */
    
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_offer_id")
	private QuoteOffer quoteOffer;
    
	
    @Override
	public ICustomFieldEntity[] getParentCFEntities() {
		if (quoteOffer != null) {
			return new ICustomFieldEntity[] { quoteOffer };
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

	public List<OrderProduct> getProducts() {
		return products;
	}

	public void setProducts(List<OrderProduct> products) {
		this.products = products;
	}

	/**
	 * @return the orderAttributes
	 */
	public List<OrderAttribute> getOrderAttributes() {
		return orderAttributes;
	}

	/**
	 * @param orderAttributes the orderAttributes to set
	 */
	public void setOrderAttributes(List<OrderAttribute> orderAttributes) {
		this.orderAttributes = orderAttributes;
	}

	public DiscountPlan getDiscountPlan() {
		return discountPlan;
	}

	public void setDiscountPlan(DiscountPlan discountPlan) {
		this.discountPlan = discountPlan;
	}

	public QuoteOffer getQuoteOffer() {
		return quoteOffer;
	}

	public void setQuoteOffer(QuoteOffer quoteOffer) {
		this.quoteOffer = quoteOffer;
	}


	
	
	
	
	
}
