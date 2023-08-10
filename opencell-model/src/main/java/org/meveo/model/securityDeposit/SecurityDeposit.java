package org.meveo.model.securityDeposit;

import java.math.BigDecimal;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.cpq.Product;
import org.meveo.model.payments.CustomerAccount;

@Entity
@Table(name = "security_deposit")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "security_deposit_seq"), })
@NamedQueries({
    @NamedQuery(name = "SecurityDeposit.sumAmountPerClient", query = "SELECT SUM(s.amount) FROM SecurityDeposit s WHERE s.customerAccount=:customerAccount"),
    @NamedQuery(name = "SecurityDeposit.countPerTemplate", query = "SELECT COUNT(s.id) FROM SecurityDeposit s WHERE s.template=:template"),
    @NamedQuery(name = "SecurityDeposit.securityDepositsToRefundIds", query = "SELECT s.id FROM SecurityDeposit s WHERE (s.validityDate <:sysDate or (s.validityPeriodUnit IS NOT NULL and s.validityPeriod IS NOT NULL)) and (s.status = 'LOCKED' or s.status = 'UNLOCKED' or s.status = 'HOLD')"),
    @NamedQuery(name = "SecurityDeposit.securityDepositsByInvoiceId", query = "SELECT s FROM SecurityDeposit s WHERE s.securityDepositInvoice.id = :invoiceId")
})
public class SecurityDeposit extends BusinessCFEntity {

    /**
     * 
     */
    private static final long serialVersionUID = -3362135495269999537L;

    @ManyToOne
    @JoinColumn(name = "templat_id")
    private SecurityDepositTemplate template;

    @ManyToOne
    @JoinColumn(name = "currency_id")
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
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private SecurityDepositStatusEnum status = SecurityDepositStatusEnum.DRAFT;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @Column(name = "external_reference")
    private String externalReference;

    @Column(name = "invoice_receipt_number")
    private String invoiceReceiptNumber;

    @ManyToOne
    @JoinColumn(name = "service_instance_id")
    private ServiceInstance serviceInstance;

    @Column(name = "refund_reason")
    private String refundReason;
    
    @Column(name = "cancel_reason")
    private String cancelReason;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sd_invoice_id")
    private Invoice securityDepositInvoice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id", nullable = false)
    private BillingAccount billingAccount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sd_adjustment_id")
    private Invoice securityDepositAdjustment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;
    
    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public Invoice getSecurityDepositInvoice() {
        return securityDepositInvoice;
    }

    public void setSecurityDepositInvoice(Invoice securityDepositInvoice) {
        this.securityDepositInvoice = securityDepositInvoice;
    }

    public SecurityDepositTemplate getTemplate() {
        return template;
    }

    public void setTemplate(SecurityDepositTemplate template) {
        this.template = template;
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

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getInvoiceReceiptNumber() {
        return invoiceReceiptNumber;
    }

    public void setInvoiceReceiptNumber(String invoiceReceiptNumber) {
        this.invoiceReceiptNumber = invoiceReceiptNumber;
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }
    
    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Invoice getSecurityDepositAdjustment() {
        return securityDepositAdjustment;
    }

    public void setSecurityDepositAdjustment(Invoice securityDepositAdjustment) {
        this.securityDepositAdjustment = securityDepositAdjustment;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

}
