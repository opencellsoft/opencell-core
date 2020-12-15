package org.meveo.model.quote;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.offer.QuoteOffer;

@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_quote_product", uniqueConstraints = @UniqueConstraint(columnNames = { "product_version_id", "offer_quote_id"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_product_seq"), })
@NamedQueries({
		@NamedQuery(name = "QuoteProduct.findByQuoteId", query = "select q from QuoteProduct q where q.quote.id=:id"),
		@NamedQuery(name = "QuoteProduct.findByQuoteCode", query = "select q from QuoteProduct q where q.quote.code=:code"),
		@NamedQuery(name = "QuoteProduct.findByQuoteVersionAndQuoteOffer", query = "select q from QuoteProduct q left join q.quoteVersion qq left join q.quoteOffre qqo where qq.id=:quoteVersionId and qqo.id=:quoteOfferId")
})
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
	@JoinColumn(name = "quote_lot_id", referencedColumnName = "id")
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
    

    @OneToMany(mappedBy = "quoteProduct", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
	private List<QuoteAttribute> quoteAttributes = new ArrayList<QuoteAttribute>();
    

	public void update(QuoteProduct other) {
    	this.quoteOffre = other.quoteOffre;
    	this.quote = other.quote;
		this.quoteVersion = other.quoteVersion;
		this.quoteLot = other.quoteLot;
		this.productVersion = other.productVersion;
		this.quantity = other.quantity;
		this.billableAccount = other.billableAccount;
		this.quoteOffre = other.quoteOffre;
		this.quoteAttributes = other.quoteAttributes;
    }
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(billableAccount, productVersion, quantity, quote, quoteLot, quoteOffre, quoteVersion);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		QuoteProduct other = (QuoteProduct) obj;
		return Objects.equals(billableAccount, other.billableAccount)
				&& Objects.equals(productVersion, other.productVersion) && Objects.equals(quantity, other.quantity)
				&& Objects.equals(quote, other.quote) && Objects.equals(quoteLot, other.quoteLot)
				&& Objects.equals(quoteOffre, other.quoteOffre) && Objects.equals(quoteVersion, other.quoteVersion);
	}
	/**
	 * @return the quoteAttributes
	 */
	public List<QuoteAttribute> getQuoteAttributes() {
		if(quoteAttributes == null)
			quoteAttributes = new ArrayList<QuoteAttribute>();
		return quoteAttributes;
	}
	/**
	 * @param quoteAttributes the quoteAttributes to set
	 */
	public void setQuoteAttributes(List<QuoteAttribute> quoteAttributes) {
		this.quoteAttributes = quoteAttributes;
	}
}
