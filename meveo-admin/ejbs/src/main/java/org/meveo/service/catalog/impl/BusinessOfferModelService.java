package org.meveo.service.catalog.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ImageUploadEventHandler;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.catalog.ServiceConfigurationDto;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.Provider;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.service.admin.impl.GenericModuleService;
import org.meveo.service.script.offer.OfferModelScriptService;
import org.meveo.service.script.service.ServiceModelScriptService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BusinessOfferModelService extends GenericModuleService<BusinessOfferModel> {

	@Inject
	private BusinessServiceModelService businessServiceModelService;

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private CatalogHierarchyBuilderService catalogHierarchyBuilderService;

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private ServiceModelScriptService serviceModelScriptService;

	@Inject
	private OfferModelScriptService offerModelScriptService;

	/**
	 * Creates an offer given a BusinessOfferModel.
	 * 
	 * @param businessOfferModel
	 * @param customFields
	 * @param code
	 * @param name
	 * @param offerDescription
	 * @param serviceCodes
	 * @param currentUser
	 * @return
	 * @throws BusinessException
	 */
	public OfferTemplate createOfferFromBOM(BusinessOfferModel businessOfferModel, List<CustomFieldDto> customFields, String code, String name, String offerDescription,
			List<ServiceConfigurationDto> serviceCodes, User currentUser) throws BusinessException {
		return createOfferFromBOM(businessOfferModel, customFields, code, name, offerDescription, serviceCodes, null, null, null, LifeCycleStatusEnum.IN_DESIGN, null, currentUser);
	}

	/**
	 * Creates an offer given a BusinessOfferModel.
	 * 
	 * @param businessOfferModel
	 * @param customFields
	 * @param code
	 * @param name
	 * @param offerDescription
	 * @param serviceCodes
	 * @param channels
	 * @param bams
	 * @param offerTemplateCategories
	 * @param currentUser
	 * @param lifeCycleStatusEnum
	 * @return
	 * @throws BusinessException
	 */
	public OfferTemplate createOfferFromBOM(BusinessOfferModel businessOfferModel, List<CustomFieldDto> customFields, String code, String name, String offerDescription,
			List<ServiceConfigurationDto> serviceCodes, List<Channel> channels, List<BusinessAccountModel> bams, List<OfferTemplateCategory> offerTemplateCategories,
			LifeCycleStatusEnum lifeCycleStatusEnum, String imagePath, User currentUser) throws BusinessException {
		OfferTemplate bomOffer = businessOfferModel.getOfferTemplate();
		bomOffer = offerTemplateService.refreshOrRetrieve(bomOffer);

		// 1 create offer
		OfferTemplate newOfferTemplate = new OfferTemplate();

		// check if offer already exists
		if (offerTemplateService.findByCode(code, currentUser.getProvider()) != null) {
			throw new BusinessException("Offer template with code " + code + " already exists");
		}

		if (businessOfferModel != null && businessOfferModel.getScript() != null) {
			try {
				offerModelScriptService.beforeCreateOfferFromBOM(customFields, businessOfferModel.getScript().getCode(), currentUser);
			} catch (BusinessException e) {
				log.error("Failed to execute a script {}", businessOfferModel.getScript().getCode(), e);
			}
		}

		newOfferTemplate.setCode(code);

		ImageUploadEventHandler<OfferTemplate> offerImageUploadEventHandler = new ImageUploadEventHandler<>();
		try {
			if (StringUtils.isBlank(imagePath)) {
				imagePath = bomOffer.getImagePath();
			}
			String newImagePath = offerImageUploadEventHandler.duplicateImage(newOfferTemplate, imagePath, code, currentUser.getProvider().getCode());
			newOfferTemplate.setImagePath(newImagePath);
		} catch (IOException e1) {
			log.error("IPIEL: Failed duplicating offer image: {}", e1.getMessage());
		}

		newOfferTemplate.setDescription(offerDescription);
		if (StringUtils.isBlank(name)) {
			newOfferTemplate.setName(bomOffer.getName());
		} else {
			newOfferTemplate.setName(name);
		}
		newOfferTemplate.setValidFrom(bomOffer.getValidFrom());
		newOfferTemplate.setValidTo(bomOffer.getValidTo());
		newOfferTemplate.setBusinessOfferModel(businessOfferModel);
		if (bomOffer.getAttachments() != null) {
			newOfferTemplate.getAttachments().addAll(bomOffer.getAttachments());
		}
		if (offerTemplateCategories != null) {
			newOfferTemplate.getOfferTemplateCategories().addAll(offerTemplateCategories);
		}
		if (channels != null) {
			newOfferTemplate.getChannels().addAll(channels);
		}
		if (bams != null) {
			newOfferTemplate.getBusinessAccountModels().addAll(bams);
		}
		newOfferTemplate.setActive(true);
		newOfferTemplate.setLifeCycleStatus(lifeCycleStatusEnum);

		offerTemplateService.create(newOfferTemplate, currentUser);

		String prefix = newOfferTemplate.getId() + "_";

		List<OfferServiceTemplate> newOfferServiceTemplates = new ArrayList<>();
		// 2 create services
		if (bomOffer.getOfferServiceTemplates() != null) {
			// check if service template exists
			if (serviceCodes != null && serviceCodes.size() > 0) {
				boolean serviceFound = false;
				for (ServiceConfigurationDto serviceCodeDto : serviceCodes) {
					String serviceCode = serviceCodeDto.getCode();

					for (OfferServiceTemplate offerServiceTemplate : bomOffer.getOfferServiceTemplates()) {
						ServiceTemplate serviceTemplate = offerServiceTemplate.getServiceTemplate();
						if (serviceCode.equals(serviceTemplate.getCode())) {
							serviceFound = true;
							break;
						}
					}

					if (!serviceFound) {
						throw new BusinessException("Service " + serviceCode + " is not defined in the offer");
					}
				}
			}

			List<PricePlanMatrix> pricePlansInMemory = new ArrayList<>();
			List<ChargeTemplate> chargeTemplateInMemory = new ArrayList<>();
			for (OfferServiceTemplate offerServiceTemplate : bomOffer.getOfferServiceTemplates()) {
				ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(offerServiceTemplate.getServiceTemplate().getCode(), currentUser.getProvider());

				boolean serviceFound = false;
				ServiceConfigurationDto serviceConfigurationDto = new ServiceConfigurationDto();
				for (ServiceConfigurationDto tempServiceCodeDto : serviceCodes) {
					String serviceCode = tempServiceCodeDto.getCode();
					if (serviceCode.equals(serviceTemplate.getCode())) {
						serviceConfigurationDto = tempServiceCodeDto;
						serviceFound = true;
					}
				}
				if (!serviceFound) {
					continue;
				}

				// get the BSM from BOM
				BusinessServiceModel bsm = null;
				for (MeveoModuleItem item : businessOfferModel.getModuleItems()) {
					if (item.getItemClass().equals(BusinessServiceModel.class.getName())) {
						bsm = businessServiceModelService.findByCode(item.getItemCode(), currentUser.getProvider());
						if (bsm.getServiceTemplate().equals(serviceTemplate)) {
							break;
						}
					}
				}

				if (bsm != null && bsm.getScript() != null) {
					try {
						serviceModelScriptService.beforeCreateServiceFromBSM(serviceConfigurationDto.getCustomFields(), bsm.getScript().getCode(), currentUser);
					} catch (BusinessException e) {
						log.error("Failed to execute a script {}", bsm.getScript().getCode(), e);
					}
				}

				OfferServiceTemplate newOfferServiceTemplate = catalogHierarchyBuilderService.duplicateService(offerServiceTemplate, serviceConfigurationDto, prefix, pricePlansInMemory,
						chargeTemplateInMemory, currentUser);
				newOfferServiceTemplates.add(newOfferServiceTemplate);

				if (bsm != null && bsm.getScript() != null) {
					try {
						serviceModelScriptService.afterCreateServiceFromBSM(newOfferServiceTemplate.getServiceTemplate(), serviceConfigurationDto.getCustomFields(), bsm
								.getScript().getCode(), currentUser);
					} catch (BusinessException e) {
						log.error("Failed to execute a script {}", bsm.getScript().getCode(), e);
					}
				}
			}
		}

		// add to offer
		for (OfferServiceTemplate newOfferServiceTemplate : newOfferServiceTemplates) {
			newOfferTemplate.addOfferServiceTemplate(newOfferServiceTemplate);
		}

		offerTemplateService.update(newOfferTemplate, currentUser);

		if (newOfferTemplate.getBusinessOfferModel() != null && newOfferTemplate.getBusinessOfferModel().getScript() != null) {
			try {
				offerModelScriptService.afterCreateOfferFromBOM(newOfferTemplate, customFields, newOfferTemplate.getBusinessOfferModel().getScript().getCode(), currentUser);
			} catch (BusinessException e) {
				log.error("Failed to execute a script {}", newOfferTemplate.getBusinessOfferModel().getScript().getCode(), e);
			}
		}

		// save the cf

		return newOfferTemplate;
	}

	@SuppressWarnings("unchecked")
	public List<BusinessOfferModel> listInstalled(Provider provider) {
		QueryBuilder qb = new QueryBuilder(BusinessOfferModel.class, "b", null, null, provider);
		qb.startOrClause();
		qb.addCriterion("installed", "=", true, true);
		qb.addSql("moduleSource is null");
		qb.endOrClause();

		try {
			return (List<BusinessOfferModel>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
}