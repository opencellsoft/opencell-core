/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.wf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.EnableEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "uuid" })
@Table(name = "wf_transition", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wf_transition_seq"), })
@NamedQueries({
        @NamedQuery(name = "WFTransition.listByFromStatus", query = "SELECT wft FROM WFTransition wft where (wft.fromStatus=:fromStatusValue or wft.fromStatus='*') and workflow=:workflowValue order by priority ASC") })
public class WFTransition extends EnableEntity implements Comparable<WFTransition> {

    private static final long serialVersionUID = 1L;

    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid = UUID.randomUUID().toString();

    @Column(name = "from_status")
    private String fromStatus;

    @Column(name = "to_status")
    private String toStatus;

    @Column(name = "priority")
    private int priority;

    @Column(name = "description", nullable = true, length = 255)
    @Size(max = 255)
    @NotNull
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
    @JoinTable(name = "wf_transition_decision_rule", joinColumns = @JoinColumn(name = "transition_id"), inverseJoinColumns = @JoinColumn(name = "decision_rule_id"))
    private Set<WFDecisionRule> wfDecisionRules = new HashSet<>();

    @OneToMany(mappedBy = "wfTransition", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("priority ASC")
    private List<WFAction> wfActions = new ArrayList<WFAction>();

    @Column(name = "condition_el", length = 2000)
    @Size(max = 2000)
    private String conditionEl;

    public String getUuid() {
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
        } else if (combinedDecisionRuleEL.toString().startsWith(AND)) {
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
        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
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