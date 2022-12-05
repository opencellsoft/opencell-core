package org.meveo.model.dunning;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.payments.ActionModeEnum;
import org.meveo.model.payments.ActionTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "dunning_action_instance")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_action_instance_seq") })
@NamedQueries({
        @NamedQuery(name = "DunningActionInstance.updateStatus", query = "UPDATE DunningActionInstance ai SET ai.actionStatus=:actionStatus WHERE ai.dunningLevelInstance=:dunningLevelInstance") })
public class DunningActionInstance extends BusinessEntity {

    private static final long serialVersionUID = 2810376973487134233L;

    @Column(name = "action_type", length = 255, nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionTypeEnum actionType;

    @Column(name = "action_mode", length = 255, nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionModeEnum actionMode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_agent_id")
    private DunningAgent actionOwner;

    @Column(name = "action_restult", length = 255)
    @Size(max = 255)
    private String actionRestult;

    @Column(name = "action_status", length = 255)
    @Enumerated(EnumType.STRING)
    @NotNull
    private DunningActionInstanceStatusEnum actionStatus = DunningActionInstanceStatusEnum.TO_BE_DONE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_collection_plan_id")
    private DunningCollectionPlan collectionPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_level_instance_id")
    private DunningLevelInstance dunningLevelInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_action_id")
    private DunningAction dunningAction;

    public DunningActionInstance() {
        super();
    }

    public ActionTypeEnum getActionType() {
        return actionType;
    }

    public void setActionType(ActionTypeEnum actionType) {
        this.actionType = actionType;
    }

    public ActionModeEnum getActionMode() {
        return actionMode;
    }

    public void setActionMode(ActionModeEnum actionMode) {
        this.actionMode = actionMode;
    }

    public DunningAgent getActionOwner() {
        return actionOwner;
    }

    public void setActionOwner(DunningAgent actionOwner) {
        this.actionOwner = actionOwner;
    }

    public String getActionRestult() {
        return actionRestult;
    }

    public void setActionRestult(String actionRestult) {
        this.actionRestult = actionRestult;
    }

    public DunningActionInstanceStatusEnum getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(DunningActionInstanceStatusEnum actionStatus) {
        this.actionStatus = actionStatus;
    }

    public DunningCollectionPlan getCollectionPlan() {
        return collectionPlan;
    }

    public void setCollectionPlan(DunningCollectionPlan collectionPlan) {
        this.collectionPlan = collectionPlan;
    }

    public DunningLevelInstance getDunningLevelInstance() {
        return dunningLevelInstance;
    }

    public void setDunningLevelInstance(DunningLevelInstance dunningLevelInstance) {
        this.dunningLevelInstance = dunningLevelInstance;
    }

    public DunningAction getDunningAction() {
        return dunningAction;
    }

    public void setDunningAction(DunningAction dunningAction) {
        this.dunningAction = dunningAction;
    }

}
