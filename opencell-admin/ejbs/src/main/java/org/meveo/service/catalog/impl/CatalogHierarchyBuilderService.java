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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.Hibernate;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ImageUploadEventHandler;
import org.meveo.api.dto.catalog.ServiceConfigurationDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.ProductChargeTemplateMapping;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceChargeTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceChargeTemplateTermination;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.GroupedAttributes;
import org.meveo.model.cpq.Media;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.ProductVersionAttribute;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.offer.OfferComponent;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.model.cpq.trade.CommercialRuleItem;
import org.meveo.model.cpq.trade.CommercialRuleLine;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.cpq.CommercialRuleHeaderService;
import org.meveo.service.cpq.CommercialRuleItemService;
import org.meveo.service.cpq.CommercialRuleLineService;
import org.meveo.service.cpq.CpqQuoteService;
import org.meveo.service.cpq.GroupedAttributeService;
import org.meveo.service.cpq.MediaService;
import org.meveo.service.cpq.OfferComponentService;
import org.meveo.service.cpq.ProductVersionAttributeService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.QuoteArticleLineService;
import org.meveo.service.cpq.QuoteAttributeService;
import org.meveo.service.cpq.QuoteProductService;
import org.meveo.service.cpq.QuoteVersionService;
import org.meveo.service.cpq.order.QuotePriceService;
import org.meveo.service.quote.QuoteOfferService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A service that duplicate a hierarchy such as {@link OfferTemplate} and {@link ServiceTemplate}.
 *
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.2
 **/
@Stateless
public class CatalogHierarchyBuilderService {

    private Logger log = LoggerFactory.getLogger(CatalogHierarchyBuilderService.class);

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    @Inject
    private ServiceChargeTemplateSubscriptionService serviceChargeTemplateSubscriptionService;

    @Inject
    private ServiceChargeTemplateTerminationService serviceChargeTemplateTerminationService;

    @Inject
    private ServiceChargeTemplateUsageService serviceChargeTemplateUsageService;

    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private CounterTemplateService counterTemplateService;

    @Inject
    private ServiceChargeTemplateRecurringService serviceChargeTemplateRecurringService;

    @Inject
    private RecurringChargeTemplateService recurringChargeTemplateService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    private ProductTemplateService productTemplateService;

    @Inject
    private ProductChargeTemplateService productChargeTemplateService;

    @Inject
    ChargeTemplateService<ChargeTemplate> chargeTemplateService;

    @Inject
    private SubscriptionService subscriptionService;
    
    @Inject
    private ProductVersionService productVersionService;
    
    @Inject
    private DiscountPlanService discountPlanService;
    
    @Inject 
    private DiscountPlanItemService discountPlanItemService;
    
    @Inject
    private OfferComponentService offerComponentService;
    
    @Inject
    private MediaService mediaService;
    
    @Inject ProductChargeTemplateMappingService productChargeTemplateMappingService;
    
    @Inject private CommercialRuleHeaderService commercialRuleHeaderService;
    
    @Inject private CommercialRuleItemService commercialRuleItemService;
    
    @Inject private CommercialRuleLineService commercialRuleLineService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;
    

    @Inject private QuoteVersionService quoteVersionService;
    @Inject private QuoteOfferService quoteOfferService;
    @Inject private QuoteProductService quoteProductService;
    @Inject private QuoteAttributeService quoteAttributeService;
    @Inject private QuoteArticleLineService articleLineService;
    @Inject private QuotePriceService quotePriceService;
    @Inject private GroupedAttributeService groupedAttributeService;
    @Inject private ProductVersionAttributeService productVersionAttributeService;
    @Inject
    private CpqQuoteService cpqQuoteService;
    @Inject
    private ChargeTemplateServiceAll chargeTemplateServiceAll;


    public void duplicateProductVersion(ProductVersion entity, List<ProductVersionAttribute> attributes, List<Tag> tags, List<GroupedAttributes> groupedAttributes, String prefix) throws BusinessException {
    
        if(attributes != null) {
            entity.setAttributes(new ArrayList<ProductVersionAttribute>());
        	for (ProductVersionAttribute  productAttribute : attributes) {
        		for(Media media : productAttribute.getAttribute().getMedias()) {
        			Media newMedia = new Media(media);
        			newMedia.setCode(media.getCode() + "_" + entity.getId());
        			mediaService.create(newMedia);
        		}
        		productAttribute = productVersionAttributeService.refreshOrRetrieve(productAttribute);
        		ProductVersionAttribute prodVersionAttribute=new ProductVersionAttribute(productAttribute,entity);
        		productVersionAttributeService.create(prodVersionAttribute);
                entity.getAttributes().add(prodVersionAttribute);
			}
        }
        
        if(tags != null) {
        	entity.setTags(new HashSet<>());
        	for (Tag tag : tags) {
				entity.getTags().add(tag);
			}
        }
        
        if(groupedAttributes != null) {
        	entity.setGroupedAttributes(new ArrayList<GroupedAttributes>());
        	for (GroupedAttributes groupedAttribute : groupedAttributes) {
        		groupedAttribute = (GroupedAttributes) Hibernate.unproxy(groupedAttribute);
        		GroupedAttributes duplicateGroupedAttribute = new GroupedAttributes(groupedAttribute);
        		duplicateGroupedAttribute.setCode(groupedAttribute.getCode() + "_" + entity.getId());
        		var groupAttr = new ArrayList<>(groupedAttribute.getAttributes());
        		duplicateGroupedAttribute.setAttributes(groupAttr);
        		groupedAttributeService.create(duplicateGroupedAttribute);
        		entity.getGroupedAttributes().add(duplicateGroupedAttribute);
			}
        }
    }
    
   
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void duplicateProduct(Product entity, ProductVersion productVersion, Set<DiscountPlan> discountPlans, 
										Set<String> modelChildren, List<OfferComponent> offerComponents, List<Media> medias, 
										List<ProductChargeTemplateMapping> productCharge, String prefix,
										List<CommercialRuleHeader> commercialRuleHeader, List<CommercialRuleLine> commercialRuleLine) {
    	if(productVersion != null) {
    		ProductVersion tmpProductVersion = productVersionService.findById(productVersion.getId());
    		tmpProductVersion.getTags().size();
    		tmpProductVersion.getAttributes().size();
    		tmpProductVersion.getAttributes().forEach(att -> {
    			att.getAttribute().getMedias().size();
    			att.getAttribute().getAssignedAttributes().size();
    		});

    		tmpProductVersion.getGroupedAttributes().forEach(ga -> {
        		ga.getAttributes().size();
        		ga.getAttributes().forEach(a ->  {
        			a.getMedias().size();
        			a.getTags().size();
        		});
        		ga.getCommercialRules().size();
        		ga.getCommercialRules().forEach(cr -> cr.getCommercialRuleItems().size());
        	});
    		
    		var tagList = new ArrayList<>(tmpProductVersion.getTags());
    		var attributList = new ArrayList<>(tmpProductVersion.getAttributes());
    		var groupedAttribute = new ArrayList<>(tmpProductVersion.getGroupedAttributes());

    		ProductVersion newProductVersion = new ProductVersion(tmpProductVersion, entity);
    		productVersionService.create(newProductVersion);    		
			duplicateProductVersion(newProductVersion, attributList, tagList, groupedAttribute, newProductVersion.getId() + "_");			
			entity.getProductVersions().add(newProductVersion);
    	}
    	if(discountPlans != null) {
    		entity.getDiscountList().clear();;
    		discountPlans.forEach(dp -> {
    			dp.getDiscountPlanItems().size();
    			DiscountPlan newDiscountPlan = new DiscountPlan(dp);
    			newDiscountPlan.setCode(discountPlanService.findDuplicateCode(dp));
    			
    			
    			discountPlanService.create(newDiscountPlan);

    			List<DiscountPlanItem> discountPlanItem = new ArrayList<DiscountPlanItem>();
    			duplicateDiscount(newDiscountPlan, discountPlanItem);
    			
        		entity.getDiscountList().add(newDiscountPlan);
    			
    		});
    	}
    	if(modelChildren != null) {
    		entity.setModelChildren(new HashSet<String>());
    		modelChildren.forEach( model -> {
    			entity.getModelChildren().add(model);
    		});
    	}
    	
    	
    	if(medias != null) {
    		medias.forEach(media -> {
    			Media newMedia = new Media(media);
    			newMedia.setCode(mediaService.findDuplicateCode(media));
    			newMedia.setMediaName(media.getMediaName() + "_" + entity.getId());
    			mediaService.create(newMedia);
    			entity.getMedias().add(newMedia);
    		});
    	}
    	
    	if(productCharge != null) {
    		productCharge.forEach(pct -> { 
    			ProductChargeTemplateMapping duplicat = new ProductChargeTemplateMapping();
    			duplicat.setCounterTemplate(pct.getCounterTemplate());
                duplicat.setChargeTemplate(chargeTemplateServiceAll.duplicateCharge(pct.getChargeTemplate()));
    			duplicat.setProduct(entity);
    			duplicat.setAccumulatorCounterTemplates(new ArrayList<>());
    			duplicat.setWalletTemplates(new ArrayList<>());
    			productChargeTemplateMappingService.create(duplicat);
    			entity.getProductCharges().add(duplicat);
    		});
    	}
    	
    }
	
	public void duplicateCommercialRuleHeader(CommercialRuleHeader entity, List<CommercialRuleItem> commercialRuleItems) {
		if(!commercialRuleItems.isEmpty()) {
			commercialRuleItems.forEach(commercialRuleItem -> {
				commercialRuleItem.getCommercialRuleLines().size();
				commercialRuleItemService.detach(commercialRuleItem);
				CommercialRuleItem duplicate = new CommercialRuleItem(commercialRuleItem);
				duplicate.setCommercialRuleHeader(entity);
				commercialRuleItemService.create(duplicate);
				duplicationCommercialRuleLine(duplicate, duplicate.getCommercialRuleLines());
				entity.getCommercialRuleItems().add(duplicate);
			});
		}
	}
	
	private void duplicationCommercialRuleLine(CommercialRuleItem entity, List<CommercialRuleLine> commercialRuleLines) {
		if(!commercialRuleLines.isEmpty()) {
			commercialRuleLines.forEach(commercialRuleLine -> {
				commercialRuleLineService.detach(commercialRuleLine);
				CommercialRuleLine duplicate = new CommercialRuleLine(commercialRuleLine);
				commercialRuleLineService.create(duplicate);
				entity.getCommercialRuleLines().add(duplicate);
			});
		}
	}
	
	/*@SuppressWarnings("unchecked")
	private void duplicateCounterTemplate(Product entity, ProductChargeTemplateMapping productChargetTemplate, List<CounterTemplate> coutnerTemplates) {
		if(productChargetTemplate.getAccumulatorCounterTemplates() != null) {
			productChargetTemplate.getAccumulatorCounterTemplates().forEach(ct -> {
				if(ct instanceof CounterTemplate) {
					CounterTemplate counter = (CounterTemplate) ct;
					CounterTemplate duplicate = new CounterTemplate();
					duplicate.setAccumulator(counter.getAccumulator());
					duplicate.setAccumulatorType(counter.getAccumulatorType());
					duplicate.setActive(counter.isActive());
					duplicate.setCode(counterTemplateService.);
				}
			});
		}
	}*/
	
	
	private void duplicateDiscount(DiscountPlan entity, List<DiscountPlanItem> discountPlanItem) {
		if(entity.getDiscountPlanItems() != null && !entity.getDiscountPlanItems().isEmpty()) {
			entity.getDiscountPlanItems().forEach(dp -> {
				final DiscountPlanItem duplicate = new DiscountPlanItem();
				duplicate.setCode(dp.getCode());
				duplicate.setId(null);
				duplicate.setDiscountPlan(entity);
				duplicate.setExpressionEl(dp.getExpressionEl());
				duplicate.setDiscountValue(dp.getDiscountValue());
				duplicate.setDiscountValueEL(dp.getDiscountValueEL());
				duplicate.setDiscountPlanItemType(dp.getDiscountPlanItemType());
				duplicate.setUUIDIfNull();
				duplicate.setCfValues(dp.getCfValues());
				duplicate.setCfAccumulatedValues(dp.getCfAccumulatedValues());
				duplicate.setInvoiceCategory(dp.getInvoiceCategory());
				
				discountPlanItemService.create(duplicate);
				discountPlanItem.add(duplicate);
			});
			entity.setDiscountPlanItems(discountPlanItem);
		}
	}
    
    public void duplicateOfferServiceTemplate(OfferTemplate entity, List<OfferServiceTemplate> offerServiceTemplates, String prefix) throws BusinessException {
        List<OfferServiceTemplate> newOfferServiceTemplates = new ArrayList<>();
        List<PricePlanMatrix> pricePlansInMemory = new ArrayList<>();
        List<ChargeTemplate> chargeTemplateInMemory = new ArrayList<>();

        if (offerServiceTemplates != null) {
            for (OfferServiceTemplate offerServiceTemplate : offerServiceTemplates) {
                newOfferServiceTemplates.add(duplicateService(offerServiceTemplate, prefix, pricePlansInMemory, chargeTemplateInMemory));
            }

            // add to offer
            for (OfferServiceTemplate newOfferServiceTemplate : newOfferServiceTemplates) {
                entity.addOfferServiceTemplate(newOfferServiceTemplate);
            }
        }
    }

    public void duplicateOfferProductTemplate(OfferTemplate entity, List<OfferProductTemplate> offerProductTemplates, String prefix) throws BusinessException {
        List<OfferProductTemplate> newOfferProductTemplates = new ArrayList<>();
        List<PricePlanMatrix> pricePlansInMemory = new ArrayList<>();
        List<ChargeTemplate> chargeTemplateInMemory = new ArrayList<>();

        if (offerProductTemplates != null) {
            for (OfferProductTemplate offerProductTemplate : offerProductTemplates) {
                newOfferProductTemplates.add(duplicateProduct(offerProductTemplate, prefix, pricePlansInMemory, chargeTemplateInMemory));
            }

            // add to offer
            for (OfferProductTemplate offerProductTemplate : newOfferProductTemplates) {
                entity.addOfferProductTemplate(offerProductTemplate);
            }
        }
    }

    public OfferProductTemplate duplicateProduct(OfferProductTemplate offerProductTemplate, String prefix, List<PricePlanMatrix> pricePlansInMemory,
            List<ChargeTemplate> chargeTemplateInMemory) throws BusinessException {
        return duplicateOfferProductTemplate(offerProductTemplate, prefix, null, pricePlansInMemory, chargeTemplateInMemory);
    }

    /**
     * Duplicate product, product charge template and prices.
     *
     * @param offerProductTemplate offer product template
     * @param chargeTemplateInMemory list of charge template
     * @param pricePlansInMemory list o price plan matrix
     * @param prefix prefix used to generate the codes
     * @param serviceConfiguration service configuration.
     * @return offer product template.
     * @throws BusinessException business exception.
     */
    public OfferProductTemplate duplicateOfferProductTemplate(OfferProductTemplate offerProductTemplate, String prefix, ServiceConfigurationDto serviceConfiguration,
            List<PricePlanMatrix> pricePlansInMemory, List<ChargeTemplate> chargeTemplateInMemory) throws BusinessException {

        OfferProductTemplate newOfferProductTemplate = new OfferProductTemplate();

        if (serviceConfiguration != null) {
            newOfferProductTemplate.setMandatory(serviceConfiguration.isMandatory());
        } else {
            newOfferProductTemplate.setMandatory(offerProductTemplate.isMandatory());
        }

        ProductTemplate productTemplate = productTemplateService.findById(offerProductTemplate.getProductTemplate().getId());

        ProductTemplate newProductTemplate = new ProductTemplate();

        // TODO note, that this value is available in GUI only - see serviceConfiguration.getCfValues() comment
        duplicateProductTemplate(prefix, serviceConfiguration != null ? serviceConfiguration.getDescription() : "", productTemplate, newProductTemplate, pricePlansInMemory,
            chargeTemplateInMemory, serviceConfiguration != null ? serviceConfiguration.getCfValues() : null);
        newOfferProductTemplate.setProductTemplate(newProductTemplate);

        return newOfferProductTemplate;
    }

    public ProductTemplate duplicateProductTemplate(String prefix, String description, ProductTemplate productTemplate, ProductTemplate newProductTemplate,
            List<PricePlanMatrix> pricePlansInMemory, List<ChargeTemplate> chargeTemplateInMemory, Map<String, List<CustomFieldValue>> customFieldValues) throws BusinessException {
        try {
            BeanUtils.copyProperties(newProductTemplate, productTemplate);
            if (description != null) {
                newProductTemplate.setDescription(description);
            }
            // true on GUI instantiation
            if (StringUtils.isBlank(prefix)) {
                prefix = "";
            }

            newProductTemplate.setId(null);
            newProductTemplate.clearUuid();
            newProductTemplate.clearCfValues();
            newProductTemplate.setVersion(0);
            newProductTemplate.setCode(prefix + productTemplate.getCode());

            newProductTemplate.setOfferTemplateCategories(new ArrayList<OfferTemplateCategory>());
            newProductTemplate.setAttachments(new ArrayList<DigitalResource>());
            newProductTemplate.setBusinessAccountModels(new ArrayList<BusinessAccountModel>());
            newProductTemplate.setChannels(new ArrayList<Channel>());
            newProductTemplate.setProductChargeTemplates(new ArrayList<ProductChargeTemplate>());

            List<WalletTemplate> walletTemplates = productTemplate.getWalletTemplates();
            newProductTemplate.setWalletTemplates(new ArrayList<WalletTemplate>());
            if (walletTemplates != null) {
                for (WalletTemplate wt : walletTemplates) {
                    newProductTemplate.addWalletTemplate(wt);
                }
            }

            List<Seller> sellers = productTemplate.getSellers();
            newProductTemplate.setSellers(new ArrayList<>());
            if (sellers != null) {
                for (Seller seller : sellers) {
                    newProductTemplate.addSeller(seller);
                }
            }

            try {
                ImageUploadEventHandler<ProductTemplate> productImageUploadEventHandler = new ImageUploadEventHandler<>(currentUser.getProviderCode());
                String newImagePath = productImageUploadEventHandler.duplicateImage(newProductTemplate, productTemplate.getImagePath());
                newProductTemplate.setImagePath(newImagePath);
            } catch (IOException e1) {
                log.error("IPIEL: Failed duplicating product image: {}", e1.getMessage());
            }

            // set custom fields
            if (customFieldValues != null) {
                newProductTemplate.getCfValuesNullSafe().setValuesByCode(customFieldValues);
            } else if (productTemplate.getCfValues() != null) {
                newProductTemplate.getCfValuesNullSafe().setValuesByCode(productTemplate.getCfValues().getValuesByCode());
            }

            // needs a refresh here so CF will not be saved.
            productTemplateService.refresh(productTemplate);
            productTemplateService.create(newProductTemplate);

            // true on GUI instantiation
            if (StringUtils.isBlank(prefix)) {
                prefix = newProductTemplate.getId() + "_";
            }

            duplicateProductPrices(productTemplate, prefix, pricePlansInMemory, chargeTemplateInMemory);
            duplicateProductOffering(productTemplate, newProductTemplate);
            duplicateProductCharges(productTemplate, newProductTemplate, prefix, chargeTemplateInMemory);

            return newProductTemplate;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    public void duplicateProductCharges(ProductTemplate productTemplate, ProductTemplate newProductTemplate, String prefix, List<ChargeTemplate> chargeTemplateInMemory)
            throws BusinessException {
        if (productTemplate.getProductChargeTemplates() != null && !productTemplate.getProductChargeTemplates().isEmpty()) {
            for (ProductChargeTemplate productCharge : productTemplate.getProductChargeTemplates()) {
                ProductChargeTemplate newChargeTemplate = new ProductChargeTemplate();
                newChargeTemplate = (ProductChargeTemplate) copyChargeTemplate((ChargeTemplate) productCharge, newChargeTemplate, prefix);

                newChargeTemplate.setProductTemplates(new ArrayList<ProductTemplate>());

                if (chargeTemplateInMemory.contains(newChargeTemplate)) {
                    continue;
                } else {
                    chargeTemplateInMemory.add(newChargeTemplate);
                }
                if (newChargeTemplate.getId() == null) {
                    productChargeTemplateService.create(newChargeTemplate);
                    productCharge = productChargeTemplateService.refreshOrRetrieve(productCharge);
                    copyEdrTemplates((ChargeTemplate) productCharge, newChargeTemplate);
                }
                newProductTemplate.addProductChargeTemplate(newChargeTemplate);
            }
        }
    }

    private void duplicateProductOffering(ProductTemplate productTemplate, ProductTemplate newProductTemplate) {
        List<OfferTemplateCategory> offerTemplateCategories = productTemplate.getOfferTemplateCategories();
        List<DigitalResource> attachments = productTemplate.getAttachments();
        List<BusinessAccountModel> businessAccountModels = productTemplate.getBusinessAccountModels();
        List<Channel> channels = productTemplate.getChannels();

        if (offerTemplateCategories != null) {
            for (OfferTemplateCategory otc : offerTemplateCategories) {
                newProductTemplate.addOfferTemplateCategory(otc);
            }
        }

        if (attachments != null) {
            for (DigitalResource dr : attachments) {
                newProductTemplate.addAttachment(dr);
            }
        }

        if (businessAccountModels != null) {
            for (BusinessAccountModel bam : businessAccountModels) {
                newProductTemplate.addBusinessAccountModel(bam);
            }
        }

        if (channels != null) {
            for (Channel channel : channels) {
                newProductTemplate.addChannel(channel);
            }
        }
    }

    public void duplicateProductPrices(ProductTemplate productTemplate, String prefix, List<PricePlanMatrix> pricePlansInMemory, List<ChargeTemplate> chargeTemplateInMemory)
            throws BusinessException {
        // create price plans
        if (productTemplate.getProductChargeTemplates() != null && !productTemplate.getProductChargeTemplates().isEmpty()) {
            for (ProductChargeTemplate productCharge : productTemplate.getProductChargeTemplates()) {
                // create price plan
                String chargeTemplateCode = productCharge.getCode();
                List<PricePlanMatrix> pricePlanMatrixes = pricePlanMatrixService.listByChargeCode(chargeTemplateCode);
                if (pricePlanMatrixes != null) {
                    for (PricePlanMatrix pricePlanMatrix : pricePlanMatrixes) {
		            	duplicatePricePlanMatrix(pricePlanMatrix, prefix, chargeTemplateCode, pricePlansInMemory);
		            	}
                }
            }
        }
    }

    public OfferServiceTemplate duplicateService(OfferServiceTemplate offerServiceTemplate, String prefix, List<PricePlanMatrix> pricePlansInMemory,
            List<ChargeTemplate> chargeTemplateInMemory) throws BusinessException {
        return duplicateService(offerServiceTemplate, null, prefix, pricePlansInMemory, chargeTemplateInMemory);
    }
    public OfferServiceTemplate duplicateServiceWithoutDuplicatingChargeTemplates(OfferServiceTemplate offerServiceTemplate, ServiceTemplate serviceTemplate, ServiceConfigurationDto serviceConfiguration, String prefix) throws BusinessException {
        OfferServiceTemplate newOfferServiceTemplate = new OfferServiceTemplate();

        if (serviceConfiguration != null) {
            newOfferServiceTemplate.setMandatory(serviceConfiguration.isMandatory());
        } else {
            newOfferServiceTemplate.setMandatory(offerServiceTemplate.isMandatory());
        }

        if (offerServiceTemplate.getIncompatibleServices() != null) {
            newOfferServiceTemplate.getIncompatibleServices().addAll(offerServiceTemplate.getIncompatibleServices());
        }
        newOfferServiceTemplate.setValidity(offerServiceTemplate.getValidity());

        ServiceTemplate newServiceTemplate = new ServiceTemplate();
        String newCode = prefix + serviceTemplate.getCode();
        try {
            BeanUtils.copyProperties(newServiceTemplate, serviceTemplate);
            boolean instantiatedFromBOM = serviceConfiguration != null && serviceConfiguration.isInstantiatedFromBSM();

            if (instantiatedFromBOM) {
                // append a unique id
                newCode = newCode + "-" + UUID.randomUUID();
            }
            newServiceTemplate.setCode(newCode);
            if (serviceConfiguration != null) {
                newServiceTemplate.setDescription(serviceConfiguration.getDescription());
            }

            newServiceTemplate.setId(null);
            newServiceTemplate.setVersion(0);
            newServiceTemplate.clearCfValues();
            newServiceTemplate.clearUuid();
            this.duplicateAndSetImgPath(serviceTemplate, newServiceTemplate, serviceConfiguration != null ? serviceConfiguration.getImagePath() : "");

            // set custom fields
            // TODO note, that this value is available in GUI only - see serviceConfiguration.getCfValues() comment
            if (serviceConfiguration != null && serviceConfiguration.getCfValues() != null) {
                newServiceTemplate.getCfValuesNullSafe().setValuesByCode(serviceConfiguration.getCfValues());

            } else if (serviceTemplate.getCfValues() != null) {
                newServiceTemplate.getCfValuesNullSafe().setValuesByCode(serviceTemplate.getCfValues().getValuesByCode());
            }
            // update code if duplicate
            if (instantiatedFromBOM) {
                Integer serviceConfItemIndex = serviceConfiguration.getItemIndex();
                if (serviceConfItemIndex != null) {
                    prefix = prefix + serviceConfItemIndex + "_";
                } else {
                    prefix = prefix + newServiceTemplate.getId() + "_";
                }
                newServiceTemplate.setCode(prefix + serviceTemplate.getCode());

            } else if (serviceTemplateService.findByCode(newCode) != null) {
                newCode = newServiceTemplate.getCode();
                newServiceTemplate.setCode(newCode + "_" + UUID.randomUUID());
            }
            duplicateCharges(newServiceTemplate);
            if (newServiceTemplate.getServiceUsageCharges() != null) {
            	for(ServiceChargeTemplateUsage s : newServiceTemplate.getServiceUsageCharges()) {
            		ArrayList<CounterTemplate> newCounterTemplates = new ArrayList<>();
            		s.getAccumulatorCounterTemplates().stream().forEach(x->newCounterTemplates.add(x));
					s.setAccumulatorCounterTemplates(newCounterTemplates);
            	}
            }
            serviceTemplateService.create(newServiceTemplate);
            serviceTemplateService.commit();

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BusinessException(e.getMessage());
        }
        newOfferServiceTemplate.setServiceTemplate(newServiceTemplate);
        return newOfferServiceTemplate;
    }

    public OfferServiceTemplate duplicateService(OfferServiceTemplate offerServiceTemplate, ServiceConfigurationDto serviceConfiguration, String prefix,
            List<PricePlanMatrix> pricePlansInMemory, List<ChargeTemplate> chargeTemplateInMemory) throws BusinessException {
        OfferServiceTemplate newOfferServiceTemplate = new OfferServiceTemplate();

        if (serviceConfiguration != null) {
            newOfferServiceTemplate.setMandatory(serviceConfiguration.isMandatory());
        } else {
            newOfferServiceTemplate.setMandatory(offerServiceTemplate.isMandatory());
        }

        if (offerServiceTemplate.getIncompatibleServices() != null) {
            newOfferServiceTemplate.getIncompatibleServices().addAll(offerServiceTemplate.getIncompatibleServices());
        }
        newOfferServiceTemplate.setValidity(offerServiceTemplate.getValidity());

        newOfferServiceTemplate.setServiceTemplate(
            duplicateServiceTemplate(offerServiceTemplate.getServiceTemplate().getCode(), prefix, serviceConfiguration, pricePlansInMemory, chargeTemplateInMemory));
        return newOfferServiceTemplate;

    }
    private void duplicateCharges(ServiceTemplate entity) {
        entity.setServiceRecurringCharges(new ArrayList<>(entity.getServiceRecurringCharges()));
        entity.getServiceRecurringCharges().forEach(sctRecurring -> {
            serviceChargeTemplateRecurringService.detach(sctRecurring);
            linkAnExistingChargeToNewServiceTemplate(entity, sctRecurring);
        });
        entity.setServiceSubscriptionCharges(new ArrayList<>(entity.getServiceSubscriptionCharges()));
        entity.getServiceSubscriptionCharges().forEach(sctSubscription -> {
            serviceChargeTemplateSubscriptionService.detach(sctSubscription);
            linkAnExistingChargeToNewServiceTemplate(entity, sctSubscription);
        });
        entity.setServiceTerminationCharges(new ArrayList<>(entity.getServiceTerminationCharges()));
        entity.getServiceTerminationCharges().forEach(sctTermination -> {
            serviceChargeTemplateTerminationService.detach(sctTermination);
            linkAnExistingChargeToNewServiceTemplate(entity, sctTermination);
        });
        entity.setServiceUsageCharges(new ArrayList<>(entity.getServiceUsageCharges()));
        entity.getServiceUsageCharges().forEach(sctUsageCharge -> {
            serviceChargeTemplateUsageService.detach(sctUsageCharge);
            linkAnExistingChargeToNewServiceTemplate(entity, sctUsageCharge);
        });
    }

    private void linkAnExistingChargeToNewServiceTemplate(ServiceTemplate entity, ServiceChargeTemplate termination) {
        termination.setId(null);
        termination.setServiceTemplate(entity);
    }

    public ServiceTemplate duplicateServiceTemplate(String serviceTemplateSourceCode, String prefix, ServiceConfigurationDto serviceConfiguration,
            List<PricePlanMatrix> pricePlansInMemory, List<ChargeTemplate> chargeTemplateInMemory) throws BusinessException {

        ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceTemplateSourceCode);
        serviceTemplate.getServiceRecurringCharges().size();
        serviceTemplate.getServiceSubscriptionCharges().size();
        serviceTemplate.getServiceTerminationCharges().size();
        serviceTemplate.getServiceUsageCharges().size(); 

        ServiceTemplate newServiceTemplate = new ServiceTemplate();
        String newCode = prefix + serviceTemplate.getCode();
        try {
            BeanUtils.copyProperties(newServiceTemplate, serviceTemplate);
            boolean instantiatedFromBOM = serviceConfiguration != null && serviceConfiguration.isInstantiatedFromBSM();

            if (instantiatedFromBOM) {
                // append a unique id
                newCode = newCode + "-" + UUID.randomUUID();
            }
            newServiceTemplate.setCode(newCode);
            if (serviceConfiguration != null) {
                newServiceTemplate.setDescription(serviceConfiguration.getDescription());
            }

            newServiceTemplate.setId(null);
            newServiceTemplate.setVersion(0);
            newServiceTemplate.clearCfValues();
            newServiceTemplate.clearUuid();
            newServiceTemplate.setServiceRecurringCharges(new ArrayList<ServiceChargeTemplateRecurring>());
            newServiceTemplate.setServiceTerminationCharges(new ArrayList<ServiceChargeTemplateTermination>());
            newServiceTemplate.setServiceSubscriptionCharges(new ArrayList<ServiceChargeTemplateSubscription>());
            newServiceTemplate.setServiceUsageCharges(new ArrayList<ServiceChargeTemplateUsage>());
            
            this.duplicateAndSetImgPath(serviceTemplate, newServiceTemplate, serviceConfiguration != null ? serviceConfiguration.getImagePath() : "");

            // set custom fields
            // TODO note, that this value is available in GUI only - see serviceConfiguration.getCfValues() comment
            if (serviceConfiguration != null && serviceConfiguration.getCfValues() != null) {
                newServiceTemplate.getCfValuesNullSafe().setValuesByCode(serviceConfiguration.getCfValues());

            } else if (serviceTemplate.getCfValues() != null) {
                newServiceTemplate.getCfValuesNullSafe().setValuesByCode(serviceTemplate.getCfValues().getValuesByCode());
            }

            serviceTemplateService.refresh(serviceTemplate);

            // update code if duplicate
            if (instantiatedFromBOM) {
                Integer serviceConfItemIndex = serviceConfiguration.getItemIndex();
                if (serviceConfItemIndex != null) {
                    prefix = prefix + serviceConfItemIndex + "_";
                } else {
                    prefix = prefix + newServiceTemplate.getId() + "_";
                }
                newServiceTemplate.setCode(prefix + serviceTemplate.getCode());

            } else if (serviceTemplateService.findByCode(newCode) != null) {
                newCode = newServiceTemplate.getCode();
                newServiceTemplate.setCode(newCode + "_" + UUID.randomUUID());
            }

            serviceTemplateService.create(newServiceTemplate);

            duplicatePrices(serviceTemplate, prefix, pricePlansInMemory);
            duplicateCharges(serviceTemplate, newServiceTemplate, prefix, chargeTemplateInMemory);

            return newServiceTemplate;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BusinessException(e.getMessage());
        }
    }

	private void duplicateAndSetImgPath(ServiceTemplate serviceTemplate, ServiceTemplate newServiceTemplate, String cfgImgPath) {
		try {
		    ImageUploadEventHandler<ServiceTemplate> serviceImageUploadEventHandler = new ImageUploadEventHandler<>(currentUser.getProviderCode());
		     
            final String sourceImgPath = isNotBlank(cfgImgPath) ? cfgImgPath : serviceTemplate.getImagePath();
		    String newImagePath = serviceImageUploadEventHandler.duplicateImage(newServiceTemplate, sourceImgPath);
		    
		    newServiceTemplate.setImagePath(newImagePath);
		} catch (IOException e1) {
		    log.error("IPIEL: Failed duplicating service image: {}", e1.getMessage());
		}
	}
	
    private void duplicatePrices(ServiceTemplate serviceTemplate, String prefix, List<PricePlanMatrix> pricePlansInMemory) throws BusinessException {

        // create price plans
		if (serviceTemplate.getServiceRecurringCharges() != null && !serviceTemplate.getServiceRecurringCharges().isEmpty()) {
		    for (ServiceChargeTemplateRecurring serviceCharge : serviceTemplate.getServiceRecurringCharges()) {
		        // create price plan
		        String chargeTemplateCode = serviceCharge.getChargeTemplate().getCode();
		        List<PricePlanMatrix> pricePlanMatrixes = pricePlanMatrixService.listByChargeCode(chargeTemplateCode);
		        if (pricePlanMatrixes != null) {
		            for (PricePlanMatrix pricePlanMatrix : pricePlanMatrixes) {
		            	duplicatePricePlanMatrix(pricePlanMatrix, prefix, chargeTemplateCode, pricePlansInMemory);
		            	}
		        }
		    }
		}

		if (serviceTemplate.getServiceSubscriptionCharges() != null && !serviceTemplate.getServiceSubscriptionCharges().isEmpty()) {
		    for (ServiceChargeTemplateSubscription serviceCharge : serviceTemplate.getServiceSubscriptionCharges()) {
		        // create price plan
		        String chargeTemplateCode = serviceCharge.getChargeTemplate().getCode();
		        List<PricePlanMatrix> pricePlanMatrixes = pricePlanMatrixService.listByChargeCode(chargeTemplateCode);
		        if (pricePlanMatrixes != null) {
		            for (PricePlanMatrix pricePlanMatrix : pricePlanMatrixes) {
		            	duplicatePricePlanMatrix(pricePlanMatrix, prefix, chargeTemplateCode, pricePlansInMemory);
		            }
		        }
		    }
		}

		if (serviceTemplate.getServiceTerminationCharges() != null && !serviceTemplate.getServiceTerminationCharges().isEmpty()) {
		    for (ServiceChargeTemplateTermination serviceCharge : serviceTemplate.getServiceTerminationCharges()) {
		        // create price plan
		        String chargeTemplateCode = serviceCharge.getChargeTemplate().getCode();
		        List<PricePlanMatrix> pricePlanMatrixes = pricePlanMatrixService.listByChargeCode(chargeTemplateCode);
		        if (pricePlanMatrixes != null) {
		            for (PricePlanMatrix pricePlanMatrix : pricePlanMatrixes) {
		            	duplicatePricePlanMatrix(pricePlanMatrix, prefix, chargeTemplateCode, pricePlansInMemory);}
		        }
		    }
		}

		if (serviceTemplate.getServiceUsageCharges() != null && !serviceTemplate.getServiceUsageCharges().isEmpty()) {
		    for (ServiceChargeTemplateUsage serviceCharge : serviceTemplate.getServiceUsageCharges()) {
		        String chargeTemplateCode = serviceCharge.getChargeTemplate().getCode();
		        List<PricePlanMatrix> pricePlanMatrixes = pricePlanMatrixService.listByChargeCode(chargeTemplateCode);
		        if (pricePlanMatrixes != null) {
		            for (PricePlanMatrix pricePlanMatrix : pricePlanMatrixes) {
		            	duplicatePricePlanMatrix(pricePlanMatrix, prefix, chargeTemplateCode, pricePlansInMemory);}
		        }
		    }
		}
    }
    
    private void duplicatePricePlanMatrix(PricePlanMatrix pricePlanMatrix, String prefix, String chargeTemplateCode, List<PricePlanMatrix> pricePlansInMemory) throws BusinessException {
    	try {
	    	pricePlanMatrix.getVersions().size();
	    	var versions = new ArrayList<>(pricePlanMatrix.getVersions());
	    	pricePlanMatrix.getVersions().clear();
	    	
	        String ppCode = prefix + pricePlanMatrix.getCode();
	        if (pricePlanMatrixService.findByCode(ppCode) != null) {
	            return;
	        }
	    	
	        PricePlanMatrix newPriceplanmaMatrix = new PricePlanMatrix(pricePlanMatrix);
	        newPriceplanmaMatrix.setEventCode(prefix + chargeTemplateCode);
	        newPriceplanmaMatrix.setCode(ppCode);
	        newPriceplanmaMatrix.setVersion(0);
	        newPriceplanmaMatrix.setOfferTemplate(null);
	        newPriceplanmaMatrix.setVersions(new ArrayList<>());
	        
	        if(versions != null) {
	        	for (PricePlanMatrixVersion version : versions) {
	        		newPriceplanmaMatrix.getVersions().add(version);
				}
	        }
	
	        if (pricePlansInMemory.contains(newPriceplanmaMatrix)) {
	            return;
	        } else {
	            pricePlansInMemory.add(newPriceplanmaMatrix);
	        }
	
	        pricePlanMatrixService.createPP(newPriceplanmaMatrix);
    	}catch(RuntimeException e) {
    		throw new BusinessException(e.getMessage());
    	}
    
    }

    private ChargeTemplate setChargeTemplate(ChargeTemplate chargeTemplate, ChargeTemplate newChargeTemplate, List<ChargeTemplate> chargeTemplateInMemory, String prefix)
            throws BusinessException {
        newChargeTemplate = copyChargeTemplate(chargeTemplate, newChargeTemplate, prefix);
        if (chargeTemplateInMemory.contains(newChargeTemplate)) {
            newChargeTemplate = chargeTemplateInMemory.get(chargeTemplateInMemory.indexOf(newChargeTemplate));
        } else {
            chargeTemplateInMemory.add(newChargeTemplate);
        }
        return newChargeTemplate;
    }

    private ChargeTemplate getChargeTemplate(@SuppressWarnings("rawtypes") ServiceChargeTemplate serviceChargeTemplate, List<ChargeTemplate> chargeTemplateInMemory, String prefix)
            throws BusinessException {
        ChargeTemplate chargeTemplate = serviceChargeTemplate.getChargeTemplate();
        ChargeTemplate newChargeTemplate = null;
        if (serviceChargeTemplate instanceof ServiceChargeTemplateRecurring) {
            newChargeTemplate = new RecurringChargeTemplate();
            newChargeTemplate = setChargeTemplate(chargeTemplate, newChargeTemplate, chargeTemplateInMemory, prefix);
            if (newChargeTemplate.getId() == null) {
                recurringChargeTemplateService.create((RecurringChargeTemplate) newChargeTemplate);
            }
        } else if (serviceChargeTemplate instanceof ServiceChargeTemplateSubscription || serviceChargeTemplate instanceof ServiceChargeTemplateTermination) {
            newChargeTemplate = new OneShotChargeTemplate();
            newChargeTemplate = setChargeTemplate(chargeTemplate, newChargeTemplate, chargeTemplateInMemory, prefix);
            if (newChargeTemplate.getId() == null) {
                oneShotChargeTemplateService.create((OneShotChargeTemplate) newChargeTemplate);
            }
        } else if (serviceChargeTemplate instanceof ServiceChargeTemplateUsage) {
            newChargeTemplate = new UsageChargeTemplate();
            newChargeTemplate = setChargeTemplate(chargeTemplate, newChargeTemplate, chargeTemplateInMemory, prefix);
            if (newChargeTemplate.getId() == null) {
                usageChargeTemplateService.create((UsageChargeTemplate) newChargeTemplate);
            }
        }

        if (newChargeTemplate != null) {
            copyEdrTemplates(chargeTemplate, newChargeTemplate);
        }
        return newChargeTemplate;
    }

    @SuppressWarnings("unchecked")
    private void setServiceChargeTemplate(ServiceTemplate newServiceTemplate, @SuppressWarnings("rawtypes") ServiceChargeTemplate serviceChargeTemplate,
            @SuppressWarnings("rawtypes") ServiceChargeTemplate newServiceChargeTemplate, List<ChargeTemplate> chargeTemplateInMemory, String prefix) throws BusinessException {

        ChargeTemplate newChargeTemplate = getChargeTemplate(serviceChargeTemplate, chargeTemplateInMemory, prefix);
        newServiceChargeTemplate.setChargeTemplate(newChargeTemplate);
        newServiceChargeTemplate.setServiceTemplate(newServiceTemplate);
        newServiceChargeTemplate.setCounterTemplate(counterTemplateService.getCounterTemplate(serviceChargeTemplate, prefix));
        if (serviceChargeTemplate.getWalletTemplates() != null) {
            newServiceChargeTemplate.setWalletTemplates(new ArrayList<WalletTemplate>());
            newServiceChargeTemplate.getWalletTemplates().addAll(serviceChargeTemplate.getWalletTemplates());
        }
    }

    private void createServiceChargeTemplateRecurring(ServiceTemplate serviceTemplate, ServiceChargeTemplateRecurring serviceChargeTemplate,
            List<ChargeTemplate> chargeTemplateInMemory, String prefix) throws BusinessException {
        ServiceChargeTemplateRecurring newServiceChargeTemplate = new ServiceChargeTemplateRecurring();
        setServiceChargeTemplate(serviceTemplate, serviceChargeTemplate, newServiceChargeTemplate, chargeTemplateInMemory, prefix);
        serviceChargeTemplateRecurringService.create(newServiceChargeTemplate);
        serviceTemplate.getServiceRecurringCharges().add(newServiceChargeTemplate);
    }

    private void createServiceChargeTemplateSubscription(ServiceTemplate serviceTemplate, ServiceChargeTemplateSubscription serviceChargeTemplate,
            List<ChargeTemplate> chargeTemplateInMemory, String prefix) throws BusinessException {
        ServiceChargeTemplateSubscription newServiceChargeTemplate = new ServiceChargeTemplateSubscription();
        setServiceChargeTemplate(serviceTemplate, serviceChargeTemplate, newServiceChargeTemplate, chargeTemplateInMemory, prefix);
        serviceChargeTemplateSubscriptionService.create(newServiceChargeTemplate);
        serviceTemplate.getServiceSubscriptionCharges().add(newServiceChargeTemplate);
    }

    private void createServiceChargeTemplateTermination(ServiceTemplate serviceTemplate, ServiceChargeTemplateTermination serviceChargeTemplate,
            List<ChargeTemplate> chargeTemplateInMemory, String prefix) throws BusinessException {
        ServiceChargeTemplateTermination newServiceChargeTemplate = new ServiceChargeTemplateTermination();
        setServiceChargeTemplate(serviceTemplate, serviceChargeTemplate, newServiceChargeTemplate, chargeTemplateInMemory, prefix);
        serviceChargeTemplateTerminationService.create(newServiceChargeTemplate);
        serviceTemplate.getServiceTerminationCharges().add(newServiceChargeTemplate);
    }

    private void createServiceChargeTemplateUsage(ServiceTemplate serviceTemplate, ServiceChargeTemplateUsage serviceChargeTemplate, List<ChargeTemplate> chargeTemplateInMemory,
            String prefix) throws BusinessException {
        ServiceChargeTemplateUsage newServiceChargeTemplate = new ServiceChargeTemplateUsage();
        setServiceChargeTemplate(serviceTemplate, serviceChargeTemplate, newServiceChargeTemplate, chargeTemplateInMemory, prefix);
        serviceChargeTemplateUsageService.create(newServiceChargeTemplate);
        serviceTemplate.getServiceUsageCharges().add(newServiceChargeTemplate);
    }

    private void duplicateCharges(ServiceTemplate serviceTemplate, ServiceTemplate newServiceTemplate, String prefix, List<ChargeTemplate> chargeTemplateInMemory)
            throws BusinessException {

        if (serviceTemplate.getServiceRecurringCharges() != null && !serviceTemplate.getServiceRecurringCharges().isEmpty()) {
            for (ServiceChargeTemplateRecurring serviceChargeTemplate : serviceTemplate.getServiceRecurringCharges()) {
                createServiceChargeTemplateRecurring(newServiceTemplate, serviceChargeTemplate, chargeTemplateInMemory, prefix);
            }
        }

        if (serviceTemplate.getServiceSubscriptionCharges() != null && !serviceTemplate.getServiceSubscriptionCharges().isEmpty()) {
            for (ServiceChargeTemplateSubscription serviceChargeTemplate : serviceTemplate.getServiceSubscriptionCharges()) {
                createServiceChargeTemplateSubscription(newServiceTemplate, serviceChargeTemplate, chargeTemplateInMemory, prefix);
            }
        }

        if (serviceTemplate.getServiceTerminationCharges() != null && !serviceTemplate.getServiceTerminationCharges().isEmpty()) {
            for (ServiceChargeTemplateTermination serviceChargeTemplate : serviceTemplate.getServiceTerminationCharges()) {
                createServiceChargeTemplateTermination(newServiceTemplate, serviceChargeTemplate, chargeTemplateInMemory, prefix);
            }
        }

        if (serviceTemplate.getServiceUsageCharges() != null && !serviceTemplate.getServiceUsageCharges().isEmpty()) {
            for (ServiceChargeTemplateUsage serviceChargeTemplate : serviceTemplate.getServiceUsageCharges()) {
                createServiceChargeTemplateUsage(newServiceTemplate, serviceChargeTemplate, chargeTemplateInMemory, prefix);
            }
        }
    }

    /**
     * Copy basic properties of a chargeTemplate to another object, or return an existing ChargeTemplate having code = prefix + sourceChargeTemplate.getCode()
     *
     * @param sourceChargeTemplate the source charge template
     * @param targetTemplate the target template
     * @param prefix the prefix
     * @return the charge template
     * @throws BusinessException the business exception
     */
    private ChargeTemplate copyChargeTemplate(ChargeTemplate sourceChargeTemplate, ChargeTemplate targetTemplate, String prefix) throws BusinessException {

        String newChargeCode = prefix + sourceChargeTemplate.getCode();
        ChargeTemplate existingChargeTemplate = chargeTemplateService.findByCode(newChargeCode);
        if (existingChargeTemplate != null) {
            return existingChargeTemplate;
        } else {
            try {
                BeanUtils.copyProperties(targetTemplate, sourceChargeTemplate);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new BusinessException(e.getMessage());
            }
            targetTemplate.setId(null);
            targetTemplate.setAuditable(null);
            targetTemplate.setCode(newChargeCode);
            targetTemplate.clearUuid();
            targetTemplate.setVersion(0);
            targetTemplate.setEdrTemplates(new ArrayList<TriggeredEDRTemplate>());
            return targetTemplate;
        }
    }

    private void copyEdrTemplates(ChargeTemplate sourceChargeTemplate, ChargeTemplate targetChargeTemplate) {
        if (sourceChargeTemplate.getEdrTemplates() != null && !sourceChargeTemplate.getEdrTemplates().isEmpty()) {
            for (TriggeredEDRTemplate triggeredEDRTemplate : sourceChargeTemplate.getEdrTemplates()) {
                if (!targetChargeTemplate.getEdrTemplates().contains(triggeredEDRTemplate)) {
                    targetChargeTemplate.getEdrTemplates().add(triggeredEDRTemplate);
                }
            }
        }
    }

    /**
     * we will delete offer with the following principles : 1. offer/service/charges deletion when not subscribed 2. offer deletion only if services are used in an other offer 3.
     * offer/services deletion only if charges are used in an other service
     *
     * @param entity instance of Offer Template which contains entities to delete.
     * @throws BusinessException exception when deletion causes some errors
     */
    public synchronized void delete(OfferTemplate entity) throws BusinessException {

        if (entity == null || entity.isTransient() || subscriptionService.hasSubscriptions(entity)) {
            return;
        }
        List<OfferServiceTemplate> offerServiceTemplates = entity.getOfferServiceTemplates();
        if (offerServiceTemplates != null) {
            for (OfferServiceTemplate offerServiceTemplate : offerServiceTemplates) {
                if (offerServiceTemplate != null) {
                    ServiceTemplate serviceTemplate = offerServiceTemplate.getServiceTemplate();
                    List<ServiceTemplate> servicesWithNotOffer = serviceTemplateService.getServicesWithNotOffer();
                    if (servicesWithNotOffer != null) {
                        for (ServiceTemplate serviceTemplateWithoutOffer : servicesWithNotOffer) {
                            if (serviceTemplateWithoutOffer == null) {
                                continue;
                            }

                            String serviceCode = serviceTemplateWithoutOffer.getCode();
                            if (serviceCode != null && serviceCode.equals(serviceTemplate.getCode())) {
                                this.deleteServiceAndCharge(serviceTemplate);
                                break;
                            }
                        }

                    }

                }
            }
        }
    }

    private void deleteUsageChargeTemplate(Map<String, List<ServiceChargeTemplateUsage>> usageCounterChargeMap,
            @SuppressWarnings("rawtypes") ServiceChargeTemplate serviceChargeTemplate) throws BusinessException {
        Long chargeId = serviceChargeTemplate.getChargeTemplate().getId();
        String chargeTemplateCode = serviceChargeTemplate.getChargeTemplate().getCode();
        List<Long> linkedServiceIds = this.oneShotChargeTemplateService.getServiceIdsLinkedToChargeUsage(chargeId);
        if (!(linkedServiceIds != null && linkedServiceIds.size() > 0)) {
            this.usageChargeTemplateService.remove(chargeId);
            // Delete counter.
            CounterTemplate counterTemplate = serviceChargeTemplate.getCounterTemplate();
            if (counterTemplate != null) {
                List<ServiceChargeTemplateUsage> serviceChargeTemplateUsageList = usageCounterChargeMap.get(counterTemplate.getCode());
                if (serviceChargeTemplateUsageList != null && serviceChargeTemplateUsageList.size() == 1
                        && chargeTemplateCode.equals(serviceChargeTemplateUsageList.get(0).getChargeTemplate().getCode())) {
                    // It means this counter is related to only current charge
                    this.counterTemplateService.remove(counterTemplate);
                }
            }

            List<PricePlanMatrix> pricePlanMatrixes = this.pricePlanMatrixService.listByChargeCode(chargeTemplateCode);
            if (pricePlanMatrixes != null) {
                for (PricePlanMatrix pricePlanMatrix : pricePlanMatrixes) {
                    if (pricePlanMatrix == null) {
                        continue;
                    }
                    this.pricePlanMatrixService.remove(pricePlanMatrix);
                }

            }
        }
    }

    private void deleteRecurringChargeTemplate(Map<String, List<ServiceChargeTemplateRecurring>> recurringCounterChargeMap,
            @SuppressWarnings("rawtypes") ServiceChargeTemplate serviceChargeTemplate) throws BusinessException {
        Long chargeId = serviceChargeTemplate.getChargeTemplate().getId();
        String chargeTemplateCode = serviceChargeTemplate.getChargeTemplate().getCode();
        List<Long> linkedServiceIds = this.oneShotChargeTemplateService.getServiceIdsLinkedToChargeRecurring(chargeId);
        if (!(linkedServiceIds != null && linkedServiceIds.size() > 0)) {
            this.recurringChargeTemplateService.remove(chargeId);
            // Delete counter.
            CounterTemplate counterTemplate = serviceChargeTemplate.getCounterTemplate();
            if (counterTemplate != null) {
                List<ServiceChargeTemplateRecurring> serviceChargeTemplateList = recurringCounterChargeMap.get(counterTemplate.getCode());
                if (serviceChargeTemplateList != null && serviceChargeTemplateList.size() == 1
                        && chargeTemplateCode.equals(serviceChargeTemplateList.get(0).getChargeTemplate().getCode())) {
                    // It means this counter is related to only current charge
                    this.counterTemplateService.remove(counterTemplate);
                }
            }
        }
    }

    /**
     * @param serviceTemplate service template.
     * @throws BusinessException business exception.
     */
    @SuppressWarnings("rawtypes")
    private void deleteServiceAndCharge(ServiceTemplate serviceTemplate) throws BusinessException {

        List<ServiceChargeTemplateTermination> serviceTerminationCharges = serviceTemplate.getServiceTerminationCharges();
        List<ServiceChargeTemplateSubscription> serviceSubscriptionCharges = serviceTemplate.getServiceSubscriptionCharges();
        List<ServiceChargeTemplateRecurring> serviceRecurringCharges = serviceTemplate.getServiceRecurringCharges();
        List<ServiceChargeTemplateUsage> serviceUsageCharges = serviceTemplate.getServiceUsageCharges();
        List<ServiceChargeTemplate> serviceChargeTemplateList = new ArrayList<>();
        serviceChargeTemplateList.addAll(serviceSubscriptionCharges);
        serviceChargeTemplateList.addAll(serviceRecurringCharges);
        serviceChargeTemplateList.addAll(serviceUsageCharges);
        serviceChargeTemplateList.addAll(serviceTerminationCharges);

        Map<String, List<ServiceChargeTemplateUsage>> usageCounterChargeMap = new HashMap<>();
        for (ServiceChargeTemplate serviceChargeTemplateUsage : serviceUsageCharges) {
            CounterTemplate counterTemplate = serviceChargeTemplateUsage.getCounterTemplate();
            if (counterTemplate != null) {
                usageCounterChargeMap.put(counterTemplate.getCode(), this.serviceChargeTemplateUsageService.findByCounterTemplate(counterTemplate));
            }
        }

        Map<String, List<ServiceChargeTemplateRecurring>> recurringCounterChargeMap = new HashMap<>();
        for (ServiceChargeTemplate serviceChargeTemplateRecurring : serviceRecurringCharges) {
            CounterTemplate counterTemplate = serviceChargeTemplateRecurring.getCounterTemplate();
            if (counterTemplate != null) {
                recurringCounterChargeMap.put(counterTemplate.getCode(), this.serviceChargeTemplateRecurringService.findByCounterTemplate(counterTemplate));
            }
        }

        try {
            this.serviceTemplateService.remove(serviceTemplate);
        } catch (BusinessException exception) {
            throw exception;
        }

        for (ServiceChargeTemplate serviceChargeTemplate : serviceChargeTemplateList) {
            if (serviceChargeTemplate == null) {
                continue;
            }

            String chargeTemplateCode = serviceChargeTemplate.getChargeTemplate().getCode();

            if (chargeTemplateCode == null) {
                continue;
            }

            List<Long> linkedServiceIds = null;
            Long chargeId = serviceChargeTemplate.getChargeTemplate().getId();
            if (serviceChargeTemplate instanceof ServiceChargeTemplateUsage) {
                deleteUsageChargeTemplate(usageCounterChargeMap, serviceChargeTemplate);
            } else if (serviceChargeTemplate instanceof ServiceChargeTemplateRecurring) {
                deleteRecurringChargeTemplate(recurringCounterChargeMap, serviceChargeTemplate);
            } else if (serviceChargeTemplate instanceof ServiceChargeTemplateSubscription) {
                linkedServiceIds = this.oneShotChargeTemplateService.getServiceIdsLinkedToChargeSubscription(chargeId);
                if (!(linkedServiceIds != null && linkedServiceIds.size() > 0)) {
                    this.oneShotChargeTemplateService.remove(chargeId);
                }
            } else if (serviceChargeTemplate instanceof ServiceChargeTemplateTermination) {
                linkedServiceIds = this.oneShotChargeTemplateService.getServiceIdsLinkedToChargeTermination(chargeId);
                if (!(linkedServiceIds != null && linkedServiceIds.size() > 0)) {
                    this.oneShotChargeTemplateService.remove(chargeId);
                }
            }

        }

    }
    
    private void breakLazyLoadForQuoteVersion(QuoteVersion quoteVersion) {
    	quoteVersion.getMedias().size();
    	quoteVersion.getQuoteOffers().size();
    	quoteVersion.getQuoteOffers().forEach(this::breakLazyLoadForQuoteOffer);
    }

    private void breakLazyLoadForQuoteOffer(QuoteOffer qo) {
            qo.getQuoteProduct().size();
            qo.getQuoteAttributes().size();
            qo.getQuoteProduct().forEach(qp -> {
                qp.getQuoteAttributes().size();
                qp.getQuoteArticleLines().size();
                qp.getQuoteArticleLines().forEach(qal -> {
                    qal.getQuotePrices().size();
                });
            });
    }
    
    
    public QuoteVersion duplicateQuoteVersion(CpqQuote entity, QuoteVersion quoteVersion) {
    	final QuoteVersion duplicate = new QuoteVersion();
    	breakLazyLoadForQuoteVersion(quoteVersion);
    	quoteVersionService.detach(quoteVersion);
    	try {
			BeanUtils.copyProperties(duplicate, quoteVersion);
		} catch (IllegalAccessException | InvocationTargetException e) {
		}
    	var quoteOffer = quoteVersion.getQuoteOffers();
    	
    	duplicate.setId(null);
    	duplicate.setQuoteOffers(new ArrayList<QuoteOffer>());
    	duplicate.setQuoteArticleLines(new ArrayList<QuoteArticleLine>());
    	duplicate.setQuotePrices(new ArrayList<QuotePrice>());
    	duplicate.setQuote(entity);
    	duplicate.setStatus(VersionStatusEnum.DRAFT);
    	duplicate.setStatusDate(Calendar.getInstance().getTime());
    	duplicate.setQuoteVersion(1);
    	duplicate.setUuid(UUID.randomUUID().toString());
    	var medias = new ArrayList<>(quoteVersion.getMedias());
		 duplicate.setMedias(new ArrayList<Media>());
		 if(quoteVersion.getMedias() != null) {
			 for (Media media : medias) {
				 duplicate.getMedias().add(mediaService.findById(media.getId()));
			}
		 }
    	
    	quoteVersionService.create(duplicate);
    	duplicateQuoteOffer(quoteOffer, duplicate);
    	return duplicate;
    }
    
    private void duplicateQuoteOffer(List<QuoteOffer> offers, QuoteVersion entity) {
    	for (QuoteOffer quoteOffer : offers) {
    		
    		duplicateQuoteOffer(quoteOffer,entity);
		}
    }
    
    private void duplicateQuoteProduct(List<QuoteProduct> products, QuoteOffer offer) {
    	for (QuoteProduct quoteProduct : products) {
			final var duplicate = new QuoteProduct(quoteProduct);
			quoteProductService.detach(quoteProduct);
			var quoteAttributes = quoteProduct.getQuoteAttributes();
			duplicate.setQuoteOffer(offer);
			duplicate.setQuote(offer.getQuoteVersion().getQuote());
			duplicate.setQuoteVersion(offer.getQuoteVersion());
			duplicate.setQuoteAttributes(new ArrayList<QuoteAttribute>());
			duplicate.setQuoteArticleLines(new ArrayList<QuoteArticleLine>());
			quoteProductService.create(duplicate);
			
			duplicateQuoteAttribute(quoteAttributes, duplicate, null);
			
		}
    }
    
    
   
    
    
    private void duplicateQuoteAttribute(List<QuoteAttribute> attributes, QuoteProduct quoteProduct, QuoteOffer offer) {
    	for (QuoteAttribute quoteAttribute : attributes) {
			final var duplicate = new QuoteAttribute(quoteAttribute);
			quoteAttributeService.detach(quoteAttribute);
			if(quoteProduct != null)
				duplicate.setQuoteProduct(quoteProduct);
			if(offer != null)
				duplicate.setQuoteOffer(offer);
			quoteAttributeService.create(duplicate);
		}
    }
    
    public QuoteOffer duplicateQuoteOffer(QuoteOffer quoteOffer, QuoteVersion quoteVersion) {
    	
        breakLazyLoadForQuoteOffer(quoteOffer);
		var quoteProducts = new ArrayList<QuoteProduct>(quoteOffer.getQuoteProduct());
		var quoteAttributes = new ArrayList<QuoteAttribute>(quoteOffer.getQuoteAttributes());

		QuoteOffer duplicate = null;

		try {
			duplicate = (QuoteOffer) BeanUtils.cloneBean(quoteOffer);
			duplicate.setId(null);
			duplicate.setUuid(UUID.randomUUID().toString());
			duplicate.setQuoteVersion(quoteVersion);
			quoteOfferService.detach(quoteOffer);
			String code = cpqQuoteService.findDuplicateCode(quoteOffer);
			duplicate.setCode(code);
			duplicate.setQuotePrices(new ArrayList<QuotePrice>());
			duplicate.setQuoteProduct(new ArrayList<QuoteProduct>());
			duplicate.setQuoteAttributes(new ArrayList<QuoteAttribute>());

			quoteOfferService.create(duplicate);

			duplicateQuoteProduct(quoteProducts,duplicate );
			duplicateQuoteAttribute(quoteAttributes,null, duplicate);
			
		} catch (Exception e) {
			log.error("Error when trying to cloneBean quoteOffer : ", e);
		}

		return duplicate;
    }
}
