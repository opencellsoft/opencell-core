package org.meveo.model.quote;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.CpqAccountingArticle;


/**
 * @author Khairi
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_quote_article_line", uniqueConstraints = @UniqueConstraint(columnNames = { "id"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_article_line_seq"), })
public class QuoteArticleLine extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_product_id")
    private QuoteProduct quoteProduct;

    /**
     * billable account
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billable_account_id", nullable = false, referencedColumnName = "id")
	@NotNull
	private BillingAccount billableAccount;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_lot_id", nullable = false, referencedColumnName = "id")
    private QuoteLot quoteLot;

    /**
     * account article
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accounting_article_id", nullable = false, referencedColumnName = "id")
	@NotNull
	private AccountingArticle accountingArticle;

    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS, nullable = false)
    private BigDecimal quantity = BigDecimal.ONE;

    @Column(name = "service_quantity", precision = NB_PRECISION, scale = NB_DECIMALS, nullable = false)
    private BigDecimal serviceQuantity;

    @OneToMany(mappedBy = "quoteArticleLine", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
	private List<QuotePrice> quotePrices = new ArrayList<>();

	public QuoteProduct getQuoteProduct() {
		return quoteProduct;
	}

	public void setQuoteProduct(QuoteProduct quoteProducts) {
		this.quoteProduct = quoteProducts;
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
	 * @return the serviceQuantity
	 */
	public BigDecimal getServiceQuantity() {
		return serviceQuantity;
	}

	/**
	 * @param serviceQuantity the serviceQuantity to set
	 */
	public void setServiceQuantity(BigDecimal serviceQuantity) {
		this.serviceQuantity = serviceQuantity;
	}

	/**
	 * @return the accountingArticle
	 */
	public AccountingArticle getAccountingArticle() {
		return accountingArticle;
	}

	/**
	 * @param accountingArticle the accountingArticle to set
	 */
	public void setAccountingArticle(AccountingArticle accountingArticle) {
		this.accountingArticle = accountingArticle;
	}

	public List<QuotePrice> getQuotePrices() {
		return quotePrices;
	}

	public void setQuotePrices(List<QuotePrice> quotePrices) {
		this.quotePrices = quotePrices;
	}

	public QuoteLot getQuoteLot() {
		return quoteLot;
	}

	public void setQuoteLot(QuoteLot quoteLot) {
		this.quoteLot = quoteLot;
	}
}
