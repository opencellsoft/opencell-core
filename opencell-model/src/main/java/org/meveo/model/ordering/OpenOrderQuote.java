package org.meveo.model.ordering;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.cpq.tags.Tag;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "open_order_quote")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "open_order_quote_seq"),})
@NamedQueries({
    @NamedQuery(name = "OpenOrderQuote.findByOpenOrderTemplate", query = "select o from OpenOrderQuote o where o.openOrderTemplate = :openOrderTemplate") })
public class OpenOrderQuote extends BusinessEntity {
	
	@Column(name = "external_reference")
	private String externalReference;

    @Enumerated(EnumType.STRING)
	@Column(name = "open_order_type", length = 50, updatable = false)
    @NotNull
    private OpenOrderTypeEnum openOrderType;
    
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "open_order_template_id")
	@NotNull
    private OpenOrderTemplate openOrderTemplate;
	
	@Column(name = "quote_number", updatable = false)
	@NotNull
	private String quoteNumber;
	
	@Column(name = "max_amount")
	@NotNull
	private BigDecimal maxAmount;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "currency_id")
	@NotNull
    private TradingCurrency currency;
	
	@Column(name = "end_validity_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endOfValidityDate;
	
	@Column(name = "activation_date")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date activationDate;
	
	@OneToMany(mappedBy = "openOrderQuote", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Threshold> thresholds = new ArrayList<>();

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "open_order_quote_products", joinColumns = @JoinColumn(name = "open_order_quote_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "open_product_id", referencedColumnName = "id"))
    private List<OpenOrderProduct> products;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "open_order_quote_articles", joinColumns = @JoinColumn(name = "open_order_quote_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "open_article_id", referencedColumnName = "id"))
    private List<OpenOrderArticle> articles;
    
    @OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_account_id")
    @NotNull
    private BillingAccount billingAccount;
    
    @Column(name = "status")
	@Enumerated(EnumType.STRING)
    @NotNull
    private OpenOrderQuoteStatusEnum status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "open_order_quote_tags", joinColumns = @JoinColumn(name = "open_order_quote_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    private List<Tag> tags;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "open_order_id", nullable = false)
	private OpenOrder openOrder;

    public OpenOrderTypeEnum getOpenOrderType() {
        return openOrderType;
    }

    public void setOpenOrderType(OpenOrderTypeEnum openOrderType) {
        this.openOrderType = openOrderType;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Threshold> getThresholds() {
        return thresholds;
    }

    public void setThresholds(List<Threshold> thresholds) {
		if (CollectionUtils.isNotEmpty(thresholds)) {
			this.thresholds.clear();
			this.thresholds.addAll(thresholds);
		}
    }

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public OpenOrderTemplate getOpenOrderTemplate() {
		return openOrderTemplate;
	}

	public void setOpenOrderTemplate(OpenOrderTemplate openOrderTemplate) {
		this.openOrderTemplate = openOrderTemplate;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public TradingCurrency getCurrency() {
		return currency;
	}

	public void setCurrency(TradingCurrency currency) {
		this.currency = currency;
	}

	public Date getEndOfValidityDate() {
		return endOfValidityDate;
	}

	public void setEndOfValidityDate(Date endOfValidityDate) {
		this.endOfValidityDate = endOfValidityDate;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public BillingAccount getBillingAccount() {
		return billingAccount;
	}

	public void setBillingAccount(BillingAccount billingAccount) {
		this.billingAccount = billingAccount;
	}

	public OpenOrderQuoteStatusEnum getStatus() {
		return status;
	}

	public void setStatus(OpenOrderQuoteStatusEnum status) {
		this.status = status;
	}

	public List<OpenOrderProduct> getProducts() {
		return products;
	}

	public void setProducts(List<OpenOrderProduct> products) {
		this.products = products;
	}

	public List<OpenOrderArticle> getArticles() {
		return articles;
	}

	public void setArticles(List<OpenOrderArticle> articles) {
		this.articles = articles;
	}

	public String getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	public OpenOrder getOpenOrder() {
		return openOrder;
	}

	public void setOpenOrder(OpenOrder openOrder) {
		this.openOrder = openOrder;
	}
}
