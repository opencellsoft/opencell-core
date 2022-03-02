package org.meveo.model.quote;

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
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.cpq.commercial.PriceLevelEnum;
import org.meveo.model.cpq.enums.PriceTypeEnum;
import org.meveo.model.cpq.offer.QuoteOffer;

@Entity
@Table(name = "cpq_quote_price", uniqueConstraints = @UniqueConstraint(columnNames = { "id"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_price_seq"), })
@NamedQueries({
	@NamedQuery(name="QuotePrice.removeByQuoteVersionAndPriceLevel", query = "delete from QuotePrice qp where qp.quoteVersion = :quoteVersion and qp.priceLevelEnum = :priceLevelEnum"),
	@NamedQuery(name="QuotePrice.removeByQuoteOfferAndPriceLevel", query = "delete from QuotePrice qp where qp.quoteOffer.id = :quoteOfferId and qp.priceLevelEnum = :priceLevelEnum"),
	@NamedQuery(name="QuotePrice.loadByQuoteOfferAndArticleCodeAndPriceLevel", query = "from QuotePrice qp where qp.quoteOffer.id = :quoteOfferId and qp.priceLevelEnum = :priceLevelEnum and qp.quoteArticleLine.accountingArticle.code = :accountingArticleCode")
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
		this.priceLevelEnum = copy.priceLevelEnum;
		this.priceTypeEnum = copy.priceTypeEnum;
		this.amountWithTax = copy.amountWithTax;
		this.unitPriceWithoutTax = copy.unitPriceWithoutTax;
		this.amountWithoutTax = copy.amountWithoutTax;
		this.amountWithoutTaxWithDiscount = copy.amountWithoutTaxWithDiscount;
		this.taxAmount = copy.taxAmount;
		this.taxRate = copy.taxRate;
		this.priceOverCharged = copy.priceOverCharged;
		this.currencyCode = copy.currencyCode;
		this.recurrenceDuration = copy.recurrenceDuration;
		this.recurrencePeriodicity = copy.recurrencePeriodicity;
		this.chargeTemplate = copy.chargeTemplate;
		this.quantity = copy.quantity;
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

	@Column(name = "amount_without_tax_with_discount")
	private BigDecimal amountWithoutTaxWithDiscount = BigDecimal.ZERO;

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
	
	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

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

	
	
}
