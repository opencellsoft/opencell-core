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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "WF_TRANSITION", uniqueConstraints = @UniqueConstraint(columnNames = {
		"FROM_STATUS", "TO_STATUS", "WORKFLOW_ID", "PRIORITY", "PROVIDER_ID"}))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "WF_TRANSITION_SEQ")
@NamedQueries({
	@NamedQuery(name = "WFTransition.listByFromStatus", query = "SELECT wft FROM WFTransition wft where wft.fromStatus=:fromStatusValue and workflow=:workflowValue")})
public class WFTransition extends AuditableEntity implements Comparable<WFTransition>{

	private static final long serialVersionUID = 1L;
 
	@Column(name = "FROM_STATUS")
	private String fromStatus;
	
	@Column(name = "TO_STATUS")
	private String toStatus;

    @Column(name = "PRIORITY")
    private int priority;

    @Column(name = "DESCRIPTION", nullable = true, length = 100)
    @Size(max = 100)
    private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WORKFLOW_ID")
	private Workflow workflow;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "WF_TRANSITION_TRANSITION_RULE", joinColumns = @JoinColumn(name = "TRANSITION_ID"), inverseJoinColumns = @JoinColumn(name = "TRANSITION_RULE_ID"))
    @OrderBy("priority ASC")
    private Set<WFTransitionRule> wfTransitionRules = new HashSet<>();

    @OneToMany(mappedBy = "wfTransition", fetch = FetchType.LAZY, cascade=CascadeType.REMOVE)
    @OrderBy("priority ASC")
    private List<WFAction> wfActions = new ArrayList<WFAction>();
	
	@Column(name = "CONDITION_EL", length = 2000)
	@Size(max = 2000)
	private String conditionEl;

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

        if (wfTransitionRules == null) {
            return conditionEl;
        }

        StringBuffer combinedEl = new StringBuffer();
        final String AND = "AND";
        if (wfTransitionRules != null) {
            for (WFTransitionRule wfTransitionRule: wfTransitionRules) {
                if (wfTransitionRule.getConditionEl() != null) {
                    combinedEl.append(conditionEl).append(AND).append(wfTransitionRule.getConditionEl());
                }
            }
        }
        if (combinedEl.toString().startsWith(AND)) {
            return combinedEl.toString().substring(3);
        }
        return combinedEl.toString();
    }

    public Set<WFTransitionRule> getWfTransitionRules() {
        return wfTransitionRules;
    }

    public void setWfTransitionRules(Set<WFTransitionRule> wfTransitionRules) {
        this.wfTransitionRules = wfTransitionRules;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		WFTransition other = (WFTransition) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

    @Override
    public int compareTo(WFTransition o) {
        return Long.compare(this.priority, o.priority);
    }
}
