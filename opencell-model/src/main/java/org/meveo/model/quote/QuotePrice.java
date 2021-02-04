package org.meveo.model.quote;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.commercial.PriceLevelEnum;
import org.meveo.model.cpq.enums.PriceTypeEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "cpq_quote_price", uniqueConstraints = @UniqueConstraint(columnNames = { "id"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_price_seq"), })
public class QuotePrice extends AuditableEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_article_line_id", nullable = false)
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
}
