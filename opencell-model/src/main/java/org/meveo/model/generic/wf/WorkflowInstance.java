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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;

/**
 * Workflow instance linked to business entity
 */
@Entity
@Table(name = "wf_instance")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wf_instance_seq"), })
public class WorkflowInstance extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Generic Workflow
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "generic_wf_id")
    private GenericWorkflow genericWorkflow;

    /**
     * Affected entity instance code
     */
    @Column(name = "entity_instance_code", nullable = false)
    private String entityInstanceCode;

    public GenericWorkflow getGenericWorkflow() {
        return genericWorkflow;
    }

    public void setGenericWorkflow(GenericWorkflow genericWorkflow) {
        this.genericWorkflow = genericWorkflow;
    }

    public String getEntityInstanceCode() {
        return entityInstanceCode;
    }

    public void setEntityInstanceCode(String entityInstanceCode) {
        this.entityInstanceCode = entityInstanceCode;
    }
}
