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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceChargeTemplateTermination;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.base.BusinessService;

/**
 * Service Template service implementation.
 * 
 */
@Stateless
public class ServiceTemplateService extends BusinessService<ServiceTemplate> {

    @Inject
    private ServiceChargeTemplateSubscriptionService serviceChargeTemplateSubscriptionService;

    @Inject
    private ServiceChargeTemplateTerminationService serviceChargeTemplateTerminationService;

    @Inject
    private ServiceChargeTemplateRecurringService serviceChargeTemplateRecurringService;

    @Inject
    private ServiceChargeTemplateUsageService serviceChargeTemplateUsageService;

    public int getNbServiceWithNotOffer() {
        return ((Long) getEntityManager().createNamedQuery("serviceTemplate.getNbServiceWithNotOffer", Long.class).getSingleResult()).intValue();
    }

    public List<ServiceTemplate> getServicesWithNotOffer() {
        return (List<ServiceTemplate>) getEntityManager().createNamedQuery("serviceTemplate.getServicesWithNotOffer", ServiceTemplate.class).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<ServiceTemplate> listAllActiveExcept(ServiceTemplate st) {
        QueryBuilder qb = new QueryBuilder(ServiceTemplate.class, "s", null);
        qb.addCriterion("id", "<>", st.getId(), true);

        try {
            return (List<ServiceTemplate>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public synchronized void duplicate(ServiceTemplate entity) throws BusinessException {
        entity = refreshOrRetrieve(entity);

        // Lazy load related values first
        entity.getServiceRecurringCharges().size();
        entity.getServiceSubscriptionCharges().size();
        entity.getServiceTerminationCharges().size();
        entity.getServiceUsageCharges().size();
        String code = findDuplicateCode(entity);

        // Detach and clear ids of entity and related entities
        detach(entity);
        entity.setId(null);

        List<ServiceChargeTemplateRecurring> recurrings = entity.getServiceRecurringCharges();
        entity.setServiceRecurringCharges(new ArrayList<ServiceChargeTemplateRecurring>());
        for (ServiceChargeTemplateRecurring recurring : recurrings) {
            recurring = serviceChargeTemplateRecurringService.findById(recurring.getId());
            recurring.getWalletTemplates().size();
            serviceChargeTemplateRecurringService.detach(recurring);
            recurring.setId(null);
            recurring.setServiceTemplate(entity);
            entity.getServiceRecurringCharges().add(recurring);
        }

        List<ServiceChargeTemplateSubscription> subscriptions = entity.getServiceSubscriptionCharges();
        entity.setServiceSubscriptionCharges(new ArrayList<ServiceChargeTemplateSubscription>());
        for (ServiceChargeTemplateSubscription subscription : subscriptions) {
            subscription = serviceChargeTemplateSubscriptionService.findById(subscription.getId());
            subscription.getWalletTemplates().size();
            serviceChargeTemplateSubscriptionService.detach(subscription);
            subscription.setId(null);
            subscription.setServiceTemplate(entity);
            entity.getServiceSubscriptionCharges().add(subscription);
        }

        List<ServiceChargeTemplateTermination> terminations = entity.getServiceTerminationCharges();
        entity.setServiceTerminationCharges(new ArrayList<ServiceChargeTemplateTermination>());
        for (ServiceChargeTemplateTermination termination : terminations) {
            termination = serviceChargeTemplateTerminationService.findById(termination.getId());
            termination.getWalletTemplates().size();
            serviceChargeTemplateTerminationService.detach(termination);
            termination.setId(null);
            termination.setServiceTemplate(entity);
            entity.getServiceTerminationCharges().add(termination);
        }

        List<ServiceChargeTemplateUsage> usages = entity.getServiceUsageCharges();
        entity.setServiceUsageCharges(new ArrayList<ServiceChargeTemplateUsage>());
        for (ServiceChargeTemplateUsage usage : usages) {
            usage = serviceChargeTemplateUsageService.findById(usage.getId());
            usage.getWalletTemplates().size();
            serviceChargeTemplateUsageService.detach(usage);
            usage.setId(null);
            usage.setServiceTemplate(entity);
            entity.getServiceUsageCharges().add(usage);
        }
        entity.setCode(code);

        create(entity);
    }
    
    @SuppressWarnings("unchecked")
    public List<ServiceTemplate> getServiceByTags(HashSet<String> tagCodes) { 
    	List<ServiceTemplate> services=new ArrayList<ServiceTemplate>();
    	try {
    		services = (List<ServiceTemplate>)getEntityManager().createNamedQuery("ServiceTemplate.findByTags").setParameter("tagCodes", tagCodes).getResultList();
    	} catch (Exception e) {
    		e.printStackTrace();
    		log.error("getServiceByTags error ", e.getMessage());
    	}

    	return services;
    }

}