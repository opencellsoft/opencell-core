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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "WF_TRANSITION_RULE", uniqueConstraints = @UniqueConstraint(columnNames = {
		"NAME", "VALUE", "TYPE", "PRIORITY", "PROVIDER_ID"}))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "WF_TRANSITION_RULE_SEQ")
public class WFTransitionRule extends AuditableEntity {

	private static final long serialVersionUID = 1L;
 
	@Column(name = "NAME")
    @Size(max = 255)
    @NotNull
	private String name;

    @Size(max = 255)
	@Column(name = "VALUE")
	private String value;

    @Column(name = "PRIORITY")
    private int priority;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private TransitionRuleTypeEnum type;
	
	@Column(name = "CONDITION_EL", length = 2000)
	@Size(max = 2000)
	private String conditionEl;

    @Column(name = "MODEL")
    private Boolean model = Boolean.FALSE;

    @ManyToMany(mappedBy="wfTransitionRules")
    private Set<WFTransition> wfTransitions = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TransitionRuleTypeEnum getType() {
        return type;
    }

    public void setType(TransitionRuleTypeEnum type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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

    public Boolean getModel() {
        return model;
    }

    public void setModel(Boolean model) {
        this.model = model;
    }

    public Set<WFTransition> getWfTransitions() {
        return wfTransitions;
    }

    public void setWfTransitions(Set<WFTransition> wfTransitions) {
        this.wfTransitions = wfTransitions;
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
		WFTransitionRule other = (WFTransitionRule) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
