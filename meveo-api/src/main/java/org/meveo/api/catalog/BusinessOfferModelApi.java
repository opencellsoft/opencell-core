package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.model.scripts.OfferModelScript;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.catalog.impl.BusinessServiceModelService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.script.OfferModelScriptService;

@Stateless
public class BusinessOfferModelApi extends BaseApi {

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private BusinessOfferModelService businessOfferModelService;

	@Inject
	private OfferModelScriptService offerModelScriptService;

	@Inject
	private BusinessServiceModelService businessServiceModelService;

	@Inject
	private MeveoModuleService meveoModuleService;

	public void create(BusinessOfferModelDto postData, User currentUser) throws MeveoApiException, BusinessException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getOfferTemplateCode())) {
			if (businessOfferModelService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(BusinessOfferModel.class, postData.getCode());
			}

			BusinessOfferModel businessOfferModel = fromDto(postData, currentUser, null);

			businessOfferModelService.create(businessOfferModel, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("bomCode");
			}
			if (StringUtils.isBlank(postData.getOfferTemplateCode())) {
				missingParameters.add("offerTemplateCode");
			}

			handleMissingParameters();
		}
	}

	public void update(BusinessOfferModelDto postData, User currentUser) throws MeveoApiException, BusinessException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getOfferTemplateCode())) {
			BusinessOfferModel businessOfferModel = businessOfferModelService.findByCode(postData.getCode(), currentUser.getProvider());
			if (businessOfferModel == null) {
				throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getCode());
			}

			businessOfferModel = fromDto(postData, currentUser, businessOfferModel);

			businessOfferModelService.update(businessOfferModel, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("bomCode");
			}
			if (StringUtils.isBlank(postData.getOfferTemplateCode())) {
				missingParameters.add("offerTemplateCode");
			}

			handleMissingParameters();
		}
	}

	public BusinessOfferModelDto find(String businessOfferModelCode, Provider provider) throws MeveoApiException {
		if (StringUtils.isBlank(businessOfferModelCode)) {
			missingParameters.add("businessOfferModelCode");
		}
		handleMissingParameters();

		BusinessOfferModel businessOfferModel = businessOfferModelService.findByCode(businessOfferModelCode, provider);
		if (businessOfferModel != null) {
			BusinessOfferModelDto businessOfferModelDto = new BusinessOfferModelDto(meveoModuleService.moduleToDto(businessOfferModel, provider));

			if (businessOfferModel.getOfferTemplate() != null) {
				businessOfferModelDto.setOfferTemplateCode(businessOfferModel.getOfferTemplate().getCode());
			}
			if (businessOfferModel.getScript() != null) {
				businessOfferModelDto.setScriptCode(businessOfferModel.getScript().getCode());
			}

			return businessOfferModelDto;
		}

		throw new EntityDoesNotExistsException(BusinessOfferModel.class, businessOfferModelCode);

	}

	public void remove(String businessOfferModelCode, Provider provider) throws MeveoApiException {
		if (StringUtils.isBlank(businessOfferModelCode)) {
			missingParameters.add("businessOfferModelCode");
		}

		handleMissingParameters();
		BusinessOfferModel businessOfferModel = businessOfferModelService.findByCode(businessOfferModelCode, provider);
		if (businessOfferModel == null) {
			throw new EntityDoesNotExistsException(BusinessOfferModel.class, businessOfferModelCode);
		}

		businessOfferModelService.remove(businessOfferModel);
	}

	public void createOrUpdate(BusinessOfferModelDto postData, User currentUser) throws MeveoApiException, BusinessException {
		BusinessOfferModel businessOfferModel = businessOfferModelService.findByCode(postData.getCode(), currentUser.getProvider());
		if (businessOfferModel == null) {
			// create
			create(postData, currentUser);
		} else {
			// update
			update(postData, currentUser);
		}
	}

	public BusinessOfferModel fromDto(BusinessOfferModelDto postData, User currentUser, BusinessOfferModel entityToUpdate) throws MeveoApiException {
		BusinessOfferModel entity = new BusinessOfferModel();
		if (entityToUpdate != null) {
			entity = entityToUpdate;
		} else {
			entity.setCode(postData.getCode());
		}

		OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplateCode(), currentUser.getProvider());
		if (offerTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplateCode());
		}

		OfferModelScript scriptInstance = null;
		if (!StringUtils.isBlank(postData.getScriptCode())) {
			scriptInstance = offerModelScriptService.findByCode(postData.getScriptCode(), currentUser.getProvider());
			if (scriptInstance == null) {
				throw new EntityDoesNotExistsException(OfferModelScript.class, postData.getScriptCode());
			}
		}

		entity.setDescription(StringUtils.isBlank(postData.getDescription()) ? postData.getCode() : postData.getDescription());
		entity.setOfferTemplate(offerTemplate);
		entity.setScript(scriptInstance);

		if (entity.getModuleItems() != null) {
			entity.getModuleItems().clear();
		}

		for (BaseDto dto : postData.getModuleItems()) {
			if (dto instanceof BusinessServiceModelDto) {
				String bsmCode = ((BusinessServiceModelDto) dto).getCode();
				BusinessServiceModel bsm = businessServiceModelService.findByCode(bsmCode, currentUser.getProvider());
				if (bsm == null) {
					throw new EntityDoesNotExistsException(BusinessServiceModel.class, bsmCode);
				}
				MeveoModuleItem meveoModuleItem = new MeveoModuleItem(bsm);
				if (!entity.getModuleItems().contains(meveoModuleItem)) {
					entity.addModuleItem(meveoModuleItem);
				}
			}
		}

		return entity;
	}

}
