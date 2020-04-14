/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.security.parameter;

import javax.inject.Inject;

import org.meveo.api.dto.account.CRMAccountHierarchyDto;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.config.SecureMethodParameterConfig;
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
    public BusinessEntity getParameterValue(SecureMethodParameterConfig parameterConfig, Object[] values) throws InvalidParameterException, MissingParameterException {

        if (parameterConfig == null) {
            return null;
        }
        // retrieve the DTO
        CRMAccountHierarchyDto dto = extractAccountHierarchyDto(parameterConfig, values);

        // retrieve the type of account hierarchy based on the dto that was
        // received.
        AccountHierarchyTypeEnum accountHierarchyTypeEnum = extractAccountHierarchyTypeEnum(dto);

        // using the account hierarchy type and dto, get the corresponding
        // entity that will be checked for authorization.
        BusinessEntity entity = getEntity(accountHierarchyTypeEnum, dto);

        return entity;
    }

    private CRMAccountHierarchyDto extractAccountHierarchyDto(SecureMethodParameterConfig parameterConfig, Object[] values) throws InvalidParameterException {

        // get the parameterConfig value based on the index.
        Object parameterValue = values[parameterConfig.getIndex()];

        if (!(parameterValue instanceof CRMAccountHierarchyDto)) {
            throw new InvalidParameterException("Parameter received at index: " + parameterConfig.getIndex() + " is not an instance of CRMAccountHierarchyDto.");
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
