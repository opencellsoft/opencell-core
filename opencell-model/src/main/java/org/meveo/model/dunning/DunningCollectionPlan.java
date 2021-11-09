package org.meveo.model.dunning;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.payments.PaymentMethod;

/**
 *The dunning collection plan
 *
 */
@Entity
@Table(name = "dunning_collection_plan")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_collection_plan_seq")})
public class DunningCollectionPlan extends AuditableEntity {

	/**
     * The collection plan billing Account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_plan_billing_account_id", referencedColumnName = "id")
     private BillingAccount collectionPlanBillingAccount;
    
    /**
     * The collection plan payment method
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_plan_payment_method_id", referencedColumnName = "id")
     private PaymentMethod collectionPlanPaymentMethod;
    
    /**
     * The collection plan pause reason
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_plan_pause_reason_id", referencedColumnName = "id")
     private DunningPauseReason collectionPlanPauseReason;
    
    /**
     * The collection plan stop reason
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_plan_stop_reason_id", referencedColumnName = "id")
     private DunningStopReason collectionPlanStopReason;
    
    /**
     * The collection plan related policy
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_plan_related_policy_id", referencedColumnName = "id")
     private DunningPolicy collectionPlanRelatedPolicy;
    
    /**
     * The collection plan current dunning level
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_plan_current_dunning_id", referencedColumnName = "id")
     private DunningLevel collectionPlanCurrentDunningLevel;
    
    /**
     * the sequence.
     */
    @Column(name = "collection_plan_current_dunning_level_sequence")
    private Integer collectionPlanCurrentDunningLevelSequence;
    
    /**
     * The collection plan start date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "collection_plan_start_date")
    private Date collectionPlanStartDate;
    
    /**
     * The collection plan days open
     */
    @Column(name = "collection_plan_days_open")
    private Integer collectionPlanDaysOpen;
    
    /**
     * The collection plan close date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "collection_plan_close_date")
    private Date collectionPlanCloseDate;
   
    /**
     * The collection plan status
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_plan_status_id", referencedColumnName = "id")
    private DunningCollectionPlanStatus collectionPlanStatus;
    
    /**
     * The collection plan last update
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "collection_plan_last_update")
    private Date collectionPlanLastUpdate;
    
    /**
     * The collection plan paused until date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "collection_plan_paused_until_date")
    private Date collectionPlanPausedUntilDate;
    
    /**
     * The collection plan assigned agent
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_plan_assigned_agent_id", referencedColumnName = "id")
    private DunningAgent collectionPlanAssignedAgent;
    
    /**
     * The collection plan total balance
     */
    @Column(name = "collection_plan_total_balance")
    private BigDecimal collectionPlanTotalBalance;
    
    /**
     * The collection plan aged balance
     */
    @Column(name = "collection_plan_aged_balance")
    private BigDecimal collectionPlanAgedBalance;
    
    /**
     * The collection plan due balance
     */
    @Column(name = "collection_plan_due_balance")
    private BigDecimal collectionPlanDueBalance;
    
    /**
     * The collection plan disputed balance
     */
    @Column(name = "collection_plan_disputed_balance")
    private BigDecimal collectionPlanDisputedBalance;
    
    /**
     * Retry payment on resume date
     */
    @Type(type = "numeric_boolean")
    @Column(name = "retry_payment_on_resume_date")
    private boolean retryPaymentOnResumeDate;

   
    
    public BillingAccount getCollectionPlanBillingAccount() {
		return collectionPlanBillingAccount;
	}

	public void setCollectionPlanBillingAccount(BillingAccount collectionPlanBillingAccount) {
		this.collectionPlanBillingAccount = collectionPlanBillingAccount;
	}

	public PaymentMethod getCollectionPlanPaymentMethod() {
		return collectionPlanPaymentMethod;
	}

	public void setCollectionPlanPaymentMethod(PaymentMethod collectionPlanPaymentMethod) {
		this.collectionPlanPaymentMethod = collectionPlanPaymentMethod;
	}

	public DunningPauseReason getCollectionPlanPauseReason() {
		return collectionPlanPauseReason;
	}

	public void setCollectionPlanPauseReason(DunningPauseReason collectionPlanPauseReason) {
		this.collectionPlanPauseReason = collectionPlanPauseReason;
	}

	public DunningStopReason getCollectionPlanStopReason() {
		return collectionPlanStopReason;
	}

	public void setCollectionPlanStopReason(DunningStopReason collectionPlanStopReason) {
		this.collectionPlanStopReason = collectionPlanStopReason;
	}

	public DunningPolicy getCollectionPlanRelatedPolicy() {
		return collectionPlanRelatedPolicy;
	}

	public void setCollectionPlanRelatedPolicy(DunningPolicy collectionPlanRelatedPolicy) {
		this.collectionPlanRelatedPolicy = collectionPlanRelatedPolicy;
	}

	public DunningLevel getCollectionPlanCurrentDunningLevel() {
		return collectionPlanCurrentDunningLevel;
	}

	public void setCollectionPlanCurrentDunningLevel(DunningLevel collectionPlanCurrentDunningLevel) {
		this.collectionPlanCurrentDunningLevel = collectionPlanCurrentDunningLevel;
	}

	public Integer getCollectionPlanCurrentDunningLevelSequence() {
		return collectionPlanCurrentDunningLevelSequence;
	}

	public void setCollectionPlanCurrentDunningLevelSequence(Integer collectionPlanCurrentDunningLevelSequence) {
		this.collectionPlanCurrentDunningLevelSequence = collectionPlanCurrentDunningLevelSequence;
	}

	public Date getCollectionPlanStartDate() {
		return collectionPlanStartDate;
	}

	public void setCollectionPlanStartDate(Date collectionPlanStartDate) {
		this.collectionPlanStartDate = collectionPlanStartDate;
	}

	public Integer getCollectionPlanDaysOpen() {
		return collectionPlanDaysOpen;
	}

	public void setCollectionPlanDaysOpen(Integer collectionPlanDaysOpen) {
		this.collectionPlanDaysOpen = collectionPlanDaysOpen;
	}

	public Date getCollectionPlanCloseDate() {
		return collectionPlanCloseDate;
	}

	public void setCollectionPlanCloseDate(Date collectionPlanCloseDate) {
		this.collectionPlanCloseDate = collectionPlanCloseDate;
	}

	public Date getCollectionPlanLastUpdate() {
		return collectionPlanLastUpdate;
	}

	public void setCollectionPlanLastUpdate(Date collectionPlanLastUpdate) {
		this.collectionPlanLastUpdate = collectionPlanLastUpdate;
	}

	public Date getCollectionPlanPausedUntilDate() {
		return collectionPlanPausedUntilDate;
	}

	public void setCollectionPlanPausedUntilDate(Date collectionPlanPausedUntilDate) {
		this.collectionPlanPausedUntilDate = collectionPlanPausedUntilDate;
	}

	public DunningAgent getCollectionPlanAssignedAgent() {
		return collectionPlanAssignedAgent;
	}

	public void setCollectionPlanAssignedAgent(DunningAgent collectionPlanAssignedAgent) {
		this.collectionPlanAssignedAgent = collectionPlanAssignedAgent;
	}

	public BigDecimal getCollectionPlanTotalBalance() {
		return collectionPlanTotalBalance;
	}

	public void setCollectionPlanTotalBalance(BigDecimal collectionPlanTotalBalance) {
		this.collectionPlanTotalBalance = collectionPlanTotalBalance;
	}

	public BigDecimal getCollectionPlanAgedBalance() {
		return collectionPlanAgedBalance;
	}

	public void setCollectionPlanAgedBalance(BigDecimal collectionPlanAgedBalance) {
		this.collectionPlanAgedBalance = collectionPlanAgedBalance;
	}

	public BigDecimal getCollectionPlanDueBalance() {
		return collectionPlanDueBalance;
	}

	public void setCollectionPlanDueBalance(BigDecimal collectionPlanDueBalance) {
		this.collectionPlanDueBalance = collectionPlanDueBalance;
	}

	public BigDecimal getCollectionPlanDisputedBalance() {
		return collectionPlanDisputedBalance;
	}

	public void setCollectionPlanDisputedBalance(BigDecimal collectionPlanDisputedBalance) {
		this.collectionPlanDisputedBalance = collectionPlanDisputedBalance;
	}

	public boolean isRetryPaymentOnResumeDate() {
		return retryPaymentOnResumeDate;
	}

	public void setRetryPaymentOnResumeDate(boolean retryPaymentOnResumeDate) {
		this.retryPaymentOnResumeDate = retryPaymentOnResumeDate;
	}

	public DunningCollectionPlanStatus getCollectionPlanStatus() {
        return collectionPlanStatus;
    }

    public void setCollectionPlanStatus(DunningCollectionPlanStatus collectionPlanStatus) {
        this.collectionPlanStatus = collectionPlanStatus;
    }
}
