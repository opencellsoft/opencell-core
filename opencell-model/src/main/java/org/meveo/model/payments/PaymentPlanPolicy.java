package org.meveo.model.payments;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.meveo.model.dunning.DunningPauseReason;

@Embeddable
public class PaymentPlanPolicy implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	public PaymentPlanPolicy() {
    }

    public PaymentPlanPolicy(PaymentPlanPolicy paymentPlanPolicy) {
    	this.setMinAllowedReceivableAmount(minAllowedReceivableAmount);
    	this.setMaxAllowedReceivableAmount(maxAllowedReceivableAmount);
    	this.setMinInstallmentAmount(minInstallmentAmount);
    	this.setMaxPaymentPlanDuration(maxPaymentPlanDuration);
    	this.setDefaultRecurrenceUnit(defaultRecurrenceUnit);
    	this.setDefaultInstallmentCount(defaultInstallmentCount);
    	this.setSplitEvenly(splitEvenly);
    	this.setAllowCustomInstallmentPlan(allowCustomInstallmentPlan);
    	this.setAddInterestRate(addInterestRate);
    	this.setDefaultInterestRate(defaultInterestRate);
    	this.setAddInstallmentFee(addInstallmentFee);
    	this.setDefaultFeePerInstallmentPlan(defaultFeePerInstallmentPlan);
    	this.setInstallmentAmountRounding(installmentAmountRounding);
    	this.setActionOnRemainingAmount(actionOnRemainingAmount);
    	this.setClearingPriority(clearingPriority);
    	this.setDefaultBlockPayments(defaultBlockPayments);
    	this.setRequireInternalApproval(requireInternalApproval);
    	this.setTheresHoldForApproval(theresHoldForApproval);
    	this.setAllowedPaymentMethods(allowedPaymentMethods);
    	this.setDunningDefaultPauseReason(dunningDefaultPauseReason);
    	this.setAllowedCreditCategories(allowedCreditCategories);
    }
    
	@Column(name = "min_allowed_receivable_amount")
	private BigDecimal minAllowedReceivableAmount;

	@Column(name = "max_allowed_receivable_amount")
	private BigDecimal maxAllowedReceivableAmount;

	@Column(name = "min_installment_amount")
	private BigDecimal minInstallmentAmount;

	@Column(name = "theres_hold_for_approval")
	private BigDecimal theresHoldForApproval;
	
	@Column(name = "max_payment_plan_duration")
	private Integer maxPaymentPlanDuration;
	
	@Column(name = "default_installment_count")
	private Integer defaultInstallmentCount;
	
	@Column(name = "default_fee_per_installment_plan")
	private Integer defaultFeePerInstallmentPlan = 0;

	@Column(name = "installment_amount_rounding")
	private Integer installmentAmountRounding =  2;
	
	@Type(type = "numeric_boolean")
	@Column(name = "split_evenly", nullable = false)	
	private boolean splitEvenly = true;

	@Type(type = "numeric_boolean")
	@Column(name = "allow_custom_installment_plan", nullable = false)	
	private boolean allowCustomInstallmentPlan = false;

	@Type(type = "numeric_boolean")
	@Column(name = "add_interest_rate", nullable = false)	
	private boolean addInterestRate = false;

	@Type(type = "numeric_boolean")
	@Column(name = "add_installment_fee", nullable = false)	
	private boolean addInstallmentFee = false;
	
	@Type(type = "numeric_boolean")
	@Column(name = "default_block_payments", nullable = false)	
	private boolean defaultBlockPayments;
	
	@Type(type = "numeric_boolean")
	@Column(name = "require_internal_approval", nullable = false)	
	private boolean requireInternalApproval  = false;
	
    @Column(name = "default_interest_rate", nullable = false)
    @NotNull
    private int defaultInterestRate = 0;
    
	@Enumerated(value = EnumType.STRING)
	@Column(name = "default_recurrence_unit")
	private RecurrenceUnitEnum defaultRecurrenceUnit = RecurrenceUnitEnum.MONTH;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "action_on_remaining_amount")
	private ActionOnRemainingAmountEnum actionOnRemainingAmount = ActionOnRemainingAmountEnum.FIRST;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "clearing_priority")
	private ClearingPriorityEnum clearingPriority = ClearingPriorityEnum.OLDEST;	

	public BigDecimal getMinAllowedReceivableAmount() {
		return minAllowedReceivableAmount;
	}

	public void setMinAllowedReceivableAmount(BigDecimal minAllowedReceivableAmount) {
		this.minAllowedReceivableAmount = minAllowedReceivableAmount;
	}

	public BigDecimal getMaxAllowedReceivableAmount() {
		return maxAllowedReceivableAmount;
	}

	public void setMaxAllowedReceivableAmount(BigDecimal maxAllowedReceivableAmount) {
		this.maxAllowedReceivableAmount = maxAllowedReceivableAmount;
	}

	public BigDecimal getMinInstallmentAmount() {
		return minInstallmentAmount;
	}

	public void setMinInstallmentAmount(BigDecimal minInstallmentAmount) {
		this.minInstallmentAmount = minInstallmentAmount;
	}

	public Integer getMaxPaymentPlanDuration() {
		return maxPaymentPlanDuration;
	}

	public void setMaxPaymentPlanDuration(Integer maxPaymentPlanDuration) {
		this.maxPaymentPlanDuration = maxPaymentPlanDuration;
	}

	public RecurrenceUnitEnum getDefaultRecurrenceUnit() {
		return defaultRecurrenceUnit;
	}

	public void setDefaultRecurrenceUnit(RecurrenceUnitEnum defaultRecurrenceUnit) {
		this.defaultRecurrenceUnit = defaultRecurrenceUnit;
	}

	public Integer getDefaultInstallmentCount() {
		return defaultInstallmentCount;
	}

	public void setDefaultInstallmentCount(Integer defaultInstallmentCount) {
		this.defaultInstallmentCount = defaultInstallmentCount;
	}

	public boolean isSplitEvenly() {
		return splitEvenly;
	}

	public void setSplitEvenly(boolean splitEvenly) {
		this.splitEvenly = splitEvenly;
	}

	public boolean isAllowCustomInstallmentPlan() {
		return allowCustomInstallmentPlan;
	}

	public void setAllowCustomInstallmentPlan(boolean allowCustomInstallmentPlan) {
		this.allowCustomInstallmentPlan = allowCustomInstallmentPlan;
	}

	public boolean isAddInterestRate() {
		return addInterestRate;
	}

	public void setAddInterestRate(boolean addInterestRate) {
		this.addInterestRate = addInterestRate;
	}

	public int getDefaultInterestRate() {
		return defaultInterestRate;
	}

	public void setDefaultInterestRate(int defaultInterestRate) {
		this.defaultInterestRate = defaultInterestRate;
	}

	public boolean isAddInstallmentFee() {
		return addInstallmentFee;
	}

	public void setAddInstallmentFee(boolean addInstallmentFee) {
		this.addInstallmentFee = addInstallmentFee;
	}

	public Integer getDefaultFeePerInstallmentPlan() {
		return defaultFeePerInstallmentPlan;
	}

	public void setDefaultFeePerInstallmentPlan(Integer defaultFeePerInstallmentPlan) {
		this.defaultFeePerInstallmentPlan = defaultFeePerInstallmentPlan;
	}

	public Integer getInstallmentAmountRounding() {
		return installmentAmountRounding;
	}

	public void setInstallmentAmountRounding(Integer installmentAmountRounding) {
		this.installmentAmountRounding = installmentAmountRounding;
	}

	public ActionOnRemainingAmountEnum getActionOnRemainingAmount() {
		return actionOnRemainingAmount;
	}

	public void setActionOnRemainingAmount(ActionOnRemainingAmountEnum actionOnRemainingAmount) {
		this.actionOnRemainingAmount = actionOnRemainingAmount;
	}

	public ClearingPriorityEnum getClearingPriority() {
		return clearingPriority;
	}

	public void setClearingPriority(ClearingPriorityEnum clearingPriority) {
		this.clearingPriority = clearingPriority;
	}

	public boolean isDefaultBlockPayments() {
		return defaultBlockPayments;
	}

	public void setDefaultBlockPayments(boolean defaultBlockPayments) {
		this.defaultBlockPayments = defaultBlockPayments;
	}

	public boolean isRequireInternalApproval() {
		return requireInternalApproval;
	}

	public void setRequireInternalApproval(boolean requireInternalApproval) {
		this.requireInternalApproval = requireInternalApproval;
	}

	public BigDecimal getTheresHoldForApproval() {
		return theresHoldForApproval;
	}

	public void setTheresHoldForApproval(BigDecimal theresHoldForApproval) {
		this.theresHoldForApproval = theresHoldForApproval;
	}
	
    /**
     * allowedPaymentMethods
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ElementCollection(targetClass = PaymentMethodEnum.class)
    @CollectionTable(name = "crm_provider_payment_plan_policy_pay_methods", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private List<PaymentMethodEnum> allowedPaymentMethods = new ArrayList<PaymentMethodEnum>();
    
	public List<PaymentMethodEnum> getAllowedPaymentMethods() {
		return allowedPaymentMethods;
	}

	public void setAllowedPaymentMethods(List<PaymentMethodEnum> allowedPaymentMethods) {
		this.allowedPaymentMethods = allowedPaymentMethods;
	}

	@ManyToOne
    @JoinColumn(name = "dunning_default_pause_reason_id")
    private DunningPauseReason dunningDefaultPauseReason;
    
    public DunningPauseReason getDunningDefaultPauseReason() {
		return dunningDefaultPauseReason;
	}

	public void setDunningDefaultPauseReason(DunningPauseReason dunningDefaultPauseReason) {
		this.dunningDefaultPauseReason = dunningDefaultPauseReason;
	}
	
	@OneToMany(mappedBy = "provider", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CreditCategory> allowedCreditCategories = new ArrayList<CreditCategory>();
    
	public List<CreditCategory> getAllowedCreditCategories() {
		return allowedCreditCategories;
	}

	public void setAllowedCreditCategories(List<CreditCategory> allowedCreditCategories) {
		this.allowedCreditCategories = allowedCreditCategories;
	}

    @Override
    public Object clone() throws CloneNotSupportedException {
    	PaymentPlanPolicy o = (PaymentPlanPolicy) super.clone();
        return o;
    }
}
