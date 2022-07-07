package org.meveo.model.ordering;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.tags.Tag;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "open_order")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "open_order_seq"),})
@NamedQueries({
		@NamedQuery(name = "OpenOrder.getOpenOrderCompatibleForIL", query = "SELECT oo FROM OpenOrder oo left join oo.products product left join oo.articles article"
				+ " WHERE oo.billingAccount.id = :billingAccountId AND oo.balance >= :ilAmountWithTax AND oo.status != :status"
				+ " AND oo.endOfValidityDate >= :ilValueDate AND oo.activationDate <= :ilValueDate"
				+ " AND (product.id = :productId or article.id = :articleId)"),
		@NamedQuery(name = "OpenOrder.availableOOForProduct", query = "SELECT oo FROM OpenOrder oo join fetch oo.products product"
				+ " WHERE oo.billingAccount.id = :billingAccountId AND oo.balance > 0 AND oo.status != :status"
				+ " AND oo.endOfValidityDate >= :eventDate AND product.id = :productId ORDER BY oo.endOfValidityDate"),
		@NamedQuery(name = "OpenOrder.availableOOForArticle", query = "SELECT oo FROM OpenOrder oo join fetch oo.articles article"
				+ " WHERE oo.billingAccount.id = :billingAccountId AND oo.balance > 0 AND oo.status != :status"
				+ " AND oo.endOfValidityDate >= :eventDate AND article.id = :articleId ORDER BY oo.endOfValidityDate")
})
public class OpenOrder extends BusinessEntity {

    @Column(name = "external_reference")
    private String externalReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50)
    @NotNull
    private OpenOrderTypeEnum type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 25)
    @NotNull
    private OpenOrderStatusEnum status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_order_template_id")
    @NotNull
    private OpenOrderTemplate openOrderTemplate;

    @Column(name = "open_order_number")
    @NotNull
    private String openOrderNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id")
    @NotNull
    private BillingAccount billingAccount;

    @Column(name = "initial_amount")
    @NotNull
    private BigDecimal initialAmount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id")
    @NotNull
    private TradingCurrency currency;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_order_quote_id")
    private OpenOrderQuote openOrderQuote;

    @Column(name = "end_of_validity_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endOfValidityDate;

    @Column(name = "activation_date")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date activationDate;

    @OneToMany(mappedBy = "openOrder", fetch = FetchType.LAZY)
    private List<Threshold> thresholds;

    @OneToMany(mappedBy = "openOrder", fetch = FetchType.LAZY)
    private List<Product> products;

    @OneToMany(mappedBy = "openOrder", fetch = FetchType.LAZY)
    private List<AccountingArticle> articles;

    @Column(name = "balance")
    private BigDecimal balance;

    @OneToMany(mappedBy = "openOrder", fetch = FetchType.LAZY)
    private List<Tag> tags;

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public OpenOrderTypeEnum getType() {
        return type;
    }

    public void setType(OpenOrderTypeEnum type) {
        this.type = type;
    }

    public OpenOrderStatusEnum getStatus() {
        return status;
    }

    public void setStatus(OpenOrderStatusEnum status) {
        this.status = status;
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

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }

    public TradingCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(TradingCurrency currency) {
        this.currency = currency;
    }

    public OpenOrderQuote getOpenOrderQuote() {
        return openOrderQuote;
    }

    public void setOpenOrderQuote(OpenOrderQuote openOrderQuote) {
        this.openOrderQuote = openOrderQuote;
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

    public List<Threshold> getThresholds() {
        return thresholds;
    }

    public void setThresholds(List<Threshold> thresholds) {
        this.thresholds = thresholds;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<AccountingArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<AccountingArticle> articles) {
        this.articles = articles;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @PostPersist
    public void postPersist() {
        this.code = this.code.substring(0, this.code.length() - 13) + this.id;
    }
}