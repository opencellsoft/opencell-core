/**
 * 
 */
package org.meveo.model.payments;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.meveo.model.ModuleItem;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ServiceTemplate;

/**
 * @author anasseh
 *
 * @since Opencell 5.2
 * @lastModifiedVersion 5.2
 */
@Entity
@ModuleItem
@CustomFieldEntity(cftCodePrefix = "PAYMENT_SCH")
@Table(name = "ar_payment_schedule")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_payment_schedule_seq"), })
public class PaymentSchedule extends EnableBusinessCFEntity {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 612388141736383814L;

    /** The status. */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentScheduleStatusEnum status;
    
    @Column(name = "status_date")  
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date statusDate;

    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date startDate;
    
    @Column(name = "amount")
    @NotNull
    private BigDecimal amount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    @NotNull
    private Calendar calendar;
    
    @Column(name = "number_payments")
    @NotNull
    private Integer numberPayments;
    
    @Column(name = "payment_label")
    @NotNull
    private String paymentLabel;
    
    @Column(name = "day_in_month")
    @NotNull
    private Integer dayInMonth;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_template_id")
    @NotNull
    private ServiceTemplate serviceTemplate;
    
    @OneToMany(mappedBy = "paymentSchedule", cascade = CascadeType.ALL)   
    private List<AccountOperationPS> accountOperationPSs;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occ_template_id")
    private OCCTemplate occTemplate;

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
     * @return the numberPayments
     */
    public Integer getNumberPayments() {
        return numberPayments;
    }

    /**
     * @param numberPayments the numberPayments to set
     */
    public void setNumberPayments(Integer numberPayments) {
        this.numberPayments = numberPayments;
    }

    /**
     * @return the paymentLabel
     */
    public String getPaymentLabel() {
        return paymentLabel;
    }

    /**
     * @param paymentLabel the paymentLabel to set
     */
    public void setPaymentLabel(String paymentLabel) {
        this.paymentLabel = paymentLabel;
    }

    /**
     * @return the dayInMonth
     */
    public Integer getDayInMonth() {
        return dayInMonth;
    }

    /**
     * @param dayInMonth the dayInMonth to set
     */
    public void setDayInMonth(Integer dayInMonth) {
        this.dayInMonth = dayInMonth;
    }

    /**
     * @return the serviceTemplate
     */
    public ServiceTemplate getServiceTemplate() {
        return serviceTemplate;
    }

    /**
     * @param serviceTemplate the serviceTemplate to set
     */
    public void setServiceTemplate(ServiceTemplate serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
    }

    /**
     * @return the accountOperationPSs
     */
    public List<AccountOperationPS> getAccountOperationPSs() {
        if(accountOperationPSs == null) {
            accountOperationPSs = new ArrayList<AccountOperationPS>();
        }
        return accountOperationPSs;
    }

    /**
     * @param accountOperationPSs the accountOperationPSs to set
     */
    public void setAccountOperationPSs(List<AccountOperationPS> accountOperationPSs) {
        this.accountOperationPSs = accountOperationPSs;
    }

    /**
     * @return the occTemplate
     */
    public OCCTemplate getOccTemplate() {
        return occTemplate;
    }

    /**
     * @param occTemplate the occTemplate to set
     */
    public void setOccTemplate(OCCTemplate occTemplate) {
        this.occTemplate = occTemplate;
    }
    
}
