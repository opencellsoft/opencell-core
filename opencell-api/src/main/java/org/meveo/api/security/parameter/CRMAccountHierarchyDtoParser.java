package org.meveo.api.security.parameter;

import javax.inject.Inject;

import org.meveo.api.dto.account.CRMAccountHierarchyDto;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.model.BusinessEntity;
import org.meveo.model.crm.AccountHierarchyTypeEnum;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.service.crm.impl.BusinessAccountModelService;
import org.meveo.service.security.SecuredBusinessEntityService;

/**
 * This will process a parameter of type {@link CRMAccountHierarchyDto} passed to a method annotated with {@link SecuredBusinessEntityMethod}.
 * 
 * @author Tony Alejandro
 *
 */
public class CRMAccountHierarchyDtoParser extends SecureMethodParameterParser<BusinessEntity> {

    @Inject
    private BusinessAccountModelService businessAccountModelService;

    @Inject
    private SecuredBusinessEntityService securedBusinessEntityService;

    @Override
    public BusinessEntity getParameterValue(SecureMethodParameter parameter, Object[] values) throws InvalidParameterException, MissingParameterException {

        if (parameter == null) {
            return null;
        }
        // retrieve the DTO
        CRMAccountHierarchyDto dto = extractAccountHierarchyDto(parameter, values);

        // retrieve the type of account hierarchy based on the dto that was
        // received.
        AccountHierarchyTypeEnum accountHierarchyTypeEnum = extractAccountHierarchyTypeEnum(dto);

        // using the account hierarchy type and dto, get the corresponding
        // entity that will be checked for authorization.
        BusinessEntity entity = getEntity(accountHierarchyTypeEnum, dto);

        return entity;
    }

    private CRMAccountHierarchyDto extractAccountHierarchyDto(SecureMethodParameter parameter, Object[] values) throws InvalidParameterException {

        // get the parameter value based on the index.
        Object parameterValue = values[parameter.index()];

        if (!(parameterValue instanceof CRMAccountHierarchyDto)) {
            throw new InvalidParameterException("Parameter received at index: " + parameter.index() + " is not an instance of CRMAccountHierarchyDto.");
        }

        // since we are sure it is of the correct type, cast it and return the
        // dto.
        CRMAccountHierarchyDto dto = (CRMAccountHierarchyDto) parameterValue;
        return dto;
    }

    private AccountHierarchyTypeEnum extractAccountHierarchyTypeEnum(CRMAccountHierarchyDto dto) throws InvalidParameterException {

        // retrieve the account hierarchy type by using the getCrmAccountType
        // property of the dto
        String crmAccountType = dto.getCrmAccountType();

        log.debug("Retrieving AccountHierarchyTypeEnum of type: {}", crmAccountType);

        AccountHierarchyTypeEnum accountHierarchyTypeEnum = null;
        BusinessAccountModel businessAccountModel = businessAccountModelService.findByCode(crmAccountType);
        if (businessAccountModel != null) {
            accountHierarchyTypeEnum = businessAccountModel.getHierarchyType();
        } else {
            try {
                accountHierarchyTypeEnum = AccountHierarchyTypeEnum.valueOf(crmAccountType);
            } catch (Exception e) {
                log.error("Account type does not match any BAM or AccountHierarchyTypeEnum", e);
                throw new InvalidParameterException(AccountHierarchyTypeEnum.class.getSimpleName(), crmAccountType);
            }
        }
        log.debug("Returning AccountHierarchyTypeEnum: {}", accountHierarchyTypeEnum);
        return accountHierarchyTypeEnum;
    }

    private BusinessEntity getEntity(AccountHierarchyTypeEnum accountHierarchyTypeEnum, CRMAccountHierarchyDto dto) throws InvalidParameterException {

        // immediately throw an error if the account hierarchy type is null.
        if (accountHierarchyTypeEnum == null) {
            throw new InvalidParameterException("Account type does not match any BAM or AccountHierarchyTypeEnum");
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
        boolean accountExist = securedBusinessEntityService.getEntityByCode(entityClass, code) != null;

        log.debug("Creating BusinessEntity using [code={}, parentCode={}, accountExist={}]", code, parentCode, accountExist);

        BusinessEntity entity = null;

        if (accountExist) {
            try {
                entity = entityClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                String message = String.format("Failed to create new %s instance.", entityClass.getName());
                log.error(message, e);
                throw new InvalidParameterException(message);
            }
            entity.setCode(code);
        } else {
            try {
                entity = parentClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                String message = String.format("Failed to create new %s instance.", parentClass.getName());
                log.error(message, e);
                throw new InvalidParameterException(message);
            }
            entity.setCode(parentCode);
        }
        log.debug("Returning entity: {}", entity);
        return entity;
    }

}
