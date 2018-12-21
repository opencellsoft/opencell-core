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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;

/**
 * Workflow instance action history
 */
@Entity
@Table(name = "wf_instance_history")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wf_instance_history_seq"), })
public class WorkflowInstanceHistory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Workflow status from
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wf_status_from", insertable = false, updatable = false)
    private WFStatus wfStatusFrom;

    /**
     * Workflow status to
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wf_status_to", insertable = false, updatable = false)
    private WFStatus wfStatusTo;

    /**
     * Action date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "action_date")
    private Date actionDate;

    /**
     * A event comment
     */
    private String event;

    public WFStatus getWfStatusFrom() {
        return wfStatusFrom;
    }

    public void setWfStatusFrom(WFStatus wfStatusFrom) {
        this.wfStatusFrom = wfStatusFrom;
    }

    public WFStatus getWfStatusTo() {
        return wfStatusTo;
    }

    public void setWfStatusTo(WFStatus wfStatusTo) {
        this.wfStatusTo = wfStatusTo;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
