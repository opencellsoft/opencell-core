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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import org.meveo.api.generics.GenericRequestMapper;
import org.meveo.api.generics.PersistenceServiceHelper;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.DatePeriod;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.Media;
import org.meveo.model.cpq.OfferTemplateAttribute;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.offer.OfferComponent;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.model.cpq.trade.CommercialRuleItem;
import org.meveo.model.cpq.trade.CommercialRuleLine;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.cpq.CommercialRuleHeaderService;
import org.meveo.service.cpq.CommercialRuleItemService;
import org.meveo.service.cpq.CommercialRuleLineService;
import org.meveo.service.cpq.MediaService;
import org.meveo.service.cpq.ProductService;

/**
 * Offer Template service implementation.
 * 
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
@Stateless
public class OfferTemplateService extends GenericProductOfferingService<OfferTemplate> {

    @Inject
    private CatalogHierarchyBuilderService catalogHierarchyBuilderService;

    @Inject
    private SubscriptionService subscriptionService;
    @Inject
    private MediaService mediaService;
    @Inject
    private CommercialRuleHeaderService commercialRuleHeaderService;

    @Inject
    private CommercialRuleItemService commercialRuleItemService;

    @Inject
    private CommercialRuleLineService commercialRuleLineService;
    @Inject
    private ProductService productService;

    @SuppressWarnings("unchecked")
    public List<OfferTemplate> findByServiceTemplate(ServiceTemplate serviceTemplate) {
        Query query = getEntityManager().createNamedQuery("OfferTemplate.findByServiceTemplate");
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
        int beforeExpiration = Integer.parseInt(paramBeanFactory.getInstance().getProperty("offer.expiration.before", "30"));

        Long result = 0L;
        Query query = getEntityManager().createNamedQuery("OfferTemplate.countExpiring");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -beforeExpiration);
        query.setParameter("nowMinusXDay", c.getTime());

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
     * @param persist true if needs to be persisted.
     * @return A persisted duplicated offer template
     * @throws BusinessException business exception.
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
     * @throws BusinessException business exception.
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
     * @throws BusinessException business exception.
     */
    public synchronized OfferTemplate duplicate(OfferTemplate offerToDuplicate, boolean duplicateHierarchy, boolean persist, boolean preserveCode) throws BusinessException {

        offerToDuplicate = refreshOrRetrieve(offerToDuplicate);

        // Lazy load related values first
        offerToDuplicate.getOfferServiceTemplates().size();
        offerToDuplicate.getBusinessAccountModels().size();
        offerToDuplicate.getAttachments().size();
        offerToDuplicate.getChannels().size();
        offerToDuplicate.getOfferProductTemplates().size();
        offerToDuplicate.getOfferTemplateCategories().size();
        offerToDuplicate.getSellers().size();
        offerToDuplicate.getCustomerCategories().size();
        offerToDuplicate.getMedias().size();
        offerToDuplicate.getCommercialRules().size();
        offerToDuplicate.getOfferComponents().size();
        offerToDuplicate.getOfferAttributes().size();
        offerToDuplicate.getOfferComponents().forEach(oc -> oc.getTagsList().size());
        offerToDuplicate.getCommercialRules()
                .forEach(commercialRuleHeader -> commercialRuleHeader.getCommercialRuleItems().size());
        offerToDuplicate.getCommercialRules()
                .forEach(commercialRuleHeader -> commercialRuleHeader.getCommercialRuleItems()
                        .forEach(commercialRuleItem -> commercialRuleItem.getCommercialRuleLines().size()));
        offerToDuplicate.getAllowedDiscountPlans().size();
        offerToDuplicate.getAllowedOffersChange().size();
        offerToDuplicate.getTags().size();


        if (offerToDuplicate.getOfferServiceTemplates() != null) {
            for (OfferServiceTemplate offerServiceTemplate : offerToDuplicate.getOfferServiceTemplates()) {
                offerServiceTemplate.getIncompatibleServices().size();
            }
        }

        // Detach and clear ids of entity and related entities
        detach(offerToDuplicate);

        OfferTemplate offer = new OfferTemplate();
        try {
             BeanUtils.copyProperties(offer, offerToDuplicate);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BusinessException("Failed to clone offer template", e);
        }

        if (!preserveCode) {
            String code = findDuplicateCode(offer);
            offer.setCode(code);
        }

        offer.setId(null);
        offer.setLifeCycleStatus(LifeCycleStatusEnum.IN_DESIGN);
        offer.setVersion(0);
        offer.setAuditable(new Auditable());
        offer.clearUuid();
        
    	offer.setOfferModel(offerToDuplicate.getIsModel() ? offerToDuplicate : null);

        List<OfferServiceTemplate> offerServiceTemplates = offer.getOfferServiceTemplates();
        offer.setOfferServiceTemplates(new ArrayList<>());

        List<BusinessAccountModel> businessAccountModels = offer.getBusinessAccountModels();
        offer.setBusinessAccountModels(new ArrayList<>());

        List<DigitalResource> attachments = offer.getAttachments();
        offer.setAttachments(new ArrayList<>());

        List<Channel> channels = offer.getChannels();
        offer.setChannels(new ArrayList<>());

        List<OfferProductTemplate> offerProductTemplates = offer.getOfferProductTemplates();
        offer.setOfferProductTemplates(new ArrayList<>());

        List<OfferTemplateCategory> offerTemplateCategories = offer.getOfferTemplateCategories();
        offer.setOfferTemplateCategories(new ArrayList<>());

        List<Seller> sellers = offer.getSellers();
        offer.setSellers(new ArrayList<>());
        
        List<OfferTemplateAttribute> offerAttributes = offer.getOfferAttributes();
        offer.setOfferAttributes(new ArrayList<>());

        List<CustomerCategory> customerCategories = offer.getCustomerCategories();
        offer.setCustomerCategories(new ArrayList<>());
        
        List<Media> medias = offer.getMedias();
        offer.setMedias(new ArrayList<>());

        List<DiscountPlan> allowedDiscountPlans = offer.getAllowedDiscountPlans();
        offer.setAllowedDiscountPlans(new ArrayList<>());

        List<OfferTemplate> allowedOffersChange = offer.getAllowedOffersChange();
        offer.setAllowedOffersChange(new ArrayList<>());

        List<Tag> tags = offer.getTags();
        offer.setTags(new ArrayList<>());

        List<OfferComponent> offerComponents = offer.getOfferComponents();
        offer.setOfferComponents(new ArrayList<>());

        List<CommercialRuleHeader> commercialRulesHeader = offer.getCommercialRules();
        offer.setCommercialRules(new ArrayList<>());


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

        if (sellers != null) {
            for (Seller seller : sellers) {
                offer.getSellers().add(seller);
            }
        }
        
        if (offerAttributes != null) {
            for (OfferTemplateAttribute offerAttribute : offerAttributes) {
                offerAttribute.setId(null);
                offerAttribute.setOfferTemplate(offer);
                offer.getOfferAttributes().add(offerAttribute);
            }
        }

        if (customerCategories != null) {
            for (CustomerCategory customerCategory : customerCategories) {
                offer.getCustomerCategories().add(customerCategory);
            }
        }
        if (persist) {
            create(offer);
        }

        if (duplicateHierarchy) {
            String prefix = offer.getId() + "_";

            if (offerServiceTemplates != null) {
                catalogHierarchyBuilderService.duplicateOfferServiceTemplate(offer, offerServiceTemplates, prefix);
            }

            if (offerProductTemplates != null) {
                catalogHierarchyBuilderService.duplicateOfferProductTemplate(offer, offerProductTemplates, prefix);
            }

            if(medias != null) {
            	for (Media media : medias) {
        			mediaService.detach(media);
        			Media newMedia = new Media(media);  
        			newMedia.setMediaName(media.getMediaName() + "_" + offer.getId());
        			newMedia.setCode(mediaService.findDuplicateCode(media));
        			mediaService.create(newMedia);
    				offer.getMedias().add(newMedia);
    			}
            }

            if (allowedDiscountPlans != null) {
                for (DiscountPlan discountPlan : allowedDiscountPlans) {
                    discountPlan.setId(null);
                    offer.getAllowedDiscountPlans().add(discountPlan);
                }
            }

            if (allowedOffersChange != null) {
                for (OfferTemplate offerTemplate : allowedOffersChange) {
                    offerTemplate.setId(null);
                    offer.getAllowedOffersChange().add(offerTemplate);
                }
            }

            if (tags != null) {
                for (Tag tag : tags) {
                    tag.setId(null);
                    offer.getTags().add(tag);
                }
            }

            if (offerComponents != null) {
                for (OfferComponent offerComponent : offerComponents) {
                    offerComponent.setId(null);
                    offerComponent.setOfferTemplate(offer);
                    offer.getOfferComponents().add(offerComponent);

                    Product product = PersistenceUtils.initializeAndUnproxy(offerComponent.getProduct());
                    offerComponent.setProduct(productService.duplicateProduct(product, duplicateHierarchy, preserveCode));

                }
            }
            if(commercialRulesHeader != null) {
                offer.setCommercialRules(duplicateCommercialRules(offer, commercialRulesHeader));
            }

        } else {
            if (offerServiceTemplates != null) {
                for (OfferServiceTemplate serviceTemplate : offerServiceTemplates) {
                    serviceTemplate.getIncompatibleServices().size();
                    serviceTemplate.setId(null);
                    List<ServiceTemplate> incompatibleServices = serviceTemplate.getIncompatibleServices();
                    serviceTemplate.setIncompatibleServices(new ArrayList<>());
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

        ImageUploadEventHandler<OfferTemplate> offerImageUploadEventHandler = new ImageUploadEventHandler<>(currentUser.getProviderCode());
        try {
            String newImagePath = offerImageUploadEventHandler.duplicateImage(offer, offer.getImagePath());
            offer.setImagePath(newImagePath);
        } catch (IOException e1) {
            log.error("IPIEL: Failed duplicating offer image: {}", e1.getMessage());
        }

        if (persist) {
            update(offer);
        }

        return offer;
    }

    private List<CommercialRuleHeader> duplicateCommercialRules(OfferTemplate offer,
                                                                List<CommercialRuleHeader> commercialRules) {
        List<CommercialRuleHeader> commercialRuleHeaders = new ArrayList<>();
        for (CommercialRuleHeader commercialRuleHeader : commercialRules) {
            commercialRuleHeaderService.detach(commercialRuleHeader);
            CommercialRuleHeader duplicate = new CommercialRuleHeader(commercialRuleHeader);
            List<CommercialRuleItem> commercialRuleItems = new ArrayList<>();
            for (CommercialRuleItem ruleItem : commercialRuleHeader.getCommercialRuleItems()) {
                commercialRuleItemService.detach(ruleItem);
                CommercialRuleItem duplicatedRuleItem = new CommercialRuleItem(ruleItem);
                List<CommercialRuleLine> commercialRuleLines = new ArrayList<>();
                for (CommercialRuleLine ruleLine : ruleItem.getCommercialRuleLines()) {
                    commercialRuleLineService.detach(ruleLine);
                    CommercialRuleLine duplicatedRuleLine = new CommercialRuleLine(ruleLine);
                    duplicatedRuleLine.setSourceOfferTemplate(offer);
                    duplicatedRuleLine.setCommercialRuleItem(duplicatedRuleItem);
                    commercialRuleLineService.create(duplicatedRuleLine);
                    commercialRuleLines.add(duplicatedRuleLine);
                }
                duplicatedRuleItem.setCommercialRuleLines(commercialRuleLines);
                duplicatedRuleItem.setCommercialRuleHeader(duplicate);
                commercialRuleItemService.create(duplicatedRuleItem);
                commercialRuleItems.add(duplicatedRuleItem);
            }
            duplicate.setCommercialRuleItems(commercialRuleItems);
            duplicate.setTargetOfferTemplate(offer);
            commercialRuleHeaderService.create(duplicate);
            duplicate.setCode(duplicate.getCode() + "_" + duplicate.getId());
            commercialRuleHeaders.add(duplicate);
        }
        return commercialRuleHeaders;
    }

    public List<OfferTemplate> list(String code, Date validFrom, Date validTo, LifeCycleStatusEnum lifeCycleStatusEnum) {
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
    
    
    @SuppressWarnings("unchecked")
    public List<OfferTemplate> getOffersByTags(HashSet<String> tagCodes) { 
    	List<OfferTemplate> offers=new ArrayList<OfferTemplate>();
    	try {
    		offers = (List<OfferTemplate>)getEntityManager().createNamedQuery("OfferTemplate.findByTags").setParameter("tagCodes", tagCodes).getResultList();
    	} catch (Exception e) {
    		e.printStackTrace();
    		log.error("listOffersByTags error ", e.getMessage());
    	}

    	return offers;
    }
    
	@SuppressWarnings("unchecked")
	public List<OfferTemplate> findByFilter(Map<String, Object> filters) {
        PaginationConfiguration configuration = getPaginationConfigurationFromFilter(filters);
        QueryBuilder query = getQuery(configuration);
		return query.getQuery(getEntityManager()).getResultList();
	}
	
    private PaginationConfiguration getPaginationConfigurationFromFilter(Map<String, Object> filters) {
        GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, PersistenceServiceHelper.getPersistenceService());
        filters = genericRequestMapper.evaluateFilters(filters, entityClass);
        return new PaginationConfiguration(filters);
    }
    
    public int massEnableDisableByIds(List<Long> offerTemplateIds, boolean flag){
        return getEntityManager()
                .createNamedQuery("OfferTemplate.massEnableDisable")
                .setParameter("ids", offerTemplateIds)
                .setParameter("flag", flag)
                .executeUpdate();
    }
}