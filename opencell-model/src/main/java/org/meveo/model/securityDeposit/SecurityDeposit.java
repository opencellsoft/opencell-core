package org.meveo.model.securityDeposit;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.Subscription;
import org.meveo.model.cpq.Product;
import org.meveo.model.payments.CustomerAccount;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "security_deposit")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "security_deposit_seq"),})
public class SecurityDeposit extends BusinessCFEntity {

    @ManyToOne
    @JoinColumn(name = "templat_id")
    private SecurityDepositTemplate templat;

    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "customer_account_id")
    private CustomerAccount customerAccount;

    @Column(name = "validity_date")
    @Temporal(TemporalType.DATE)
    private Date validityDate;

    @Column(name = "validity_period")
    private Integer validityPeriod;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "validity_period_unit")
    private ValidityPeriodUnit validityPeriodUnit;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "current_balance")
    private BigDecimal currentBalance;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private SecurityDepositStatusEnum status;

    @Column(name = "product_instance")
    private String productInstance;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


    @Column(name = "external_reference")
    private String externalReference;

    public SecurityDepositTemplate getTemplat() {
        return templat;
    }

    public void setTemplat(SecurityDepositTemplate templat) {
        this.templat = templat;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(CustomerAccount customerAccount) {
        this.customerAccount = customerAccount;
    }

    public Date getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(Date validityDate) {
        this.validityDate = validityDate;
    }

    public Integer getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(Integer validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public ValidityPeriodUnit getValidityPeriodUnit() {
        return validityPeriodUnit;
    }

    public void setValidityPeriodUnit(ValidityPeriodUnit validityPeriodUnit) {
        this.validityPeriodUnit = validityPeriodUnit;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public SecurityDepositStatusEnum getStatus() {
        return status;
    }

    public void setStatus(SecurityDepositStatusEnum status) {
        this.status = status;
    }

    public String getProductInstance() {
        return productInstance;
    }

    public void setProductInstance(String productInstance) {
        this.productInstance = productInstance;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }
}



