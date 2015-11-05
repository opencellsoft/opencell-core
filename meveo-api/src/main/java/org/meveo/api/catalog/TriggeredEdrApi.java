package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class TriggeredEdrApi extends BaseApi {

	@Inject
	private TriggeredEDRTemplateService triggeredEDRTemplateService;

	public void create(TriggeredEdrTemplateDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getQuantityEl())) {
			if (triggeredEDRTemplateService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(TriggeredEDRTemplate.class, postData.getCode());
			}

			TriggeredEDRTemplate edrTemplate = new TriggeredEDRTemplate();
			edrTemplate.setCode(postData.getCode());
			edrTemplate.setDescription(postData.getDescription());
			edrTemplate.setSubscriptionEl(postData.getSubscriptionEl());
			edrTemplate.setConditionEl(postData.getConditionEl());
			edrTemplate.setQuantityEl(postData.getQuantityEl());
			edrTemplate.setParam1El(postData.getParam1El());
			edrTemplate.setParam2El(postData.getParam2El());
			edrTemplate.setParam3El(postData.getParam3El());
			edrTemplate.setParam4El(postData.getParam4El());

			triggeredEDRTemplateService.create(edrTemplate, currentUser, currentUser.getProvider());
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getQuantityEl())) {
				missingParameters.add("quantityEl");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(TriggeredEdrTemplateDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getQuantityEl())) {
			TriggeredEDRTemplate edrTemplate = triggeredEDRTemplateService.findByCode(postData.getCode(), currentUser.getProvider());
			if (edrTemplate == null) {
				throw new EntityDoesNotExistsException(TriggeredEDRTemplate.class, postData.getCode());
			}

			edrTemplate.setDescription(postData.getDescription());
			edrTemplate.setSubscriptionEl(postData.getSubscriptionEl());
			edrTemplate.setConditionEl(postData.getConditionEl());
			edrTemplate.setQuantityEl(postData.getQuantityEl());
			edrTemplate.setParam1El(postData.getParam1El());
			edrTemplate.setParam2El(postData.getParam2El());
			edrTemplate.setParam3El(postData.getParam3El());
			edrTemplate.setParam4El(postData.getParam4El());

			triggeredEDRTemplateService.update(edrTemplate, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getQuantityEl())) {
				missingParameters.add("quantityEl");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String triggeredEdrCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(triggeredEdrCode)) {
			TriggeredEDRTemplate edrTemplate = triggeredEDRTemplateService.findByCode(triggeredEdrCode, provider);
			if (edrTemplate == null) {
				throw new EntityDoesNotExistsException(TriggeredEDRTemplate.class, triggeredEdrCode);
			}

			triggeredEDRTemplateService.remove(edrTemplate);
		} else {
			missingParameters.add("code");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public TriggeredEdrTemplateDto find(String triggeredEdrCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(triggeredEdrCode)) {
			TriggeredEDRTemplate edrTemplate = triggeredEDRTemplateService.findByCode(triggeredEdrCode, provider);
			if (edrTemplate == null) {
				throw new EntityDoesNotExistsException(TriggeredEDRTemplate.class, triggeredEdrCode);
			}

			TriggeredEdrTemplateDto edrTemplateDto = new TriggeredEdrTemplateDto(edrTemplate);
			return edrTemplateDto;
		} else {
			missingParameters.add("code");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
	
	public void createOrUpdate(TriggeredEdrTemplateDto postData, User currentUser) throws MeveoApiException {
		if (triggeredEDRTemplateService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
			create(postData, currentUser);
		} else {
			update(postData, currentUser);
		}
	}
}
