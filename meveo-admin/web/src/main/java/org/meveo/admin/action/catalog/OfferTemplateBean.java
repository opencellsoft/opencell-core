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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.catalog.ServiceConfigurationDto;
import org.meveo.export.EntityExportImportService;
import org.meveo.export.ExportTemplate;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.catalog.impl.ChannelService;
import org.meveo.service.catalog.impl.OfferProductTemplateService;
import org.meveo.service.catalog.impl.OfferServiceTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.BusinessAccountModelService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;

/**
 * Standard backing bean for {@link OfferTemplate} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components. s
 * 
 */
@Named
@ViewScoped
public class OfferTemplateBean extends CustomFieldBean<OfferTemplate> {

	private static final long serialVersionUID = 1L;

	@Inject
	private SubscriptionService subscriptionService;

	/**
	 * Injected @{link OfferTemplate} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	protected OfferTemplateService offerTemplateService;

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	protected CustomFieldInstanceService customFieldInstanceService;

	@Inject
	private OfferServiceTemplateService offerServiceTemplateService;

	@Inject
	private OfferProductTemplateService offerProductTemplateService;

	@Inject
	private BusinessOfferModelService businessOfferModelService;

	@Inject
	private EntityToDtoConverter entityToDtoConverter;

	@Inject
	private ChannelService channelService;

	@Inject
	private BusinessAccountModelService businessAccountModelService;

	@Inject
	private OfferTemplateCategoryService offerTemplateCategoryService;

	@Inject
	private EntityExportImportService entityExportImportService;

	private Long bomId;

	private DualListModel<ServiceTemplate> incompatibleServices;
	private DualListModel<BusinessAccountModel> businessAccountModelsDM;
	private DualListModel<Channel> channelsDM;
	private DualListModel<OfferTemplateCategory> offerTemplateCategoriesDM;
	private OfferServiceTemplate offerServiceTemplate = new OfferServiceTemplate();
	private OfferProductTemplate offerProductTemplate = new OfferProductTemplate();
	private UploadedFile uploadedFile;
	private BusinessOfferModel businessOfferModel;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public OfferTemplateBean() {
		super(OfferTemplate.class);
	}

	@Override
	public OfferTemplate initEntity() {
		OfferTemplate result = super.initEntity();

		if (bomId != null) {
			businessOfferModel = businessOfferModelService.findById(bomId);
		}

		return result;
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
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider", "offerTemplateCategories", "channels", "businessAccountModels");
	}

	public List<OfferTemplate> listActive() {
		Map<String, Object> filters = getFilters();
		filters.put("disabled", false);
		PaginationConfiguration config = new PaginationConfiguration(filters);

		return offerTemplateService.list(config);
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}

	@ActionMethod
	public void duplicate() {
		if (entity != null && entity.getId() != null) {
			try {
				offerTemplateService.duplicate(entity, getCurrentUser());
				messages.info(new BundleKey("messages", "save.successful"));
			} catch (BusinessException e) {
				log.error("Error encountered persisting offer template entity: {}: {}", entity.getCode(), e);
				messages.error(new BundleKey("messages", "save.unsuccessful"));
			}
		}
	}

	public boolean isUsedInSubscription() {
		return (getEntity() != null && !getEntity().isTransient() && (subscriptionService.findByOfferTemplate(getEntity()) != null) && subscriptionService.findByOfferTemplate(
				getEntity()).size() > 0) ? true : false;
	}

	@Override
	@ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		if (bomId != null && businessOfferModel != null) {
			Map<String, List<CustomFieldInstance>> customFieldInstances = customFieldDataEntryBean.getFieldValueHolderByUUID(entity.getUuid()).getValues();
			CustomFieldsDto cfsDto = entityToDtoConverter.getCustomFieldsDTO(entity, customFieldInstances);

			List<ServiceConfigurationDto> servicesConfigurations = new ArrayList<>();
			// process the services
			for (OfferServiceTemplate ost : entity.getOfferServiceTemplates()) {
				ServiceTemplate st = ost.getServiceTemplate();
				if (st.isSelected()) {
					Map<String, List<CustomFieldInstance>> stCustomFieldInstances = customFieldDataEntryBean.getFieldValueHolderByUUID(st.getUuid()).getValues();
					CustomFieldsDto stCfsDto = entityToDtoConverter.getCustomFieldsDTO(st, stCustomFieldInstances);

					ServiceConfigurationDto serviceConfigurationDto = new ServiceConfigurationDto();
					serviceConfigurationDto.setCode(st.getCode());
					serviceConfigurationDto.setDescription(st.getDescription());
					servicesConfigurations.add(serviceConfigurationDto);
					if (stCfsDto != null) {
						serviceConfigurationDto.setCustomFields(stCfsDto.getCustomField());
					}
				}
			}

			OfferTemplate newOfferTemplate = businessOfferModelService.createOfferFromBOM(businessOfferModel, cfsDto != null ? cfsDto.getCustomField() : null,
					offerTemplateService.findDuplicateCode(entity, " - Instance", currentUser), entity.getName(), entity.getDescription(), servicesConfigurations,
					entity.getChannels(), entity.getBusinessAccountModels(), entity.getOfferTemplateCategories(), currentUser);

			// populate service custom fields
			for (OfferServiceTemplate ost : entity.getOfferServiceTemplates()) {
				ServiceTemplate serviceTemplate = ost.getServiceTemplate();
				if (serviceTemplate.isSelected()) {
					for (OfferServiceTemplate newOst : newOfferTemplate.getOfferServiceTemplates()) {
						ServiceTemplate newServiceTemplate = newOst.getServiceTemplate();
						String serviceTemplateCode = newOfferTemplate.getId() + "_" + serviceTemplate.getCode();
						if (serviceTemplateCode.equals(newServiceTemplate.getCode())) {
							Map<String, List<CustomFieldInstance>> stCustomFieldInstances = customFieldDataEntryBean.getFieldValueHolderByUUID(serviceTemplate.getUuid())
									.getValues();
							if (stCustomFieldInstances != null) {
								// populate offer cf
								customFieldDataEntryBean.saveCustomFieldsToEntity(newServiceTemplate, serviceTemplate.getUuid(), true, false);
								break;
							}
						}
					}
				}
			}

			// populate offer cf
			customFieldDataEntryBean.saveCustomFieldsToEntity(newOfferTemplate, entity.getUuid(), true, false);

			return back();
		} else {
			boolean newEntity = (entity.getId() == null);

			if (channelsDM != null && (channelsDM.getSource() != null || channelsDM.getTarget() != null)) {
				entity.getChannels().clear();
				entity.getChannels().addAll(channelService.refreshOrRetrieve(channelsDM.getTarget()));
			}
			if (businessAccountModelsDM != null && (businessAccountModelsDM.getSource() != null || businessAccountModelsDM.getTarget() != null)) {
				entity.getBusinessAccountModels().clear();
				entity.getBusinessAccountModels().addAll(businessAccountModelService.refreshOrRetrieve(businessAccountModelsDM.getTarget()));
			}
			if (offerTemplateCategoriesDM != null && (offerTemplateCategoriesDM.getSource() != null || offerTemplateCategoriesDM.getTarget() != null)) {
				entity.getOfferTemplateCategories().clear();
				entity.getOfferTemplateCategories().addAll(offerTemplateCategoryService.refreshOrRetrieve(offerTemplateCategoriesDM.getTarget()));
			}

			String outcome = super.saveOrUpdate(killConversation);

			if (outcome != null) {

				if (outcome.equals("mm_offers")) {
					// populate service custom fields
					for (OfferServiceTemplate ost : entity.getOfferServiceTemplates()) {
						ServiceTemplate serviceTemplate = ost.getServiceTemplate();
						Map<String, List<CustomFieldInstance>> stCustomFieldInstances = customFieldDataEntryBean.getFieldValueHolderByUUID(serviceTemplate.getUuid()).getValues();
						if (stCustomFieldInstances != null) {
							// populate offer cf
							customFieldDataEntryBean.saveCustomFieldsToEntity(serviceTemplate, serviceTemplate.getUuid(), true, false);
						}
					}
				}

				return (newEntity && !outcome.equals("mm_offers")) ? getEditViewName() : outcome;
			}
		}

		return null;
	}

	public void saveOfferServiceTemplate() {
		log.info("saveOfferServiceTemplate getObjectId={}", getObjectId());

		try {
			if (offerServiceTemplate != null && offerServiceTemplate.getServiceTemplate() == null) {
				messages.error(new BundleKey("messages", "save.unsuccessful"));
			}
			offerServiceTemplate.setIncompatibleServices(serviceTemplateService.refreshOrRetrieve(incompatibleServices.getTarget()));
			if (offerServiceTemplate.getId() != null) {
				offerServiceTemplate = offerServiceTemplateService.update(offerServiceTemplate, getCurrentUser());
				entity = getPersistenceService().refreshOrRetrieve(entity);
				messages.info(new BundleKey("messages", "update.successful"));

			} else {
				offerServiceTemplate.setOfferTemplate(entity);
				offerServiceTemplateService.create(offerServiceTemplate, getCurrentUser());
				entity.addOfferServiceTemplate(offerServiceTemplate);
				entity = getPersistenceService().update(entity, getCurrentUser());
				messages.info(new BundleKey("messages", "save.successful"));
			}

			offerServiceTemplate.getIncompatibleServices().clear();
			offerServiceTemplate.getIncompatibleServices().addAll(serviceTemplateService.refreshOrRetrieve(incompatibleServices.getTarget()));

		} catch (Exception e) {
			log.error("exception when saving offer service template !", e.getMessage());
			messages.error(new BundleKey("messages", "save.unsuccessful"));
		}

		newOfferServiceTemplate();
	}

    @ActionMethod
    public void deleteOfferServiceTemplate(OfferServiceTemplate offerServiceTemplate) {
        try {
            entity.getOfferServiceTemplates().remove(offerServiceTemplate);
            entity = getPersistenceService().update(entity, getCurrentUser());
            offerServiceTemplateService.remove(offerServiceTemplate.getId(), getCurrentUser());
            messages.info(new BundleKey("messages", "delete.successful"));

            newOfferServiceTemplate();

        } catch (Exception e) {
            messages.error(new BundleKey("messages", "error.delete.unexpected"));
        }
	}

	public void deleteOfferProductTemplate(OfferProductTemplate offerProductTemplate) throws BusinessException {
		entity.getOfferProductTemplates().remove(offerProductTemplate);
		messages.info(new BundleKey("messages", "delete.successful"));

		newOfferProductTemplate();
	}

	public void editOfferServiceTemplate(OfferServiceTemplate offerServiceTemplate) {
		this.offerServiceTemplate = offerServiceTemplate;
		setIncompatibleServices(null);
	}

	public void newOfferProductTemplate() {
		this.offerProductTemplate = new OfferProductTemplate();
	}

	public void editOfferProductTemplate(OfferProductTemplate offerProductTemplate) {
		this.offerProductTemplate = offerProductTemplate;
	}

	public void saveOfferProductTemplate() {
		log.info("saveOfferProductTemplate getObjectId={}", getObjectId());

		try {
			if (offerProductTemplate != null && offerProductTemplate.getProductTemplate() == null) {
				messages.error(new BundleKey("messages", "save.unsuccessful"));
			}

			if (offerProductTemplate.getId() != null) {
				offerProductTemplate = offerProductTemplateService.update(offerProductTemplate, getCurrentUser());
				entity = getPersistenceService().refreshOrRetrieve(entity);
				messages.info(new BundleKey("messages", "update.successful"));

			} else {
				offerProductTemplate.setOfferTemplate(entity);
				offerProductTemplateService.create(offerProductTemplate, getCurrentUser());
				entity.addOfferProductTemplate(offerProductTemplate);
				entity = getPersistenceService().update(entity, getCurrentUser());
				messages.info(new BundleKey("messages", "save.successful"));
			}

		} catch (Exception e) {
			log.error("exception when saving offer product template !", e.getMessage());
			messages.error(new BundleKey("messages", "save.unsuccessful"));
		}

		newOfferProductTemplate();
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
				source = serviceTemplateService.listAllActiveExcept(offerServiceTemplate.getServiceTemplate(), getCurrentProvider());
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

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public void handleFileUpload(FileUploadEvent event) throws BusinessException {
		uploadedFile = event.getFile();

		if (uploadedFile != null) {
			byte[] contents = uploadedFile.getContents();
			try {
				entity.setImage(new SerialBlob(contents));
			} catch (SQLException e) {
				entity.setImage(null);
			}
			entity.setImageContentType(uploadedFile.getContentType());

			saveOrUpdate(entity);

			initEntity();

			messages.info(new BundleKey("messages", "message.upload.succesful"));
		}
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

	public DualListModel<BusinessAccountModel> getBusinessAccountModelsDM() {
		if (businessAccountModelsDM == null) {
			List<BusinessAccountModel> perksSource = null;
			if (entity != null && entity.getProvider() != null) {
				perksSource = businessAccountModelService.listInstalled(entity.getProvider());
			} else {
				perksSource = businessAccountModelService.listInstalled(currentUser.getProvider());
			}

			List<BusinessAccountModel> perksTarget = new ArrayList<BusinessAccountModel>();
			if (entity.getBusinessAccountModels() != null) {
				perksTarget.addAll(entity.getBusinessAccountModels());
			}

			perksSource.removeAll(perksTarget);
			businessAccountModelsDM = new DualListModel<BusinessAccountModel>(perksSource, perksTarget);
		}

		return businessAccountModelsDM;
	}

	public void setBusinessAccountModelsDM(DualListModel<BusinessAccountModel> businessAccountModelsDM) {
		this.businessAccountModelsDM = businessAccountModelsDM;
	}

	public DualListModel<Channel> getChannelsDM() {
		if (channelsDM == null) {
			List<Channel> perksSource = null;
			if (entity != null && entity.getProvider() != null) {
				perksSource = channelService.list(entity.getProvider());
			} else {
				perksSource = channelService.list(currentUser.getProvider());
			}

			List<Channel> perksTarget = new ArrayList<Channel>();
			if (entity.getChannels() != null) {
				perksTarget.addAll(entity.getChannels());
			}

			perksSource.removeAll(perksTarget);
			channelsDM = new DualListModel<Channel>(perksSource, perksTarget);
		}

		return channelsDM;
	}

	public void setChannelsDM(DualListModel<Channel> channelsDM) {
		this.channelsDM = channelsDM;
	}

	public DualListModel<OfferTemplateCategory> getOfferTemplateCategoriesDM() {
		if (offerTemplateCategoriesDM == null) {
			List<OfferTemplateCategory> perksSource = null;
			if (entity != null && entity.getProvider() != null) {
				perksSource = offerTemplateCategoryService.list(entity.getProvider());
			} else {
				perksSource = offerTemplateCategoryService.list(currentUser.getProvider());
			}

			List<OfferTemplateCategory> perksTarget = new ArrayList<OfferTemplateCategory>();
			if (entity.getOfferTemplateCategories() != null) {
				perksTarget.addAll(entity.getOfferTemplateCategories());
			}

			perksSource.removeAll(perksTarget);
			offerTemplateCategoriesDM = new DualListModel<OfferTemplateCategory>(perksSource, perksTarget);
		}

		return offerTemplateCategoriesDM;
	}

	public void setOfferTemplateCategoriesDM(DualListModel<OfferTemplateCategory> offerTemplateCategoriesDM) {
		this.offerTemplateCategoriesDM = offerTemplateCategoriesDM;
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

	public void addProductTemplateToOffer(ProductTemplate prodTemplate) {
		boolean found = false;
		for (OfferProductTemplate opt : entity.getOfferProductTemplates()) {
			if (prodTemplate.equals(opt.getProductTemplate())) {
				found = true;
			}
		}

		if (!found) {
			OfferProductTemplate opt = new OfferProductTemplate();
			opt.setProductTemplate(prodTemplate);
			opt.setOfferTemplate(entity);

			try {
				entity.addOfferProductTemplate(opt);
				entity = getPersistenceService().update(entity, currentUser);
			} catch (BusinessException e) {
				log.error("IPIEL: fail creating opt {}", e.getMessage());
				messages.error(e.getMessage());
			}
		}
	}

	public void removeProductTemplateFromOffer(OfferProductTemplate offerProductTemplate) throws BusinessException {
		entity.getOfferProductTemplates().remove(offerProductTemplate);
		entity = getPersistenceService().update(entity, getCurrentUser());

		messages.info(new BundleKey("messages", "delete.successful"));
	}
}
