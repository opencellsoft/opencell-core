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
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.catalog.Calendar;

/**
 * @author anasseh
 *
 * @since Opencell 5.2
 * @lastModifiedVersion 5.2
 */
@Entity
@Table(name = "ar_payment_schedule_inst")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_payment_schedule_inst_seq"), })
public class PaymentScheduleInstance extends EnableBusinessEntity {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 322388141736383861L;

    /** The status. */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentScheduleStatusEnum status;
    
    @Column(name = "status_date")  
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date statusDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_schedule_tmpl_id")
    @NotNull
    private PaymentScheduleTemplate paymentScheduleTemplate;
    
    @Column(name = "start_date")  
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date startDate;
    
    @Column(name = "end_date")  
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date endDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_instance_id")
    @NotNull
    private ServiceInstance serviceInstance;
    
    @Column(name = "amount")
    @NotNull
    private BigDecimal amount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    @NotNull
    private Calendar calendar;
    
    @Column(name = "due_date_days")
    @NotNull
    private Integer dueDateDays;
    
    
    
  
    
    @OneToMany(mappedBy = "paymentScheduleInstance", cascade = CascadeType.ALL)   
    private List<PaymentScheduleInstanceItem> paymentScheduleInstanceItems;

    /**
     * @return the status
     */
    public PaymentScheduleStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(PaymentScheduleStatusEnum status) {
        this.status = status;
    }

    /**
     * @return the paymentScheduleTemplate
     */
    public PaymentScheduleTemplate getPaymentScheduleTemplate() {
        return paymentScheduleTemplate;
    }

    /**
     * @param paymentScheduleTemplate the paymentScheduleTemplate to set
     */
    public void setPaymentScheduleTemplate(PaymentScheduleTemplate paymentScheduleTemplate) {
        this.paymentScheduleTemplate = paymentScheduleTemplate;
    }

    /**
     * @return the serviceInstance
     */
    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    /**
     * @param serviceInstance the serviceInstance to set
     */
    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    /**
     * @return the paymentScheduleInstanceItems
     */
    public List<PaymentScheduleInstanceItem> getPaymentScheduleInstanceItems() {
        return paymentScheduleInstanceItems;
    }

    /**
     * @param paymentScheduleInstanceItems the paymentScheduleInstanceItems to set
     */
    public void setPaymentScheduleInstanceItems(List<PaymentScheduleInstanceItem> paymentScheduleInstanceItems) {
        this.paymentScheduleInstanceItems = paymentScheduleInstanceItems;
    }

    /**
     * @return the statusDate
     */
    public Date getStatusDate() {
        return statusDate;
    }

    /**
     * @param statusDate the statusDate to set
     */
    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return the calendar
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * @param calendar the calendar to set
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the dueDateDays
     */
    public Integer getDueDateDays() {
        return dueDateDays;
    }

    /**
     * @param dueDateDays the dueDateDays to set
     */
    public void setDueDateDays(Integer dueDateDays) {
        this.dueDateDays = dueDateDays;
    }

      
}
