package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.ServiceModelScript;
import org.meveo.service.catalog.impl.BusinessServiceModelService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.script.ServiceModelScriptService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BusinessServiceModelApi extends BaseApi {

	@Inject
	private BusinessServiceModelService businessServiceModelService;

	@Inject
	private ServiceModelScriptService serviceModelScriptService;

	@Inject
	private ServiceTemplateService serviceTemplateService;

	public void create(BusinessServiceModelDto postData, User currentUser) throws MeveoApiException {
		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getServiceTemplateCode())) {
			missingParameters.add("serviceTemplateCode");
		}

		handleMissingParameters();

		if (businessServiceModelService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
			throw new EntityAlreadyExistsException(BusinessServiceModel.class, postData.getCode());
		}

		ServiceModelScript serviceModelScript = null;
		if (!StringUtils.isBlank(postData.getScriptCode())) {
			serviceModelScript = serviceModelScriptService.findByCode(postData.getScriptCode(), currentUser.getProvider());
			if (serviceModelScript == null) {
				throw new EntityDoesNotExistsException(ServiceModelScript.class, postData.getScriptCode());
			}
		}

		ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(postData.getServiceTemplateCode(), currentUser.getProvider());
		if (serviceTemplate == null) {
			throw new EntityDoesNotExistsException(ServiceTemplate.class, postData.getServiceTemplateCode());
		}

		try {
			businessServiceModelService.create(postData.getCode(), postData.getDescription(), postData.isDuplicatePricePlan(), postData.isDuplicateService(), serviceModelScript,
					serviceTemplate, currentUser);
		} catch (BusinessException e) {
			throw new MeveoApiException(e.getMessage());
		}
	}

	public void update(BusinessServiceModelDto postData, User currentUser) throws MeveoApiException {
		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getServiceTemplateCode())) {
			missingParameters.add("serviceTemplateCode");
		}

		handleMissingParameters();

		BusinessServiceModel bsm = businessServiceModelService.findByCode(postData.getCode(), currentUser.getProvider());
		if (bsm == null) {
			throw new EntityDoesNotExistsException(BusinessServiceModel.class, postData.getCode());
		}

		ServiceModelScript serviceModelScript = null;
		if (!StringUtils.isBlank(postData.getScriptCode())) {
			serviceModelScript = serviceModelScriptService.findByCode(postData.getScriptCode(), currentUser.getProvider());
			if (serviceModelScript == null) {
				throw new EntityDoesNotExistsException(ServiceModelScript.class, postData.getScriptCode());
			}
		}

		ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(postData.getServiceTemplateCode(), currentUser.getProvider());
		if (serviceTemplate == null) {
			throw new EntityDoesNotExistsException(ServiceTemplate.class, postData.getServiceTemplateCode());
		}

		try {
			businessServiceModelService.update(bsm, postData.getDescription(), postData.isDuplicatePricePlan(), postData.isDuplicateService(), serviceModelScript, serviceTemplate,
					currentUser);
		} catch (BusinessException e) {
			throw new MeveoApiException(e.getMessage());
		}
	}

	public BusinessServiceModelDto find(String businessServiceModelCode, Provider provider) throws MeveoApiException {
		if (StringUtils.isBlank(businessServiceModelCode)) {
			missingParameters.add("businessServiceModelCode");
		}
		handleMissingParameters();

		BusinessServiceModel businessServiceModel = businessServiceModelService.findByCode(businessServiceModelCode, provider);
		if (businessServiceModel != null) {
			BusinessServiceModelDto businessServiceModelDto = new BusinessServiceModelDto();
			businessServiceModelDto.setCode(businessServiceModel.getCode());
			businessServiceModelDto.setDescription(businessServiceModel.getDescription());
			if (businessServiceModel.getServiceTemplate() != null) {
				businessServiceModelDto.setServiceTemplateCode(businessServiceModel.getServiceTemplate().getCode());
			}
			if (businessServiceModel.getScript() != null) {
				businessServiceModelDto.setScriptCode(businessServiceModel.getScript().getCode());
			}

			return businessServiceModelDto;
		}

		throw new EntityDoesNotExistsException(BusinessServiceModel.class, businessServiceModelCode);

	}

	public void remove(String businessServiceModelCode, Provider provider) throws MeveoApiException {
		if (StringUtils.isBlank(businessServiceModelCode)) {
			missingParameters.add("businessServiceModelCode");
		}

		handleMissingParameters();
		BusinessServiceModel businessServiceModel = businessServiceModelService.findByCode(businessServiceModelCode, provider);
		if (businessServiceModel == null) {
			throw new EntityDoesNotExistsException(BusinessServiceModel.class, businessServiceModelCode);
		}

		businessServiceModelService.remove(businessServiceModel);
	}

	public void createOrUpdate(BusinessServiceModelDto postData, User currentUser) throws MeveoApiException {
		BusinessServiceModel businessServiceModel = businessServiceModelService.findByCode(postData.getCode(), currentUser.getProvider());
		if (businessServiceModel == null) {
			// create
			create(postData, currentUser);
		} else {
			// update
			update(postData, currentUser);
		}
	}
}
