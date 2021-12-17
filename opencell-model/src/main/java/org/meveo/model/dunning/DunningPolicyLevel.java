package org.meveo.model.dunning;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

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

    private static final long serialVersionUID = 1L;

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
     private DunningCollectionPlanStatus collectionPlanStatus;


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

    public DunningCollectionPlanStatus getCollectionPlanStatus() {
        return collectionPlanStatus;
    }

    public void setCollectionPlanStatus(DunningCollectionPlanStatus collectionPlanStatus) {
        this.collectionPlanStatus = collectionPlanStatus;
    }

    public DunningPolicy getDunningPolicy() {
        return dunningPolicy;
    }

    public void setDunningPolicy(DunningPolicy dunningPolicy) {
        this.dunningPolicy = dunningPolicy;
    }
}
