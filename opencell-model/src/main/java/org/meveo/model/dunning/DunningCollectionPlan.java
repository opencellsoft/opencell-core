package org.meveo.model.dunning;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;

/**
 *The dunning collection plan
 *
 */
@Entity
@Table(name = "dunning_collection_plan")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_collection_plan_seq")})
@NamedQueries({
		@NamedQuery(name = "DunningCollectionPlan.DCPtoResume", query = "SELECT dcp FROM DunningCollectionPlan dcp where dcp.status.status='PAUSED' and dcp.pausedUntilDate <= :resumeDate"),
		@NamedQuery(name = "DunningCollectionPlan.findByInvoiceId", query = "SELECT dcp FROM DunningCollectionPlan dcp where dcp.relatedInvoice.id = :invoiceID"),
		@NamedQuery(name = "DunningCollectionPlan.findByPolicy", query = "SELECT dcp FROM DunningCollectionPlan dcp where dcp.relatedPolicy = :dunningPolicy"),
		@NamedQuery(name = "DunningCollectionPlan.activeCollectionPlansIds", query = "SELECT dcp.id FROM DunningCollectionPlan dcp where dcp.status.status = 'ACTIVE'")
})
public class DunningCollectionPlan extends AuditableEntity {
	
    private static final long serialVersionUID = 1L;
    
    /**
     * The collection plan id
     */
    @Column(name = "collection_plan_number")
	@NotNull
	private String collectionPlanNumber;

    /**
     * The collection plan related policy
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_policy_id", referencedColumnName = "id")
     private DunningPolicy relatedPolicy;
    
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
    @JoinColumn(name = "billing_account_id", referencedColumnName = "id")
     private BillingAccount billingAccount;
    

    /**
    * The collection plan related invoice
    */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_invoice_id")
    private Invoice relatedInvoice;
    
    /**
     * The collection plan pause reason
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pause_reason_id", referencedColumnName = "id")
     private DunningPauseReason pauseReason;
    
    /**
     * The collection plan stop reason
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_reason_id", referencedColumnName = "id")
     private DunningStopReason stopReason;
    
    /**
     * the sequence.
     */
    @Column(name = "current_dunning_level_sequence")
    private Integer currentDunningLevelSequence;
    
    /**
     * The collection plan start date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate;
    
    /**
     * The collection plan days open
     */
    @Column(name = "days_open")
    private Integer daysOpen;
    
    /**
     * The collection plan close date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "close_date")
    private Date closeDate;
   
    /**
     * The collection plan status
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private DunningCollectionPlanStatus status;
    

    /**
     * The collection plan paused until date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "paused_until_date")
    private Date pausedUntilDate;
    
    /**
     * The collection plan total balance
     */
    @Column(name = "balance")
    private BigDecimal balance;
    
    /**
     * Retry payment on resume date
     */
    @Type(type = "numeric_boolean")
    @Column(name = "retry_payment_on_resume_date")
    private boolean retryPaymentOnResumeDate;
    
    /**
     * The dunning level instances
     */
    @OneToMany(mappedBy = "collectionPlan", fetch = FetchType.LAZY)
	private List<DunningLevelInstance> dunningLevelInstances;
    
    /**
     * The collection plan next action
     */
    @Column(name = "next_action")
	private String nextAction;
    
    /**
     * The collection plan next action date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "next_action_date")
    private Date nextActionDate;
    
    /**
     * The collection plan next action
     */
    @Column(name = "last_action")
	private String lastAction;
    
    /**
     * The collection plan last action date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_action_date")
    private Date lastActionDate;
    
    /**
     * The collection plan days open
     */
    @Column(name = "total_dunning_levels")
    private Integer totalDunningLevels;
    
    /**
     * The collection plan pause duration
     */
    @Column(name = "pause_duration")
    private Integer pauseDuration;

    public DunningCollectionPlan() {};

	public DunningCollectionPlan(Long id) {
		this.id = id;
	}

	public DunningPolicy getRelatedPolicy() {
		return relatedPolicy;
	}

	public void setRelatedPolicy(DunningPolicy relatedPolicy) {
		this.relatedPolicy = relatedPolicy;
	}

	public DunningCollectionPlan getInitialCollectionPlan() {
		return initialCollectionPlan;
	}

	public void setInitialCollectionPlan(DunningCollectionPlan initialCollectionPlan) {
		this.initialCollectionPlan = initialCollectionPlan;
	}

	public BillingAccount getBillingAccount() {
		return billingAccount;
	}

	public void setBillingAccount(BillingAccount billingAccount) {
		this.billingAccount = billingAccount;
	}

	public Invoice getRelatedInvoice() {
		return relatedInvoice;
	}

	public void setRelatedInvoice(Invoice relatedInvoice) {
		this.relatedInvoice = relatedInvoice;
	}

	public DunningPauseReason getPauseReason() {
		return pauseReason;
	}

	public void setPauseReason(DunningPauseReason pauseReason) {
		this.pauseReason = pauseReason;
	}

	public DunningStopReason getStopReason() {
		return stopReason;
	}

	public void setStopReason(DunningStopReason stopReason) {
		this.stopReason = stopReason;
	}

	public Integer getCurrentDunningLevelSequence() {
		return currentDunningLevelSequence;
	}

	public void setCurrentDunningLevelSequence(Integer currentDunningLevelSequence) {
		this.currentDunningLevelSequence = currentDunningLevelSequence;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Integer getDaysOpen() {
		return daysOpen;
	}

	public void setDaysOpen(Integer daysOpen) {
		this.daysOpen = daysOpen;
	}

	public Date getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	public DunningCollectionPlanStatus getStatus() {
		return status;
	}

	public void setStatus(DunningCollectionPlanStatus status) {
		this.status = status;
	}

	public Date getPausedUntilDate() {
		return pausedUntilDate;
	}

	public void setPausedUntilDate(Date pausedUntilDate) {
		this.pausedUntilDate = pausedUntilDate;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public boolean isRetryPaymentOnResumeDate() {
		return retryPaymentOnResumeDate;
	}

	public void setRetryPaymentOnResumeDate(boolean retryPaymentOnResumeDate) {
		this.retryPaymentOnResumeDate = retryPaymentOnResumeDate;
	}

	public String getNextAction() {
		return nextAction;
	}

	public void setNextAction(String nextAction) {
		this.nextAction = nextAction;
	}

	public Date getNextActionDate() {
		return nextActionDate;
	}

	public void setNextActionDate(Date nextActionDate) {
		this.nextActionDate = nextActionDate;
	}

	public String getLastAction() {
		return lastAction;
	}

	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
	}

	public Date getLastActionDate() {
		return lastActionDate;
	}

	public void setLastActionDate(Date lastActionDate) {
		this.lastActionDate = lastActionDate;
	}

	public List<DunningLevelInstance> getDunningLevelInstances() {
		return dunningLevelInstances;
	}

	public void setDunningLevelInstances(List<DunningLevelInstance> dunningLevelInstances) {
		this.dunningLevelInstances = dunningLevelInstances;
	}

	public Integer getTotalDunningLevels() {
		return totalDunningLevels;
	}

	public void setTotalDunningLevels(Integer totalDunningLevels) {
		this.totalDunningLevels = totalDunningLevels;
	}

	public Integer getPauseDuration() {
		return pauseDuration;
	}

	public void setPauseDuration(Integer pauseDuration) {
		this.pauseDuration = pauseDuration;
	}

	public String getCollectionPlanNumber() {
		return collectionPlanNumber;
	}

	public void setCollectionPlanNumber(String collectionPlanNumber) {
		this.collectionPlanNumber = collectionPlanNumber;
	}

	/**
	 * @param days
	 */
	public void addPauseDuration(int days) {
		this.pauseDuration = this.pauseDuration == null ? days : this.pauseDuration + days;
	}
}
