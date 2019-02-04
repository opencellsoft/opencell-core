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
package org.meveo.model.generic.wf;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.scripts.ScriptInstance;

@Entity
@ExportIdentifier({ "uuid" })
@Table(name = "wf_generic_transition", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wf_generic_transition_seq"), })
@NamedQueries({
        @NamedQuery(name = "GWFTransition.listByFromStatus", query = "SELECT gwft FROM GWFTransition gwft where (gwft.fromStatus=:fromStatusValue or gwft.fromStatus='*') and genericWorkflow=:genericWorkflowValue order by priority ASC") })
public class GWFTransition extends BaseEntity implements Comparable<GWFTransition> {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier - UUID
     */
    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid = UUID.randomUUID().toString();

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
     * Generic workflow
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generic_wf_id")
    private GenericWorkflow genericWorkflow;

    /**
     * The action script
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "action_script_id")
    private ScriptInstance actionScript;

    /*
     * Expression to check if transition applies
     */
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

    public GenericWorkflow getGenericWorkflow() {
        return genericWorkflow;
    }

    public void setGenericWorkflow(GenericWorkflow genericWorkflow) {
        this.genericWorkflow = genericWorkflow;
    }

    public ScriptInstance getActionScript() {
        return actionScript;
    }

    public void setActionScript(ScriptInstance actionScript) {
        this.actionScript = actionScript;
    }

    public String getCombinedEl() {

        StringBuffer combinedEl = new StringBuffer();
        final String AND = " AND ";
        StringBuffer combinedDecisionRuleEL = new StringBuffer();
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
        } else if (!(obj instanceof GWFTransition)) {
            return false;
        }

        GWFTransition other = (GWFTransition) obj;

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
    public int compareTo(GWFTransition o) {
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