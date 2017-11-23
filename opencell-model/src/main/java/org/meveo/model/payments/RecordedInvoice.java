/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.payments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

@Entity
@DiscriminatorValue(value = "I")
@NamedQueries({        
        @NamedQuery(name = "RecordedInvoice.listRecordedInvoiceToPayByDate", query = "Select ri.id from RecordedInvoice as ri,PaymentMethod as pm  where ri.matchingStatus ='O' "
                + "and  ri.customerAccount.excludedFromPayment = false and ri.dueDate >=:fromDueDate and ri.dueDate<=:toDueDate and ri.customerAccount.id = pm.customerAccount.id and pm.paymentType =:payMethod  and pm.preferred is true and ri.unMatchingAmount <> 0") })
public class RecordedInvoice extends AccountOperation {

    private static final long serialVersionUID = 1L;

    @Column(name = "production_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date productionDate;

    @Column(name = "invoice_date")
    @Temporal(TemporalType.DATE)
    private Date invoiceDate;

    @Column(name = "amount_without_tax", precision = 23, scale = 12)
    private BigDecimal amountWithoutTax;

    @Column(name = "tax_amount", precision = 23, scale = 12)
    private BigDecimal taxAmount;

    @Column(name = "net_to_pay", precision = 23, scale = 12)
    private BigDecimal netToPay;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethod;

    @Column(name = "payment_info", length = 255)
    @Size(max = 255)
    private String paymentInfo;// IBAN for direct debit

    @Column(name = "payment_info1", length = 255)
    @Size(max = 255)
    private String paymentInfo1;// bank code

    @Column(name = "payment_info2", length = 255)
    @Size(max = 255)
    private String paymentInfo2;// code guichet

    @Column(name = "payment_info3", length = 255)
    @Size(max = 255)
    private String paymentInfo3;// Num compte

    @Column(name = "payment_info4", length = 255)
    @Size(max = 255)
    private String paymentInfo4;// RIB

    @Column(name = "payment_info5", length = 255)
    @Size(max = 255)
    private String paymentInfo5;// bankName

    @Column(name = "payment_info6", length = 255)
    @Size(max = 255)
    private String paymentInfo6;// bic

    @Column(name = "billing_account_name", length = 255)
    @Size(max = 255)
    private String billingAccountName;

    @OneToMany(mappedBy = "recordedInvoice", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DDRequestItem> ddrequestItems = new ArrayList<DDRequestItem>();

    public Date getProductionDate() {
	return productionDate;
    }

    public void setProductionDate(Date productionDate) {
	this.productionDate = productionDate;
    }

    public Date getInvoiceDate() {
	return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
	this.invoiceDate = invoiceDate;
    }

    public BigDecimal getAmountWithoutTax() {
	return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
	this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getTaxAmount() {
	return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
	this.taxAmount = taxAmount;
    }

    public BigDecimal getNetToPay() {
	return netToPay;
    }

    public void setNetToPay(BigDecimal netToPay) {
	this.netToPay = netToPay;
    }

    public PaymentMethodEnum getPaymentMethod() {
	return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
	this.paymentMethod = paymentMethod;
    }

    public String getPaymentInfo() {
	return paymentInfo;
    }

    public void setPaymentInfo(String paymentInfo) {
	this.paymentInfo = paymentInfo;
    }

    public String getPaymentInfo1() {
	return paymentInfo1;
    }

    public void setPaymentInfo1(String paymentInfo1) {
	this.paymentInfo1 = paymentInfo1;
    }

    public String getPaymentInfo2() {
	return paymentInfo2;
    }

    public void setPaymentInfo2(String paymentInfo2) {
	this.paymentInfo2 = paymentInfo2;
    }

    public String getPaymentInfo3() {
	return paymentInfo3;
    }

    public void setPaymentInfo3(String paymentInfo3) {
	this.paymentInfo3 = paymentInfo3;
    }

    public String getPaymentInfo4() {
	return paymentInfo4;
    }

    public void setPaymentInfo4(String paymentInfo4) {
	this.paymentInfo4 = paymentInfo4;
    }

    public void setBillingAccountName(String billingAccountName) {
	this.billingAccountName = billingAccountName;
    }

    public String getBillingAccountName() {
	return billingAccountName;
    }

    public void setPaymentInfo5(String paymentInfo5) {
	this.paymentInfo5 = paymentInfo5;
    }

    public String getPaymentInfo5() {
	return paymentInfo5;
    }

    public String getPaymentInfo6() {
	return paymentInfo6;
    }

    public void setPaymentInfo6(String paymentInfo6) {
	this.paymentInfo6 = paymentInfo6;
    }

    /**
     * @return the ddrequestItems
     */
    public List<DDRequestItem> getDdrequestItems() {
	return ddrequestItems;
    }

    /**
     * @param ddrequestItems
     *            the ddrequestItems to set
     */
    public void setDdrequestItems(List<DDRequestItem> ddrequestItems) {
	this.ddrequestItems = ddrequestItems;
    }

    @Transient
    public DDRequestItem getPayedDDRequestItem() {
	for (DDRequestItem ddRequestItem : ddrequestItems) {
	    if (!ddRequestItem.hasError()) {
		return ddRequestItem;
	    }
	}
	return null;
    }

}
