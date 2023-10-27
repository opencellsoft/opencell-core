package org.meveo.model.quote;

import static javax.persistence.FetchType.LAZY;

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
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.Tax;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.commercial.PriceLevelEnum;
import org.meveo.model.cpq.contract.ContractItem;
import org.meveo.model.cpq.enums.PriceTypeEnum;
import org.meveo.model.cpq.offer.QuoteOffer;

@Entity
@Table(name = "cpq_quote_price", uniqueConstraints = @UniqueConstraint(columnNames = { "id"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_price_seq"), })
@NamedQueries({ 
		@NamedQuery(name="QuotePrice.removeByQuoteVersionAndPriceLevel", query = "delete from QuotePrice qp where qp.quoteVersion = :quoteVersion and qp.priceLevelEnum = :priceLevelEnum"),
		@NamedQuery(name="QuotePrice.loadByQuoteVersionAndPriceLevel", query = "select qp from QuotePrice qp where qp.quoteVersion = :quoteVersion and qp.priceLevelEnum = :priceLevelEnum"),
		@NamedQuery(name="QuotePrice.removeByQuoteOfferAndPriceLevel", query = "delete from QuotePrice qp where qp.quoteOffer.id = :quoteOfferId and qp.priceLevelEnum = :priceLevelEnum"),
		@NamedQuery(name="QuotePrice.loadByQuoteOfferAndArticleCodeAndPriceLevel", query = "select qp from QuotePrice qp where qp.quoteOffer.id = :quoteOfferId and qp.priceLevelEnum = :priceLevelEnum and qp.quoteArticleLine.accountingArticle.code = :accountingArticleCode")
	})
public class QuotePrice extends AuditableEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QuotePrice() {
		super();
	}

	public QuotePrice(QuotePrice copy) {
		this.quoteArticleLine = copy.quoteArticleLine;
		this.quoteVersion = copy.quoteVersion;
		this.quoteOffer = copy.quoteOffer;
		this.priceLevelEnum = copy.priceLevelEnum;
		this.priceTypeEnum = copy.priceTypeEnum;
		this.amountWithTax = copy.amountWithTax;
		this.unitPriceWithoutTax = copy.unitPriceWithoutTax;
		this.amountWithoutTax = copy.amountWithoutTax;
		this.amountWithoutTaxWithoutDiscount = copy.amountWithoutTaxWithoutDiscount;
		this.taxAmount = copy.taxAmount;
		this.taxRate = copy.taxRate;
		this.priceOverCharged = copy.priceOverCharged;
		this.currencyCode = copy.currencyCode;
		this.recurrenceDuration = copy.recurrenceDuration;
		this.recurrencePeriodicity = copy.recurrencePeriodicity;
		this.chargeTemplate = copy.chargeTemplate;
		this.quantity = copy.quantity;
		this.discountedQuotePrice = copy.discountedQuotePrice;
		this.discountPlan=copy.discountPlan;

		this.discountPlanItem=copy.discountPlanItem;
		this.discountPlanType=copy.discountPlanType;
		this.discountValue=copy.discountValue;
		this.applyDiscountsOnOverridenPrice=copy.getApplyDiscountsOnOverridenPrice();
		this.overchargedUnitAmountWithoutTax=copy.getOverchargedUnitAmountWithoutTax();
		this.discountedAmount=copy.getDiscountedAmount();
		this.sequence=copy.getSequence();
		this.contractItem=copy.contractItem;
		this.pricePlanMatrixVersion=copy.getPricePlanMatrixVersion();
		this.pricePlanMatrixLine=copy.getPricePlanMatrixLine();
		this.tax=copy.getTax();
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_article_line_id")
	private QuoteArticleLine quoteArticleLine;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_version_id", nullable = false)
	@NotNull
	private QuoteVersion quoteVersion;

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

	@Column(name = "amount_without_tax_without_discount")
	private BigDecimal amountWithoutTaxWithoutDiscount = BigDecimal.ZERO;

	@Column(name = "tax_amount")
	private BigDecimal taxAmount;

	@Column(name = "tax_rate")
	private BigDecimal taxRate;

	@Type(type = "numeric_boolean")
	@Column(name = "price_over_charged")
	private Boolean priceOverCharged=Boolean.FALSE;

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
	@JoinColumn(name = "quote_offer_id")
	private QuoteOffer quoteOffer;

	@Column(name = "quantity")
	private BigDecimal quantity = BigDecimal.ONE;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "discounted_quote_price")
	private QuotePrice discountedQuotePrice;
	

	@Column(name = "uuid")
	private String uuid;

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
    
    @Column(name = "overcharged_unit_amount_without_tax")
  	private BigDecimal overchargedUnitAmountWithoutTax;
    
    /**The amount after discount**/
    @Column(name = "discounted_amount")
   	private BigDecimal discountedAmount;
    
    /**
	 * 
	 *filled only for price lines related to applied discounts, and contains the application sequence composed by the concatenation of the DP sequence and DPI sequence
	 */
	@Column(name = "sequence")
	private Integer sequence;
	
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_plan_matrix_version_id")
    private PricePlanMatrixVersion pricePlanMatrixVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_plan_matrix_line_id")
    private PricePlanMatrixLine pricePlanMatrixLine;
    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_item_id")
    private ContractItem contractItem;
	
    @ManyToOne(fetch = LAZY)
	@JoinColumn(name = "tax_id")
    private Tax tax;
    
	public QuoteArticleLine getQuoteArticleLine() {
		return quoteArticleLine;
	}

	public void setQuoteArticleLine(QuoteArticleLine quoteArticleLine) {
		this.quoteArticleLine = quoteArticleLine;
	}

	public QuoteVersion getQuoteVersion() {
		return quoteVersion;
	}

	public void setQuoteVersion(QuoteVersion quoteVersion) {
		this.quoteVersion = quoteVersion;
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

	public BigDecimal getAmountWithoutTaxWithoutDiscount() {
		return amountWithoutTaxWithoutDiscount;
	}

	public void setAmountWithoutTaxWithoutDiscount(BigDecimal amountWithoutTaxWithDiscount) {
		this.amountWithoutTaxWithoutDiscount = amountWithoutTaxWithDiscount;
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

	public PriceLevelEnum getPriceLevelEnum() {
		return priceLevelEnum;
	}

	public void setPriceLevelEnum(PriceLevelEnum priceLevelEnum) {
		this.priceLevelEnum = priceLevelEnum;
	}

	public ChargeTemplate getChargeTemplate() {
		return chargeTemplate;
	}

	public void setChargeTemplate(ChargeTemplate chargeTemplate) {
		this.chargeTemplate = chargeTemplate;
	}

	public QuoteOffer getQuoteOffer() {
		return quoteOffer;
	}

	public void setQuoteOffer(QuoteOffer quoteOffer) {
		this.quoteOffer = quoteOffer;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public QuotePrice getDiscountedQuotePrice() {
		return discountedQuotePrice;
	}

	public void setDiscountedQuotePrice(QuotePrice discountedQuotePrice) {
		this.discountedQuotePrice = discountedQuotePrice;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public BigDecimal getOverchargedUnitAmountWithoutTax() {
		return overchargedUnitAmountWithoutTax;
	}

	public void setOverchargedUnitAmountWithoutTax(BigDecimal overchargedUnitAmountWithoutTax) {
		this.overchargedUnitAmountWithoutTax = overchargedUnitAmountWithoutTax;
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

	public PricePlanMatrixVersion getPricePlanMatrixVersion() {
		return pricePlanMatrixVersion;
	}

	public void setPricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion) {
		this.pricePlanMatrixVersion = pricePlanMatrixVersion;
	}

	public PricePlanMatrixLine getPricePlanMatrixLine() {
		return pricePlanMatrixLine;
	}

	public void setPricePlanMatrixLine(PricePlanMatrixLine pricePlanMatrixLine) {
		this.pricePlanMatrixLine = pricePlanMatrixLine;
	}

	public ContractItem getContractItem() {
		return contractItem;
	}

	public void setContractItem(ContractItem contractItem) {
		this.contractItem = contractItem;
	}

	public Tax getTax() {
		return tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}

	

	
	
	
	
}
