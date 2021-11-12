package org.meveo.model.dunning;

import static javax.persistence.FetchType.LAZY;

import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "dunning_level_instance")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_level_instance_seq") })
@NamedQueries({
        @NamedQuery(name = "DunningLevelInstance.findByPolicyLevelId", query = "SELECT li FROM DunningLevelInstance li where li.policyLevel.id = :policyLevelId")
})
public class DunningLevelInstance extends AuditableEntity {

    private static final long serialVersionUID = -5809793412586160209L;

    @Column(name = "sequence")
    @NotNull
    private Integer sequence;

    @Column(name = "days_overdue")
    @NotNull
    private Integer daysOverdue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_policy_level_id")
    private DunningPolicyLevel policyLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_collection_plan_id")
    private DunningCollectionPlan collectionPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_collection_plan_status_id")
    private DunningCollectionPlanStatus collectionPlanStatus;

    @OneToMany(mappedBy = "dunningLevelInstance", fetch = LAZY)
    private List<DunningActionInstance> actions;

    @Column(name = "level_status", length = 255)
    @Enumerated(EnumType.STRING)
    @NotNull
    private DunningLevelInstanceStatusEnum levelStatus;

    public DunningLevelInstance() {
        super();
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getDaysOverdue() {
        return daysOverdue;
    }

    public void setDaysOverdue(Integer daysOverdue) {
        this.daysOverdue = daysOverdue;
    }

    public DunningPolicyLevel getPolicyLevel() {
        return policyLevel;
    }

    public void setPolicyLevel(DunningPolicyLevel policyLevel) {
        this.policyLevel = policyLevel;
    }

    public DunningCollectionPlan getCollectionPlan() {
        return collectionPlan;
    }

    public void setCollectionPlan(DunningCollectionPlan collectionPlan) {
        this.collectionPlan = collectionPlan;
    }

    public DunningCollectionPlanStatus getCollectionPlanStatus() {
        return collectionPlanStatus;
    }

    public void setCollectionPlanStatus(DunningCollectionPlanStatus collectionPlanStatus) {
        this.collectionPlanStatus = collectionPlanStatus;
    }

    public List<DunningActionInstance> getActions() {
        return actions;
    }

    public void setActions(List<DunningActionInstance> actions) {
        this.actions = actions;
    }

    public DunningLevelInstanceStatusEnum getLevelStatus() {
        return levelStatus;
    }

    public void setLevelStatus(DunningLevelInstanceStatusEnum levelStatus) {
        this.levelStatus = levelStatus;
    }
}
