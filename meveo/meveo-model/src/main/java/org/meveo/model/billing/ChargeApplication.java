/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BusinessEntity;

/**
 * @author R.AITYAAZZA
 * 
 */
@Entity
@Table(name = "BILLING_CHARGE_APPLIC")
@AttributeOverrides( { @AttributeOverride(name = "code", column = @Column(name = "code", unique = false)) })
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_CHARGE_APPLIC_SEQ")
public class ChargeApplication extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "STATUS_DATE")
    private Date statusDate;

	
    @Temporal(TemporalType.DATE)
    @Column(name = "APPLICATION_DATE")
    private Date applicationDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "SUBSCRIPTION_DATE")
    private Date subscriptionDate;

    @Column(name = "PARAMETER_1")
    private String parameter1;

    @Column(name = "PARAMETER_2")
    private String parameter2;

    @Column(name = "PARAMETER_3")
    private String parameter3;

    @Column(name = "PARAMETER_4")
    private String parameter4;

    @Column(name = "AMOUNT_WITHOUT_TAX", precision = 23, scale = 12)
    private BigDecimal amountWithoutTax;

    @Column(name = "AMOUNT_2", precision = 23, scale = 12)
    private BigDecimal amount2;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private ApplicationChgStatusEnum status;

    @Column(name = "APPLICATION_TYPE")
    @Enumerated(EnumType.STRING)
    private ApplicationTypeEnum applicationType;

    @Column(name = "APPLICATION_MODE")
    @Enumerated(EnumType.STRING)
    private ChargeApplicationModeEnum applicationMode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBSCRIPTION_ID")
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHARGE_INST_ID")
    private ChargeInstance chargeInstance;

    @Column(name = "CHARGE_CODE")
    private String chargeCode;

    @Column(name = "QUANTITY")
    protected Integer quantity;

    @Column(name = "TAX_CODE")
    private String taxCode;

    @Column(name = "TAX_PERCENT", precision = 23, scale = 12)
    private BigDecimal taxPercent;

    @Column(name = "DISCOUNT_PERCENT", precision = 23, scale = 12)
    private BigDecimal discountPercent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_SUB_CATEGORY_ID")
    private InvoiceSubCategory invoiceSubCategory;

    @Column(name = "CRITERIA_1")
    private String criteria1;

    @Column(name = "CRITERIA_2")
    private String criteria2;

    @Column(name = "CRITERIA_3")
    private String criteria3;

    @Column(name = "REJECTION_REASON")
    private String rejectionReason;

    @OneToMany(mappedBy = "chargeApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RatedTransaction> ratedTransactions = new HashSet<RatedTransaction>();

    /**
     * Id of input history that represents on which batch this charge
     * application was processed.
     */
    @Column(name = "INPUT_HISTORY_ID")
    private Long inputHistoryId;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    private Date startDate;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    private Date endDate;
    

    public ChargeApplication() {
        super();
    }

    public ChargeApplication(String code, String description, Subscription subscription, ChargeInstance chargeInstance,
            String chargeCode, ApplicationChgStatusEnum status, ApplicationTypeEnum applicationType,
            Date applicationDate, BigDecimal amountWithoutTax, BigDecimal amount2, int quantity, String taxCode,
            BigDecimal taxPercent, BigDecimal discountPercent, Date nextApplicationDate,
            InvoiceSubCategory invoiceSubCategory, String parameter1, String parameter2, String parameter3,
            String parameter4, String criteria1, String criteria2, String criteria3) {
        setCode(code);
        setDescription(description);
        this.subscription = subscription;
        if (subscription != null) {
            this.subscriptionDate = subscription.getSubscriptionDate();
        }
        this.chargeInstance = chargeInstance;
        this.chargeCode = chargeCode;
        this.status = status;
        this.applicationDate = applicationDate;
        this.amountWithoutTax = amountWithoutTax;
        this.amount2 = amount2;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.parameter3 = parameter3;
        this.parameter4 = parameter4;
        this.quantity = quantity;
        this.applicationType = applicationType;
        this.taxCode = taxCode;
        this.discountPercent = discountPercent;
        this.taxPercent = taxPercent;
        this.invoiceSubCategory = invoiceSubCategory;
        this.criteria1 = criteria1;
        this.criteria2 = criteria2;
        this.criteria3 = criteria3;
    }

    /**
     * @return the taxCode
     */
    public String getTaxCode() {
        return taxCode;
    }

    /**
     * @param taxCode
     *            the taxCode to set
     */
    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    /**
     * @return the taxPercent
     */
    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    /**
     * @param taxPercent
     *            the taxPercent to set
     */
    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public String getParameter3() {
        return parameter3;
    }

    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }

    public String getParameter4() {
        return parameter4;
    }

    public void setParameter4(String parameter4) {
        this.parameter4 = parameter4;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public ApplicationChgStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ApplicationChgStatusEnum status) {
        this.status = status;
        this.statusDate = new Date();
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public ChargeInstance getChargeInstance() {
        return chargeInstance;
    }

    public void setChargeInstance(ChargeInstance chargeInstance) {
        this.chargeInstance = chargeInstance;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmount2() {
        return amount2;
    }

    public void setAmount2(BigDecimal amount2) {
        this.amount2 = amount2;
    }

    public ApplicationTypeEnum getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(ApplicationTypeEnum applicationType) {
        this.applicationType = applicationType;
    }

    public String getChargeCode() {
        return chargeCode;
    }

    public void setChargeCode(String chargeCode) {
        this.chargeCode = chargeCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the discountPercent
     */
    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    /**
     * @param discountPercent
     *            the discountPercent to set
     */
    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    /**
     * @return the invoiceSubCategory
     */
    public InvoiceSubCategory getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    /**
     * @param invoiceSubCategory
     *            the invoiceSubCategory to set
     */
    public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    /**
     * @return the criteria1
     */
    public String getCriteria1() {
        return criteria1;
    }

    /**
     * @param criteria1
     *            the criteria1 to set
     */
    public void setCriteria1(String criteria1) {
        this.criteria1 = criteria1;
    }

    /**
     * @return the criteria2
     */
    public String getCriteria2() {
        return criteria2;
    }

    /**
     * @param criteria2
     *            the criteria2 to set
     */
    public void setCriteria2(String criteria2) {
        this.criteria2 = criteria2;
    }

    /**
     * @return the criteria3
     */
    public String getCriteria3() {
        return criteria3;
    }

    /**
     * @param criteria3
     *            the criteria3 to set
     */
    public void setCriteria3(String criteria3) {
        this.criteria3 = criteria3;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Set<RatedTransaction> getRatedTransactions() {
        return ratedTransactions;
    }

    public void setRatedTransactions(Set<RatedTransaction> ratedTransactions) {
        this.ratedTransactions = ratedTransactions;
    }

    public ChargeApplicationModeEnum getApplicationMode() {
        return applicationMode;
    }

    public void setApplicationMode(ChargeApplicationModeEnum applicationMode) {
        this.applicationMode = applicationMode;
    }

    public Long getInputHistoryId() {
        return inputHistoryId;
    }

    public void setInputHistoryId(Long inputHistoryId) {
        this.inputHistoryId = inputHistoryId;
    }

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
    
    
    
}
