package org.meveo.model.ordering;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.cpq.tags.Tag;

@Entity
@Table(name = "open_order_quote")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "open_order_quote_seq"),})
public class OpenOrderQuote extends BusinessEntity {
	
	@Column(name = "external_reference")
	private String externalReference;

    @Enumerated(EnumType.STRING)
	@Column(name = "open_order_type", length = 50)
    @NotNull
    private OpenOrderTypeEnum openOrderType;
    
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "open_order_template_id")
	@NotNull
    private OpenOrderTemplate openOrderTemplate;
	
	@Column(name = "open_order_number")
	@NotNull
	private String openOrderNumber;
	
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
	
	@OneToMany(mappedBy = "openOrderQuote", fetch = FetchType.LAZY)
    private List<Threshold> thresholds;

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
        this.thresholds = thresholds;
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

	public String getOpenOrderNumber() {
		return openOrderNumber;
	}

	public void setOpenOrderNumber(String openOrderNumber) {
		this.openOrderNumber = openOrderNumber;
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
}
