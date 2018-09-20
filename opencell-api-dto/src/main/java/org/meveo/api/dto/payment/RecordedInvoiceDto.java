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
    
}
