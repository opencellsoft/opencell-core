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

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
@FilterResults(property = "billingAccounts.billingAccount", entityClass = BillingAccount.class)
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
    
    private BigDecimal balance = BigDecimal.ZERO;
    private BigDecimal totalInvoiceBalance = BigDecimal.ZERO;
    // currently not use
    private Date terminationDate;
    private String dueDateDelayEL;

    @XmlElementWrapper(name = "paymentMethods")  
    @XmlElement(name="paymentMethod")
    private List<PaymentMethodDto> paymentMethods;

    /**
     * Field was deprecated in 4.6 version. Use 'paymentMethods' field instead
     */
    @Deprecated
    @XmlElement(name="paymentMethodEnum")
    private PaymentMethodEnum paymentMethod;

    /**
     * Use for GET / LIST only.
     */
    private BillingAccountsDto billingAccounts;

    public CustomerAccountDto() {
        super();
    }

    public CustomerAccountStatusEnum getStatus() {
        return status;
    }

    public void setStatus(CustomerAccountStatusEnum status) {
        this.status = status;
    }

    public String getCreditCategory() {
        return creditCategory;
    }

    public void setCreditCategory(String creditCategory) {
        this.creditCategory = creditCategory;
    }

    public Date getDateStatus() {
        return dateStatus;
    }

    public void setDateStatus(Date dateStatus) {
        this.dateStatus = dateStatus;
    }

    public Date getDateDunningLevel() {
        return dateDunningLevel;
    }

    public void setDateDunningLevel(Date dateDunningLevel) {
        this.dateDunningLevel = dateDunningLevel;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public DunningLevelEnum getDunningLevel() {
        return dunningLevel;
    }

    public void setDunningLevel(DunningLevelEnum dunningLevel) {
        this.dunningLevel = dunningLevel;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getTotalInvoiceBalance() {
        return totalInvoiceBalance;
    }

    public void setTotalInvoiceBalance(BigDecimal totalInvoiceBalance) {
        this.totalInvoiceBalance = totalInvoiceBalance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "CustomerAccountDto [customer=" + customer + ", currency=" + currency + ", language=" + language + ", status=" + status + ", creditCategory=" + creditCategory
                + ", dateStatus=" + dateStatus + ", dateDunningLevel=" + dateDunningLevel + ", contactInformation=" + contactInformation + ", dunningLevel=" + dunningLevel
                + ",  balance=" + balance + ", terminationDate=" + terminationDate
                + ", billingAccounts=" + billingAccounts + "]";
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public BillingAccountsDto getBillingAccounts() {
        return billingAccounts;
    }

    public void setBillingAccounts(BillingAccountsDto billingAccounts) {
        this.billingAccounts = billingAccounts;
    }

    public ContactInformationDto getContactInformation() {
        if (contactInformation == null) {
            contactInformation = new ContactInformationDto();
        }
        return contactInformation;
    }

    public void setContactInformation(ContactInformationDto contactInformation) {
        this.contactInformation = contactInformation;
    }

    public String getDueDateDelayEL() {
        return dueDateDelayEL;
    }

    public void setDueDateDelayEL(String dueDateDelayEL) {
        this.dueDateDelayEL = dueDateDelayEL;
    }

    public List<PaymentMethodDto> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethodDto> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

	public String getMandateIdentification() {
		return mandateIdentification;
	}

	public void setMandateIdentification(String mandateIdentification) {
		this.mandateIdentification = mandateIdentification;
	}

	public Date getMandateDate() {
		return mandateDate;
	}

	public void setMandateDate(Date mandateDate) {
		this.mandateDate = mandateDate;
	}
    
}