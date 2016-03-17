package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
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
import org.meveo.service.catalog.impl.BusinessOfferService;
import org.meveo.service.catalog.impl.BusinessServiceService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.script.OfferModelScriptService;

@Stateless
public class BusinessOfferModelApi extends BaseApi {

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private BusinessOfferService businessOfferService;

	@Inject
	private OfferModelScriptService offerModelScriptService;

	@Inject
	private BusinessServiceService businessServiceService;

	public void create(BusinessOfferModelDto postData, User currentUser) throws MeveoApiException, BusinessException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getOfferTemplateCode())) {
			if (businessOfferService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(BusinessOfferModel.class, postData.getCode());
			}

			OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplateCode(), currentUser.getProvider());
			if (offerTemplate == null) {
				throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplateCode());
			}

			OfferModelScript scriptInstance = offerModelScriptService.findByCode(postData.getScriptCode(), currentUser.getProvider());

			BusinessOfferModel businessOfferModel = new BusinessOfferModel();
			businessOfferModel.setCode(postData.getCode());
			businessOfferModel.setOfferTemplate(offerTemplate);
			businessOfferModel.setScript(scriptInstance);
			businessOfferModel.setDescription(StringUtils.isBlank(postData.getDescription()) ? postData.getCode() : postData.getDescription());

			// create bsm
			if (postData.getBsmCodes() != null) {
				for (String bsmCode : postData.getBsmCodes()) {
					BusinessServiceModel bsm = businessServiceService.findByCode(bsmCode, currentUser.getProvider());
					if (bsm == null) {
						throw new EntityDoesNotExistsException(BusinessServiceModel.class, bsmCode);
					}
					MeveoModuleItem meveoModuleItem = new MeveoModuleItem(bsm);
					businessOfferModel.addModuleItem(meveoModuleItem);
				}
			}

			businessOfferService.create(businessOfferModel, currentUser);
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
			BusinessOfferModel businessOfferModel = businessOfferService.findByCode(postData.getCode(), currentUser.getProvider());
			if (businessOfferModel == null) {
				throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getCode());
			}

			OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplateCode(), currentUser.getProvider());
			if (offerTemplate == null) {
				throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplateCode());
			}

			OfferModelScript scriptInstance = offerModelScriptService.findByCode(postData.getScriptCode(), currentUser.getProvider());

			businessOfferModel.setDescription(StringUtils.isBlank(postData.getDescription()) ? postData.getCode() : postData.getDescription());
			businessOfferModel.setOfferTemplate(offerTemplate);
			businessOfferModel.setScript(scriptInstance);

			businessOfferService.update(businessOfferModel, currentUser);
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

		BusinessOfferModel businessOfferModel = businessOfferService.findByCode(businessOfferModelCode, provider);
		if (businessOfferModel != null) {
			BusinessOfferModelDto businessOfferModelDto = new BusinessOfferModelDto();
			businessOfferModelDto.setCode(businessOfferModel.getCode());
			businessOfferModelDto.setDescription(businessOfferModel.getDescription());
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
		BusinessOfferModel businessOfferModel = businessOfferService.findByCode(businessOfferModelCode, provider);
		if (businessOfferModel == null) {
			throw new EntityDoesNotExistsException(BusinessOfferModel.class, businessOfferModelCode);
		}

		businessOfferService.remove(businessOfferModel);
	}

	public void createOrUpdate(BusinessOfferModelDto postData, User currentUser) throws MeveoApiException, BusinessException {
		BusinessOfferModel businessOfferModel = businessOfferService.findByCode(postData.getCode(), currentUser.getProvider());
		if (businessOfferModel == null) {
			// create
			create(postData, currentUser);
		} else {
			// update
			update(postData, currentUser);
		}
	}
}
