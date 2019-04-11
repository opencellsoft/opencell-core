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
import javax.validation.constraints.Size;

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
 * The Class PaymentScheduleTemplate.
 *
 * @author anasseh
 * @since Opencell 5.2
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "PaymentScheduleTemplate")
@Table(name = "ar_payment_schedule_tmpl")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_payment_schedule_tmpl_seq"), })
public class PaymentScheduleTemplate extends EnableBusinessCFEntity {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 612388141736383814L;

    /** The payment label. */
    @Column(name = "payment_label")
    @NotNull
    private String paymentLabel;

    /** The payment day in month. */
    @Column(name = "payment_day_in_month")
    @NotNull
    private Integer paymentDayInMonth;

    /** The amount. */
    @Column(name = "amount")
    @NotNull
    private BigDecimal amount;

    /** The calendar. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    @NotNull
    private Calendar calendar;

    /** The service template. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_template_id")
    @NotNull
    private ServiceTemplate serviceTemplate;

    /** The advance payment invoice type. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adv_pay_inv_type_id")
    @NotNull
    private InvoiceType advancePaymentInvoiceType;

    /** The generate advance payment invoice. */
    @Type(type = "numeric_boolean")
    @Column(name = "is_generate_adv_pay_inv", nullable = false)
    @NotNull
    private boolean generateAdvancePaymentInvoice;

    /** The advance payment invoice sub category. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adv_pay_sub_cat_id")
    @NotNull
    private InvoiceSubCategory advancePaymentInvoiceSubCategory;

    /** The do payment. */
    @Type(type = "numeric_boolean")
    @Column(name = "is_do_payment", nullable = false)
    @NotNull
    private boolean doPayment;

    /** The payment schedule instances. */
    @OneToMany(mappedBy = "paymentScheduleTemplate", cascade = CascadeType.ALL)
    private List<PaymentScheduleInstance> paymentScheduleInstances;

    /** The apply agreement. */
    @Type(type = "numeric_boolean")
    @Column(name = "apply_agreement")
    private boolean applyAgreement = false;

    /** The script instance. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance scriptInstance;
    
    /** The amount el. */
    @Column(name = "amount_el", length = 2000)
    @Size(max = 2000)
    private String amountEl;
    
    /** The filter el. */
    @Column(name = "filter_el", length = 2000)
    @Size(max = 2000)
    private String filterEl;
    

    /**
     * Gets the payment label.
     *
     * @return the paymentLabel
     */
    public String getPaymentLabel() {
        return paymentLabel;
    }

    /**
     * Sets the payment label.
     *
     * @param paymentLabel the paymentLabel to set
     */
    public void setPaymentLabel(String paymentLabel) {
        this.paymentLabel = paymentLabel;
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

    /**
     * Gets the service template.
     *
     * @return the serviceTemplate
     */
    public ServiceTemplate getServiceTemplate() {
        return serviceTemplate;
    }

    /**
     * Sets the service template.
     *
     * @param serviceTemplate the serviceTemplate to set
     */
    public void setServiceTemplate(ServiceTemplate serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
    }

    /**
     * Gets the advance payment invoice type.
     *
     * @return the advancePaymentInvoiceType
     */
    public InvoiceType getAdvancePaymentInvoiceType() {
        return advancePaymentInvoiceType;
    }

    /**
     * Sets the advance payment invoice type.
     *
     * @param advancePaymentInvoiceType the advancePaymentInvoiceType to set
     */
    public void setAdvancePaymentInvoiceType(InvoiceType advancePaymentInvoiceType) {
        this.advancePaymentInvoiceType = advancePaymentInvoiceType;
    }

    /**
     * Checks if is generate advance payment invoice.
     *
     * @return the generateAdvancePaymentInvoice
     */
    public boolean isGenerateAdvancePaymentInvoice() {
        return generateAdvancePaymentInvoice;
    }

    /**
     * Sets the generate advance payment invoice.
     *
     * @param generateAdvancePaymentInvoice the generateAdvancePaymentInvoice to set
     */
    public void setGenerateAdvancePaymentInvoice(boolean generateAdvancePaymentInvoice) {
        this.generateAdvancePaymentInvoice = generateAdvancePaymentInvoice;
    }

    /**
     * Gets the advance payment invoice sub category.
     *
     * @return the advancePaymentInvoiceSubCategory
     */
    public InvoiceSubCategory getAdvancePaymentInvoiceSubCategory() {
        return advancePaymentInvoiceSubCategory;
    }

    /**
     * Sets the advance payment invoice sub category.
     *
     * @param advancePaymentInvoiceSubCategory the advancePaymentInvoiceSubCategory to set
     */
    public void setAdvancePaymentInvoiceSubCategory(InvoiceSubCategory advancePaymentInvoiceSubCategory) {
        this.advancePaymentInvoiceSubCategory = advancePaymentInvoiceSubCategory;
    }

    /**
     * Gets the payment schedule instances.
     *
     * @return the paymentScheduleInstances
     */
    public List<PaymentScheduleInstance> getPaymentScheduleInstances() {
        return paymentScheduleInstances;
    }

    /**
     * Sets the payment schedule instances.
     *
     * @param paymentScheduleInstances the paymentScheduleInstances to set
     */
    public void setPaymentScheduleInstances(List<PaymentScheduleInstance> paymentScheduleInstances) {
        this.paymentScheduleInstances = paymentScheduleInstances;
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
     * Checks if is do payment.
     *
     * @return the doPayment
     */
    public boolean isDoPayment() {
        return doPayment;
    }

    /**
     * Sets the do payment.
     *
     * @param doPayment the doPayment to set
     */
    public void setDoPayment(boolean doPayment) {
        this.doPayment = doPayment;
    }

    /**
     * Checks if is apply agreement.
     *
     * @return the applyAgreement
     */
    public boolean isApplyAgreement() {
        return applyAgreement;
    }

    /**
     * Sets the apply agreement.
     *
     * @param applyAgreement the applyAgreement to set
     */
    public void setApplyAgreement(boolean applyAgreement) {
        this.applyAgreement = applyAgreement;
    }

    /**
     * Gets the script instance.
     *
     * @return the script instance
     */
    public ScriptInstance getScriptInstance() {
        return scriptInstance;
    }

    /**
     * Sets the script instance.
     *
     * @param scriptInstance the new script instance
     */
    public void setScriptInstance(ScriptInstance scriptInstance) {
        this.scriptInstance = scriptInstance;
    }

    /**
     * Gets the amount el.
     *
     * @return the amountEl
     */
    public String getAmountEl() {
        return amountEl;
    }

    /**
     * Sets the amount el.
     *
     * @param amountEl the amountEl to set
     */
    public void setAmountEl(String amountEl) {
        this.amountEl = amountEl;
    }

    /**
     * Gets the filter el.
     *
     * @return the filterEl
     */
    public String getFilterEl() {
        return filterEl;
    }

    /**
     * Sets the filter el.
     *
     * @param filterEl the filterEl to set
     */
    public void setFilterEl(String filterEl) {
        this.filterEl = filterEl;
    }
    
    
}
