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

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

/**
 * Workflow action
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ExportIdentifier({ "uuid" })
@Table(name = "wf_action", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wf_action_seq"), })
@NamedQueries({ @NamedQuery(name = "WFAction.listByTransition", query = "SELECT wfa FROM WFAction wfa where  wfa.wfTransition=:wfTransition order by priority ASC") })
public class WFAction extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier - UUID
     */
    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid;

    /**
     * Expression to resolve action, or execute a sript
     */
    @Column(name = "action_el", length = 2000)
    @Size(max = 2000)
    @NotNull
    private String actionEl;

    /**
     * Priority
     */
    @Column(name = "priority")
    private int priority;

    /**
     * Expression to check if worklow action applies
     */
    @Column(name = "condition_el", length = 2000)
    @Size(max = 2000)
    private String conditionEl;

    /**
     * Parent workflow transition
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wf_transition_id")
    private WFTransition wfTransition;

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
     * @return the actionEl
     */
    public String getActionEl() {
        return actionEl;
    }

    /**
     * @param actionEl the actionEl to set
     */
    public void setActionEl(String actionEl) {
        this.actionEl = actionEl;
    }

    /**
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * @return the wfTransition
     */
    public WFTransition getWfTransition() {
        return wfTransition;
    }

    /**
     * @param wfTransition the wfTransition to set
     */
    public void setWfTransition(WFTransition wfTransition) {
        this.wfTransition = wfTransition;
    }

    public String getConditionEl() {
        return conditionEl;
    }

    public void setConditionEl(String conditionEl) {
        this.conditionEl = conditionEl;
    }

    public String getUserGroupCode() {
        if (!StringUtils.isBlank(actionEl) && actionEl.indexOf(",") >= 0) {
            int startIndexCode = actionEl.indexOf(",") + 2;
            int endIndexCode = actionEl.length() - 3;
            String userGroupCode = actionEl.substring(startIndexCode, endIndexCode);
            return userGroupCode;
        }
        return null;
    }

    @Override
    public int hashCode() {
        return 961 + ("WfAction" + id).hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof WFAction)) {
            return false;
        }

        WFAction other = (WFAction) obj;
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
    public String toString() {
        return String.format("WFAction [actionEl=%s, conditionEl=%s]", actionEl, conditionEl);
    }

    public String clearUuid() {
        String oldUuid = uuid;
        uuid = UUID.randomUUID().toString();
        return oldUuid;
    }

}