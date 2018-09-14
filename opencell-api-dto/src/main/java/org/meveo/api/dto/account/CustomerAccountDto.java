package org.meveo.api.dto.account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * Customer Account DTO.
 *
 * @author anasseh
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
// @FilterResults(propertyToFilter = "billingAccounts.billingAccount", itemPropertiesToFilter = { @FilterProperty(property = "code", entityClass = BillingAccount.class) })
public class CustomerAccountDto extends AccountDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -137632696663739285L;

    /** The customer. */
    @XmlElement(required = true)
    private String customer;

    /** The currency. */
    @XmlElement(required = true)
    private String currency;

    /** The language. */
    @XmlElement(required = true)
    private String language;

    /** The status. */
    private CustomerAccountStatusEnum status;

    /** The credit category. */
    private String creditCategory;

    /** The date status. */
    private Date dateStatus;

    /** The date dunning level. */
    private Date dateDunningLevel;

    /** The contact information. */
    private ContactInformationDto contactInformation;

    /** The dunning level. */
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

    /** balanceExigible (status=O, P; isDue=true; dunning=false) - creditBalance. */
    private BigDecimal balance = BigDecimal.ZERO;

    /** exigibleWithoutLitigation; status=O, P; isDue=true; dunning=true. */
    private BigDecimal totalInvoiceBalance = BigDecimal.ZERO;

    /** totalInvoiceBalance - creditBalance. */
    private BigDecimal accountBalance = BigDecimal.ZERO;
    /**
     * Balance. status=O, P, I; isDue=false; dunning=false; category=CREDIT.
     */
    private BigDecimal creditBalance = BigDecimal.ZERO;

    /** The termination date. */
    // currently not use
    private Date terminationDate;

    /**
     * Expression to calculate Invoice due date delay value
     */
    private String dueDateDelayEL;

    /**
     * Expression to calculate Invoice due date delay value - for Spark
     */
    private String dueDateDelayELSpark;

    /** The payment methods. */
    @XmlElementWrapper(name = "paymentMethods")
    @XmlElement(name = "methodOfPayment")
    private List<PaymentMethodDto> paymentMethods;

    /** The excluded from payment. */
    private boolean excludedFromPayment;

    /**
     * Field was deprecated in 4.6 version. Use 'paymentMethods' field instead.
     */
    @Deprecated
    private PaymentMethodEnum paymentMethod;

    /**
     * Use for GET / LIST only.
     */
    private BillingAccountsDto billingAccounts = new BillingAccountsDto();

    private List<AccountOperationDto> accountOperations;

    /**
     * Instantiates a new customer account dto.
     */
    public CustomerAccountDto() {
        super();
    }

    /**
     * Instantiates a new customer account dto.
     * 
     * @param e CustomerAccount entity
     */
    public CustomerAccountDto(CustomerAccount e) {
        super(e);

        if (e.getCustomer() != null) {
            setCustomer(e.getCustomer().getCode());
        }

        if (e.getTradingCurrency() != null) {
            setCurrency(e.getTradingCurrency().getCurrencyCode());
        }

        if (e.getTradingLanguage() != null) {
            setLanguage(e.getTradingLanguage().getLanguageCode());
        }

        setStatus(e.getStatus());
        setDateStatus(e.getDateStatus());
        try {
            setCreditCategory(e.getCreditCategory().getCode());
        } catch (NullPointerException ex) {
        }
        setDunningLevel(e.getDunningLevel());
        setDateStatus(e.getDateStatus());
        setDateDunningLevel(e.getDateDunningLevel());
        if (e.getContactInformation() != null) {
            setContactInformation(new ContactInformationDto(e.getContactInformation()));
        }
        setDueDateDelayEL(e.getDueDateDelayEL());
        setDueDateDelayELSpark(e.getDueDateDelayELSpark());
        setExcludedFromPayment(e.isExcludedFromPayment());

        if (e.getPaymentMethods() != null && !e.getPaymentMethods().isEmpty()) {
            setPaymentMethods(new ArrayList<>());
            for (PaymentMethod pm : e.getPaymentMethods()) {
                getPaymentMethods().add(new PaymentMethodDto(pm));
            }

            // Start compatibility with pre-4.6 versions
            setPaymentMethod(e.getPaymentMethods().get(0).getPaymentType());
            // End compatibility with pre-4.6 versions
        }
    }

    /**
     * Gets the customer.
     *
     * @return the customer
     */
    public String getCustomer() {
        return customer;
    }

    /**
     * Sets the customer.
     *
     * @param customer the customer to set
     */
    public void setCustomer(String customer) {
        this.customer = customer;
    }

    /**
     * Gets the currency.
     *
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency.
     *
     * @param currency the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the language.
     *
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language.
     *
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public CustomerAccountStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the status to set
     */
    public void setStatus(CustomerAccountStatusEnum status) {
        this.status = status;
    }

    /**
     * Gets the credit category.
     *
     * @return the creditCategory
     */
    public String getCreditCategory() {
        return creditCategory;
    }

    /**
     * Sets the credit category.
     *
     * @param creditCategory the creditCategory to set
     */
    public void setCreditCategory(String creditCategory) {
        this.creditCategory = creditCategory;
    }

    /**
     * Gets the date status.
     *
     * @return the dateStatus
     */
    public Date getDateStatus() {
        return dateStatus;
    }

    /**
     * Sets the date status.
     *
     * @param dateStatus the dateStatus to set
     */
    public void setDateStatus(Date dateStatus) {
        this.dateStatus = dateStatus;
    }

    /**
     * Gets the date dunning level.
     *
     * @return the dateDunningLevel
     */
    public Date getDateDunningLevel() {
        return dateDunningLevel;
    }

    /**
     * Sets the date dunning level.
     *
     * @param dateDunningLevel the dateDunningLevel to set
     */
    public void setDateDunningLevel(Date dateDunningLevel) {
        this.dateDunningLevel = dateDunningLevel;
    }

    /**
     * Gets the contact information.
     *
     * @return the contactInformation
     */
    public ContactInformationDto getContactInformation() {
        return contactInformation;
    }

    /**
     * Sets the contact information.
     *
     * @param contactInformation the contactInformation to set
     */
    public void setContactInformation(ContactInformationDto contactInformation) {
        this.contactInformation = contactInformation;
    }

    /**
     * Gets the dunning level.
     *
     * @return the dunningLevel
     */
    public DunningLevelEnum getDunningLevel() {
        return dunningLevel;
    }

    /**
     * Sets the dunning level.
     *
     * @param dunningLevel the dunningLevel to set
     */
    public void setDunningLevel(DunningLevelEnum dunningLevel) {
        this.dunningLevel = dunningLevel;
    }

    /**
     * Gets the mandate identification.
     *
     * @return the mandateIdentification
     */
    public String getMandateIdentification() {
        return mandateIdentification;
    }

    /**
     * Sets the mandate identification.
     *
     * @param mandateIdentification the mandateIdentification to set
     */
    public void setMandateIdentification(String mandateIdentification) {
        this.mandateIdentification = mandateIdentification;
    }

    /**
     * Gets the mandate date.
     *
     * @return the mandateDate
     */
    public Date getMandateDate() {
        return mandateDate;
    }

    /**
     * Sets the mandate date.
     *
     * @param mandateDate the mandateDate to set
     */
    public void setMandateDate(Date mandateDate) {
        this.mandateDate = mandateDate;
    }

    /**
     * Gets the balance.
     *
     * @return the balance
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Sets the balance.
     *
     * @param balance the balance to set
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * Gets the total invoice balance.
     *
     * @return the totalInvoiceBalance
     */
    public BigDecimal getTotalInvoiceBalance() {
        return totalInvoiceBalance;
    }

    /**
     * Sets the total invoice balance.
     *
     * @param totalInvoiceBalance the totalInvoiceBalance to set
     */
    public void setTotalInvoiceBalance(BigDecimal totalInvoiceBalance) {
        this.totalInvoiceBalance = totalInvoiceBalance;
    }

    /**
     * Gets the termination date.
     *
     * @return the terminationDate
     */
    public Date getTerminationDate() {
        return terminationDate;
    }

    /**
     * Sets the termination date.
     *
     * @param terminationDate the terminationDate to set
     */
    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    /**
     * @return Expression to calculate Invoice due date delay value
     */
    public String getDueDateDelayEL() {
        return dueDateDelayEL;
    }

    /**
     * @param dueDateDelayEL Expression to calculate Invoice due date delay value
     */
    public void setDueDateDelayEL(String dueDateDelayEL) {
        this.dueDateDelayEL = dueDateDelayEL;
    }

    /**
     * @return Expression to calculate Invoice due date delay value - for Spark
     */
    public String getDueDateDelayELSpark() {
        return dueDateDelayELSpark;
    }

    /**
     * @param dueDateDelayELSpark Expression to calculate Invoice due date delay value - for Spark
     */
    public void setDueDateDelayELSpark(String dueDateDelayELSpark) {
        this.dueDateDelayELSpark = dueDateDelayELSpark;
    }

    /**
     * Gets the payment methods.
     *
     * @return the paymentMethods
     */
    public List<PaymentMethodDto> getPaymentMethods() {
        return paymentMethods;
    }

    /**
     * Sets the payment methods.
     *
     * @param paymentMethods the paymentMethods to set
     */
    public void setPaymentMethods(List<PaymentMethodDto> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    /**
     * Checks if is excluded from payment.
     *
     * @return the excludedFromPayment
     */
    public boolean isExcludedFromPayment() {
        return excludedFromPayment;
    }

    /**
     * Sets the excluded from payment.
     *
     * @param excludedFromPayment the excludedFromPayment to set
     */
    public void setExcludedFromPayment(boolean excludedFromPayment) {
        this.excludedFromPayment = excludedFromPayment;
    }

    /**
     * Gets the payment method.
     *
     * @return the paymentMethod
     */
    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the payment method.
     *
     * @param paymentMethod the paymentMethod to set
     */
    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * Gets the billing accounts.
     *
     * @return the billingAccounts
     */
    public BillingAccountsDto getBillingAccounts() {
        return billingAccounts;
    }

    /**
     * Sets the billing accounts.
     *
     * @param billingAccounts the billingAccounts to set
     */
    public void setBillingAccounts(BillingAccountsDto billingAccounts) {
        this.billingAccounts = billingAccounts;
    }

    /**
     * Gets the account balance.
     *
     * @return the account balance
     */
    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    /**
     * Sets the account balance.
     *
     * @param accountBalance the new account balance
     */
    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }

    /**
     * Gets the credit balance.
     *
     * @return the credit balance
     */
    public BigDecimal getCreditBalance() {
        return creditBalance;
    }

    /**
     * Sets the credit balance.
     *
     * @param creditBalance the new credit balance
     */
    public void setCreditBalance(BigDecimal creditBalance) {
        this.creditBalance = creditBalance;
    }

    @Override
    public String toString() {
        return "CustomerAccountDto [code=" + code + ", customer=" + customer + ", currency=" + currency + ", language=" + language + ", status=" + status + ", creditCategory="
                + creditCategory + ", dateStatus=" + dateStatus + ", dateDunningLevel=" + dateDunningLevel + ", contactInformation=" + contactInformation + ", dunningLevel="
                + dunningLevel + ",  balance=" + balance + ", terminationDate=" + terminationDate + ", billingAccounts=" + billingAccounts + "]";
    }

    public List<AccountOperationDto> getAccountOperations() {
        if (accountOperations == null) {
            accountOperations = new ArrayList<>();
        }
        return accountOperations;
    }

    public void setAccountOperations(List<AccountOperationDto> accountOperations) {
        this.accountOperations = accountOperations;
    }
}