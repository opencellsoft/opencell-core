package org.meveo.api.dto.account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
@FilterResults(propertyToFilter = "userAccounts.userAccount", itemPropertiesToFilter = { @FilterProperty(property = "code", entityClass = UserAccount.class) })
public class BillingAccountDto extends AccountDto {

    private static final long serialVersionUID = 8701417481481359155L;

    @XmlElement(required = true)
    private String customerAccount;

    @XmlElement(required = true)
    private String billingCycle;

    @XmlElement(required = true)
    private String country;

    @XmlElement(required = true)
    private String language;

    private Date nextInvoiceDate;
    private Date subscriptionDate;
    private Date terminationDate;
    private Boolean electronicBilling;
    private AccountStatusEnum status;
    private Date statusDate;
    private String terminationReason;
    private String email;
    private List<InvoiceDto> invoices = new ArrayList<>();
    private BigDecimal invoicingThreshold;
    private String discountPlan;
    protected String phone;

    /**
     * Field was deprecated in 4.6 version. Use 'paymentMethods' field on CustomerAccount entity instead.
     */
    @Deprecated
    private PaymentMethodEnum paymentMethod;

    /**
     * Field was deprecated in 4.6 version. Use 'paymentMethods' field on CustomerAccount entity instead.
     */
    @Deprecated
    private BankCoordinatesDto bankCoordinates;

    /**
     * Field was deprecated in 4.6 version. Use custom fields instead.
     */
    @Deprecated
    private String paymentTerms;

    /**
     * Use for GET / LIST only.
     */
    private UserAccountsDto userAccounts = new UserAccountsDto();

    public BillingAccountDto() {

    }

    public String getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(String customerAccount) {
        this.customerAccount = customerAccount;
    }

    public String getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Date getNextInvoiceDate() {
        return nextInvoiceDate;
    }

    public void setNextInvoiceDate(Date nextInvoiceDate) {
        this.nextInvoiceDate = nextInvoiceDate;
    }

    public Boolean getElectronicBilling() {
        return electronicBilling;
    }

    public void setElectronicBilling(Boolean electronicBilling) {
        this.electronicBilling = electronicBilling;
    }

    @Override
    public String toString() {
        return "BillingAccountDto [code=" + code + ", description=" + description + "]";
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public AccountStatusEnum getStatus() {
        return status;
    }

    public void setStatus(AccountStatusEnum status) {
        this.status = status;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public String getTerminationReason() {
        return terminationReason;
    }

    public void setTerminationReason(String terminationReason) {
        this.terminationReason = terminationReason;
    }

    public UserAccountsDto getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(UserAccountsDto userAccounts) {
        this.userAccounts = userAccounts;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<InvoiceDto> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<InvoiceDto> invoices) {
        this.invoices = invoices;
    }

    /**
     * @return the invoicingThreshold
     */
    public BigDecimal getInvoicingThreshold() {
        return invoicingThreshold;
    }

    /**
     * @param invoicingThreshold the invoicingThreshold to set
     */
    public void setInvoicingThreshold(BigDecimal invoicingThreshold) {
        this.invoicingThreshold = invoicingThreshold;
    }

    public String getDiscountPlan() {
        return discountPlan;
    }

    public void setDiscountPlan(String discountPlan) {
        this.discountPlan = discountPlan;
    }

    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BankCoordinatesDto getBankCoordinates() {
        return bankCoordinates;
    }

    public void setBankCoordinates(BankCoordinatesDto bankCoordinates) {
        this.bankCoordinates = bankCoordinates;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}