/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.wf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Workflow status transition group
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ExportIdentifier({ "uuid" })
@Table(name = "wf_transition", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wf_transition_seq"), })
@NamedQueries({
        @NamedQuery(name = "WFTransition.listByFromStatus", query = "SELECT wft FROM WFTransition wft where (wft.fromStatus=:fromStatusValue or wft.fromStatus='*') and workflow=:workflowValue order by priority ASC") })
public class WFTransition extends BaseEntity implements Comparable<WFTransition> {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier - UUID
     */
    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid;

    /**
     * 
     * Workflow status change - from status
     */
    @Column(name = "from_status")
    private String fromStatus;

    /**
     * 
     * Workflow status change - to status
     */
    @Column(name = "to_status")
    private String toStatus;

    /**
     * Execution priority sorting sequence
     */
    @Column(name = "priority")
    private int priority;

    /**
     * Description
     */
    @Column(name = "description", nullable = true, length = 255)
    @Size(max = 255)
    @NotNull
    private String description;

    /**
     * Workflow
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

    /**
     * Decision rules
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
    @JoinTable(name = "wf_transition_decision_rule", joinColumns = @JoinColumn(name = "transition_id"), inverseJoinColumns = @JoinColumn(name = "decision_rule_id"))
    private Set<WFDecisionRule> wfDecisionRules = new HashSet<>();

    /**
     * Actions
     */
    @OneToMany(mappedBy = "wfTransition", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("priority ASC")
    private List<WFAction> wfActions = new ArrayList<WFAction>();

    /*
     * Expression to check if transition applies
     */
    @Column(name = "condition_el", length = 2000)
    @Size(max = 2000)
    private String conditionEl;

    /**
     * setting uuid if null
     */
    @PrePersist
    public void setUUIDIfNull() {
    	if (uuid == null) {
    		uuid = UUID.randomUUID().toString();
    	}
    }
    
    public String getUuid() {
    	setUUIDIfNull(); // setting uuid if null to be sure that the existing code expecting uuid not null will not be impacted
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the fromStatus
     */
    public String getFromStatus() {
        return fromStatus;
    }

    /**
     * @param fromStatus the fromStatus to set
     */
    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    /**
     * @return the toStatus
     */
    public String getToStatus() {
        return toStatus;
    }

    /**
     * @param toStatus the toStatus to set
     */
    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the workflow
     */
    public Workflow getWorkflow() {
        return workflow;
    }

    /**
     * @param workflow the workflow to set
     */
    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    /**
     * @return the wfActions
     */
    public List<WFAction> getWfActions() {
        return wfActions;
    }

    /**
     * @param wfActions the wfActions to set
     */
    public void setWfActions(List<WFAction> wfActions) {
        this.wfActions = wfActions;
    }

    /**
     * @return the conditionEl
     */
    public String getConditionEl() {
        return conditionEl;
    }

    /**
     * @param conditionEl the conditionEl to set
     */
    public void setConditionEl(String conditionEl) {
        this.conditionEl = conditionEl;
    }

    public String getCombinedEl() {
        if (CollectionUtils.isEmpty(wfDecisionRules)) {
            return conditionEl;
        }

        StringBuffer combinedEl = new StringBuffer();
        final String AND = " AND ";
        StringBuffer combinedDecisionRuleEL = new StringBuffer();
        for (WFDecisionRule wfDecisionRule : wfDecisionRules) {
            if (!StringUtils.isBlank(wfDecisionRule.getConditionEl())) {
                combinedDecisionRuleEL.append(AND).append(wfDecisionRule.getConditionEl());
            }
        }
        String trimmedEl = "";
        String elWithoutBrackets = "";
        if (!StringUtils.isBlank(conditionEl)) {
            trimmedEl = conditionEl.trim();
            if (trimmedEl != null && trimmedEl.indexOf("{") >= 0 && trimmedEl.indexOf("}") >= 0) {
                elWithoutBrackets = trimmedEl.substring(2, trimmedEl.length() - 1);
            }
        }
        if (StringUtils.isBlank(elWithoutBrackets)) {
            return combinedDecisionRuleEL.substring(5);
        } else if (trimmedEl != null && combinedDecisionRuleEL.toString().startsWith(AND)) {
            combinedEl.append(trimmedEl.substring(0, trimmedEl.length() - 1)).append(combinedDecisionRuleEL).append("}");
        }
        return combinedEl.toString();
    }

    public Set<WFDecisionRule> getWfDecisionRules() {
        return wfDecisionRules;
    }

    public void setWfDecisionRules(Set<WFDecisionRule> wfDecisionRules) {
        this.wfDecisionRules = wfDecisionRules;
    }

    @Override
    public int hashCode() {
        return 961 + ("WFTransition" + id).hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof WFTransition)) {
            return false;
        }

        WFTransition other = (WFTransition) obj;

        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }

        if (uuid == null) {
            if (other.getUuid() != null) {
                return false;
            }
        } else if (!uuid.equals(other.getUuid())) {
            return false;
        }

        return true;
    }

    @Override
    public int compareTo(WFTransition o) {
        return this.priority - o.priority;
    }

    @Override
    public String toString() {
        return String.format("WFTransition [fromStatus=%s, toStatus=%s, priority=%s, conditionEl=%s, combinedEl=%s]", fromStatus, toStatus, priority, conditionEl, getCombinedEl());
    }

    public String clearUuid() {
        String oldUuid = uuid;
        uuid = UUID.randomUUID().toString();
        return oldUuid;
    }

}