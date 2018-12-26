/**
 * 
 */
package org.meveo.model.payments;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.catalog.Calendar;

/**
 * The Class PaymentScheduleInstance.
 *
 * @author anasseh
 * @since Opencell 5.2
 * @lastModifiedVersion 5.3
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "PAYMENT_SCH_INSTANCE", inheritCFValuesFrom = "paymentScheduleTemplate")
@Table(name = "ar_payment_schedule_inst")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_payment_schedule_inst_seq"), })
public class PaymentScheduleInstance extends EnableBusinessCFEntity {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 322388141736383861L;

    /** The status. */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentScheduleStatusEnum status;

    /** The status date. */
    @Column(name = "status_date")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date statusDate;

    /** The payment schedule template. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_schedule_tmpl_id")
    @NotNull
    private PaymentScheduleTemplate paymentScheduleTemplate;

    /** The start date. */
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date startDate;

    /** The end date. */
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date endDate;

    /** The service instance. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_instance_id")
    @NotNull
    private ServiceInstance serviceInstance;

    /** The amount. */
    @Column(name = "amount")
    @NotNull
    private BigDecimal amount;

    /** The calendar. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    @NotNull
    private Calendar calendar;

    /** The payment day in month. */
    @Column(name = "payment_day_in_month")
    @NotNull
    private Integer paymentDayInMonth;

    /** The payment schedule instance items. */
    @OneToMany(mappedBy = "paymentScheduleInstance", cascade = CascadeType.ALL)
    private List<PaymentScheduleInstanceItem> paymentScheduleInstanceItems;

    /**
     * Gets the status.
     *
     * @return the status
     */
    public PaymentScheduleStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the status to set
     */
    public void setStatus(PaymentScheduleStatusEnum status) {
        this.status = status;
    }

    /**
     * Gets the payment schedule template.
     *
     * @return the paymentScheduleTemplate
     */
    public PaymentScheduleTemplate getPaymentScheduleTemplate() {
        return paymentScheduleTemplate;
    }

    /**
     * Sets the payment schedule template.
     *
     * @param paymentScheduleTemplate the paymentScheduleTemplate to set
     */
    public void setPaymentScheduleTemplate(PaymentScheduleTemplate paymentScheduleTemplate) {
        this.paymentScheduleTemplate = paymentScheduleTemplate;
    }

    /**
     * Gets the service instance.
     *
     * @return the serviceInstance
     */
    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    /**
     * Sets the service instance.
     *
     * @param serviceInstance the serviceInstance to set
     */
    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    /**
     * Gets the payment schedule instance items.
     *
     * @return the paymentScheduleInstanceItems
     */
    public List<PaymentScheduleInstanceItem> getPaymentScheduleInstanceItems() {
        return paymentScheduleInstanceItems;
    }

    /**
     * Sets the payment schedule instance items.
     *
     * @param paymentScheduleInstanceItems the paymentScheduleInstanceItems to set
     */
    public void setPaymentScheduleInstanceItems(List<PaymentScheduleInstanceItem> paymentScheduleInstanceItems) {
        this.paymentScheduleInstanceItems = paymentScheduleInstanceItems;
    }

    /**
     * Gets the status date.
     *
     * @return the statusDate
     */
    public Date getStatusDate() {
        return statusDate;
    }

    /**
     * Sets the status date.
     *
     * @param statusDate the statusDate to set
     */
    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    /**
     * Gets the start date.
     *
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date.
     *
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the amount.
     *
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the amount.
     *
     * @param amount the amount to set
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Gets the calendar.
     *
     * @return the calendar
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * Sets the calendar.
     *
     * @param calendar the calendar to set
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     * Gets the end date.
     *
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date.
     *
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    /**
     * Gets the payment day in month.
     *
     * @return the payment day in month
     */
    public Integer getPaymentDayInMonth() {
        return paymentDayInMonth;
    }


    /**
     * Sets the payment day in month.
     *
     * @param paymentDayInMonth the new payment day in month
     */
    public void setPaymentDayInMonth(Integer paymentDayInMonth) {
        this.paymentDayInMonth = paymentDayInMonth;
    }
    
    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        if (paymentScheduleTemplate != null) {
            return new ICustomFieldEntity[] { paymentScheduleTemplate };
        }
        return null;
    }

}
