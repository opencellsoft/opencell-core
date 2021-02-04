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

package org.meveo.api.catalog;

import java.util.Arrays;
import java.util.function.BiFunction;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.elasticsearch.common.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.meveo.service.cpq.AttributeService;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 **/
@Stateless
public class UsageChargeTemplateApi extends ChargeTemplateApi<UsageChargeTemplate, UsageChargeTemplateDto> {

    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;
    
    @Inject private AttributeService attributeService;

    @Override
    public UsageChargeTemplate create(UsageChargeTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        } 
        
        handleMissingParametersAndValidate(postData);

        UsageChargeTemplate chargeTemplate = usageChargeTemplateService.findByCode(postData.getCode());
        if (chargeTemplate != null) {
            throw new EntityAlreadyExistsException(UsageChargeTemplate.class, postData.getCode());
        }

        chargeTemplate = dtoToEntity(postData, null);

        usageChargeTemplateService.create(chargeTemplate);
        return chargeTemplate;
    }

    @Override
    public UsageChargeTemplate update(UsageChargeTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParametersAndValidate(postData);

        UsageChargeTemplate chargeTemplate = usageChargeTemplateService.findByCode(postData.getCode());
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(UsageChargeTemplate.class, postData.getCode());
        }
        chargeTemplate = dtoToEntity(postData, chargeTemplate);
        return usageChargeTemplateService.update(chargeTemplate);
    }

    /**
     * Convert/update DTO object to an entity object
     *
     * @param postData DTO object
     * @param chargeTemplate Entity object to update
     * @return A new or updated entity object
     * @throws MeveoApiException General API exception
     * @throws BusinessException General exception
     */
    private UsageChargeTemplate dtoToEntity(UsageChargeTemplateDto postData, UsageChargeTemplate chargeTemplate) throws MeveoApiException, BusinessException {

        boolean isNew = chargeTemplate == null;

        if (isNew) {
            chargeTemplate = new UsageChargeTemplate();
            chargeTemplate.setCode(postData.getCode());
        } else {
            chargeTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        }
        
        if(!Strings.isEmpty(postData.getUsageQuantityAttributeCode())) {
        	chargeTemplate.setUsageQuantityAttribute(loadEntityByCode(attributeService, postData.getUsageQuantityAttributeCode(), Attribute.class));
        }

        super.dtoToEntity(postData, chargeTemplate, isNew);

        if (postData.getPriority() != null) {
            chargeTemplate.setPriority(postData.getPriority());
        }
        if (postData.getFilterParam1() != null) {
            chargeTemplate.setFilterParam1(StringUtils.getDefaultIfEmpty(postData.getFilterParam1(), null));
        }
        if (postData.getFilterParam2() != null) {
            chargeTemplate.setFilterParam2(StringUtils.getDefaultIfEmpty(postData.getFilterParam2(), null));
        }
        if (postData.getFilterParam3() != null) {
            chargeTemplate.setFilterParam3(StringUtils.getDefaultIfEmpty(postData.getFilterParam3(), null));
        }
        if (postData.getFilterParam4() != null) {
            chargeTemplate.setFilterParam4(StringUtils.getDefaultIfEmpty(postData.getFilterParam4(), null));
        }
        if (postData.getTriggerNextCharge() != null) {
            chargeTemplate.setTriggerNextCharge(postData.getTriggerNextCharge());
        }
        chargeTemplate.setDropZeroWo(postData.isDropZeroWo());

        if (postData.getTriggerNextChargeEL() != null) {
            chargeTemplate.setTriggerNextChargeEL(StringUtils.getDefaultIfEmpty(postData.getTriggerNextChargeEL(), null));
        }

        return chargeTemplate;
    }

    @Override
    public UsageChargeTemplateDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("usageChargeTemplateCode");
            handleMissingParameters();
        }

        UsageChargeTemplateDto result;

        // check if code already exists
        UsageChargeTemplate chargeTemplate = usageChargeTemplateService.findByCode(code, Arrays.asList("invoiceSubCategory"));
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(UsageChargeTemplateDto.class, code);
        }

        result = new UsageChargeTemplateDto(chargeTemplate, entityToDtoConverter.getCustomFieldsDTO(chargeTemplate, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));

        return result;
    }
    
    @Override
    protected BiFunction<UsageChargeTemplate, CustomFieldsDto, UsageChargeTemplateDto> getEntityToDtoFunction() {
        return UsageChargeTemplateDto::new;
    }

}