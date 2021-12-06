package org.meveo.api.dto.response.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.dunning.DunningPauseReason;
import org.meveo.model.payments.ActionOnRemainingAmountEnum;
import org.meveo.model.payments.ClearingPriorityEnum;
import org.meveo.model.payments.CreditCategory;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentPlanPolicy;
import org.meveo.model.payments.RecurrenceUnitEnum;

/**
 * The Class PaymentPlanPolicyDto.
 *
 * @author HHAN
 */
@XmlRootElement(name = "PaymentPlanPolicy")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentPlanPolicyDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

	@XmlAttribute(required = true)
	private Integer defaultInstallmentCount;
	private Integer maxPaymentPlanDuration;
	private Integer defaultFeePerInstallmentPlan;
	private Integer installmentAmountRounding;
    private Integer defaultInterestRate;
	
	private BigDecimal minAllowedReceivableAmount;
	private BigDecimal maxAllowedReceivableAmount;
	private BigDecimal minInstallmentAmount;
	private BigDecimal theresHoldForApproval;
	
	private boolean splitEvenly;
	private boolean allowCustomInstallmentPlan;
	private boolean addInterestRate;
	private boolean addInstallmentFee;
	private boolean defaultBlockPayments;
	private boolean requireInternalApproval;
	private RecurrenceUnitEnum defaultRecurrenceUnit;
	private ActionOnRemainingAmountEnum actionOnRemainingAmount;
	private ClearingPriorityEnum clearingPriority; 
	public List<PaymentMethodEnum> allowedPaymentMethods;
	public List<Long> dunningDefaultPauseReason;
	public List<Long> allowedCreditCategories;
	
	public List<PaymentMethodEnum> getAllowedPaymentMethods() {
		return allowedPaymentMethods;
	}

	public void setAllowedPaymentMethods(List<PaymentMethodEnum> allowedPaymentMethods) {
		this.allowedPaymentMethods = allowedPaymentMethods;
	}
	
	public List<Long> getAllowedCreditCategories() {
		return allowedCreditCategories;
	}

	public void setAllowedCreditCategories(List<Long> allowedCreditCategories) {
		this.allowedCreditCategories = allowedCreditCategories;
	}

	public List<Long> getDunningDefaultPauseReason() {
		return dunningDefaultPauseReason;
	}

	public void setDunningDefaultPauseReason(List<Long> dunningDefaultPauseReason) {
		this.dunningDefaultPauseReason = dunningDefaultPauseReason;
	}

	public Integer getDefaultInstallmentCount() {
		return defaultInstallmentCount;
	}

	public Integer getMaxPaymentPlanDuration() {
		return maxPaymentPlanDuration;
	}

	public Integer getDefaultFeePerInstallmentPlan() {
		return defaultFeePerInstallmentPlan;
	}

	public Integer getInstallmentAmountRounding() {
		return installmentAmountRounding;
	}

	public Integer getDefaultInterestRate() {
		return defaultInterestRate;
	}

	public BigDecimal getMinAllowedReceivableAmount() {
		return minAllowedReceivableAmount;
	}

	public BigDecimal getMaxAllowedReceivableAmount() {
		return maxAllowedReceivableAmount;
	}

	public BigDecimal getMinInstallmentAmount() {
		return minInstallmentAmount;
	}

	public BigDecimal getTheresHoldForApproval() {
		return theresHoldForApproval;
	}

	public boolean isSplitEvenly() {
		return splitEvenly;
	}

	public boolean isAllowCustomInstallmentPlan() {
		return allowCustomInstallmentPlan;
	}

	public boolean isAddInterestRate() {
		return addInterestRate;
	}

	public boolean isAddInstallmentFee() {
		return addInstallmentFee;
	}

	public boolean isDefaultBlockPayments() {
		return defaultBlockPayments;
	}

	public boolean isRequireInternalApproval() {
		return requireInternalApproval;
	}

	public RecurrenceUnitEnum getDefaultRecurrenceUnit() {
		return defaultRecurrenceUnit;
	}

	public ActionOnRemainingAmountEnum getActionOnRemainingAmount() {
		return actionOnRemainingAmount;
	}

	public ClearingPriorityEnum getClearingPriority() {
		return clearingPriority;
	}

	public void setDefaultInstallmentCount(Integer defaultInstallmentCount) {
		this.defaultInstallmentCount = defaultInstallmentCount;
	}

	public void setMaxPaymentPlanDuration(Integer maxPaymentPlanDuration) {
		this.maxPaymentPlanDuration = maxPaymentPlanDuration;
	}

	public void setDefaultFeePerInstallmentPlan(Integer defaultFeePerInstallmentPlan) {
		this.defaultFeePerInstallmentPlan = defaultFeePerInstallmentPlan;
	}

	public void setInstallmentAmountRounding(Integer installmentAmountRounding) {
		this.installmentAmountRounding = installmentAmountRounding;
	}

	public void setDefaultInterestRate(Integer defaultInterestRate) {
		this.defaultInterestRate = defaultInterestRate;
	}

	public void setMinAllowedReceivableAmount(BigDecimal minAllowedReceivableAmount) {
		this.minAllowedReceivableAmount = minAllowedReceivableAmount;
	}

	public void setMaxAllowedReceivableAmount(BigDecimal maxAllowedReceivableAmount) {
		this.maxAllowedReceivableAmount = maxAllowedReceivableAmount;
	}

	public void setMinInstallmentAmount(BigDecimal minInstallmentAmount) {
		this.minInstallmentAmount = minInstallmentAmount;
	}

	public void setTheresHoldForApproval(BigDecimal theresHoldForApproval) {
		this.theresHoldForApproval = theresHoldForApproval;
	}

	public void setSplitEvenly(boolean splitEvenly) {
		this.splitEvenly = splitEvenly;
	}

	public void setAllowCustomInstallmentPlan(boolean allowCustomInstallmentPlan) {
		this.allowCustomInstallmentPlan = allowCustomInstallmentPlan;
	}

	public void setAddInterestRate(boolean addInterestRate) {
		this.addInterestRate = addInterestRate;
	}

	public void setAddInstallmentFee(boolean addInstallmentFee) {
		this.addInstallmentFee = addInstallmentFee;
	}

	public void setDefaultBlockPayments(boolean defaultBlockPayments) {
		this.defaultBlockPayments = defaultBlockPayments;
	}

	public void setRequireInternalApproval(boolean requireInternalApproval) {
		this.requireInternalApproval = requireInternalApproval;
	}

	public void setDefaultRecurrenceUnit(RecurrenceUnitEnum defaultRecurrenceUnit) {
		this.defaultRecurrenceUnit = defaultRecurrenceUnit;
	}

	public void setActionOnRemainingAmount(ActionOnRemainingAmountEnum actionOnRemainingAmount) {
		this.actionOnRemainingAmount = actionOnRemainingAmount;
	}

	public void setClearingPriority(ClearingPriorityEnum clearingPriority) {
		this.clearingPriority = clearingPriority;
	}

	/**
     * Instantiates a new Payment Plan Policy Dto.
     */
    public PaymentPlanPolicyDto() {

    }

    /**
     * Instantiates a new payment Plan Policy dto.
     *
     * @param paymentPlanPolicy the paymentPlanPolicy entity
     */
    public PaymentPlanPolicyDto(PaymentPlanPolicy paymentPlanPolicy) {
        if (paymentPlanPolicy == null) {
            return;
        }
        defaultInstallmentCount = paymentPlanPolicy.getDefaultInstallmentCount();
        maxPaymentPlanDuration	 = paymentPlanPolicy.getMaxPaymentPlanDuration();
        defaultFeePerInstallmentPlan = paymentPlanPolicy.getDefaultFeePerInstallmentPlan();
        installmentAmountRounding = paymentPlanPolicy.getInstallmentAmountRounding();
        defaultInterestRate = paymentPlanPolicy.getDefaultInterestRate();
        minAllowedReceivableAmount = paymentPlanPolicy.getMinAllowedReceivableAmount();
        maxAllowedReceivableAmount = paymentPlanPolicy.getMaxAllowedReceivableAmount();
        minInstallmentAmount = paymentPlanPolicy.getMinInstallmentAmount();
        theresHoldForApproval = paymentPlanPolicy.getTheresHoldForApproval();
        splitEvenly = paymentPlanPolicy.isSplitEvenly();
        allowCustomInstallmentPlan = paymentPlanPolicy.isAllowCustomInstallmentPlan();
        addInterestRate = paymentPlanPolicy.isAddInterestRate();
        addInstallmentFee = paymentPlanPolicy.isAddInstallmentFee();
        defaultBlockPayments = paymentPlanPolicy.isDefaultBlockPayments();
        requireInternalApproval = paymentPlanPolicy.isRequireInternalApproval();
        defaultRecurrenceUnit = paymentPlanPolicy.getDefaultRecurrenceUnit();
        actionOnRemainingAmount = paymentPlanPolicy.getActionOnRemainingAmount();
        clearingPriority = paymentPlanPolicy.getClearingPriority();
        allowedPaymentMethods = new ArrayList<PaymentMethodEnum>();
        for (PaymentMethodEnum elementPaymentMethodEnum : paymentPlanPolicy.getAllowedPaymentMethods()) {
        	allowedPaymentMethods.add(elementPaymentMethodEnum);
        }
        List<Long> listDunningDefaultPauseReasonDto = new ArrayList<Long>();
        for (DunningPauseReason elementDunningDefaultPauseReason : paymentPlanPolicy.getDunningDefaultPauseReason()) {
        	listDunningDefaultPauseReasonDto.add(elementDunningDefaultPauseReason.getId());
        }
        dunningDefaultPauseReason = listDunningDefaultPauseReasonDto;
        
        List<Long> listAllowedCreditCategoriesDto = new ArrayList<Long>();
        for (CreditCategory elementAllowedCreditCategories : paymentPlanPolicy.getAllowedCreditCategories()) {
        	listAllowedCreditCategoriesDto.add(elementAllowedCreditCategories.getId());
        }
        allowedCreditCategories = listAllowedCreditCategoriesDto;
    }

    
    /**
     * From dto.
     *
     * @return the payment Plan Policy
     */
    public PaymentPlanPolicy fromDto() {
    	PaymentPlanPolicy paymentPlanPolicy = new PaymentPlanPolicy();
    	paymentPlanPolicy.setDefaultInstallmentCount(getDefaultInstallmentCount());
    	paymentPlanPolicy.setMaxPaymentPlanDuration	(getMaxPaymentPlanDuration());
    	paymentPlanPolicy.setDefaultFeePerInstallmentPlan(getDefaultFeePerInstallmentPlan());
    	paymentPlanPolicy.setInstallmentAmountRounding(getInstallmentAmountRounding());
    	paymentPlanPolicy.setDefaultInterestRate(getDefaultInterestRate());
    	paymentPlanPolicy.setMinAllowedReceivableAmount(getMinAllowedReceivableAmount());
    	paymentPlanPolicy.setMaxAllowedReceivableAmount(getMaxAllowedReceivableAmount());
    	paymentPlanPolicy.setMinInstallmentAmount(getMinInstallmentAmount());
    	paymentPlanPolicy.setTheresHoldForApproval(getTheresHoldForApproval());
    	paymentPlanPolicy.setSplitEvenly(isSplitEvenly());
    	paymentPlanPolicy.setAllowCustomInstallmentPlan(isAllowCustomInstallmentPlan());
    	paymentPlanPolicy.setAddInterestRate(isAddInterestRate());
    	paymentPlanPolicy.setAddInstallmentFee(isAddInstallmentFee());
    	paymentPlanPolicy.setDefaultBlockPayments(isDefaultBlockPayments());
    	paymentPlanPolicy.setRequireInternalApproval(isRequireInternalApproval());
    	paymentPlanPolicy.setDefaultRecurrenceUnit(getDefaultRecurrenceUnit());
    	paymentPlanPolicy.setActionOnRemainingAmount(getActionOnRemainingAmount());
    	paymentPlanPolicy.setClearingPriority(getClearingPriority());
    	paymentPlanPolicy.setAllowedPaymentMethods(paymentPlanPolicy.getAllowedPaymentMethods());        
    	paymentPlanPolicy.setDunningDefaultPauseReason(paymentPlanPolicy.getDunningDefaultPauseReason());        
    	paymentPlanPolicy.setAllowedCreditCategories(paymentPlanPolicy.getAllowedCreditCategories());        
        return paymentPlanPolicy;
    }

    @Override
    public String toString() {
        return "PaymentPlanPolicyDto [defaultInstallmentCount=" + defaultInstallmentCount + 
    		", maxPaymentPlanDuration=" + maxPaymentPlanDuration + 
    		", defaultFeePerInstallmentPlan=" + defaultFeePerInstallmentPlan + 
    		", installmentAmountRounding=" + installmentAmountRounding + 
    		", defaultInterestRate=" + defaultInterestRate + 
    		", minAllowedReceivableAmount=" + minAllowedReceivableAmount + 
    		", maxAllowedReceivableAmount=" + maxAllowedReceivableAmount + 
    		", minInstallmentAmount=" + minInstallmentAmount + 
    		", theresHoldForApproval=" + theresHoldForApproval + 
    		", splitEvenly=" + splitEvenly + 
    		", allowCustomInstallmentPlan=" + allowCustomInstallmentPlan + 
    		", addInterestRate=" + addInterestRate + 
    		", addInstallmentFee=" + addInstallmentFee + 
    		", defaultBlockPayments=" + defaultBlockPayments + 
    		", requireInternalApproval=" + requireInternalApproval + 
    		", defaultRecurrenceUnit=" + defaultRecurrenceUnit + 
    		", actionOnRemainingAmount=" + actionOnRemainingAmount + 
    		", clearingPriority=" + clearingPriority + "]";
    }
}