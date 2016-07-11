package org.meveo.api.security.parameter;

import javax.inject.Inject;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.account.CRMAccountHierarchyDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.AccountHierarchyTypeEnum;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.crm.impl.BusinessAccountModelService;
import org.meveo.service.security.SecuredBusinessEntityService;

public class CRMAccountHierarchyDtoParser extends SecureMethodParameterParser<BusinessEntity> {

	private static final String RETURNING_BUSINESS_ENTITY_CLASS = "Returning BusinessEntity Class: {}";
	private static final String RETRIEVING_BUSINESS_ENTITY_CLASS = "Retrieving BusinessEntity Class for AccountHierarchyTypeEnum: {}";
	private static final String RETURNING_ENTITY = "Returning entity: {}";
	private static final String CREATING_BUSINESS_ENTITY = "Creating BusinessEntity using [code={}, parentCode={}, accountExist={}, accountType={}]";
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
		
		CRMAccountHierarchyDto dto = extractAccountHierarchyDto(parameter, values);

		AccountHierarchyTypeEnum accountHierarchyTypeEnum = extractAccountHierarchyTypeEnum(dto, user);

		BusinessEntity entity = getEntity(accountHierarchyTypeEnum, dto, user);

		return entity;
	}

	private CRMAccountHierarchyDto extractAccountHierarchyDto(SecureMethodParameter parameter, Object[] values) throws MeveoApiException {
		
		Object parameterValue = values[parameter.index()];

		if (!(parameterValue instanceof CRMAccountHierarchyDto)) {
			throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, String.format(INVALID_PARAMETER_TYPE, CRMAccountHierarchyDto.class.getTypeName()));
		}

		CRMAccountHierarchyDto dto = (CRMAccountHierarchyDto) parameterValue;
		return dto;
	}

	private AccountHierarchyTypeEnum extractAccountHierarchyTypeEnum(CRMAccountHierarchyDto dto, User user) throws MeveoApiException {

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

		Class<? extends BusinessEntity> entityClass = getEntityClass(accountHierarchyTypeEnum);
		String code = dto.getCode();
		String parentCode = dto.getCrmParentCode();
		boolean accountExist = securedBusinessEntityService.getEntityByCode(entityClass, code, user) != null;
		int accountType = accountHierarchyTypeEnum.getHighLevel();

		log.debug(CREATING_BUSINESS_ENTITY, code, parentCode, accountExist, accountType);

		BusinessEntity entity = null;
		// UA=0, BA=1, CA=2, C=3, S=4
		switch (accountType) {
		case 0:
			if (accountExist) {
				entity = new UserAccount();
				entity.setCode(code);
			} else {
				entity = new BillingAccount();
				entity.setCode(parentCode);
			}
			break;
		case 1:
			if (accountExist) {
				entity = new BillingAccount();
				entity.setCode(code);
			} else {
				entity = new CustomerAccount();
				entity.setCode(parentCode);
			}
			break;
		case 2:
			if (accountExist) {
				entity = new CustomerAccount();
				entity.setCode(code);
			} else {
				entity = new Customer();
				entity.setCode(parentCode);
			}
			break;
		case 3:
			if (accountExist) {
				entity = new Customer();
				entity.setCode(code);
			} else {
				entity = new Seller();
				entity.setCode(dto.getCrmParentCode());
			}
			break;
		case 4:
			entity = new Seller();
			entity.setCode(code);
			break;
		default:
			throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, String.format(FAILED_TO_INSTANTIATE_ENTITY, BusinessEntity.class.getTypeName()));
		}
		log.debug(RETURNING_ENTITY, entity);
		return entity;
	}

	private Class<? extends BusinessEntity> getEntityClass(AccountHierarchyTypeEnum accountHierarchyTypeEnum) throws MeveoApiException {

		log.debug(RETRIEVING_BUSINESS_ENTITY_CLASS, accountHierarchyTypeEnum);

		Class<? extends BusinessEntity> entityClass = null;

		if (accountHierarchyTypeEnum.getHighLevel() == 4) {
			entityClass = Seller.class;

		} else if (accountHierarchyTypeEnum.getHighLevel() >= 3 && accountHierarchyTypeEnum.getLowLevel() <= 3) {
			entityClass = Customer.class;

		} else if (accountHierarchyTypeEnum.getHighLevel() >= 2 && accountHierarchyTypeEnum.getLowLevel() <= 2) {
			entityClass = CustomerAccount.class;

		} else if (accountHierarchyTypeEnum.getHighLevel() >= 1 && accountHierarchyTypeEnum.getLowLevel() <= 1) {
			entityClass = BillingAccount.class;

		} else {
			entityClass = UserAccount.class;
		}
		log.debug(RETURNING_BUSINESS_ENTITY_CLASS, entityClass.getTypeName());
		return entityClass;
	}

}
