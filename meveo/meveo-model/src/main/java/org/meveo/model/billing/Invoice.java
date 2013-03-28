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


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.meveo.model.AuditableEntity;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * @author R.AITYAAZZA
 * 
 */
@Entity
@Table(name = "BILLING_INVOICE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_INVOICE_SEQ")
public class Invoice extends AuditableEntity {

    private static final Logger logger = Logger.getLogger(Invoice.class);

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILLING_ACCOUNT_ID")
    private BillingAccount billingAccount=new BillingAccount();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILLING_RUN_ID")
    private BillingRun billingRun;

    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
    private List<InvoiceAgregate> invoiceAgregates = new ArrayList<InvoiceAgregate>();

    @Column(name = "INVOICE_NUMBER", length = 20)
    private String invoiceNumber;

    @Column(name = "TEMPORARY_INVOICE_NUMBER", length = 20, unique = true)
    private String temporaryInvoiceNumber;

    @Column(name = "PRODUCT_DATE")
    private Date productDate;

    @Column(name = "INVOICE_DATE")
    private Date invoiceDate;

    @Column(name = "DUE_DATE")
    private Date dueDate;

    @Column(name = "AMOUNT", precision = 23, scale = 12)
    private BigDecimal amount;

    @Column(name = "DISCOUNT", precision = 23, scale = 12)
    private BigDecimal discount;

    @Column(name = "AMOUNT_WITHOUT_TAX", precision = 23, scale = 12)
    private BigDecimal amountWithoutTax;

    @Column(name = "AMOUNT_TAX", precision = 23, scale = 12)
    private BigDecimal amountTax;

    @Column(name = "AMOUNT_WITH_TAX", precision = 23, scale = 12)
    private BigDecimal amountWithTax;

    @Column(name = "PAYMENT_METHOD")
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethod;

    @Column(name = "IBAN")
    private String iban;

    @Column(name = "ALIAS")
    private String alias;

    @Column(name = "PDF")
    @Lob
    @Basic(fetch = FetchType.LAZY)
    //private Blob pdfBlob;
    private byte[] pdf;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRADING_CURRENCY_ID")
    private TradingCurrency tradingCurrency ;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRADING_COUNTRY_ID")
    private TradingCountry tradingCountry; 
    

    @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "TRADING_LANGUAGE_ID")
   private TradingLanguage tradingLanguage;
    
    @Column(name = "INVOICE_TYPE",length = 20)
    private String invoiceType;

    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
    private List<RatedTransaction> ratedTransactions = new ArrayList<RatedTransaction>();
  

	public List<RatedTransaction> getRatedTransactions() {
        return ratedTransactions;
    }

    public void setRatedTransactions(List<RatedTransaction> ratedTransactions) {
        this.ratedTransactions = ratedTransactions;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Date getProductDate() {
        return productDate;
    }

    public void setProductDate(Date productDate) {
        this.productDate = productDate;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
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

    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public BillingRun getBillingRun() {
        return billingRun;
    }

    public void setBillingRun(BillingRun billingRun) {
        this.billingRun = billingRun;
    }

    public List<InvoiceAgregate> getInvoiceAgregates() {
        return invoiceAgregates;
    }

    public void setInvoiceAgregates(List<InvoiceAgregate> invoiceAgregates) {
        this.invoiceAgregates = invoiceAgregates;
    }

    public void addAmountWithTax(BigDecimal amountToAdd) {
        if (amountWithTax == null) {
            amountWithTax = new BigDecimal("0");
        }
        amountWithTax = amountWithTax.add(amountToAdd);
    }

    public void addAmountWithoutTax(BigDecimal amountToAdd) {
        if (amountWithoutTax == null) {
            amountWithoutTax = new BigDecimal("0");
        }
        amountWithoutTax = amountWithoutTax.add(amountToAdd);
    }

    public void addAmountTax(BigDecimal amountToAdd) {
        if (amountTax == null) {
            amountTax = new BigDecimal("0");
        }
        amountTax = amountTax.add(amountToAdd);
    }

    /*public Blob getPdfBlob() {
        return pdfBlob;
    }

    public void setPdfBlob(Blob pdfBlob) {
        this.pdfBlob = pdfBlob;
    }*/

   /* public byte[] getPdf() {
        byte[] result = null;
        try {
            if (pdfBlob != null) {
                int size;
                size = (int) pdfBlob.length();
                result = pdfBlob.getBytes(1L, size);
            }
        } catch (SQLException e) {
            logger.error("Error while accessing pdf blob field in database. Return null.", e);
        }
        return result;
    }

    public void setPdf(byte[] pdf) {
        this.pdfBlob = Hibernate.createBlob(pdf);
    }*/


    public byte[] getPdf() {
		return pdf;
	}

	public void setPdf(byte[] pdf) {
		this.pdf = pdf;
	}
    
    public String getTemporaryInvoiceNumber() {
        return temporaryInvoiceNumber;
    }

	public void setTemporaryInvoiceNumber(String temporaryInvoiceNumber) {
        this.temporaryInvoiceNumber = temporaryInvoiceNumber;
    }

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
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
    
    

}
