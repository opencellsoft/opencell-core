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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BaseEntity;
import org.meveo.model.ProviderBusinessEntity;

/**
 * @author R.AITYAAZZA
 * 
 */
@Entity
@Table(name = "BILLING_RATED_TRANSACTION")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_RATED_TRANSACTION_SEQ")
public class RatedTransaction extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WALLET_ID")
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILLING_RUN_ID")
    private BillingRun billingRun;

    @Column(name = "USAGE_CODE", length = 35)
    private String usageCode;

    @Column(name = "SUBUSAGE1_CODE", length = 20)
    private String subUsageCode1;

    @Column(name = "SUBUSAGE2_CODE", length = 20)
    private String subUsageCode2;

    @Column(name = "PR_DESCRIPTION", length = 255)
    private String prDescription;
    
    @Column(name = "USAGE_DESCRIPTION", length = 255)
    private String usageDescription;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "USAGE_DATE")
    private Date usageDate;

    @Column(name = "USAGE_AMOUNT")
    private Integer usageAmount;

    @Column(name = "UNIT_PRICE_1", precision = 23, scale = 12)
    private BigDecimal unitPrice1;

    @Column(name = "UNIT_PRICE_2", precision = 23, scale = 12)
    private BigDecimal unitPrice2;

    @Column(name = "DISCOUNT", precision = 23, scale = 12)
    private BigDecimal discount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_SUB_CATEGORY_ID")
    private InvoiceSubCategory invoiceSubCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAX_ID")
    private Tax tax;

    @Column(name = "TAX_PERCENT", precision = 23, scale = 12)
    private BigDecimal taxPercent;

    @Column(name = "AMOUNT", precision = 23, scale = 12)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "AMOUNT_WITHOUT_TAX", precision = 23, scale = 12)
    private BigDecimal amountWithoutTax = BigDecimal.ZERO;

    @Column(name = "AMOUNT_TAX", precision = 23, scale = 12)
    private BigDecimal amountTax = BigDecimal.ZERO;

    @Column(name = "AMOUNT_WITH_TAX", precision = 23, scale = 12)
    private BigDecimal amountWithTax = BigDecimal.ZERO;

    @Column(name = "AMOUNT_2_WITHOUT_TAX", precision = 23, scale = 12)
    private BigDecimal amount2WithoutTax = BigDecimal.ZERO;
    
    @Column(name = "AMOUNT_2_TAX", precision = 23, scale = 12)
    private BigDecimal amount2Tax = BigDecimal.ZERO;

    @Column(name = "AMOUNT_2_WITH_TAX", precision = 23, scale = 12)
    private BigDecimal amount2WithTax = BigDecimal.ZERO;
    
    @Column(name = "AMOUNT_2", precision = 23, scale = 12)
    private BigDecimal amount2 = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBSCRIPTION_ID")
    private Subscription subscription;

    /**
     * Specifies value for a combination of national/roaming and upload/download values
     */
    
    @Column(name = "grouping_id")
    private Integer groupingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_ID")
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "AGGREGATE_ID_F")
    private SubCategoryInvoiceAgregate invoiceAgregateF;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "AGGREGATE_ID_R")
    private CategoryInvoiceAgregate invoiceAgregateR;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "AGGREGATE_ID_T")
    private TaxInvoiceAgregate invoiceAgregateT;

    @Column(name = "PARAMETER_1", length = 50)
    private String parameter1;

    @Column(name = "PARAMETER_2", length = 50)
    private String parameter2;

    @Column(name = "PARAMETER_3", length = 50)
    private String parameter3;

    @Column(name = "PARAMETER_4", length = 50)
    private String parameter4;

    @Column(name = "PARAMETER_5", length = 50)
    private String parameter5;

    @Column(name = "USAGE_QUANTITY")
    private Integer usageQuantity;

    @Column(name = "UNIT_PRICE_RATIO", precision = 23, scale = 12)
    private BigDecimal unitPriceRatio;

    @Column(name = "DISCOUNT_PERCENT", precision = 23, scale = 12)
    private BigDecimal discountPercent;

    // used to cancel transactions when a charge is cancelled
    @ManyToOne(optional = true)
    @JoinColumn(name = "CHARGE_APPLIC_ID")
    private ChargeApplication chargeApplication;

    // used for rerating
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private RatedTransactionStatusEnum status;

    // used for rerating
    @Column(name = "REJECTION_REASON")
    private String rejectionReason;
    
    @Column(name = "DO_NOT_TRIGGER_INVOICING")
    private boolean doNotTriggerInvoicing  = false;

    /**
     * Id of input history that represents on which batch this charge application was processed.
     */
    @Column(name = "INPUT_HISTORY_ID")
    private Long inputHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRADING_CURRENCY_ID")
    private TradingCurrency tradingCurrency ;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRADING_COUNTRY_ID")
    private TradingCountry tradingCountry; 
    

    @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "TRADING_LANGUAGE_ID")
   private TradingLanguage tradingLanguage;
    
    
    @Column(name = "DISCOUNT_CODE", length = 20)
    private String discountCode;
    

	public String getDiscountCode() {
		return discountCode;
	}

	public void setDiscountCode(String discountCode) {
		this.discountCode = discountCode;
	}
 

	public BigDecimal getUnitPriceRatio() {
        return unitPriceRatio;
    }

    public void setUnitPriceRatio(BigDecimal unitPriceRatio) {
        this.unitPriceRatio = unitPriceRatio;
    }

  
    /**
     * @return the discountPercent
     */
    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    /**
     * @param discountPercent the discountPercent to set
     */
    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Integer getUsageQuantity() {
        return usageQuantity;
    }

    public void setUsageQuantity(Integer usageQuantity) {
        this.usageQuantity = usageQuantity;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public String getUsageCode() {
        return usageCode;
    }

    public void setUsageCode(String usageCode) {
        this.usageCode = usageCode;
    }

    public String getSubUsageCode1() {
        return subUsageCode1;
    }

    public void setSubUsageCode1(String subUsageCode1) {
        this.subUsageCode1 = subUsageCode1;
    }

    public String getSubUsageCode2() {
        return subUsageCode2;
    }

    public void setSubUsageCode2(String subUsageCode2) {
        this.subUsageCode2 = subUsageCode2;
    }

   

    public String getUsageDescription() {
        return usageDescription;
    }

    public void setUsageDescription(String usageDescription) {
        this.usageDescription = usageDescription;
    }

    public Date getUsageDate() {
        return usageDate;
    }

    public void setUsageDate(Date usageDate) {
        this.usageDate = usageDate;
    }

    public Integer getUsageAmount() {
        return usageAmount;
    }

    public void setUsageAmount(Integer usageAmount) {
        this.usageAmount = usageAmount;
    }

    public BigDecimal getUnitPrice1() {
        return unitPrice1;
    }

    public void setUnitPrice1(BigDecimal unitPrice1) {
        this.unitPrice1 = unitPrice1;
    }

    public BigDecimal getUnitPrice2() {
        return unitPrice2;
    }

    public void setUnitPrice2(BigDecimal unitPrice2) {
        this.unitPrice2 = unitPrice2;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public InvoiceSubCategory getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountTax() {
        return amountTax;
    }

    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public BigDecimal getAmount2WithoutTax() {
		return amount2WithoutTax;
	}

	public void setAmount2WithoutTax(BigDecimal amount2WithoutTax) {
		this.amount2WithoutTax = amount2WithoutTax;
	}

	public BigDecimal getAmount2Tax() {
		return amount2Tax;
	}

	public void setAmount2Tax(BigDecimal amount2Tax) {
		this.amount2Tax = amount2Tax;
	}

	public BigDecimal getAmount2() {
		return amount2;
	}

	public void setAmount2(BigDecimal amount2) {
		this.amount2 = amount2;
	}

	public void setAmount2WithTax(BigDecimal prAmountWithTax) {
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public SubCategoryInvoiceAgregate getInvoiceAgregateF() {
        return invoiceAgregateF;
    }

    public void setInvoiceAgregateF(SubCategoryInvoiceAgregate invoiceAgregateF) {
        this.invoiceAgregateF = invoiceAgregateF;
        if (invoiceAgregateF != null) {
            invoiceAgregateF.getRatedtransactions().add(this);
        }
    }

    public CategoryInvoiceAgregate getInvoiceAgregateR() {
        return invoiceAgregateR;
    }

    public void setInvoiceAgregateR(CategoryInvoiceAgregate invoiceAgregateR) {
        this.invoiceAgregateR = invoiceAgregateR;
    }

    public TaxInvoiceAgregate getInvoiceAgregateT() {
        return invoiceAgregateT;
    }

    public void setInvoiceAgregateT(TaxInvoiceAgregate invoiceAgregateT) {
        this.invoiceAgregateT = invoiceAgregateT;
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

    public String getParameter5() {
        return parameter5;
    }

    public void setParameter5(String parameter5) {
        this.parameter5 = parameter5;
    }

    public BillingRun getBillingRun() {
        return billingRun;
    }

    public void setBillingRun(BillingRun billingRun) {
        this.billingRun = billingRun;
    }

    public ChargeApplication getChargeApplication() {
        return chargeApplication;
    }

    public void setChargeApplication(ChargeApplication chargeApplication) {
        this.chargeApplication = chargeApplication;
    }

    public RatedTransactionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(RatedTransactionStatusEnum status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Long getInputHistoryId() {
        return inputHistoryId;
    }

    public void setInputHistoryId(Long inputHistoryId) {
        this.inputHistoryId = inputHistoryId;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Integer getGroupingId() {
        return groupingId;
    }
    

    public void setGroupingId(Integer groupingId) {
        this.groupingId = groupingId;
    }

    public void setGroupingId(boolean roaming, boolean upload) {

        groupingId = RatedTransaction.translateToGroupingId(roaming, upload);
    }

    /**
     * Translate national/roaming and upload/download combination into a single grouping Id value
     * 
     * @param roaming Is roaming
     * @param upload Is upload
     * @return Grouping Id value
     */
    public static int translateToGroupingId(boolean roaming, boolean upload) {

        int grpId = 0;

        if (!roaming && !upload) {
            grpId = 0;

        } else if (roaming && !upload) {
            grpId = 1;
        } else if (!roaming && upload) {
            grpId = 2;
        } else {
            grpId = 3;
        }
        return grpId;
    }

    /**
     * Translate groupId value to national/roaming
     * 
     * @param grpId Group id
     * @return True if roaming
     */
    public static boolean translateGroupIdToRoaming(int grpId) {
        return (grpId == 1 || grpId == 3);
    }

    /**
     * Translate groupId value to upload/download
     * 
     * @param grpId Group id
     * @return True if upload
     */
    public static boolean translateGroupIdToUpload(int grpId) {
        return (grpId == 2 || grpId == 3);
    }

	public boolean isDoNotTriggerInvoicing() {
		return doNotTriggerInvoicing;
	}

	public void setDoNotTriggerInvoicing(boolean doNotTriggerInvoicing) {
		this.doNotTriggerInvoicing = doNotTriggerInvoicing;
	}

 
	public BigDecimal getAmount2WithTax() {
		return amount2WithTax;
	}

	public TradingCurrency getTradingCurrency() {
		return tradingCurrency;
	}

	public void setTradingCurrency(TradingCurrency tradingCurrency) {
		this.tradingCurrency = tradingCurrency;
	}

	public TradingCountry getTradingCountry() {
		return tradingCountry;
	}

	public void setTradingCountry(TradingCountry tradingCountry) {
		this.tradingCountry = tradingCountry;
	}

	public TradingLanguage getTradingLanguage() {
		return tradingLanguage;
	}

	public void setTradingLanguage(TradingLanguage tradingLanguage) {
		this.tradingLanguage = tradingLanguage;
	}

	public String getPrDescription() {
		return prDescription;
	}

	public void setPrDescription(String prDescription) {
		this.prDescription = prDescription;
	}
	
	
    
    
}
