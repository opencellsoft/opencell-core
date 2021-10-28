package org.meveo.model.dunning;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *The dunning policy level
 * @author khalid.horri
 *
 */
@Entity
@Table(name = "dunning_policy_level")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_policy_level_seq")})
@NamedQueries({
        @NamedQuery(name = "DunningPolicyLevel.findDunningPolicyLevels", query = "SELECT dpl FROM DunningPolicyLevel dpl where dpl.dunningPolicy.id=:policyId")})
public class DunningPolicyLevel extends AuditableEntity {

    /**
     * the sequence.
     */
    @Column(name = "sequence")
    @NotNull
    private Integer sequence;
    /**
     * The dunning level
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_level_id", referencedColumnName = "id")
    private DunningLevel dunningLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_policy_id")
    private DunningPolicy dunningPolicy;

    /**
     * The collection plan status
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_plan_status_id", referencedColumnName = "id")
     private CollectionPlanStatus collectionPlanStatus;

    /**
     * The invoice dunning status
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_dunning_statuses_id", referencedColumnName = "id")
     private DunningInvoiceStatus invoiceDunningStatuses;

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public DunningLevel getDunningLevel() {
        return dunningLevel;
    }

    public void setDunningLevel(DunningLevel dunningLevel) {
        this.dunningLevel = dunningLevel;
    }

    public CollectionPlanStatus getCollectionPlanStatus() {
        return collectionPlanStatus;
    }

    public void setCollectionPlanStatus(CollectionPlanStatus collectionPlanStatus) {
        this.collectionPlanStatus = collectionPlanStatus;
    }

    public DunningInvoiceStatus getInvoiceDunningStatuses() {
        return invoiceDunningStatuses;
    }

    public void setInvoiceDunningStatuses(DunningInvoiceStatus invoiceDunningStatuses) {
        this.invoiceDunningStatuses = invoiceDunningStatuses;
    }

    public DunningPolicy getDunningPolicy() {
        return dunningPolicy;
    }

    public void setDunningPolicy(DunningPolicy dunningPolicy) {
        this.dunningPolicy = dunningPolicy;
    }
}
