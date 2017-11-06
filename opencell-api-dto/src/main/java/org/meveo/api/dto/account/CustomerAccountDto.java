package org.meveo.api.dto.account;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * Customer Account DTO
 * 
 * @author anasseh
 *
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
@FilterResults(propertyToFilter = "billingAccounts.billingAccount", itemPropertiesToFilter = { @FilterProperty(property = "code", entityClass = BillingAccount.class) })
public class CustomerAccountDto extends AccountDto {

    private static final long serialVersionUID = -137632696663739285L;

    @XmlElement(required = true)
    private String customer;

    @XmlElement(required = true)
    private String currency;

    @XmlElement(required = true)
    private String language;

    private CustomerAccountStatusEnum status;
    private String creditCategory;
    private Date dateStatus;
    private Date dateDunningLevel;

    private ContactInformationDto contactInformation;

    private DunningLevelEnum dunningLevel;
    /**
     * Field was deprecated in 4.6 version. Use 'DDpaymentMethods' field instead
     */
    @Deprecated
    private String mandateIdentification;
    /**
     * Field was deprecated in 4.6 version. Use 'DDpaymentMethods' field instead
     */
    @Deprecated
    private Date mandateDate;

    /**
     * Balance due. status=O, P, I; isDue=false, dunning=false
     */
    private BigDecimal balance = BigDecimal.ZERO;
    /**
     * exigibleWithoutLitigation; status=O, P, isDue=true, dunning=true
     */
    private BigDecimal totalInvoiceBalance = BigDecimal.ZERO;
    /**
     * exigible; status=O, P, isDue=true, dunning=true
     */
    private BigDecimal accountBalance = BigDecimal.ZERO;
    /**
     * Blance. status=O, P, I; isDue=false, dunning=false, category=CREDIT.
     */
    private BigDecimal creditBalance = BigDecimal.ZERO;
    
    // currently not use
    private Date terminationDate;
    private String dueDateDelayEL;

    @XmlElementWrapper(name = "paymentMethods")
    @XmlElement(name = "methodOfPayment")
    private List<PaymentMethodDto> paymentMethods;

    private boolean excludedFromPayment;

    /**
     * Field was deprecated in 4.6 version. Use 'paymentMethods' field instead.
     */
    @Deprecated
    private PaymentMethodEnum paymentMethod;

    /**
     * Use for GET / LIST only.
     */
    private BillingAccountsDto billingAccounts;

    public CustomerAccountDto() {
        super();
    }

    @Override
    public String toString() {
        return "CustomerAccountDto [customer=" + customer + ", currency=" + currency + ", language=" + language + ", status=" + status + ", creditCategory=" + creditCategory
                + ", dateStatus=" + dateStatus + ", dateDunningLevel=" + dateDunningLevel + ", contactInformation=" + contactInformation + ", dunningLevel=" + dunningLevel
                + ",  balance=" + balance + ", terminationDate=" + terminationDate + ", billingAccounts=" + billingAccounts + "]";
    }

    /**
     * @return the customer
     */
    public String getCustomer() {
        return customer;
    }

    /**
     * @param customer the customer to set
     */
    public void setCustomer(String customer) {
        this.customer = customer;
    }

    /**
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the status
     */
    public CustomerAccountStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(CustomerAccountStatusEnum status) {
        this.status = status;
    }

    /**
     * @return the creditCategory
     */
    public String getCreditCategory() {
        return creditCategory;
    }

    /**
     * @param creditCategory the creditCategory to set
     */
    public void setCreditCategory(String creditCategory) {
        this.creditCategory = creditCategory;
    }

    /**
     * @return the dateStatus
     */
    public Date getDateStatus() {
        return dateStatus;
    }

    /**
     * @param dateStatus the dateStatus to set
     */
    public void setDateStatus(Date dateStatus) {
        this.dateStatus = dateStatus;
    }

    /**
     * @return the dateDunningLevel
     */
    public Date getDateDunningLevel() {
        return dateDunningLevel;
    }

    /**
     * @param dateDunningLevel the dateDunningLevel to set
     */
    public void setDateDunningLevel(Date dateDunningLevel) {
        this.dateDunningLevel = dateDunningLevel;
    }

    /**
     * @return the contactInformation
     */
    public ContactInformationDto getContactInformation() {
        return contactInformation;
    }

    /**
     * @param contactInformation the contactInformation to set
     */
    public void setContactInformation(ContactInformationDto contactInformation) {
        this.contactInformation = contactInformation;
    }

    /**
     * @return the dunningLevel
     */
    public DunningLevelEnum getDunningLevel() {
        return dunningLevel;
    }

    /**
     * @param dunningLevel the dunningLevel to set
     */
    public void setDunningLevel(DunningLevelEnum dunningLevel) {
        this.dunningLevel = dunningLevel;
    }

    /**
     * @return the mandateIdentification
     */
    public String getMandateIdentification() {
        return mandateIdentification;
    }

    /**
     * @param mandateIdentification the mandateIdentification to set
     */
    public void setMandateIdentification(String mandateIdentification) {
        this.mandateIdentification = mandateIdentification;
    }

    /**
     * @return the mandateDate
     */
    public Date getMandateDate() {
        return mandateDate;
    }

    /**
     * @param mandateDate the mandateDate to set
     */
    public void setMandateDate(Date mandateDate) {
        this.mandateDate = mandateDate;
    }

    /**
     * @return the balance
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * @return the totalInvoiceBalance
     */
    public BigDecimal getTotalInvoiceBalance() {
        return totalInvoiceBalance;
    }

    /**
     * @param totalInvoiceBalance the totalInvoiceBalance to set
     */
    public void setTotalInvoiceBalance(BigDecimal totalInvoiceBalance) {
        this.totalInvoiceBalance = totalInvoiceBalance;
    }

    /**
     * @return the terminationDate
     */
    public Date getTerminationDate() {
        return terminationDate;
    }

    /**
     * @param terminationDate the terminationDate to set
     */
    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    /**
     * @return the dueDateDelayEL
     */
    public String getDueDateDelayEL() {
        return dueDateDelayEL;
    }

    /**
     * @param dueDateDelayEL the dueDateDelayEL to set
     */
    public void setDueDateDelayEL(String dueDateDelayEL) {
        this.dueDateDelayEL = dueDateDelayEL;
    }

    /**
     * @return the paymentMethods
     */
    public List<PaymentMethodDto> getPaymentMethods() {
        return paymentMethods;
    }

    /**
     * @param paymentMethods the paymentMethods to set
     */
    public void setPaymentMethods(List<PaymentMethodDto> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    /**
     * @return the excludedFromPayment
     */
    public boolean isExcludedFromPayment() {
        return excludedFromPayment;
    }

    /**
     * @param excludedFromPayment the excludedFromPayment to set
     */
    public void setExcludedFromPayment(boolean excludedFromPayment) {
        this.excludedFromPayment = excludedFromPayment;
    }

    /**
     * @return the paymentMethod
     */
    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * @param paymentMethod the paymentMethod to set
     */
    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * @return the billingAccounts
     */
    public BillingAccountsDto getBillingAccounts() {
        return billingAccounts;
    }

    /**
     * @param billingAccounts the billingAccounts to set
     */
    public void setBillingAccounts(BillingAccountsDto billingAccounts) {
        this.billingAccounts = billingAccounts;
    }

	public BigDecimal getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}

	public BigDecimal getCreditBalance() {
		return creditBalance;
	}

	public void setCreditBalance(BigDecimal creditBalance) {
		this.creditBalance = creditBalance;
	}
}