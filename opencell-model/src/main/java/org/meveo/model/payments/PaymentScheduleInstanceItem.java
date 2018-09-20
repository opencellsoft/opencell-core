/**
 * 
 */
package org.meveo.model.payments;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.EnableEntity;
import org.meveo.model.billing.Invoice;

/**
 * @author anasseh
 *
 * @since Opencell 5.2
 * @lastModifiedVersion 5.2
 */
@Entity
@Table(name = "ar_payment_schedule_inst_item")
@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code", unique = false)) })
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_payment_schedule_inst_item_seq"), })
@NamedQueries({
        @NamedQuery(name = "PaymentScheduleInstanceItem.listItemsToProcess", query = "Select psii  from PaymentScheduleInstanceItem as psii where psii.invoice is null and  psii.recordedInvoice is null and psii.paymentScheduleInstance.status ='IN_PROGRESS' and psii.requestPaymentDate <=:requestPaymentDateIN  "),
        @NamedQuery(name = "PaymentScheduleInstanceItem.countPaidItems", query = "Select count(*) from PaymentScheduleInstanceItem as psii where   psii.recordedInvoice is not null and psii.recordedInvoice.matchingStatus ='L'and psii.paymentScheduleInstance.serviceInstance.id=:serviceInstanceIdIN "),
        @NamedQuery(name = "PaymentScheduleInstanceItem.countIncomingItems", query = "Select count(*) from PaymentScheduleInstanceItem as psii where  ( psii.recordedInvoice is null or psii.recordedInvoice.matchingStatus ='O') and psii.paymentScheduleInstance.serviceInstance.id=:serviceInstanceIdIN and psii.paymentScheduleInstance.status ='IN_PROGRESS'"),
        @NamedQuery(name = "PaymentScheduleInstanceItem.amountPaidItems", query = "Select sum(psii.paymentScheduleInstance.amount) from PaymentScheduleInstanceItem as psii where   psii.recordedInvoice is not null and psii.recordedInvoice.matchingStatus ='L'and psii.paymentScheduleInstance.serviceInstance.id=:serviceInstanceIdIN "),
        @NamedQuery(name = "PaymentScheduleInstanceItem.amountIncomingItems", query = "Select sum(psii.paymentScheduleInstance.amount) from PaymentScheduleInstanceItem as psii where  ( psii.recordedInvoice is null or psii.recordedInvoice.matchingStatus ='O') and psii.paymentScheduleInstance.serviceInstance.id=:serviceInstanceIdIN and psii.paymentScheduleInstance.status ='IN_PROGRESS'")})
public class PaymentScheduleInstanceItem extends EnableEntity {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 612308141236383114L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_schedule_inst_id")
    @NotNull
    private PaymentScheduleInstance paymentScheduleInstance;

    @Column(name = "due_date")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date dueDate;

    @Column(name = "request_pay_date")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date requestPaymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ao_ps_id")
    private RecordedInvoice recordedInvoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @Type(type = "numeric_boolean")
    @Column(name = "is_last", nullable = false)
    @NotNull
    private boolean last;

    /**
     * @return the paymentScheduleInstance
     */
    public PaymentScheduleInstance getPaymentScheduleInstance() {
        return paymentScheduleInstance;
    }

    /**
     * @param paymentScheduleInstance the paymentScheduleInstance to set
     */
    public void setPaymentScheduleInstance(PaymentScheduleInstance paymentScheduleInstance) {
        this.paymentScheduleInstance = paymentScheduleInstance;
    }

    /**
     * @return the dueDate
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * @param dueDate the dueDate to set
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * @return the requestPaymentDate
     */
    public Date getRequestPaymentDate() {
        return requestPaymentDate;
    }

    /**
     * @param requestPaymentDate the requestPaymentDate to set
     */
    public void setRequestPaymentDate(Date requestPaymentDate) {
        this.requestPaymentDate = requestPaymentDate;
    }

    /**
     * @return the recordedInvoice
     */
    public RecordedInvoice getRecordedInvoice() {
        return recordedInvoice;
    }

    /**
     * @param recordedInvoice the recordedInvoice to set
     */
    public void setRecordedInvoice(RecordedInvoice recordedInvoice) {
        this.recordedInvoice = recordedInvoice;
    }

    /**
     * @return the invoice
     */
    public Invoice getInvoice() {
        return invoice;
    }

    /**
     * @param invoice the invoice to set
     */
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    /**
     * @return the last
     */
    public boolean isLast() {
        return last;
    }

    /**
     * @param last the last to set
     */
    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isPaid() {
        if (getRecordedInvoice() != null && getRecordedInvoice().getUnMatchingAmount() != null && getRecordedInvoice().getUnMatchingAmount().compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }
        return false;

    }

}
