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
package org.meveo.service.catalog.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.RatingCacheContainerProvider;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;

/**
 * Charge Template service implementation.
 * 
 */
@Stateless
public class UsageChargeTemplateService extends ChargeTemplateService<UsageChargeTemplate> {

    @Inject
    private RatingCacheContainerProvider ratingCacheContainerProvider;

    @Override
    public void create(UsageChargeTemplate e) throws BusinessException {

        e.setFilterExpression(StringUtils.stripToNull(e.getFilterExpression()));
        e.setFilterParam1(StringUtils.stripToNull(e.getFilterParam1()));
        e.setFilterParam2(StringUtils.stripToNull(e.getFilterParam2()));
        e.setFilterParam3(StringUtils.stripToNull(e.getFilterParam3()));
        e.setFilterParam4(StringUtils.stripToNull(e.getFilterParam4()));

        super.create(e);
    }

    @Override
    public UsageChargeTemplate update(UsageChargeTemplate e) throws BusinessException {

        e.setFilterExpression(StringUtils.stripToNull(e.getFilterExpression()));
        e.setFilterParam1(StringUtils.stripToNull(e.getFilterParam1()));
        e.setFilterParam2(StringUtils.stripToNull(e.getFilterParam2()));
        e.setFilterParam3(StringUtils.stripToNull(e.getFilterParam3()));
        e.setFilterParam4(StringUtils.stripToNull(e.getFilterParam4()));

        ratingCacheContainerProvider.updateUsageChargeTemplateInCache(e); // Make sure to update cache before DB, as to not loose if code and priority has changed

        e = super.update(e);
        return e;
    }

    public List<UsageChargeTemplate> findAssociatedToEDRTemplate(TriggeredEDRTemplate triggeredEDRTemplate) {
        return getEntityManager().createNamedQuery("UsageChargeTemplate.getWithTemplateEDR", UsageChargeTemplate.class).setParameter("edrTemplate", triggeredEDRTemplate)
            .getResultList();
    }

    public int getNbrUsagesChrgWithNotPricePlan() {
        return ((Long) getEntityManager().createNamedQuery("usageChargeTemplate.getNbrUsagesChrgWithNotPricePlan", Long.class).getSingleResult()).intValue();
    }

    public List<UsageChargeTemplate> getUsagesChrgWithNotPricePlan() {
        return (List<UsageChargeTemplate>) getEntityManager().createNamedQuery("usageChargeTemplate.getUsagesChrgWithNotPricePlan", UsageChargeTemplate.class).getResultList();
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