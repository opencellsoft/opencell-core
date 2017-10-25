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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.beanutils.BeanUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ImageUploadEventHandler;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.service.billing.impl.SubscriptionService;

/**
 * Offer Template service implementation.
 * 
 */
@Stateless
public class OfferTemplateService extends GenericProductOfferingService<OfferTemplate> {

    @Inject
    private CatalogHierarchyBuilderService catalogHierarchyBuilderService;

    @Inject
    private SubscriptionService subscriptionService;

    @SuppressWarnings("unchecked")
    public List<OfferTemplate> findByServiceTemplate(ServiceTemplate serviceTemplate) {
        Query query = getEntityManager().createQuery("FROM OfferTemplate t WHERE :serviceTemplate MEMBER OF t.serviceTemplates");
        query.setParameter("serviceTemplate", serviceTemplate);

        try {
            return (List<OfferTemplate>) query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public long countActive() {
        Long result = 0L;
        Query query = getEntityManager().createNamedQuery("OfferTemplate.countActive");
        try {
            result = (long) query.getSingleResult();
        } catch (NoResultException e) {

        }
        return result;
    }

    public long countDisabled() {
        Long result = 0L;
        Query query = getEntityManager().createNamedQuery("OfferTemplate.countDisabled");
        try {
            result = (long) query.getSingleResult();
        } catch (NoResultException e) {

        }
        return result;
    }

    public long countExpiring() {
        Long result = 0L;
        Query query = getEntityManager().createNamedQuery("OfferTemplate.countExpiring");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        query.setParameter("nowMinus1Day", c.getTime());

        try {
            result = (long) query.getSingleResult();
        } catch (NoResultException e) {

        }
        return result;
    }

    /**
     * Create a shallow duplicate of an offer template (main offer template information and custom fields). A new offer template will have a code with suffix "- Copy"
     * 
     * @param offer Offer template to duplicate
     * @return A persisted duplicated offer template
     * @throws BusinessException
     */
    public synchronized OfferTemplate duplicate(OfferTemplate offer, boolean persist) throws BusinessException {
        return duplicate(offer, false, persist, false);
    }

    /**
     * Create a new version of an offer. It is a shallow copy of an offer template (main offer template information and custom fields) with identical code and validity start date
     * matching latest version's validity end date or current date. Note: new entity is not persisted
     * 
     * @param offer Offer template to create new version for
     * @return Copy of offer template
     * @throws BusinessException
     */
    public synchronized OfferTemplate instantiateNewVersion(OfferTemplate offer) throws BusinessException {

        // Find the latest version of an offer for duplication and to calculate a validity start date for a new offer
        OfferTemplate latestVersion = findTheLatestVersion(offer.getCode());
        Date startDate = null;
        Date endDate = null;
        if (latestVersion.getValidity() != null) {
            startDate = latestVersion.getValidity().getFrom();
            endDate = latestVersion.getValidity().getTo();
        }

        offer = duplicate(latestVersion, false, false, true);

        Date from = endDate != null ? endDate : new Date();
        if (startDate != null && from.before(startDate)) {
            from = startDate;
        }
        offer.setValidity(new DatePeriod(from, null));

        return offer;
    }

    /**
     * @param entity instance of OfferTemplate
     * @throws BusinessException exception when error happens
     */
    public synchronized void delete(OfferTemplate entity) throws BusinessException {
        entity = refreshOrRetrieve(entity);

        if (entity == null || entity.isTransient() || subscriptionService.hasSubscriptions(entity)) {
            return;
        }
        this.remove(entity);
        this.catalogHierarchyBuilderService.delete(entity);
    }

    /**
     * Create a duplicate of a given Offer template with an option to duplicate superficial data (Offer and CFs) or all hierarchy deep - services, charges, price plans
     * 
     * @param offerToDuplicate Offer template to duplicate
     * @param duplicateHierarchy To duplicate superficial data (offer info (including services and products, CFs) or all hierarchy deep - new service templates, charge templates,
     *        price plans
     * @param persist Shall new entity be persisted
     * @param preserveCode Shall a code be preserved or a " Copy" be added to a code
     * @return A copy of Offer template
     * @throws BusinessException
     */
    private synchronized OfferTemplate duplicate(OfferTemplate offerToDuplicate, boolean duplicateHierarchy, boolean persist, boolean preserveCode) throws BusinessException {

        offerToDuplicate = refreshOrRetrieve(offerToDuplicate);

        // Lazy load related values first
        offerToDuplicate.getOfferServiceTemplates().size();
        offerToDuplicate.getBusinessAccountModels().size();
        offerToDuplicate.getAttachments().size();
        offerToDuplicate.getChannels().size();
        offerToDuplicate.getOfferProductTemplates().size();
        offerToDuplicate.getOfferTemplateCategories().size();

        if (offerToDuplicate.getOfferServiceTemplates() != null) {
            for (OfferServiceTemplate offerServiceTemplate : offerToDuplicate.getOfferServiceTemplates()) {
                offerServiceTemplate.getIncompatibleServices().size();
            }
        }

        // Detach and clear ids of entity and related entities
        detach(offerToDuplicate);

        OfferTemplate offer;
        try {
            offer = (OfferTemplate) BeanUtils.cloneBean(offerToDuplicate);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new BusinessException("Failed to clone offer template", e);
        }

        if (!preserveCode) {
            String code = findDuplicateCode(offer);
            offer.setCode(code);
        }

        offer.setId(null);

        offer.setVersion(0);
        offer.setAuditable(new Auditable());
        offer.clearUuid();

        List<OfferServiceTemplate> offerServiceTemplates = offer.getOfferServiceTemplates();
        offer.setOfferServiceTemplates(new ArrayList<OfferServiceTemplate>());

        List<BusinessAccountModel> businessAccountModels = offer.getBusinessAccountModels();
        offer.setBusinessAccountModels(new ArrayList<BusinessAccountModel>());

        List<DigitalResource> attachments = offer.getAttachments();
        offer.setAttachments(new ArrayList<DigitalResource>());

        List<Channel> channels = offer.getChannels();
        offer.setChannels(new ArrayList<Channel>());

        List<OfferProductTemplate> offerProductTemplates = offer.getOfferProductTemplates();
        offer.setOfferProductTemplates(new ArrayList<OfferProductTemplate>());

        List<OfferTemplateCategory> offerTemplateCategories = offer.getOfferTemplateCategories();
        offer.setOfferTemplateCategories(new ArrayList<OfferTemplateCategory>());

        if (businessAccountModels != null) {
            for (BusinessAccountModel bam : businessAccountModels) {
                offer.getBusinessAccountModels().add(bam);
            }
        }

        if (attachments != null) {
            for (DigitalResource attachment : attachments) {
                offer.addAttachment(attachment);
            }
        }

        if (channels != null) {
            for (Channel channel : channels) {
                offer.getChannels().add(channel);
            }
        }

        if (offerTemplateCategories != null) {
            for (OfferTemplateCategory offerTemplateCategory : offerTemplateCategories) {
                offer.getOfferTemplateCategories().add(offerTemplateCategory);
            }
        }

        if (duplicateHierarchy) {
            String prefix = offer.getId() + "_";

            if (offerServiceTemplates != null) {
                catalogHierarchyBuilderService.duplicateOfferServiceTemplate(offer, offerServiceTemplates, prefix);
            }

            if (offerProductTemplates != null) {
                catalogHierarchyBuilderService.duplicateOfferProductTemplate(offer, offerProductTemplates, prefix);
            }

        } else {
            if (offerServiceTemplates != null) {
                for (OfferServiceTemplate serviceTemplate : offerServiceTemplates) {
                    serviceTemplate.getIncompatibleServices().size();
                    serviceTemplate.setId(null);
                    List<ServiceTemplate> incompatibleServices = serviceTemplate.getIncompatibleServices();
                    serviceTemplate.setIncompatibleServices(new ArrayList<ServiceTemplate>());
                    if (incompatibleServices != null) {
                        for (ServiceTemplate incompatibleService : incompatibleServices) {
                            serviceTemplate.addIncompatibleServiceTemplate(incompatibleService);
                        }
                    }
                    serviceTemplate.setOfferTemplate(offer);
                    offer.addOfferServiceTemplate(serviceTemplate);
                }
            }

            if (offerProductTemplates != null) {
                for (OfferProductTemplate offerProductTemplate : offerProductTemplates) {
                    offerProductTemplate.setId(null);
                    offerProductTemplate.setOfferTemplate(offer);
                    offer.getOfferProductTemplates().add(offerProductTemplate);
                }
            }
        }

        ImageUploadEventHandler<OfferTemplate> offerImageUploadEventHandler = new ImageUploadEventHandler<>(appProvider);
        try {
            String newImagePath = offerImageUploadEventHandler.duplicateImage(offer, offer.getImagePath());
            offer.setImagePath(newImagePath);
        } catch (IOException e1) {
            log.error("IPIEL: Failed duplicating offer image: {}", e1.getMessage());
        }

        if (persist) {
            create(offer);
        }

        return offer;
    }
    
    public List<OfferTemplate> list(String code, Date validFrom, Date validTo) {
    	return list(code, validFrom, validTo, null);
    }
    
	public List<OfferTemplate> list(String code, Date validFrom, Date validTo,
			LifeCycleStatusEnum lifeCycleStatusEnum) {
		List<OfferTemplate> listOfferTemplates = null;

        if (StringUtils.isBlank(code) && validFrom == null && validTo == null && lifeCycleStatusEnum == null) {
            listOfferTemplates = list();
        } else {

            Map<String, Object> filters = new HashMap<String, Object>();
            if (!StringUtils.isBlank(code)) {
                filters.put("code", code);
            }

            // If only validTo date is provided, a search will return products valid from today to a given date.
            if (validFrom == null && validTo != null) {
                validFrom = new Date();
            }

            // search by a single date
            if (validFrom != null && validTo == null) {
                filters.put("minmaxOptionalRange validity.from validity.to", validFrom);

                // search by date range
            } else if (validFrom != null && validTo != null) {
                filters.put("overlapOptionalRange validity.from validity.to", new Date[] { validFrom, validTo });
            }
            
			if (!StringUtils.isBlank(lifeCycleStatusEnum)) {
				filters.put("lifeCycleStatus", lifeCycleStatusEnum);
			}
			
			filters.put("disabled", false);

            PaginationConfiguration config = new PaginationConfiguration(filters);
            listOfferTemplates = list(config);
        }
        
        return listOfferTemplates;
    }
}