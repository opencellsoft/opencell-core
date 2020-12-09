package org.meveo.model.quote;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.offer.QuoteOffer;

@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_quote_product", uniqueConstraints = @UniqueConstraint(columnNames = { "product_version_id", "offer_quote_id"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_product_seq"), })
@NamedQuery(name = "QuoteProduct.findByQuoteId", query = "select q from QuoteProduct q where q.quote.id=:id")
@NamedQuery(name = "QuoteProduct.findByQuoteCode", query = "select q from QuoteProduct q where q.quote.code=:code")
@NamedQuery(name = "QuoteProduct.findByQuoteVersionAndQuoteOffer", query = "select q from QuoteProduct q left join q.quoteVersion qq left join q.quoteOffre qqo where qq.id=:quoteVersionId and qqo.id=:quoteOfferId")
public class QuoteProduct extends AuditableEntity {

    /**
     * quote
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_quote_id", nullable = false, referencedColumnName = "id")
	@NotNull
    private CpqQuote quote;

    /**
     * quote Version
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_version_id", nullable = false, referencedColumnName = "id")
	@NotNull
    private QuoteVersion quoteVersion;

    /**
     * quote customer
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_customer_service_id", referencedColumnName = "id")
	@NotNull
    private QuoteLot quoteLot;

    /**
     * product
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_version_id", referencedColumnName = "id")
	@NotNull
    private ProductVersion productVersion;

    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS, nullable = false)
    @NotNull
    private BigDecimal quantity = BigDecimal.ONE;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billable_account_id", referencedColumnName = "id")
    private BillingAccount billableAccount;
    

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "offer_quote_id", referencedColumnName = "id")
    private QuoteOffer quoteOffre;

	/**
	 * @return the quote
	 */
	public CpqQuote getQuote() {
		return quote;
	}

	/**
	 * @param quote the quote to set
	 */
	public void setQuote(CpqQuote quote) {
		this.quote = quote;
	}

	/**
	 * @return the quoteVersion
	 */
	public QuoteVersion getQuoteVersion() {
		return quoteVersion;
	}

	/**
	 * @param quoteVersion the quoteVersion to set
	 */
	public void setQuoteVersion(QuoteVersion quoteVersion) {
		this.quoteVersion = quoteVersion;
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
	 * @return the billableAccount
	 */
	public BillingAccount getBillableAccount() {
		return billableAccount;
	}

	/**
	 * @param billableAccount the billableAccount to set
	 */
	public void setBillableAccount(BillingAccount billableAccount) {
		this.billableAccount = billableAccount;
	}


	/**
	 * @return the quoteLot
	 */
	public QuoteLot getQuoteLot() {
		return quoteLot;
	}

	/**
	 * @param quoteLot the quoteLot to set
	 */
	public void setQuoteLot(QuoteLot quoteLot) {
		this.quoteLot = quoteLot;
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

	/**
	 * @return the quoteOffre
	 */
	public QuoteOffer getQuoteOffre() {
		return quoteOffre;
	}

	/**
	 * @param quoteOffre the quoteOffre to set
	 */
	public void setQuoteOffre(QuoteOffer quoteOffre) {
		this.quoteOffre = quoteOffre;
	}
}
