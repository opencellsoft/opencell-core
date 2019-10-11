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
package org.meveo.model.catalog;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;

import org.hibernate.annotations.Type;

/**
 * One shot charge template
 * 
 * @author Andrius Karpavicius
 */
@Entity
@DiscriminatorValue("O")
@NamedQueries({
        @NamedQuery(name = "oneShotChargeTemplate.getNbrSubscriptionChrgNotAssociated", query = "select count (*) from  OneShotChargeTemplate o where (o.id not in (select distinct serv.chargeTemplate.id from ServiceChargeTemplateSubscription serv) "
                + "OR o.code not in (select distinct p.eventCode from  PricePlanMatrix p where p.eventCode is not null))"
                + " and  oneShotChargeTemplateType=:oneShotChargeTemplateType", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "true") }),

        @NamedQuery(name = "oneShotChargeTemplate.getSubscriptionChrgNotAssociated", query = "from  OneShotChargeTemplate o where (o.id not in (select distinct serv.chargeTemplate.id from ServiceChargeTemplateSubscription serv) "
                + " OR o.code not in (select distinct p.eventCode from  PricePlanMatrix p where p.eventCode is not null)) and  oneShotChargeTemplateType=:oneShotChargeTemplateType"),

        @NamedQuery(name = "oneShotChargeTemplate.getNbrTerminationChrgNotAssociated", query = "select count (*) from  OneShotChargeTemplate o where (o.id not in (select distinct serv.chargeTemplate.id from ServiceChargeTemplateTermination serv) "
                + " OR o.code not in (select distinct p.eventCode from  PricePlanMatrix p where p.eventCode is not null))"
                + " and  oneShotChargeTemplateType=:oneShotChargeTemplateType ", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "true") }),

        @NamedQuery(name = "oneShotChargeTemplate.getTerminationChrgNotAssociated", query = "from  OneShotChargeTemplate o where (o.id not in (select distinct serv.chargeTemplate.id from ServiceChargeTemplateTermination serv) "
                + " OR o.code not in (select distinct p.eventCode from  PricePlanMatrix p where p.eventCode is not null)) and  oneShotChargeTemplateType=:oneShotChargeTemplateType") })
public class OneShotChargeTemplate extends ChargeTemplate {

    private static final long serialVersionUID = 5969419152119380029L;

    public static final String CHARGE_TYPE = "ONESHOT";

    /**
     * One shot charge type
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private OneShotChargeTemplateTypeEnum oneShotChargeTemplateType;

    /**
     * Immediate invoicing
     */
    @Type(type = "numeric_boolean")
    @Column(name = "immediate_invoicing")
    private Boolean immediateInvoicing = false;

    public OneShotChargeTemplateTypeEnum getOneShotChargeTemplateType() {
        return oneShotChargeTemplateType;
    }

    public void setOneShotChargeTemplateType(OneShotChargeTemplateTypeEnum oneShotChargeTemplateType) {
        this.oneShotChargeTemplateType = oneShotChargeTemplateType;
    }

    public Boolean getImmediateInvoicing() {
        return immediateInvoicing;
    }

    public void setImmediateInvoicing(Boolean immediateInvoicing) {
        this.immediateInvoicing = immediateInvoicing;
    }

    @Override
    public String getChargeType() {
        return CHARGE_TYPE;
    }
}