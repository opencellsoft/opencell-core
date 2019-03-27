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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;

/**
 * Workflow instance linked to business entity
 */
@Entity
@Table(name = "wf_instance", uniqueConstraints = @UniqueConstraint(columnNames = { "entity_instance_code", "generic_wf_id", "target_cet_code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wf_instance_seq") })
public class WorkflowInstance extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Qualified name of Workflowed entities
     */
    @Column(name = "target_entity_class", length = 255, nullable = false)
    private String targetEntityClass;

    /**
     * Affected entity instance code
     */
    @Column(name = "entity_instance_code", length = 255, nullable = false)
    private String entityInstanceCode;

    /**
     * Custom entity template code
     */
    @Column(name = "target_cet_code", length = 255)
    private String targetCetCode;

    /**
     * Generic Workflow
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "generic_wf_id")
    private GenericWorkflow genericWorkflow;

    /**
     * Workflow status
     */
    @OneToOne
    @JoinColumn(name = "current_wf_status_id")
    private WFStatus currentStatus;

    /**
     * A list of workflow instance hitories
     */
    @OneToMany(mappedBy = "workflowInstance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowInstanceHistory> wfHistories = new ArrayList<>();

    public String getTargetEntityClass() {
        return targetEntityClass;
    }

    public void setTargetEntityClass(String targetEntityClass) {
        this.targetEntityClass = targetEntityClass;
    }

    public GenericWorkflow getGenericWorkflow() {
        return genericWorkflow;
    }

    public void setGenericWorkflow(GenericWorkflow genericWorkflow) {
        this.genericWorkflow = genericWorkflow;
    }

    public WFStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(WFStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public List<WorkflowInstanceHistory> getWfHistories() {
        return wfHistories;
    }

    public void setWfHistories(List<WorkflowInstanceHistory> wfHistories) {
        this.wfHistories = wfHistories;
    }

    public String getEntityInstanceCode() {
        return entityInstanceCode;
    }

    public void setEntityInstanceCode(String entityInstanceCode) {
        this.entityInstanceCode = entityInstanceCode;
    }

    public String getTargetCetCode() {
        return targetCetCode;
    }

    public void setTargetCetCode(String targetCetCode) {
        this.targetCetCode = targetCetCode;
    }
}
