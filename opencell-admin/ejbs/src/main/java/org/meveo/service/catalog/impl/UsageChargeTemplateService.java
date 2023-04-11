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
package org.meveo.service.catalog.impl;

import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;

/**
 * Charge Template service implementation.
 * 
 */

public class UsageChargeTemplateService extends ChargeTemplateService<UsageChargeTemplate> {

    @Override
    public void create(UsageChargeTemplate charge) throws BusinessException {

        charge.setFilterExpression(StringUtils.stripToNull(charge.getFilterExpression()));
        charge.setFilterParam1(StringUtils.stripToNull(charge.getFilterParam1()));
        charge.setFilterParam2(StringUtils.stripToNull(charge.getFilterParam2()));
        charge.setFilterParam3(StringUtils.stripToNull(charge.getFilterParam3()));
        charge.setFilterParam4(StringUtils.stripToNull(charge.getFilterParam4()));

        super.create(charge);
    }

    @Override
    public UsageChargeTemplate update(UsageChargeTemplate charge) throws BusinessException {

        charge.setFilterExpression(StringUtils.stripToNull(charge.getFilterExpression()));
        charge.setFilterParam1(StringUtils.stripToNull(charge.getFilterParam1()));
        charge.setFilterParam2(StringUtils.stripToNull(charge.getFilterParam2()));
        charge.setFilterParam3(StringUtils.stripToNull(charge.getFilterParam3()));
        charge.setFilterParam4(StringUtils.stripToNull(charge.getFilterParam4()));

        boolean priorityChanged = charge.isPriorityChanged();

        charge = super.update(charge);

        // Need to update priority values in usage charge instance entities
        if (priorityChanged) {
            getEntityManager().createQuery("update UsageChargeInstance ci set ci.priority=:priority where ci.chargeTemplate=:chargeTemplate")
                .setParameter("priority", charge.getPriority()).setParameter("chargeTemplate", charge).executeUpdate();
        }

        return charge;
    }

    public List<UsageChargeTemplate> findAssociatedToEDRTemplate(TriggeredEDRTemplate triggeredEDRTemplate) {
        return getEntityManager().createNamedQuery("UsageChargeTemplate.getWithTemplateEDR", UsageChargeTemplate.class).setParameter("edrTemplate", triggeredEDRTemplate)
            .getResultList();
    }

    public int getNbrUsagesChrgNotAssociated() {
        return ((Long) getEntityManager().createNamedQuery("usageChargeTemplate.getNbrUsagesChrgNotAssociated", Long.class).getSingleResult()).intValue();
    }

    public List<UsageChargeTemplate> getUsagesChrgNotAssociated() {
        return (List<UsageChargeTemplate>) getEntityManager().createNamedQuery("usageChargeTemplate.getUsagesChrgNotAssociated", UsageChargeTemplate.class).getResultList();
    }

    /**
     * Get a list of identifiers of subscriptions that have charge instances of a given usage charge template
     * 
     * @param usageChargeTemplate Usage charge template
     * @return A list of subscription identifiers
     */
    public List<Long> getSubscriptionsAssociated(UsageChargeTemplate usageChargeTemplate) {
        return getEntityManager().createNamedQuery("Subscription.getIdsByUsageChargeTemplate", Long.class).setParameter("chargeTemplate", usageChargeTemplate).getResultList();
    }
}