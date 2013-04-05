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

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BaseEntity;
import org.meveo.model.rating.UsageType;

/**
 * Usage counter table.
 * 
 * @author Ignas Lelys
 * @created Apr 19, 2010
 * 
 */
// TODO switch to UsageCounterNew when possible
@Entity
@Table(name = "USAGE_COUNTER")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEDINA_USAGE_COUNTER_SEQ")
public class UsageCounterNew extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "PARTITION_ID")
    private Integer partitionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USAGE_TYPE_ID")
    private UsageType usageType;

    @Column(name = "COUNTER_VALUE", precision = 23, scale = 12)
    private BigDecimal counterValue;

    /**
     * Last communication date. It is date used from ticket, to know when
     * actually usage started.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_USAGE_DATE")
    private Date lastUsageDate;

    /**
     * Date when last ticket was processed for this subscription and this usage
     * type.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_TICKET_PROCESSING_DATE")
    private Date lastTicketProcessingDate;

    public Integer getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(Integer partitionId) {
        this.partitionId = partitionId;
    }

    public UsageType getUsageType() {
        return usageType;
    }

    public void setUsageType(UsageType usageType) {
        this.usageType = usageType;
    }

    public Date getLastUsageDate() {
        return lastUsageDate;
    }

    public void setLastUsageDate(Date lastUsageDate) {
        this.lastUsageDate = lastUsageDate;
    }

    public Date getLastTicketProcessingDate() {
        return lastTicketProcessingDate;
    }

    public void setLastTicketProcessingDate(Date lastTicketProcessingDate) {
        this.lastTicketProcessingDate = lastTicketProcessingDate;
    }

    public BigDecimal getCounterValue() {
        return counterValue;
    }

    public void setCounterValue(BigDecimal counterValue) {
        this.counterValue = counterValue;
    }

}
