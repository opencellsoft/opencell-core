/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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

import org.meveo.api.dto.GDPRInfoDto;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.model.billing.ThresholdOptionsEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * Customer Account DTO.
 * 
 * @author Edward P. Legaspi
 * @author anasseh
 * @lastModifiedVersion 5.2
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

    /** The dunning level. */
    @Deprecated
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

    /** Due balance up to today including litigation (status=O, P, I; useDueDate=true) */
    private BigDecimal balance = BigDecimal.ZERO;

    /** Due balance including litigation, irrelevant of due date (status=O, P, I; ) */
    private BigDecimal totalBalance = BigDecimal.ZERO;

    /** Due balance excluding litigation, irrelevant of due date (status=O, P; ) */
    private BigDecimal totalBalanceExigible = BigDecimal.ZERO;

    /** Due balance up to today without litigation (status=O, P; useDueDate=true) */
    private BigDecimal totalInvoiceBalance = BigDecimal.ZERO;

    /** Account balance by transaction date up to today (status=O, P, I; useDueDate=false) */
    private BigDecimal accountBalance = BigDecimal.ZERO;
    /**
     * Credit balance (status=O, P, I; useDueDate=false; category=CREDIT)
     */
    private BigDecimal creditBalance = BigDecimal.ZERO;

    /** The termination date. */
    // currently not use
    private Date terminationDate;

    /**
     * Expression to calculate Invoice due date delay value
     */
    private String dueDateDelayEL;

    /** The payment methods. */
    @XmlElementWrapper(name = "paymentMethods")
    @XmlElement(name = "methodOfPayment")
    private List<PaymentMethodDto> paymentMethods;

    /** The excluded from payment. */
    private Boolean excludedFromPayment;

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
     * Invoicing threshold - do not invoice for a lesser amount.
     */
    private BigDecimal invoicingThreshold;

    /**
     * The option on how to check the threshold.
     */
    private ThresholdOptionsEnum checkThreshold;

    /**
     * 
     * check the threshold per entity/invoice.
     */
    @XmlElement
    private Boolean thresholdPerEntity;

    /** information GDPR **/
    private List<GDPRInfoDto> infoGdpr;

    /** General client account code **/
    private String generalClientAccountCode;

    public Boolean isThresholdPerEntity() {
		return thresholdPerEntity;
	}

	public void setThresholdPerEntity(Boolean thresholdPerEntity) {
		this.thresholdPerEntity = thresholdPerEntity;
	}

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
        if(e.getCreditCategory() != null) {
            setCreditCategory(e.getCreditCategory().getCode());
        }
        setDunningLevel(e.getDunningLevel());
        setDateStatus(e.getDateStatus());
        setDateDunningLevel(e.getDateDunningLevel());
        if (e.getContactInformation() != null) {
            setContactInformation(new ContactInformationDto(e.getContactInformation()));
        }
        setDueDateDelayEL(e.getDueDateDelayEL());
        setExcludedFromPayment(e.isExcludedFromPayment());
        setRegistrationNo(e.getRegistrationNo());
        setVatNo(e.getVatNo());

        if (e.getPaymentMethods() != null && !e.getPaymentMethods().isEmpty()) {
            setPaymentMethods(new ArrayList<>());
            for (PaymentMethod pm : e.getPaymentMethods()) {
                getPaymentMethods().add(new PaymentMethodDto(pm));
            }

            // Start compatibility with pre-4.6 versions
            setPaymentMethod(e.getPaymentMethods().get(0).getPaymentType());
            // End compatibility with pre-4.6 versions
        }
        if (e.getMinimumAmountEl() != null) {
            setMinimumAmountEl(e.getMinimumAmountEl());
        }
        if (e.getMinimumLabelEl() != null) {
            setMinimumLabelEl(e.getMinimumLabelEl());
        }
        if (e.getMinimumTargetAccount() != null) {
            setMinimumTargetAccount(e.getMinimumTargetAccount().getCode());
        }
        if (e.getInvoicingThreshold() != null) {
            setInvoicingThreshold(e.getInvoicingThreshold());
        }
        if (e.getCheckThreshold() != null) {
            setCheckThreshold(e.getCheckThreshold());
            setThresholdPerEntity(e.isThresholdPerEntity());
        }
    }
    
    public CustomerAccountDto(CustomerAccount e, List<GDPRInfoDto> gdpr) {
    	this(e);
    	if(gdpr != null && !gdpr.isEmpty()) {
    		setInfoGdpr(gdpr);
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
     * @return Due balance up to today including litigation
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * @param balance Due balance up to today including litigation
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * @return Due balance including litigation, irrelevant of due date
     */
    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    /**
     * @param totalBalance Due balance including litigation, irrelevant of due date
     */
    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    /**
     * @return Due balance excluding litigation, irrelevant of due date
     */
    public BigDecimal getTotalBalanceExigible() {
        return totalBalanceExigible;
    }

    /**
     * @param totalBalanceExigible Due balance excluding litigation, irrelevant of due date
     */
    public void setTotalBalanceExigible(BigDecimal totalBalanceExigible) {
        this.totalBalanceExigible = totalBalanceExigible;
    }

    /**
     * @return Due balance up to today without litigation
     */
    public BigDecimal getTotalInvoiceBalance() {
        return totalInvoiceBalance;
    }

    /**
     * @param totalInvoiceBalance Due balance up to today without litigation
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
    public Boolean isExcludedFromPayment() {
        return excludedFromPayment;
    }

    /**
     * Sets the excluded from payment.
     *
     * @param excludedFromPayment the excludedFromPayment to set
     */
    public void setExcludedFromPayment(Boolean excludedFromPayment) {
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
     * @return Account balance by transaction date up to today
     */
    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    /**
     * @param accountBalance Account balance by transaction date up to today
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
        return "CustomerAccountDto [code=" + code + ", customer=" + customer + ", currency=" + currency + ", language=" + language + ", status=" + status + ", creditCategory=" + creditCategory + ", dateStatus="
                + dateStatus + ", dateDunningLevel=" + dateDunningLevel + ", contactInformation=" + getContactInformation() + ", dunningLevel=" + dunningLevel + ",  balance=" + balance + ", terminationDate="
                + terminationDate + ", billingAccounts=" + billingAccounts + "]";
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

    /**
     * Gets the threshold option.
     *
     * @return the threshold option
     */
    public ThresholdOptionsEnum getCheckThreshold() {
        return checkThreshold;
    }

    /**
     * Sets the threshold option.
     *
     * @param checkThreshold the threshold option
     */
    public void setCheckThreshold(ThresholdOptionsEnum checkThreshold) {
        this.checkThreshold = checkThreshold;
    }

	/**
	 * @return the infoGdpr
	 */
	public List<GDPRInfoDto> getInfoGdpr() {
		return infoGdpr;
	}

	/**
	 * @param infoGdpr the infoGdpr to set
	 */
	public void setInfoGdpr(List<GDPRInfoDto> infoGdpr) {
		this.infoGdpr = infoGdpr;
	}

    public String getGeneralClientAccountCode() {
        return generalClientAccountCode;
    }

    public void setGeneralClientAccountCode(String generalClientAccountCode) {
        this.generalClientAccountCode = generalClientAccountCode;
    }
}