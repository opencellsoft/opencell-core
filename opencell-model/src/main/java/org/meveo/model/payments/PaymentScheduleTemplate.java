/**
 * 
 */
package org.meveo.model.payments;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.scripts.ScriptInstance;

/**
 * @author anasseh
 *
 * @since Opencell 5.2
 * @lastModifiedVersion 5.2
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "PAYMENT_SCH")
@Table(name = "ar_payment_schedule_tmpl")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_payment_schedule_tmpl_seq"), })
public class PaymentScheduleTemplate extends EnableBusinessCFEntity {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 612388141736383814L;

    @Column(name = "payment_label")
    @NotNull
    private String paymentLabel;

    @Column(name = "due_date_days")
    @NotNull
    private Integer dueDateDays;

    @Column(name = "amount")
    @NotNull
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    @NotNull
    private Calendar calendar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_template_id")
    @NotNull
    private ServiceTemplate serviceTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adv_pay_inv_type_id")
    @NotNull
    private InvoiceType advancePaymentInvoiceType;

    @Type(type = "numeric_boolean")
    @Column(name = "is_generate_adv_pay_inv", nullable = false)
    @NotNull
    private boolean generateAdvancePaymentInvoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adv_pay_sub_cat_id")
    @NotNull
    private InvoiceSubCategory advancePaymentInvoiceSubCategory;

    @Type(type = "numeric_boolean")
    @Column(name = "is_do_payment", nullable = false)
    @NotNull
    private boolean doPayment;

    @OneToMany(mappedBy = "paymentScheduleTemplate", cascade = CascadeType.ALL)
    private List<PaymentScheduleInstance> paymentScheduleInstances;

    @Type(type = "numeric_boolean")
    @Column(name = "apply_agreement")
    private boolean applyAgreement = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance scriptInstance;

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
     * @return the advancePaymentInvoiceType
     */
    public InvoiceType getAdvancePaymentInvoiceType() {
        return advancePaymentInvoiceType;
    }

    /**
     * @param advancePaymentInvoiceType the advancePaymentInvoiceType to set
     */
    public void setAdvancePaymentInvoiceType(InvoiceType advancePaymentInvoiceType) {
        this.advancePaymentInvoiceType = advancePaymentInvoiceType;
    }

    /**
     * @return the generateAdvancePaymentInvoice
     */
    public boolean isGenerateAdvancePaymentInvoice() {
        return generateAdvancePaymentInvoice;
    }

    /**
     * @param generateAdvancePaymentInvoice the generateAdvancePaymentInvoice to set
     */
    public void setGenerateAdvancePaymentInvoice(boolean generateAdvancePaymentInvoice) {
        this.generateAdvancePaymentInvoice = generateAdvancePaymentInvoice;
    }

    /**
     * @return the advancePaymentInvoiceSubCategory
     */
    public InvoiceSubCategory getAdvancePaymentInvoiceSubCategory() {
        return advancePaymentInvoiceSubCategory;
    }

    /**
     * @param advancePaymentInvoiceSubCategory the advancePaymentInvoiceSubCategory to set
     */
    public void setAdvancePaymentInvoiceSubCategory(InvoiceSubCategory advancePaymentInvoiceSubCategory) {
        this.advancePaymentInvoiceSubCategory = advancePaymentInvoiceSubCategory;
    }

    /**
     * @return the paymentScheduleInstances
     */
    public List<PaymentScheduleInstance> getPaymentScheduleInstances() {
        return paymentScheduleInstances;
    }

    /**
     * @param paymentScheduleInstances the paymentScheduleInstances to set
     */
    public void setPaymentScheduleInstances(List<PaymentScheduleInstance> paymentScheduleInstances) {
        this.paymentScheduleInstances = paymentScheduleInstances;
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
     * @return the doPayment
     */
    public boolean isDoPayment() {
        return doPayment;
    }

    /**
     * @param doPayment the doPayment to set
     */
    public void setDoPayment(boolean doPayment) {
        this.doPayment = doPayment;
    }

    /**
     * @return the applyAgreement
     */
    public boolean isApplyAgreement() {
        return applyAgreement;
    }

    /**
     * @param applyAgreement the applyAgreement to set
     */
    public void setApplyAgreement(boolean applyAgreement) {
        this.applyAgreement = applyAgreement;
    }

    public ScriptInstance getScriptInstance() {
        return scriptInstance;
    }

    public void setScriptInstance(ScriptInstance scriptInstance) {
        this.scriptInstance = scriptInstance;
    }
}
