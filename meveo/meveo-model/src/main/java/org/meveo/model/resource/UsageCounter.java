/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.model.resource;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BaseEntity;

/**
 * Counter of AccessPoint usage.
 * 
 * @author Donatas Remeika
 * @created Mar 10, 2009
 */
@Entity
@Table(name = "RM_USAGE_COUNTER")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "RM_USAGE_COUNTER_SEQ")
public class UsageCounter extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "PARTITION_ID")
    private Integer partitionId;

    /** DATA usage count */
    @Column(name = "COUNTER1")
    private Long counterData;

    /** SMS usage count */
    @Column(name = "COUNTER2")
    private Long counterSMS;

    /** VOICE usage count */
    @Column(name = "COUNTER3")
    private Long counterVOICE;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_COMMUNICATION_DATE")
    private Date lastCommunicationDate;

    public Integer getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(Integer partitionId) {
        this.partitionId = partitionId;
    }

    public Long getCounterData() {
        return counterData;
    }

    public void setCounterData(Long counterData) {
        this.counterData = counterData;
    }

    public Long getCounterSMS() {
        return counterSMS;
    }

    public void setCounterSMS(Long counterSMS) {
        this.counterSMS = counterSMS;
    }

    public Long getCounterVOICE() {
        return counterVOICE;
    }

    public void setCounterVOICE(Long counterVOICE) {
        this.counterVOICE = counterVOICE;
    }

    public Date getLastCommunicationDate() {
        return lastCommunicationDate;
    }

    public void setLastCommunicationDate(Date lastCommunicationDate) {
        this.lastCommunicationDate = lastCommunicationDate;
    }

}
