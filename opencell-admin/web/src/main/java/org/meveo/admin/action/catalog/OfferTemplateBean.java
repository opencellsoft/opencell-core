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
package org.meveo.admin.action.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ImageUploadEventHandler;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.catalog.ServiceConfigurationDto;
import org.meveo.export.EntityExportImportService;
import org.meveo.export.ExportTemplate;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValueHolder;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.BOMInstantiationParameters;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.primefaces.model.DualListModel;

/**
 * Standard backing bean for {@link OfferTemplate} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components. s
 * 
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @author Said Ramli
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.3
 * 
 */
@Named
@ViewScoped
public class OfferTemplateBean extends CustomFieldBean<OfferTemplate> {

    private static final long serialVersionUID = 1L;

    @Inject
    private SubscriptionService subscriptionService;

    /**
     * Injected @{link OfferTemplate} service. Extends {@link PersistenceService}.
     */
    @Inject
    protected OfferTemplateService offerTemplateService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private BusinessOfferModelService businessOfferModelService;

    @Inject
    private EntityToDtoConverter entityToDtoConverter;

    @Inject
    private EntityExportImportService entityExportImportService;

    @Inject
    private ProductTemplateService productTemplateService;

    private Long bomId;

    private boolean newVersion;
    private boolean duplicateOffer;

    /**
     * These configurations are used to show / hide the checkbox to select the service templates as well as the custom fields for each service.
     */
    private boolean visibleServiceCheckbox;
    private boolean instantiatedFromBom;
    private boolean newVersionFlag;
    private boolean duplicateOfferFlag;

    private DualListModel<ServiceTemplate> incompatibleServices;
    private OfferServiceTemplate offerServiceTemplate;
    private OfferProductTemplate offerProductTemplate;
    private BusinessOfferModel businessOfferModel;
    private List<ProductTemplate> productTemplatesLookup;
    private List<OfferServiceTemplate> sortedOfferServiceTemplates;
    private List<OfferServiceTemplate> bsmServiceTemplates;
    private List<BusinessServiceModel> businessServiceModels;
    private List<BusinessServiceModel> selectedBsms;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public OfferTemplateBean() {
        super(OfferTemplate.class);
    }

    @Override
    public OfferTemplate initEntity() {
        visibleServiceCheckbox = false;
        instantiatedFromBom = false;

        // offer instantiation
        if (bomId != null) {
            duplicateFromBom();

            bomId = null;
            visibleServiceCheckbox = true;
            instantiatedFromBom = true;

        } else if (newVersion) {
            // creates a new version by setting new validity date
            instantiateNewVersion();
            newVersion = false;
            visibleServiceCheckbox = true;
            newVersionFlag = true;

        } else if (duplicateOffer) {
            // duplicate the offer, detach and set id to null
            duplicateAndSave();
            duplicateOffer = false;
            duplicateOfferFlag = true;

        } else {
            visibleServiceCheckbox = true;
            // creates new entity
            super.initEntity();
            this.businessOfferModel = this.entity.getBusinessOfferModel();
        }

        // lazy loading service templates
        for (OfferServiceTemplate ost : entity.getOfferServiceTemplates()) {
            ost.getServiceTemplate().getCode();
            for (ServiceTemplate serviceTemplate : ost.getIncompatibleServices()) {
                serviceTemplate.getCode();
            }
        }

        // lazy loading product templates
        List<ProductTemplate> productTemplates = new ArrayList<>();
        for (OfferProductTemplate opt : entity.getOfferProductTemplates()) {
            productTemplates.add(opt.getProductTemplate());
        }

        productTemplatesLookup = productTemplateService.listByLifeCycleStatus(LifeCycleStatusEnum.ACTIVE);
        productTemplatesLookup.removeAll(productTemplates);

        if (entity.getValidity() == null) {
            entity.setValidity(new DatePeriod());
        }

        return entity;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<OfferTemplate> getPersistenceService() {
        return offerTemplateService;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getFormFieldsToFetch()
     */
    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("offerTemplateCategories", "channels", "businessAccountModels", "customerCategories");
    }

    /**
     * List active offer templates of a current type valid on a given date
     *
     * @param date Date to match validity
     * @return A list of offer templates
     */
    public List<OfferTemplate> listActiveByDate(Date date) {
        List<OfferTemplate> result = new ArrayList<>();
        List<OfferTemplate> listActiveOfferTemplates = listActive();

        for (OfferTemplate offerTemplate : listActiveOfferTemplates) {
            if (offerTemplate.getLifeCycleStatus() == LifeCycleStatusEnum.ACTIVE && (offerTemplate.getValidity() == null ||
                    offerTemplate.getValidity().isCorrespondsToPeriod(date))) {
                result.add(offerTemplate);
            }
        }
        return result;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    public List<ProductTemplate> getProductTemplatesLookup() {
        return productTemplatesLookup;
    }

    public void setProductTemplatesLookup(List<ProductTemplate> productTemplatesLookup) {
        this.productTemplatesLookup = productTemplatesLookup;
    }

    public boolean isDuplicateOffer() {
        return duplicateOffer;
    }

    public void setDuplicateOffer(boolean duplicateOffer) {
        this.duplicateOffer = duplicateOffer;
    }

    @ActionMethod
    public void duplicate() {
        if (entity != null && entity.getId() != null) {
            try {
                offerTemplateService.duplicate(entity, true);
                messages.info(new BundleKey("messages", "duplicate.successfull"));
            } catch (BusinessException e) {
                log.error("Error encountered duplicating offer template entity: {}", entity.getCode(), e);
                messages.error(new BundleKey("messages", "error.duplicate.unexpected"));
            }
        }
    }

    /**
     * @param givenEntity entity to check
     * @return true/false
     */
    public boolean isUsedInSubscription(OfferTemplate givenEntity) {
        return givenEntity != null && !givenEntity.isTransient() && subscriptionService.hasSubscriptions(givenEntity);
    }

    /**
     * delete all entities related to Offer( used only for Marketing Manager)
     * 
     * @param entity offer template entity 
     */
    @ActionMethod
    public void deleteCatalogHierarchy(OfferTemplate entity) {
        if (!this.isUsedInSubscription(entity)) {
            if (entity != null && entity.getId() != null) {
                try {
                    entity = offerTemplateService.refreshOrRetrieve(entity);
                    offerTemplateService.delete(entity);
                    messages.info(new BundleKey("messages", "delete.successful"));
                } catch (BusinessException e) {
                    log.error("Error encountered while deleting offer template entity: {}: {}", entity.getCode(), e);
                    messages.error(new BundleKey("messages", "error.delete.unexpected"));
                }
            }
        } else {
            log.error("This entity is already in subscription: {}", entity.getCode());
            messages.error(new BundleKey("messages", "error.delete.entityUsed "));
        }
    }

    @ActionMethod
    public void instantiateNewVersion() {

        if (getObjectId() != null) {
            OfferTemplate offer = offerTemplateService.findById(getObjectId());
            if (offer != null) {
                try {
                    businessOfferModel = offer.getBusinessOfferModel();
                    entity = offerTemplateService.instantiateNewVersion(offer);

                    setObjectId(null);
                    messages.info(new BundleKey("messages", "newVersion.successful"));
                } catch (BusinessException e) {
                    log.error("Error encountered instantiating new offer template entity version: {}", offer.getCode(), e);
                    messages.error(new BundleKey("messages", "error.newVersion.unsuccessful"));
                }
            }
        }
    }

    public boolean isUsedInSubscription() {
        return isUsedInSubscription(entity);
    }

    /**
     * 
     * @return sorted offer services templates
     */
    public List<OfferServiceTemplate> getSortedOfferServiceTemplates() {
        if (sortedOfferServiceTemplates == null) {
            if (entity != null) {

                // add all offer service templates
                sortedOfferServiceTemplates = initListOfferServiceTemplates();

                // duplication, new version
                if (entity.isTransient() && entity.getBusinessOfferModel() != null) {
                    for (OfferServiceTemplate ostOffer : sortedOfferServiceTemplates) {
                        ostOffer.getServiceTemplate().setSelected(true);
                    }

                    // add services from BOM offer
                    // this is not used anymore?
                    if (!newVersionFlag && !duplicateOfferFlag) {
                        for (OfferServiceTemplate ostBom : entity.getBusinessOfferModel().getOfferTemplate().getOfferServiceTemplates()) {
                            boolean found = false;
                            for (OfferServiceTemplate ostOffer : sortedOfferServiceTemplates) {
                                if (ostOffer.getServiceTemplate().equals(ostBom.getServiceTemplate())) {
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) {
                                sortedOfferServiceTemplates.add(ostBom.duplicate(entity));
                            }
                        }
                    }
                }

                Collections.sort(sortedOfferServiceTemplates, new DescriptionComparator());
            }
        }

        return sortedOfferServiceTemplates;
    }

    /**
     * Inits the list offer service templates to be displayed.
     *
     * @return the list
     */
    private List<OfferServiceTemplate> initListOfferServiceTemplates() {
        
        sortedOfferServiceTemplates = new ArrayList<>();
        List<OfferServiceTemplate> entityLisyServices = entity.getOfferServiceTemplates();
        
        sortedOfferServiceTemplates.addAll(entityLisyServices);
        
        List<String> listEntityOstCodes = new ArrayList<>();
        
        for (OfferServiceTemplate ost : sortedOfferServiceTemplates) {
            ServiceTemplate st =  ost.getServiceTemplate();
            if (!st.isInstantiatedFromBSM()) {
                st.setSelected(!instantiatedFromBom);
                listEntityOstCodes.add(st.getCode());
            }
        }
        
        BusinessOfferModel bom = this.entity.getBusinessOfferModel();
        if (bom != null) {
            OfferTemplate offerTemplate =  bom.getOfferTemplate();
            if (offerTemplate != null) {
                
                String offerIdPrefix = entity.getId() != null ? entity.getId() + "_" : "";
                List<OfferServiceTemplate> bomServiceTemplates =  offerTemplate.getOfferServiceTemplates();
                
                for (OfferServiceTemplate ost : bomServiceTemplates) {
                    ServiceTemplate st =  ost.getServiceTemplate(); 
                    // Consider the Service template if not already added :
                    if (!st.isInstantiatedFromBSM() && !listEntityOstCodes.stream().anyMatch(ostCode -> ostCode.startsWith(offerIdPrefix+st.getCode()))) {
                        sortedOfferServiceTemplates.add(ost);
                    }     
                }
            }
        }
        return sortedOfferServiceTemplates;
    }
    
    
    

    public void resortOfferServiceTemplates() {
        if (sortedOfferServiceTemplates != null && !sortedOfferServiceTemplates.isEmpty()) {
            // Collections.sort(sortedOfferServiceTemplates, new DescriptionComparator());
        }
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        // Instantiating a new offer from BOM by using the data (only the data) entered in offer
        // template that was duplicated in initEntity() method
        if (instantiatedFromBom) {
            Map<String, List<CustomFieldValue>> offerCfValues = customFieldDataEntryBean.getFieldValueHolderByUUID(entity.getUuid()).getValuesByCode();
            CustomFieldsDto offerCfs = entityToDtoConverter.getCustomFieldsDTO(entity, offerCfValues, CustomFieldInheritanceEnum.INHERIT_NONE);

            List<ServiceConfigurationDto> servicesConfigurations = new ArrayList<>();
            // process the services
            int itemIndex = 1;
            for (OfferServiceTemplate ost : getSortedOfferServiceTemplates()) {
                ServiceTemplate st = ost.getServiceTemplate();
                if (st.isSelected()) {
                    ServiceConfigurationDto serviceConfigurationDto = toServiceConfigurationDto(ost, st);
                    serviceConfigurationDto.setItemIndex(itemIndex++);
                    servicesConfigurations.add(serviceConfigurationDto);
                    }
                }

            List<ServiceConfigurationDto> productsConfigurations = new ArrayList<>();
            // process products
            for (OfferProductTemplate opt : entity.getOfferProductTemplates()) {
                ProductTemplate pt = opt.getProductTemplate();
                ServiceConfigurationDto serviceConfigurationDto = new ServiceConfigurationDto();
                serviceConfigurationDto.setCode(pt.getCode());
                serviceConfigurationDto.setDescription(pt.getDescription());
                productsConfigurations.add(serviceConfigurationDto);
            }

            businessOfferModel = businessOfferModelService.refreshOrRetrieve(businessOfferModel);

            BOMInstantiationParameters bomParams = new BOMInstantiationParameters();

            bomParams.setBusinessOfferModel(businessOfferModel);
            bomParams.setCustomFields(offerCfs != null ? offerCfs.getCustomField() : null);
            bomParams.setCode(entity.getCode());
            bomParams.setName(entity.getName());
            bomParams.setOfferDescription(entity.getDescription());
            bomParams.setServiceCodes(servicesConfigurations);
            bomParams.setProductCodes(productsConfigurations);
            bomParams.setChannels(entity.getChannels());
            bomParams.setBams(entity.getBusinessAccountModels());
            bomParams.setOfferTemplateCategories(entity.getOfferTemplateCategories());
            bomParams.setLifeCycleStatusEnum(entity.getLifeCycleStatus());
            bomParams.setImagePath(entity.getImagePath());
            bomParams.setValidFrom(entity.getValidity() != null ? entity.getValidity().getFrom() : null);
            bomParams.setValidTo(entity.getValidity() != null ? entity.getValidity().getTo() : null);
            bomParams.setDescriptionI18n(entity.getDescriptionI18n());
            bomParams.setLongDescription(entity.getLongDescription());
            bomParams.setLongDescriptionI18n(entity.getLongDescriptionI18n());
            bomParams.setOfferCfValue(offerCfValues);

            businessOfferModelService.instantiateFromBOM(bomParams);

            if (entity.getImagePath() != null) {
                try {
                    ImageUploadEventHandler<OfferTemplate> imageUploadEventHandler = new ImageUploadEventHandler<>(currentUser.getProviderCode());
                    imageUploadEventHandler.deleteImage(entity);
                } catch (IOException e) {
                    log.error("Failed deleting image file", e);
                }
            }

            return back();

        } else {
            boolean isNewEntity = (entity.getId() == null);

            String outcome = super.saveOrUpdate(killConversation);

            if (outcome != null) {

                if (outcome.equals("mm_offers")) {
                    if (isNewEntity) {
                        entity.getOfferServiceTemplates().clear();
                        for (OfferServiceTemplate ostGui : sortedOfferServiceTemplates) {
                            if (ostGui.getServiceTemplate().isSelected()) {
                                entity.addOfferServiceTemplate(ostGui);
                            }
                        }

                        entity = offerTemplateService.update(entity);
                    } else {
                        // update the entity's OfferServiceTemplates in case of editing an offer : 
                        this.updateEntityOfferServiceTemplates();
                    }

                    // populate service custom fields
                    for (OfferServiceTemplate ost : entity.getOfferServiceTemplates()) {
                        ServiceTemplate serviceTemplate = ost.getServiceTemplate();
                        CustomFieldValueHolder cfValueHolder = customFieldDataEntryBean.getFieldValueHolderByUUID(serviceTemplate.getUuid());
                        if (cfValueHolder != null) {
                            Map<String, List<CustomFieldValue>> stCustomFieldInstances = cfValueHolder.getValuesByCode();
                        if (stCustomFieldInstances != null) {
                            // populate offer cf
                            customFieldDataEntryBean.saveCustomFieldsToEntity(serviceTemplate, serviceTemplate.getUuid(), true, false, false);
                            serviceTemplate = serviceTemplateService.update(serviceTemplate);
                        }
                    }
                }
                }

                return (isNewEntity && !outcome.equals("mm_offers")) ? getEditViewName() : outcome;
            }
        }

        return null;
    }

    private ServiceConfigurationDto toServiceConfigurationDto(OfferServiceTemplate ost, ServiceTemplate st) throws BusinessException {
        ServiceConfigurationDto serviceConfigurationDto = new ServiceConfigurationDto();
        
        Map<String, List<CustomFieldValue>> stCfValues = customFieldDataEntryBean.saveCustomFieldsToEntity(null, st.getUuid(), false, true, false);
        serviceConfigurationDto.setCode(st.getCode());
        serviceConfigurationDto.setDescription(st.getDescription());
        serviceConfigurationDto.setMandatory(ost.isMandatory());
        serviceConfigurationDto.setInstantiatedFromBSM(st.isInstantiatedFromBSM());
        serviceConfigurationDto.setImagePath(st.getImagePath());
        if (stCfValues != null) {
            serviceConfigurationDto.setCfValues(stCfValues);
        }
        return serviceConfigurationDto;
    }

    
    /**
     * Update the entity offer service templates by the selected services from the GUI
     * @throws BusinessException 
     */
    private void updateEntityOfferServiceTemplates() throws BusinessException {

        if (CollectionUtils.isNotEmpty(this.sortedOfferServiceTemplates)) {
            BusinessOfferModel bom = this.businessOfferModelService.retrieveIfNotManaged(this.entity.getBusinessOfferModel());
            
            List<ServiceConfigurationDto> listOstServiceConfig = new ArrayList<>();
            List<OfferServiceTemplate> entityOstList =  this.entity.getOfferServiceTemplates();
            
            for (OfferServiceTemplate ost : this.sortedOfferServiceTemplates) {
                ServiceTemplate st =  ost.getServiceTemplate();
                if (st.isSelected() && (st.isInstantiatedFromBSM() || ost.getOfferTemplate().getId() != this.entity.getId())) {
                    // new service selected from bom
                    listOstServiceConfig.add(toServiceConfigurationDto(ost, st));
                } else if (!st.isSelected() && entityOstList.contains(ost)) {
                    entityOstList.remove(ost);
                }
            } 
            // creating the selected OfferServiceTemplate elements : 
            if (CollectionUtils.isNotEmpty(listOstServiceConfig)) {
                if (bom != null) {
                    List<OfferServiceTemplate> newOstFromBomList = businessOfferModelService.instantiateServiceTemplate(this.entity.getId() + "_", bom.getOfferTemplate(), this.entity,listOstServiceConfig, bom);
                    if (CollectionUtils.isNotEmpty(newOstFromBomList)) { // adding the new OfferServiceTemplate list to the entity : 
                        for (OfferServiceTemplate newOst : newOstFromBomList) {
                            this.entity.addOfferServiceTemplate(newOst);
                        }
                    }
                }
            }
        }
    }

    @ActionMethod
    public void saveOfferServiceTemplate() {
        log.info("saveOfferServiceTemplate getObjectId={}", getObjectId());

        try {
            ServiceTemplate serviceTemplate = null;
            if (offerServiceTemplate != null && offerServiceTemplate.getServiceTemplate() == null) {
                messages.error(new BundleKey("messages", "save.unsuccessful"));
            }

            if (offerServiceTemplate != null) {
                serviceTemplate = offerServiceTemplate.getServiceTemplate();
                offerServiceTemplate.setIncompatibleServices(serviceTemplateService.refreshOrRetrieve(incompatibleServices.getTarget()));
            }

            if (offerServiceTemplate != null && offerServiceTemplate.getId() != null) {
                messages.info(new BundleKey("messages", "offerTemplate.serviceTemplate.update.successful"));

            } else {

                // Validate that such service was not added earlier
                if (serviceTemplate != null && entity.containsServiceTemplate(serviceTemplate)) {
                    messages.error(new BundleKey("messages", "offerTemplate.alreadyContainsService"), serviceTemplate.getDescriptionOrCode());
                    return;
                }
                if (offerServiceTemplate != null) {
                    offerServiceTemplate.setOfferTemplate(entity);
                }
                entity.addOfferServiceTemplate(offerServiceTemplate);
                messages.info(new BundleKey("messages", "offerTemplate.serviceTemplate.create.successful"));
            }

        } catch (Exception e) {
            log.error("exception when saving offer service template !", e.getMessage());
            messages.error(new BundleKey("messages", "save.unsuccessful"));
        }

        offerServiceTemplate = null;
    }

    @ActionMethod
    public void deleteOfferServiceTemplate(OfferServiceTemplate offerServiceTemplate) {
        try {

            entity.getOfferServiceTemplates().remove(offerServiceTemplate);
            this.offerServiceTemplate = null;

            messages.info(new BundleKey("messages", "offerTemplate.serviceTemplate.delete.successful"));

        } catch (Exception e) {
            messages.error(new BundleKey("messages", "error.delete.unexpected"));
        }
    }

    @ActionMethod
    public void deleteOfferProductTemplate(OfferProductTemplate offerProductTemplate) throws BusinessException {
        try {
            entity.getOfferProductTemplates().remove(offerProductTemplate);
            this.offerProductTemplate = null;

            messages.info(new BundleKey("messages", "offerTemplate.productTemplate.delete.successful"));

        } catch (Exception e) {
            messages.error(new BundleKey("messages", "error.delete.unexpected"));
        }
    }

    public void editOfferServiceTemplate(OfferServiceTemplate offerServiceTemplate) {
        this.offerServiceTemplate = offerServiceTemplate;
        setIncompatibleServices(null);
    }

    public void cancelOfferServiceTemplateEdit() {
        this.offerServiceTemplate = null;
    }

    public void newOfferProductTemplate() {
        this.offerProductTemplate = new OfferProductTemplate();
    }

    public void editOfferProductTemplate(OfferProductTemplate offerProductTemplate) {
        this.offerProductTemplate = offerProductTemplate;
    }

    public void cancelOfferProductTemplateEdit() {
        this.offerProductTemplate = null;
    }

    @ActionMethod
    public void saveOfferProductTemplate() {
        log.info("saveOfferProductTemplate getObjectId={}", getObjectId());

        try {
            ProductTemplate productTemplate = null;
            if (offerProductTemplate != null && offerProductTemplate.getProductTemplate() == null) {
                messages.error(new BundleKey("messages", "save.unsuccessful"));
            }

            if (offerProductTemplate != null && offerProductTemplate.getId() != null) {
                messages.info(new BundleKey("messages", "offerTemplate.productTemplate.update.successful"));

            } 
            
            if (offerProductTemplate != null) {
                productTemplate = offerProductTemplate.getProductTemplate();
                // Validate that such service was not added earlier
                if (productTemplate != null && entity.containsProductTemplate(productTemplate)) {
                    messages.error(new BundleKey("messages", "offerTemplate.alreadyContainsProduct"), productTemplate.getDescriptionOrCode());
                    return;
                }

                offerProductTemplate.setOfferTemplate(entity);
                entity.addOfferProductTemplate(offerProductTemplate);
                messages.info(new BundleKey("messages", "offerTemplate.productTemplate.create.successful"));
            }

        } catch (Exception e) {
            log.error("Failed to save offer product template !", e.getMessage());
            messages.error(new BundleKey("messages", "save.unsuccessful"));
        }

        offerProductTemplate = null;
    }

    public void newOfferServiceTemplate() {
        this.offerServiceTemplate = new OfferServiceTemplate();
        this.incompatibleServices = null;
    }

    public OfferServiceTemplate getOfferServiceTemplate() {
        return offerServiceTemplate;
    }

    public void setOfferServiceTemplate(OfferServiceTemplate offerServiceTemplate) {
        this.offerServiceTemplate = offerServiceTemplate;
    }

    public DualListModel<ServiceTemplate> getIncompatibleServices() {

        if (incompatibleServices == null) {
            List<ServiceTemplate> source = null;
            if (offerServiceTemplate == null || offerServiceTemplate.isTransient()) {
                source = serviceTemplateService.listActive();
            } else {
                source = serviceTemplateService.listAllActiveExcept(offerServiceTemplate.getServiceTemplate());
            }

            List<ServiceTemplate> target = new ArrayList<ServiceTemplate>();

            if (offerServiceTemplate != null && offerServiceTemplate.getIncompatibleServices() != null && offerServiceTemplate.getIncompatibleServices().size() > 0) {
                target.addAll(offerServiceTemplate.getIncompatibleServices());
            }
            source.removeAll(target);
            incompatibleServices = new DualListModel<ServiceTemplate>(source, target);
        }
        return incompatibleServices;
    }

    public void setIncompatibleServices(DualListModel<ServiceTemplate> incompatibleServices) {
        this.incompatibleServices = incompatibleServices;
    }

    public OfferProductTemplate getOfferProductTemplate() {
        return offerProductTemplate;
    }

    public void setOfferProductTemplate(OfferProductTemplate offerProductTemplate) {
        this.offerProductTemplate = offerProductTemplate;
    }

    public Long getBomId() {
        return bomId;
    }

    public void setBomId(Long bomId) {
        this.bomId = bomId;
    }

    public void setNewVersion(boolean newVersion) {
        this.newVersion = newVersion;
    }

    public boolean isNewVersion() {
        return newVersion;
    }

    public List<OfferServiceTemplate> getBsmServiceTemplates() {
        return bsmServiceTemplates;
    }

    public void setBsmServiceTemplates(List<OfferServiceTemplate> bsmServiceTemplates) {
        this.bsmServiceTemplates = bsmServiceTemplates;
    }

    public ExportTemplate getMarketingCatalogExportTemplate() {
        return entityExportImportService.getExportImportTemplate("Offers");
    }

    public BusinessOfferModel getBusinessOfferModel() {
        return businessOfferModel;
    }

    public void setBusinessOfferModel(BusinessOfferModel businessOfferModel) {
        this.businessOfferModel = businessOfferModel;
    }

    public void onNameChange() {
        if (StringUtils.isEmpty(entity.getCode())) {
            entity.setCode(entity.getName());
        }
    }

    public void onCodeChange() {
        if (StringUtils.isEmpty(entity.getName())) {
            entity.setName(entity.getCode());
        }
    }

    @ActionMethod
    public void addProductTemplateToOffer(ProductTemplate productTemplate) {

        if (entity.containsProductTemplate(productTemplate)) {
            messages.error(new BundleKey("messages", "offerTemplate.alreadyContainsProduct"), productTemplate.getDescriptionOrCode());
            return;
        }

        OfferProductTemplate opt = new OfferProductTemplate();
        opt.setProductTemplate(productTemplate);
        opt.setOfferTemplate(entity);

        entity.addOfferProductTemplate(opt);

        messages.info(new BundleKey("messages", "offerTemplate.productTemplate.create.successful"));
    }

    public boolean displayStatus(OfferTemplate offer) {

        if ((Arrays.asList(LifeCycleStatusEnum.ACTIVE, LifeCycleStatusEnum.LAUNCHED, LifeCycleStatusEnum.IN_TEST).contains(offer.getLifeCycleStatus()))) {
            return offer.getValidity() == null || offer.getValidity().isCorrespondsToPeriod(new Date());
        }

        return false;
    }

    public boolean validateUniqueVersion(FacesContext context, List<UIInput> components, List<Object> values) {

        if (values.size() != 3) {
            throw new RuntimeException("Please bind validator to two components in the following order: offer/product/bundle template code, dateFrom, dateTo");
        }

        String code = (String) values.get(0);
        Date from = (Date) values.get(1);
        Date to = (Date) values.get(2);

        List<ProductOffering> matchedVersions = offerTemplateService.getMatchingVersions(code, from, to, entity.getId(), true);

        if (!matchedVersions.isEmpty()) {
            messages.error(new BundleKey("messages", "offerTemplate.version.exists"),
                matchedVersions.get(0).getValidity() == null ? " / " : matchedVersions.get(0).getValidity().toString(paramBeanFactory.getInstance().getDateFormat()));
            return false;
        }

        return true;
    }

    private void duplicateAndSave() {

        if (getObjectId() != null) {
            OfferTemplate offer = offerTemplateService.findById(getObjectId());
            if (offer != null) {
                try {
                    entity = offerTemplateService.duplicate(offer, true, true, false);
                    businessOfferModel = entity.getBusinessOfferModel();
                    setObjectId(null);

                    messages.info(new BundleKey("messages", "message.duplicate.ok"));

                } catch (BusinessException e) {
                    log.error("Error encountered while duplicating an offer template: {}", offer.getCode(), e);
                    messages.error(new BundleKey("messages", "message.duplicate.ok"));
                }
            }
        }
    }

    private void duplicateFromBom() {
        try {

            businessOfferModel = businessOfferModelService.findById(bomId);
            OfferTemplate offer = businessOfferModel.getOfferTemplate();

            businessOfferModelService.detach(businessOfferModel);

            String code = offer.getCode();

            entity = offerTemplateService.duplicate(offer, false);
            // Preserve the offer template original code
            entity.setCode(code);
            // the new offer should be in design
            entity.setLifeCycleStatus(LifeCycleStatusEnum.IN_DESIGN);
            setObjectId(null);

        } catch (BusinessException e) {
            log.error("Error encountered while duplicating offer template from BOM: {}", bomId, e);
        }
    }

    public void instantiateBSM() {
        if (selectedBsms != null && !selectedBsms.isEmpty()) {
            for (BusinessServiceModel bsm : selectedBsms) {
                OfferServiceTemplate ost = new OfferServiceTemplate();
                ServiceTemplate stSource = bsm.getServiceTemplate();
                stSource = serviceTemplateService.refreshOrRetrieve(stSource);

                ServiceTemplate stTarget = new ServiceTemplate();
                stTarget.setCode(stSource.getCode());
                stTarget.setDescription(stSource.getDescription() + " (" + bsm.getDescriptionOrCode() + ")");
                stTarget.setSelected(true);
                stTarget.clearUuid();
                stTarget.clearCfValues();
                stTarget.setInstantiatedFromBSM(true);

                ost.setServiceTemplate(stTarget);
                ost.setOfferTemplate(entity);

                sortedOfferServiceTemplates.add(ost);
            }
        }
    }

    public void resetBsm() {
        sortedOfferServiceTemplates = null;
    }

    public boolean isVisibleServiceCheckbox() {
        return visibleServiceCheckbox;
    }

    public void setVisibleServiceCheckbox(boolean visibleServiceCheckbox) {
        this.visibleServiceCheckbox = visibleServiceCheckbox;
    }

    public boolean isInstantiatedFromBom() {
        return instantiatedFromBom;
    }

    public void setInstantiatedFromBom(boolean instantiatedFromBom) {
        this.instantiatedFromBom = instantiatedFromBom;
    }

    public boolean isNewVersionFlag() {
        return newVersionFlag;
    }

    public void setNewVersionFlag(boolean newVersionFlag) {
        this.newVersionFlag = newVersionFlag;
    }

    public List<BusinessServiceModel> getBusinessServiceModels() {
        if (businessOfferModel != null && businessServiceModels == null) {
            businessServiceModels = businessOfferModelService.getBusinessServiceModels(businessOfferModel);
        }

        return businessServiceModels;
    }

    public void setBusinessServiceModels(List<BusinessServiceModel> businessServiceModels) {
        this.businessServiceModels = businessServiceModels;
    }

    public List<BusinessServiceModel> getSelectedBsms() {
        return selectedBsms;
    }

    public void setSelectedBsms(List<BusinessServiceModel> selectedBsms) {
        this.selectedBsms = selectedBsms;
    }

    public boolean isDuplicateOfferFlag() {
        return duplicateOfferFlag;
    }

    public void setDuplicateOfferFlag(boolean duplicateOfferFlag) {
        this.duplicateOfferFlag = duplicateOfferFlag;
    }

}