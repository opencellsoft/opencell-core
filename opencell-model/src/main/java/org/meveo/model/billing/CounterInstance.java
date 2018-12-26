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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.shared.DateUtils;

/**
 * Instantiated counter
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "billing_counter")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_counter_instance_seq"), })
public class CounterInstance extends BusinessEntity {
    private static final long serialVersionUID = -4924601467998738157L;

    /**
     * Counter template that counter was instantiated from
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counter_template_id")
    private CounterTemplate counterTemplate;

    /**
     * User account that counter is tracked on
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    /**
     * Billing account that counter is tracked on
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id")
    private BillingAccount billingAccount;

    /**
     * Counter periods
     */
    @OneToMany(mappedBy = "counterInstance", fetch = FetchType.LAZY)
    private List<CounterPeriod> counterPeriods = new ArrayList<CounterPeriod>();

    public CounterTemplate getCounterTemplate() {
        return counterTemplate;
    }

    public void setCounterTemplate(CounterTemplate counterTemplate) {
        this.counterTemplate = counterTemplate;
        if (counterTemplate != null) {
            this.code = counterTemplate.getCode();
            this.description = counterTemplate.getDescription();
        } else {
            this.code = null;
            this.description = null;
        }
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public List<CounterPeriod> getCounterPeriods() {
        return counterPeriods;
    }

    public void setCounterPeriods(List<CounterPeriod> counterPeriods) {
        this.counterPeriods = counterPeriods;
    }

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public CounterPeriod getCounterPeriod(Date date) {
        for (CounterPeriod counterPeriod : counterPeriods) {
            if (DateUtils.isDateTimeWithinPeriod(date, counterPeriod.getPeriodStartDate(), counterPeriod.getPeriodEndDate())) {
                return counterPeriod;
            }
        }
        return null;
    }

}
