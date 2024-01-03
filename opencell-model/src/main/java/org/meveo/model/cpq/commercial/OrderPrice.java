package org.meveo.model.cpq.commercial;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.billing.Tax;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.cpq.enums.PriceTypeEnum;

@Entity
@Table(name = "order_price", uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "order_price_seq")})
@NamedQueries({
@NamedQuery(name="OrderPrice.findByOrder", query = "select o from OrderPrice o where o.order=:commercialOrder"),
@NamedQuery(name="OrderPrice.findByQuote", query = "select o from OrderPrice o where o.order.quote=:quote"),
@NamedQuery(name = "OrderPrice.sumPricesByArticle", query = "SELECT o.orderArticleLine.id,o.discountedOrderPrice.id,o.taxRate, sum(o.amountWithoutTax) as sumAmountWOTax, sum(o.amountWithTax), sum(o.taxAmount),"
		+ "sum(o.quantity) FROM OrderPrice o WHERE o.order.id=:orderId AND o.priceTypeEnum=:priceType AND (o.priceLevelEnum=:priceLevel OR o.discountPlan is not null) GROUP BY o.orderArticleLine.id,o.discountedOrderPrice.id,o.taxRate order by sumAmountWOTax DESC"),
@NamedQuery(name="OrderPrice.loadOverridenPricesByOrderProductAndCharge", query = "select op from OrderPrice op where op.orderArticleLine.orderProduct.id = :orderProductId and op.priceOverCharged = true and op.chargeTemplate.id = :chargeTemplateId")
})
public class OrderPrice extends BusinessEntity {


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_article_line_id", nullable = false) 
    private OrderArticleLine orderArticleLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull
    private CommercialOrder order;

    @Column(name = "price_level")
    @Enumerated(EnumType.STRING)
    private PriceLevelEnum priceLevelEnum;

    @Column(name = "price_type")
    @Enumerated(EnumType.STRING)
    private PriceTypeEnum priceTypeEnum;

    @Column(name = "amount_with_tax")
    private BigDecimal amountWithTax;

    @Column(name = "unit_price_without_tax")
    private BigDecimal unitPriceWithoutTax;

    @Column(name = "amount_without_tax")
    private BigDecimal amountWithoutTax;

    @Column(name = "amount_without_tax_with_discount")
    private BigDecimal amountWithoutTaxWithDiscount;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    @Column(name = "tax_rate")
    private BigDecimal taxRate;

    @Type(type = "numeric_boolean")
    @Column(name = "price_over_charged")
    private Boolean priceOverCharged;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "recurrence_duration")
    private Long recurrenceDuration;

    @Column(name = "recurrence_periodicity")
    private String recurrencePeriodicity;

    @OneToOne
    @JoinColumn(name= "charge_template_id")
    private ChargeTemplate chargeTemplate;
    
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_offer_id")
	private OrderOffer orderOffer;
	
	@Column(name = "quantity")
	private BigDecimal quantity = BigDecimal.ONE;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "discounted_order_price")
	private OrderPrice discountedOrderPrice;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_plan_id")
    private DiscountPlan discountPlan;

    @Column(name = "discount_value")
 	private BigDecimal discountValue;
     
     @Enumerated(EnumType.STRING)
     @Column(name = "discount_plan_type", length = 50)
     private DiscountPlanItemTypeEnum discountPlanType;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "discount_plan_item_id")
     private DiscountPlanItem discountPlanItem;
     
     @Type(type = "numeric_boolean")
     @Column(name = "apply_discounts_on_overriden_price")
     private Boolean applyDiscountsOnOverridenPrice;
     
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "tax_id")
     private Tax tax;
     
     /**The amount after discount**/
     @Column(name = "discounted_amount")
    	private BigDecimal discountedAmount;
     
     /**
 	 * 
 	 *filled only for price lines related to applied discounts, and contains the application sequence composed by the concatenation of the DP sequence and DPI sequence
 	 */
 	@Column(name = "sequence")
 	private Integer sequence;
     
    public OrderArticleLine getOrderArticleLine() {
        return orderArticleLine;
    }

    public void setOrderArticleLine(OrderArticleLine orderArticleLine) {
        this.orderArticleLine = orderArticleLine;
    }

    public CommercialOrder getOrder() {
        return order;
    }

    public void setOrder(CommercialOrder order) {
        this.order = order;
    }

    public PriceLevelEnum getPriceLevelEnum() {
        return priceLevelEnum;
    }

    public void setPriceLevelEnum(PriceLevelEnum priceLevelEnum) {
        this.priceLevelEnum = priceLevelEnum;
    }

    public PriceTypeEnum getPriceTypeEnum() {
        return priceTypeEnum;
    }

    public void setPriceTypeEnum(PriceTypeEnum priceTypeEnum) {
        this.priceTypeEnum = priceTypeEnum;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public BigDecimal getUnitPriceWithoutTax() {
        return unitPriceWithoutTax;
    }

    public void setUnitPriceWithoutTax(BigDecimal unitPriceWithoutTax) {
        this.unitPriceWithoutTax = unitPriceWithoutTax;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountWithoutTaxWithDiscount() {
        return amountWithoutTaxWithDiscount;
    }

    public void setAmountWithoutTaxWithDiscount(BigDecimal amountWithoutTaxWithDiscount) {
        this.amountWithoutTaxWithDiscount = amountWithoutTaxWithDiscount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public Boolean getPriceOverCharged() {
        return priceOverCharged;
    }

    public void setPriceOverCharged(Boolean priceOverCharged) {
        this.priceOverCharged = priceOverCharged;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Long getRecurrenceDuration() {
        return recurrenceDuration;
    }

    public void setRecurrenceDuration(Long recurrenceDuration) {
        this.recurrenceDuration = recurrenceDuration;
    }

    public String getRecurrencePeriodicity() {
        return recurrencePeriodicity;
    }

    public void setRecurrencePeriodicity(String recurrencePeriodicity) {
        this.recurrencePeriodicity = recurrencePeriodicity;
    }

    public ChargeTemplate getChargeTemplate() {
        return chargeTemplate;
    }

    public void setChargeTemplate(ChargeTemplate chargeTemplate) {
        this.chargeTemplate = chargeTemplate;
    }

	public OrderOffer getOrderOffer() {
		return orderOffer;
	}

	public void setOrderOffer(OrderOffer orderOffer) {
		this.orderOffer = orderOffer;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public OrderPrice getDiscountedOrderPrice() {
		return discountedOrderPrice;
	}

	public void setDiscountedOrderPrice(OrderPrice discountedOrderPrice) {
		this.discountedOrderPrice = discountedOrderPrice;
	}

	public DiscountPlan getDiscountPlan() {
		return discountPlan;
	}

	public void setDiscountPlan(DiscountPlan discountPlan) {
		this.discountPlan = discountPlan;
	}

	public BigDecimal getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(BigDecimal discountValue) {
		this.discountValue = discountValue;
	}

	public DiscountPlanItemTypeEnum getDiscountPlanType() {
		return discountPlanType;
	}

	public void setDiscountPlanType(DiscountPlanItemTypeEnum discountPlanType) {
		this.discountPlanType = discountPlanType;
	}

	public DiscountPlanItem getDiscountPlanItem() {
		return discountPlanItem;
	}

	public void setDiscountPlanItem(DiscountPlanItem discountPlanItem) {
		this.discountPlanItem = discountPlanItem;
	}

	public Boolean getApplyDiscountsOnOverridenPrice() {
		return applyDiscountsOnOverridenPrice;
	}

	public void setApplyDiscountsOnOverridenPrice(Boolean applyDiscountsOnOverridenPrice) {
		this.applyDiscountsOnOverridenPrice = applyDiscountsOnOverridenPrice;
	}

	public BigDecimal getDiscountedAmount() {
		return discountedAmount;
	}

	public void setDiscountedAmount(BigDecimal discountedAmount) {
		this.discountedAmount = discountedAmount;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Tax getTax() {
		return tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}
	
	
	
	
    
    
}
