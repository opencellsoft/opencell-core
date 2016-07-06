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
import org.meveo.service.security.SecuredBusinessEntityServiceFactory;

public class CRMAccountHierarchyDtoParser extends SecureMethodParameterParser<BusinessEntity> {

	private static final String FAILED_TO_RETRIEVE_SERVICE = "Failed to retrieve SecuredBusinessEntityService.";
	private static final String ACCOUNT_TYPE_DOES_NOT_MATCH = "Account type does not match any BAM or AccountHierarchyTypeEnum";

	@Inject
	private BusinessAccountModelService businessAccountModelService;

	@Inject
	SecuredBusinessEntityServiceFactory serviceFactory;

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
		
		log.debug("Retrieving AccountHierarchyTypeEnum of type: {}", crmAccountType);
		
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
		log.debug("Returning AccountHierarchyTypeEnum: {}", accountHierarchyTypeEnum);
		return accountHierarchyTypeEnum;
	}

	private BusinessEntity getEntity(AccountHierarchyTypeEnum accountHierarchyTypeEnum, CRMAccountHierarchyDto dto, User user) throws MeveoApiException {

		SecuredBusinessEntityService service = getService(accountHierarchyTypeEnum);
		BusinessEntity entity = null;
		String code = dto.getCode();
		String parentCode = dto.getCrmParentCode();
		boolean accountExist = service.getEntityByCode(code, user) != null;
		int accountType = accountHierarchyTypeEnum.getHighLevel();

		log.debug("Creating BusinessEntity using [Service={}, code={}, parentCode={}, accountExist={}, accountType={}]", service, code, parentCode, accountExist, accountType);

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
		log.debug("Returning entity: {}", entity);
		return entity;
	}

	private SecuredBusinessEntityService getService(AccountHierarchyTypeEnum accountHierarchyTypeEnum) throws MeveoApiException {

		log.debug("Retrieving SecuredBusinessEntityService for AccountHierarchyTypeEnum: {}", accountHierarchyTypeEnum);

		SecuredBusinessEntityService service = null;

		if (accountHierarchyTypeEnum.getHighLevel() == 4) {
			service = serviceFactory.getService(Seller.class);

		} else if (accountHierarchyTypeEnum.getHighLevel() >= 3 && accountHierarchyTypeEnum.getLowLevel() <= 3) {
			service = serviceFactory.getService(Customer.class);

		} else if (accountHierarchyTypeEnum.getHighLevel() >= 2 && accountHierarchyTypeEnum.getLowLevel() <= 2) {
			service = serviceFactory.getService(CustomerAccount.class);

		} else if (accountHierarchyTypeEnum.getHighLevel() >= 1 && accountHierarchyTypeEnum.getLowLevel() <= 1) {
			service = serviceFactory.getService(BillingAccount.class);

		} else {
			service = serviceFactory.getService(UserAccount.class);
		}

		if (service == null) {
			throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, FAILED_TO_RETRIEVE_SERVICE);
		}
		log.debug("Returning SecuredBusinessEntityService: {}", service);
		return service;
	}

}
