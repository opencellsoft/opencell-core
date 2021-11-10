package org.meveo.model.dunning;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.RatedTransaction;

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
     * The collection plan related policy
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_plan_related_policy_id", referencedColumnName = "id")
     private DunningPolicy collectionPlanRelatedPolicy;
    
    /**
     * The collection plan billing Account
     */
     @OneToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "initial_collection_plan_id")
     private DunningCollectionPlan initialCollectionPlan;

	/**
     * The collection plan billing Account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_plan_billing_account_id", referencedColumnName = "id")
     private BillingAccount collectionPlanBillingAccount;
    

    /**
    * The collection plan related invoice
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_plan_related_invoice_id", referencedColumnName = "id")
    private Invoice collectionPlanRelatedInvoice;
    
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
     * The collection plan current dunning level
     */
     /*@ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "collection_plan_current_dunning_id", referencedColumnName = "id")
     private DunningLevelInstance collectionPlanCurrentDunningLevel;*/
    
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
     * The collection plan paused until date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "collection_plan_paused_until_date")
    private Date collectionPlanPausedUntilDate;
    
    /**
     * The collection plan total balance
     */
    @Column(name = "collection_plan_balance")
    private BigDecimal collectionPlanBalance;
    
    /**
     * Retry payment on resume date
     */
    @Type(type = "numeric_boolean")
    @Column(name = "retry_payment_on_resume_date")
    private boolean retryPaymentOnResumeDate;
    
    /**
     * The dunning level instances
     */
    /*@OneToMany(mappedBy = "dunningCollectionPlan", fetch = FetchType.LAZY)
	private List<DunningLevelInstance> dunningLevelInstances;*/
    
    /**
     * The collection plan next action
     */
    @Column(name = "collection_plan_next_action")
	private String collectionPlanNextAction;
    
    /**
     * The collection plan next action date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "collection_plan_next_Action_date")
    private Date collectionPlanNextActionDate;
    
    /**
     * The collection plan next action
     */
    @Column(name = "collection_plan_last_action")
	private String collectionPlanLastAction;
    
    /**
     * The collection plan last action date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "collection_plan_last_Action_date")
    private Date collectionPlanLastActionDate;

	public DunningPolicy getCollectionPlanRelatedPolicy() {
		return collectionPlanRelatedPolicy;
	}

	public void setCollectionPlanRelatedPolicy(DunningPolicy collectionPlanRelatedPolicy) {
		this.collectionPlanRelatedPolicy = collectionPlanRelatedPolicy;
	}

	public DunningCollectionPlan getInitialCollectionPlan() {
		return initialCollectionPlan;
	}

	public void setInitialCollectionPlan(DunningCollectionPlan initialCollectionPlan) {
		this.initialCollectionPlan = initialCollectionPlan;
	}

	public BillingAccount getCollectionPlanBillingAccount() {
		return collectionPlanBillingAccount;
	}

	public void setCollectionPlanBillingAccount(BillingAccount collectionPlanBillingAccount) {
		this.collectionPlanBillingAccount = collectionPlanBillingAccount;
	}

	public Invoice getCollectionPlanRelatedInvoice() {
		return collectionPlanRelatedInvoice;
	}

	public void setCollectionPlanRelatedInvoice(Invoice collectionPlanRelatedInvoice) {
		this.collectionPlanRelatedInvoice = collectionPlanRelatedInvoice;
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

	public DunningCollectionPlanStatus getCollectionPlanStatus() {
		return collectionPlanStatus;
	}

	public void setCollectionPlanStatus(DunningCollectionPlanStatus collectionPlanStatus) {
		this.collectionPlanStatus = collectionPlanStatus;
	}

	public Date getCollectionPlanPausedUntilDate() {
		return collectionPlanPausedUntilDate;
	}

	public void setCollectionPlanPausedUntilDate(Date collectionPlanPausedUntilDate) {
		this.collectionPlanPausedUntilDate = collectionPlanPausedUntilDate;
	}

	public BigDecimal getCollectionPlanBalance() {
		return collectionPlanBalance;
	}

	public void setCollectionPlanBalance(BigDecimal collectionPlanBalance) {
		this.collectionPlanBalance = collectionPlanBalance;
	}

	public boolean isRetryPaymentOnResumeDate() {
		return retryPaymentOnResumeDate;
	}

	public void setRetryPaymentOnResumeDate(boolean retryPaymentOnResumeDate) {
		this.retryPaymentOnResumeDate = retryPaymentOnResumeDate;
	}

	public String getCollectionPlanNextAction() {
		return collectionPlanNextAction;
	}

	public void setCollectionPlanNextAction(String collectionPlanNextAction) {
		this.collectionPlanNextAction = collectionPlanNextAction;
	}

	public Date getCollectionPlanNextActionDate() {
		return collectionPlanNextActionDate;
	}

	public void setCollectionPlanNextActionDate(Date collectionPlanNextActionDate) {
		this.collectionPlanNextActionDate = collectionPlanNextActionDate;
	}

	public String getCollectionPlanLastAction() {
		return collectionPlanLastAction;
	}

	public void setCollectionPlanLastAction(String collectionPlanLastAction) {
		this.collectionPlanLastAction = collectionPlanLastAction;
	}

	public Date getCollectionPlanLastActionDate() {
		return collectionPlanLastActionDate;
	}

	public void setCollectionPlanLastActionDate(Date collectionPlanLastActionDate) {
		this.collectionPlanLastActionDate = collectionPlanLastActionDate;
	}
    
}
