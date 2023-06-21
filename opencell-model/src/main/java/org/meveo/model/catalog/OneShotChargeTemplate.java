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
                + "OR not exists elements(o.pricePlans) )"
                + " and  oneShotChargeTemplateType=:oneShotChargeTemplateType", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "true") }),

        @NamedQuery(name = "oneShotChargeTemplate.getSubscriptionChrgNotAssociated", query = "from  OneShotChargeTemplate o where (o.id not in (select distinct serv.chargeTemplate.id from ServiceChargeTemplateSubscription serv) "
                + " OR not exists elements(o.pricePlans) ) and  oneShotChargeTemplateType=:oneShotChargeTemplateType"),

        @NamedQuery(name = "oneShotChargeTemplate.getNbrTerminationChrgNotAssociated", query = "select count (*) from  OneShotChargeTemplate o where (o.id not in (select distinct serv.chargeTemplate.id from ServiceChargeTemplateTermination serv) "
                + " OR not exists elements(o.pricePlans) )"
                + " and  oneShotChargeTemplateType=:oneShotChargeTemplateType ", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "true") }),

        @NamedQuery(name = "oneShotChargeTemplate.getTerminationChrgNotAssociated", query = "from  OneShotChargeTemplate o where (o.id not in (select distinct serv.chargeTemplate.id from ServiceChargeTemplateTermination serv) "
                + " OR not exists elements(o.pricePlans) ) and  oneShotChargeTemplateType=:oneShotChargeTemplateType") })
public class OneShotChargeTemplate extends ChargeTemplate {

    private static final long serialVersionUID = 5969419152119380029L;

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
    public ChargeMainTypeEnum getChargeMainType() {
        return ChargeMainTypeEnum.ONESHOT;
    }
}