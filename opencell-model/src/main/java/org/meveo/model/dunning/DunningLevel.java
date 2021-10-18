package org.meveo.model.dunning;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *The dunning level
 * @author khalid.horri
 *
 */
@Entity
@Table(name = "dunning_dunning_level")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_level_seq")})
public class DunningLevel extends AuditableEntity {
    /**
     * the level sequence.
     */
    @Column(name = "level_sequence")
    @NotNull
    private Integer LevelSequence;

    /**
     * Collection plan status
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_level_collection_plan_status_id", referencedColumnName = "id")
    private CollectionPlanStatus policyLevelCollectionPlanStatus;
    /**
     * An invoice dunning status
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_level_invoice_status_id", referencedColumnName = "id")
    private InvoiceDunningStatuses policyLevelInvoiceStatus;

    /**
     * Get the level sequence
     * @return a level sequence
     */
    public Integer getLevelSequence() {
        return LevelSequence;
    }

    /**
     * Set the level sequence
     * @param levelSequence
     */
    public void setLevelSequence(Integer levelSequence) {
        LevelSequence = levelSequence;
    }

    /**
     * Get the collection plan status
     * @return  collection plan status
     */
    public CollectionPlanStatus getPolicyLevelCollectionPlanStatus() {
        return policyLevelCollectionPlanStatus;
    }

    /**
     * Set collection plan status
     * @param policyLevelCollectionPlanStatus
     */
    public void setPolicyLevelCollectionPlanStatus(CollectionPlanStatus policyLevelCollectionPlanStatus) {
        this.policyLevelCollectionPlanStatus = policyLevelCollectionPlanStatus;
    }

    /**
     * Get invoice dunning status
     * @return invoice dunning status
     */
    public InvoiceDunningStatuses getPolicyLevelInvoiceStatus() {
        return policyLevelInvoiceStatus;
    }

    /**
     * Set invoice dunning status
     * @param policyLevelInvoiceStatus
     */
    public void setPolicyLevelInvoiceStatus(InvoiceDunningStatuses policyLevelInvoiceStatus) {
        this.policyLevelInvoiceStatus = policyLevelInvoiceStatus;
    }
}
