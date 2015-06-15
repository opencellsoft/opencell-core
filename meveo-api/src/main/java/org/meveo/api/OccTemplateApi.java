package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.OccTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.slf4j.Logger;
 
@Stateless
public class OccTemplateApi extends BaseApi {
	
	@Inject
	private Logger log;

	@Inject
	private OCCTemplateService occTemplateService;

	public void create(OccTemplateDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription()) && !StringUtils.isBlank(postData.getAccountCode())
				&& !StringUtils.isBlank(postData.getOccCategory())) {
			Provider provider = currentUser.getProvider();

			if (occTemplateService.findByCode(postData.getCode(), provider) != null) {
				throw new EntityAlreadyExistsException(OCCTemplate.class,
						postData.getCode());
			} 

			OCCTemplate occTemplate = new OCCTemplate();
			occTemplate.setProvider(provider);
			occTemplate.setCode(postData.getCode());
			occTemplate.setDescription(postData.getDescription());
			occTemplate.setAccountCode(postData.getAccountCode());
			occTemplate.setAccountCodeClientSide(postData.getAccountCodeClientSide());
			try {
				occTemplate.setOccCategory(OperationCategoryEnum.valueOf(postData.getOccCategory()));
			} catch (IllegalArgumentException e) {
				log.error("InvalidEnum for type with name={}", postData.getOccCategory());
				throw new MeveoApiException(MeveoApiErrorCode.INVALID_ENUM_VALUE, "Enum for OperationCategoryEnum with name=" + postData.getOccCategory() + " does not exists.");
			} 

			occTemplateService.create(occTemplate, currentUser,
					provider);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getAccountCode())) {
				missingParameters.add("accountCode");
			}
			if (StringUtils.isBlank(postData.getOccCategory())) {
				missingParameters.add("occCategory");
			}  
			
			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public void update(OccTemplateDto postData, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription()) && !StringUtils.isBlank(postData.getAccountCode())
				&& !StringUtils.isBlank(postData.getOccCategory())) {
			Provider provider = currentUser.getProvider();

			OCCTemplate occTemplate = occTemplateService
					.findByCode(postData.getCode(), provider);
			if (occTemplate == null) {
				throw new EntityDoesNotExistsException(OCCTemplate.class,
						postData.getCode());
			} 
		
			
			occTemplate.setDescription(postData.getDescription());
			occTemplate.setAccountCode(postData.getAccountCode());
			occTemplate.setAccountCodeClientSide(postData.getAccountCodeClientSide());
			try {
				occTemplate.setOccCategory(OperationCategoryEnum.valueOf(postData.getOccCategory()));
			} catch (IllegalArgumentException e) {
				log.error("InvalidEnum for type with name={}", postData.getOccCategory());
				throw new MeveoApiException(MeveoApiErrorCode.INVALID_ENUM_VALUE, "Enum for OperationCategoryEnum with name=" + postData.getOccCategory() + " does not exists.");
			}

			occTemplateService.update(occTemplate, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getAccountCode())) {
				missingParameters.add("accountCode");
			}
			if (StringUtils.isBlank(postData.getOccCategory())) {
				missingParameters.add("occCategory");
			}  
			
			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public OccTemplateDto find(String code, Provider provider)
			throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			OCCTemplate occTemplate = occTemplateService
					.findByCode(code, provider);
			if (occTemplate == null) {
				throw new EntityDoesNotExistsException(OCCTemplate.class,
						code);
			}

			return new OccTemplateDto(occTemplate);
		} else {
			missingParameters.add("occTemplateCode");

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public void remove(String code, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			OCCTemplate occTemplate = occTemplateService
					.findByCode(code, provider);
			if (occTemplate == null) {
				throw new EntityDoesNotExistsException(OCCTemplate.class,
						code);
			}

			occTemplateService.remove(occTemplate);
		} else {
			missingParameters.add("occTemplateCode");

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

}
