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
package org.meveo.model.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.ProviderBusinessEntity;
import org.meveo.model.billing.ServiceInstance;

/**
 * @author R.AITYAAZZA
 * 
 */
@Entity
@Table(name = "CAT_SERVICE_TEMPLATE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_SERVICE_TEMPLATE_SEQ")
public class ServiceTemplate extends ProviderBusinessEntity {

    private static final long serialVersionUID = 1L;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CAT_SERV_RECCHARGE_TEMPLATES", joinColumns = @JoinColumn(name = "SERVICE_TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "CHARGE_TEMPLATE_ID"))
    private List<RecurringChargeTemplate> recurringCharges;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CAT_SERV_ONECHARGE_S_TEMPLATES", joinColumns = @JoinColumn(name = "SERVICE_TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "CHARGE_TEMPLATE_ID"))
    private List<OneShotChargeTemplate> subscriptionCharges=new ArrayList<OneShotChargeTemplate>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CAT_SERV_ONECHARGE_T_TEMPLATES", joinColumns = @JoinColumn(name = "SERVICE_TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "CHARGE_TEMPLATE_ID"))
    private List<OneShotChargeTemplate> terminationCharges=new ArrayList<OneShotChargeTemplate>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DURATION_TERM_CALENDAR")
    private Calendar durationTermCalendar;

    @OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY)
    private List<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();

    public List<RecurringChargeTemplate> getRecurringCharges() {
        return recurringCharges;
    }

    public void setRecurringCharges(List<RecurringChargeTemplate> recurringCharges) {
        this.recurringCharges = recurringCharges;
    }

    public List<OneShotChargeTemplate> getSubscriptionCharges() {
        return subscriptionCharges;
    }

    public void setSubscriptionCharges(List<OneShotChargeTemplate> subscriptionCharges) {
        this.subscriptionCharges = subscriptionCharges;
    }

    public List<OneShotChargeTemplate> getTerminationCharges() {
        return terminationCharges;
    }

    public void setTerminationCharges(List<OneShotChargeTemplate> terminationCharges) {
        this.terminationCharges = terminationCharges;
    }

    public List<ServiceInstance> getServiceInstances() {
        return serviceInstances;
    }

    public void setServiceInstances(List<ServiceInstance> serviceInstances) {
        this.serviceInstances = serviceInstances;
    }

    public Calendar getDurationTermCalendar() {
        return durationTermCalendar;
    }

    public void setDurationTermCalendar(Calendar durationTermCalendar) {
        this.durationTermCalendar = durationTermCalendar;
    }

}
