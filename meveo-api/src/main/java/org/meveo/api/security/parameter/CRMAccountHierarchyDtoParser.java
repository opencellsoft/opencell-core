package org.meveo.api.security.parameter;

import javax.inject.Inject;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.account.CRMAccountHierarchyDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountHierarchyTypeEnum;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.service.crm.impl.BusinessAccountModelService;
import org.meveo.service.security.SecuredBusinessEntityService;

/**
 * This will process a parameter of type {@link CRMAccountHierarchyDto} passed
 * to a method annotated with {@link SecuredBusinessEntityMethod}.
 * 
 * @author Tony Alejandro
 *
 */
public class CRMAccountHierarchyDtoParser extends SecureMethodParameterParser<BusinessEntity> {

	private static final String RETURNING_ENTITY = "Returning entity: {}";
	private static final String CREATING_BUSINESS_ENTITY = "Creating BusinessEntity using [code={}, parentCode={}, accountExist={}]";
	private static final String RETURNING_ACCOUNT_HIERARCHY_TYPE = "Returning AccountHierarchyTypeEnum: {}";
	private static final String RETRIEVING_ACCOUNT_HIERARCHY_TYPE = "Retrieving AccountHierarchyTypeEnum of type: {}";
	private static final String ACCOUNT_TYPE_DOES_NOT_MATCH = "Account type does not match any BAM or AccountHierarchyTypeEnum";

	@Inject
	private BusinessAccountModelService businessAccountModelService;

	@Inject
	private SecuredBusinessEntityService securedBusinessEntityService;

	@Override
	public BusinessEntity getParameterValue(SecureMethodParameter parameter, Object[] values, User user) throws MeveoApiException {

		if (parameter == null) {
			return null;
		}
		// retrieve the DTO
		CRMAccountHierarchyDto dto = extractAccountHierarchyDto(parameter, values);

		// retrieve the type of account hierarchy based on the dto that was
		// received.
		AccountHierarchyTypeEnum accountHierarchyTypeEnum = extractAccountHierarchyTypeEnum(dto, user);

		// using the account hierarchy type and dto, get the corresponding
		// entity that will be checked for authorization.
		BusinessEntity entity = getEntity(accountHierarchyTypeEnum, dto, user);

		return entity;
	}

	private CRMAccountHierarchyDto extractAccountHierarchyDto(SecureMethodParameter parameter, Object[] values) throws MeveoApiException {

		// get the parameter value based on the index.
		Object parameterValue = values[parameter.index()];

		if (!(parameterValue instanceof CRMAccountHierarchyDto)) {
			throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, String.format(INVALID_PARAMETER_TYPE, CRMAccountHierarchyDto.class.getName()));
		}

		// since we are sure it is of the correct type, cast it and return the
		// dto.
		CRMAccountHierarchyDto dto = (CRMAccountHierarchyDto) parameterValue;
		return dto;
	}

	private AccountHierarchyTypeEnum extractAccountHierarchyTypeEnum(CRMAccountHierarchyDto dto, User user) throws MeveoApiException {

		// retrieve the account hierarchy type by using the getCrmAccountType
		// property of the dto
		String crmAccountType = dto.getCrmAccountType();

		log.debug(RETRIEVING_ACCOUNT_HIERARCHY_TYPE, crmAccountType);

		AccountHierarchyTypeEnum accountHierarchyTypeEnum = null;
		BusinessAccountModel businessAccountModel = businessAccountModelService.findByCode(crmAccountType, user.getProvider());
		if (businessAccountModel != null) {
			accountHierarchyTypeEnum = businessAccountModel.getHierarchyType();
		} else {
			try {
				accountHierarchyTypeEnum = AccountHierarchyTypeEnum.valueOf(crmAccountType);
			} catch (Exception e) {
				throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, ACCOUNT_TYPE_DOES_NOT_MATCH, e);
			}
		}
		log.debug(RETURNING_ACCOUNT_HIERARCHY_TYPE, accountHierarchyTypeEnum);
		return accountHierarchyTypeEnum;
	}

	private BusinessEntity getEntity(AccountHierarchyTypeEnum accountHierarchyTypeEnum, CRMAccountHierarchyDto dto, User user) throws MeveoApiException {

		// immediately throw an error if the account hierarchy type is null.
		if (accountHierarchyTypeEnum == null) {
			throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, ACCOUNT_TYPE_DOES_NOT_MATCH);
		}

		// retrieve the class type and the parent type from the account
		// hierarchy
		Class<? extends BusinessEntity> entityClass = accountHierarchyTypeEnum.topClass();
		Class<? extends BusinessEntity> parentClass = accountHierarchyTypeEnum.parentClass();

		// retrieve the codes from the dto
		String code = dto.getCode();
		String parentCode = dto.getCrmParentCode();

		// check if the account already exists. If it is, we start the
		// validation from the given entity. Otherwise, if the account does not
		// exist, we need to start the authorization check starting with the
		// parent class.
		boolean accountExist = securedBusinessEntityService.getEntityByCode(entityClass, code, user) != null;

		log.debug(CREATING_BUSINESS_ENTITY, code, parentCode, accountExist);

		BusinessEntity entity = null;

		if (accountExist) {
			try {
				entity = entityClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, String.format(FAILED_TO_INSTANTIATE_ENTITY, entityClass.getName()), e);
			}
			entity.setCode(code);
		} else {
			try {
				entity = parentClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, String.format(FAILED_TO_INSTANTIATE_ENTITY, parentClass.getName()), e);
			}
			entity.setCode(parentCode);
		}
		log.debug(RETURNING_ENTITY, entity);
		return entity;
	}

}
