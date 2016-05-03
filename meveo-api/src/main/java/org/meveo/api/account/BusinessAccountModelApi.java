package org.meveo.api.account;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.account.BusinessAccountModelDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidEnumValueException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountHierarchyTypeEnum;
import org.meveo.model.crm.AccountModelScript;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.service.crm.impl.AccountModelScriptService;
import org.meveo.service.crm.impl.BusinessAccountModelService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BusinessAccountModelApi extends BaseApi {

	@Inject
	private AccountModelScriptService accountModelScriptService;

	@Inject
	private BusinessAccountModelService businessAccountModelService;

	public BusinessAccountModel create(BusinessAccountModelDto postData, User currentUser) throws MeveoApiException, BusinessException {
		AccountModelScript script = null;
		if (!StringUtils.isBlank(postData.getScriptCode())) {
			script = accountModelScriptService.findByCode(postData.getScriptCode(), currentUser.getProvider());
		}

		if (businessAccountModelService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
			throw new EntityAlreadyExistsException(BusinessAccountModel.class, postData.getCode());
		}

		BusinessAccountModel businessAccountModel = new BusinessAccountModel();
		businessAccountModel.setCode(postData.getCode());
		businessAccountModel.setScript(script);
		businessAccountModel.setDescription(StringUtils.isBlank(postData.getDescription()) ? postData.getCode() : postData.getDescription());
		try {
			businessAccountModel.setType(AccountHierarchyTypeEnum.valueOf(postData.getType()));
		} catch (IllegalArgumentException e) {
			throw new InvalidEnumValueException(AccountHierarchyTypeEnum.class.getName(), postData.getType());
		}
		businessAccountModelService.create(businessAccountModel, currentUser);

		return businessAccountModel;
	}

	public BusinessAccountModel update(BusinessAccountModelDto postData, User currentUser) throws MeveoApiException, BusinessException {
		BusinessAccountModel businessAccountModel = businessAccountModelService.findByCode(postData.getCode(), currentUser.getProvider());
		if (businessAccountModel == null) {
			throw new EntityDoesNotExistsException(BusinessAccountModel.class, postData.getCode());
		}

		businessAccountModel.setDescription(StringUtils.isBlank(postData.getDescription()) ? postData.getCode() : postData.getDescription());

		if (!StringUtils.isBlank(postData.getScriptCode())) {
			AccountModelScript script = accountModelScriptService.findByCode(postData.getScriptCode(), currentUser.getProvider());
			businessAccountModel.setScript(script);
		}
		if (!StringUtils.isBlank(postData.getType())) {
			try {
				businessAccountModel.setType(AccountHierarchyTypeEnum.valueOf(postData.getType()));
			} catch (IllegalArgumentException e) {
				throw new InvalidEnumValueException(AccountHierarchyTypeEnum.class.getName(), postData.getType());
			}
		}

		businessAccountModelService.update(businessAccountModel, currentUser);

		return businessAccountModel;
	}

	public void remove(String code, User currentUser) throws MeveoApiException {
		BusinessAccountModel businessAccountModel = businessAccountModelService.findByCode(code, currentUser.getProvider());
		if (businessAccountModel == null) {
			throw new EntityDoesNotExistsException(BusinessAccountModel.class, code);
		}

		businessAccountModelService.remove(businessAccountModel);
	}

	public BusinessAccountModelDto find(String code, User currentUser) throws MeveoApiException {
		BusinessAccountModel businessAccountModel = businessAccountModelService.findByCode(code, currentUser.getProvider());
		if (businessAccountModel == null) {
			throw new EntityDoesNotExistsException(BusinessAccountModel.class, code);
		}

		BusinessAccountModelDto result = new BusinessAccountModelDto();
		result.setCode(businessAccountModel.getCode());
		result.setDescription(businessAccountModel.getDescription());
		if (businessAccountModel.getType() != null) {
			result.setType(businessAccountModel.getType().name());
		}
		if (businessAccountModel.getScript() != null) {
			result.setScriptCode(businessAccountModel.getScript().getCode());
		}

		return result;
	}

	public List<BusinessAccountModelDto> list() {
		List<BusinessAccountModelDto> result = new ArrayList<>();

		List<BusinessAccountModel> businessAccountModels = businessAccountModelService.list();
		if (businessAccountModels != null) {
			for (BusinessAccountModel businessAccountModel : businessAccountModels) {
				BusinessAccountModelDto businessAccountModelDto = new BusinessAccountModelDto(businessAccountModel);
				result.add(businessAccountModelDto);
			}
		}

		return result;
	}

}
