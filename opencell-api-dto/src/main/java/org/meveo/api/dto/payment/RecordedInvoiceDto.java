package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.model.payments.RecordedInvoice;

/**
 * The Class RecordedInvoiceDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RecordedInvoiceDto extends AccountOperationDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6965598553420278018L;

    /** The production date. */
    private Date productionDate;
    
    /** The invoice date. */
    private Date invoiceDate;
    
    /** The net to pay. */
    private BigDecimal netToPay;
    
    /** The payment info. */
    private String paymentInfo;// IBAN for direct debit
    
    /** The payment info 1. */
    private String paymentInfo1;// bank code
    
    /** The payment info 2. */
    private String paymentInfo2;// code guichet
    
    /** The payment info 3. */
    private String paymentInfo3;// Num compte
    
    /** The payment info 4. */
    private String paymentInfo4;// RIB
    
    /** The payment info 5. */
    private String paymentInfo5;// bankName
    
    /** The payment info 6. */
    private String paymentInfo6;// bic
    
    /** The billing account name. */
    private String billingAccountName;

    /**
     * Instantiates a new recorded invoice dto.
     */
    public RecordedInvoiceDto() {
        super.setType("I");
    }

    /**
     * Instantiates a new recorded invoice dto.
     *
     * @param recordedInvoice the RecordedInvoice entity
     */
    public RecordedInvoiceDto(RecordedInvoice recordedInvoice) {
        super();
        setMatchingStatus(recordedInvoice.getMatchingStatus());
        setInvoiceDate(recordedInvoice.getInvoiceDate());
        setDueDate(recordedInvoice.getDueDate());
        setReference(recordedInvoice.getReference());
    }

    /**
     * Gets the production date.
     *
     * @return the production date
     */
    public Date getProductionDate() {
        return productionDate;
    }

    /**
     * Sets the production date.
     *
     * @param productionDate the new production date
     */
    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
    }

    /**
     * Gets the invoice date.
     *
     * @return the invoice date
     */
    public Date getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the invoice date.
     *
     * @param invoiceDate the new invoice date
     */
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /**
     * Gets the net to pay.
     *
     * @return the net to pay
     */
    public BigDecimal getNetToPay() {
        return netToPay;
    }

    /**
     * Sets the net to pay.
     *
     * @param netToPay the new net to pay
     */
    public void setNetToPay(BigDecimal netToPay) {
        this.netToPay = netToPay;
    }

    /**
     * Gets the payment info.
     *
     * @return the payment info
     */
    public String getPaymentInfo() {
        return paymentInfo;
    }

    /**
     * Sets the payment info.
     *
     * @param paymentInfo the new payment info
     */
    public void setPaymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    /**
     * Gets the payment info 1.
     *
     * @return the payment info 1
     */
    public String getPaymentInfo1() {
        return paymentInfo1;
    }

    /**
     * Sets the payment info 1.
     *
     * @param paymentInfo1 the new payment info 1
     */
    public void setPaymentInfo1(String paymentInfo1) {
        this.paymentInfo1 = paymentInfo1;
    }

    /**
     * Gets the payment info 2.
     *
     * @return the payment info 2
     */
    public String getPaymentInfo2() {
        return paymentInfo2;
    }

    /**
     * Sets the payment info 2.
     *
     * @param paymentInfo2 the new payment info 2
     */
    public void setPaymentInfo2(String paymentInfo2) {
        this.paymentInfo2 = paymentInfo2;
    }

    /**
     * Gets the payment info 3.
     *
     * @return the payment info 3
     */
    public String getPaymentInfo3() {
        return paymentInfo3;
    }

    /**
     * Sets the payment info 3.
     *
     * @param paymentInfo3 the new payment info 3
     */
    public void setPaymentInfo3(String paymentInfo3) {
        this.paymentInfo3 = paymentInfo3;
    }

    /**
     * Gets the payment info 4.
     *
     * @return the payment info 4
     */
    public String getPaymentInfo4() {
        return paymentInfo4;
    }

    /**
     * Sets the payment info 4.
     *
     * @param paymentInfo4 the new payment info 4
     */
    public void setPaymentInfo4(String paymentInfo4) {
        this.paymentInfo4 = paymentInfo4;
    }

    /**
     * Gets the payment info 5.
     *
     * @return the payment info 5
     */
    public String getPaymentInfo5() {
        return paymentInfo5;
    }

    /**
     * Sets the payment info 5.
     *
     * @param paymentInfo5 the new payment info 5
     */
    public void setPaymentInfo5(String paymentInfo5) {
        this.paymentInfo5 = paymentInfo5;
    }

    /**
     * Gets the payment info 6.
     *
     * @return the payment info 6
     */
    public String getPaymentInfo6() {
        return paymentInfo6;
    }

    /**
     * Sets the payment info 6.
     *
     * @param paymentInfo6 the new payment info 6
     */
    public void setPaymentInfo6(String paymentInfo6) {
        this.paymentInfo6 = paymentInfo6;
    }

    /**
     * Gets the billing account name.
     *
     * @return the billing account name
     */
    public String getBillingAccountName() {
        return billingAccountName;
    }

    /**
     * Sets the billing account name.
     *
     * @param billingAccountName the new billing account name
     */
    public void setBillingAccountName(String billingAccountName) {
        this.billingAccountName = billingAccountName;
    }

}
