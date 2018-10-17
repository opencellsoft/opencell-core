/**
 * 
 */
package org.meveo.api.dto.payment;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.model.payments.PaymentScheduleInstanceItem;

/**
 * The Class PaymentScheduleInstanceItemDto.
 *
 * @author anasseh
 */

@XmlRootElement(name = "PaymentScheduleInstanceItemDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentScheduleInstanceItemDto extends AuditableEntityDto {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The id. */
    private Long id;
    
    /** The due date. */
    private Date dueDate;
    
    /** The request payment date. */
    private Date requestPaymentDate;
    
    /** The recorded invoice. */
    private RecordedInvoiceDto recordedInvoice;
    
    /** The last. */
    private boolean last;
    
    /** The paid. */
    private boolean paid;
    
    /**
     * Instantiates a new payment schedule instance item dto.
     */
    public PaymentScheduleInstanceItemDto(){
        
    }
    
    /**
     * Instantiates a new payment schedule instance item dto.
     *
     * @param paymentScheduleInstanceItem the payment schedule instance item
     */
    public PaymentScheduleInstanceItemDto(PaymentScheduleInstanceItem paymentScheduleInstanceItem) {
        this.id = paymentScheduleInstanceItem.getId();
        this.dueDate = paymentScheduleInstanceItem.getDueDate();
        this.requestPaymentDate = paymentScheduleInstanceItem.getRequestPaymentDate();
        this.recordedInvoice = paymentScheduleInstanceItem.getRecordedInvoice() == null ? null : new RecordedInvoiceDto(paymentScheduleInstanceItem.getRecordedInvoice());
        this.last = paymentScheduleInstanceItem.isLast();
        this.paid = paymentScheduleInstanceItem.isPaid();        
    }
    
    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Sets the id.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the due date.
     *
     * @return the dueDate
     */
    public Date getDueDate() {
        return dueDate;
    }
    
    /**
     * Sets the due date.
     *
     * @param dueDate the dueDate to set
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    
    /**
     * Gets the request payment date.
     *
     * @return the requestPaymentDate
     */
    public Date getRequestPaymentDate() {
        return requestPaymentDate;
    }
    
    /**
     * Sets the request payment date.
     *
     * @param requestPaymentDate the requestPaymentDate to set
     */
    public void setRequestPaymentDate(Date requestPaymentDate) {
        this.requestPaymentDate = requestPaymentDate;
    }
    
    /**
     * Gets the recorded invoice.
     *
     * @return the recordedInvoice
     */
    public RecordedInvoiceDto getRecordedInvoice() {
        return recordedInvoice;
    }
    
    /**
     * Sets the recorded invoice.
     *
     * @param recordedInvoice the recordedInvoice to set
     */
    public void setRecordedInvoice(RecordedInvoiceDto recordedInvoice) {
        this.recordedInvoice = recordedInvoice;
    }
    
    /**
     * Checks if is last.
     *
     * @return the last
     */
    public boolean isLast() {
        return last;
    }
    
    /**
     * Sets the last.
     *
     * @param last the last to set
     */
    public void setLast(boolean last) {
        this.last = last;
    }
    
    /**
     * Checks if is paid.
     *
     * @return the paid
     */
    public boolean isPaid() {
        return paid;
    }
    
    /**
     * Sets the paid.
     *
     * @param paid the paid to set
     */
    public void setPaid(boolean paid) {
        this.paid = paid;
    }
    
    

}
