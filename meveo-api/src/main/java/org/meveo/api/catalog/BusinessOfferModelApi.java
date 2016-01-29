package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.OfferModelScript;
import org.meveo.service.catalog.impl.BusinessOfferService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.script.offer.OfferScriptService;

@Stateless
public class BusinessOfferModelApi extends BaseApi {

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private BusinessOfferService bomEntityService;

	@Inject
	private OfferScriptService scriptInstanceService;

	public void create(BusinessOfferModelDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getOfferTemplateCode())) {
			if (bomEntityService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(BusinessOfferModel.class, postData.getCode());
			}

			OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplateCode(),
					currentUser.getProvider());
			if (offerTemplate == null) {
				throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplateCode());
			}

			OfferModelScript scriptInstance = scriptInstanceService.findByCode(postData.getScriptCode(),
					currentUser.getProvider());

			BusinessOfferModel bomEntity = new BusinessOfferModel();
			bomEntity.setCode(postData.getCode());
			bomEntity.setOfferTemplate(offerTemplate);
			bomEntity.setScript(scriptInstance);
			bomEntity.setDescription(StringUtils.isBlank(postData.getDescription()) ? postData.getCode() : postData
					.getDescription());

			bomEntityService.create(bomEntity, currentUser, currentUser.getProvider());
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("bomCode");
			}
			if (StringUtils.isBlank(postData.getOfferTemplateCode())) {
				missingParameters.add("offerTemplateCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(BusinessOfferModelDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getOfferTemplateCode())) {
			BusinessOfferModel bomEntity = bomEntityService.findByCode(postData.getCode(), currentUser.getProvider());
			if (bomEntity == null) {
				throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getCode());
			}

			OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplateCode(),
					currentUser.getProvider());
			if (offerTemplate == null) {
				throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplateCode());
			}

			OfferModelScript scriptInstance = scriptInstanceService.findByCode(postData.getScriptCode(),
					currentUser.getProvider());

			bomEntity.setDescription(StringUtils.isBlank(postData.getDescription()) ? postData.getCode() : postData
					.getDescription());
			bomEntity.setOfferTemplate(offerTemplate);
			bomEntity.setScript(scriptInstance);

			bomEntityService.update(bomEntity, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("bomCode");
			}
			if (StringUtils.isBlank(postData.getOfferTemplateCode())) {
				missingParameters.add("offerTemplateCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public BusinessOfferModelDto find(String bomEntityCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(bomEntityCode)) {
			BusinessOfferModel bomEntity = bomEntityService.findByCode(bomEntityCode, provider);
			if (bomEntity != null) {
				BusinessOfferModelDto bomEntityDto = new BusinessOfferModelDto();
				bomEntityDto.setCode(bomEntity.getCode());
				bomEntityDto.setDescription(bomEntity.getDescription());
				if (bomEntity.getOfferTemplate() != null) {
					bomEntityDto.setOfferTemplateCode(bomEntity.getOfferTemplate().getCode());
				}
				if (bomEntity.getScript() != null) {
					bomEntityDto.setScriptCode(bomEntity.getScript().getCode());
				}

				return bomEntityDto;
			}

			throw new EntityDoesNotExistsException(BusinessOfferModel.class, bomEntityCode);
		} else {
			if (StringUtils.isBlank(bomEntityCode)) {
				missingParameters.add("bomEntityCode");
			}

			throw new MeveoApiException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String bomEntityCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(bomEntityCode)) {
			BusinessOfferModel bomEntity = bomEntityService.findByCode(bomEntityCode, provider);
			if (bomEntity == null) {
				throw new EntityDoesNotExistsException(BusinessOfferModel.class, bomEntityCode);
			}

			bomEntityService.remove(bomEntity);
		} else {
			if (StringUtils.isBlank(bomEntityCode)) {
				missingParameters.add("bomEntityCode");
			}

			throw new MeveoApiException(getMissingParametersExceptionMessage());
		}
	}

	public void createOrUpdate(BusinessOfferModelDto postData, User currentUser) throws MeveoApiException {
		BusinessOfferModel bomEntity = bomEntityService.findByCode(postData.getCode(), currentUser.getProvider());
		if (bomEntity == null) {
			// create
			create(postData, currentUser);
		} else {
			// update
			update(postData, currentUser);
		}
	}
}
