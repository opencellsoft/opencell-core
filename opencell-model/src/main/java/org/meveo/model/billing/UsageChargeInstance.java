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
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.catalog.UsageChargeTemplate;

@Entity
@Table(name = "billing_usage_charge_inst")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_usage_charge_inst_seq"), })
@NamedQueries({ @NamedQuery(name = "UsageChargeInstance.listPrepaid", query = "SELECT c FROM UsageChargeInstance c where c.prepaid=true and  c.status='ACTIVE'"),
        @NamedQuery(name = "UsageChargeInstance.getActiveUsageChargesBySubscriptionId", query = "SELECT c FROM UsageChargeInstance c join fetch c.serviceInstance si where c.status='ACTIVE' and c.subscription.id=:subscriptionId order by c.priority ASC", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true") }) })
public class UsageChargeInstance extends ChargeInstance {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_instance_id")
    private ServiceInstance serviceInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counter_id")
    private CounterInstance counter;

    @Column(name = "rating_unit_description", length = 20)
    @Size(max = 20)
    private String ratingUnitDescription;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_update")
    private Date lastUpdate;

    /**
     * The lower number, the higher the priority is. Value is a copy from UsageChargeTemplate.priority field and is synchronized when UsageChargeTemplate.priority value change.
     */
    @Column(name = "priority", columnDefinition = "int default 1")
    private int priority = 1;

    /**
     * Instantiate Usage charge instance
     */
    public UsageChargeInstance() {
    }

    /**
     * Instantiate Usage charge instance from a Usage charge template
     * 
     * @param amountWithoutTax Amount without tax to override
     * @param amountWithTax Amount with tax to override
     * @param usageChargeTemplate Usage charge template to instantiate from
     * @param serviceInstance Service instance that charge will belong to
     * @param status Activation status
     */
    public UsageChargeInstance(BigDecimal amountWithoutTax, BigDecimal amountWithTax, UsageChargeTemplate usageChargeTemplate, ServiceInstance serviceInstance,
            InstanceStatusEnum status) {

        super(amountWithoutTax, amountWithTax, usageChargeTemplate, serviceInstance, status);

        this.ratingUnitDescription = usageChargeTemplate.getRatingUnitDescription();
        this.serviceInstance = serviceInstance;
        this.priority = usageChargeTemplate.getPriority();
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public CounterInstance getCounter() {
        return counter;
    }

    public void setCounter(CounterInstance counter) {
        this.counter = counter;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getRatingUnitDescription() {
        return ratingUnitDescription;
    }

    public void setRatingUnitDescription(String ratingUnitDescription) {
        this.ratingUnitDescription = ratingUnitDescription;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}