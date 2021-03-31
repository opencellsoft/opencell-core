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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.catalog.ProductChargeTemplateDto;
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.catalog.impl.ProductChargeTemplateService;

@Stateless
public class ProductChargeTemplateApi extends ChargeTemplateApi<ProductChargeTemplate, ProductChargeTemplateDto> {

    @Inject
    private ProductChargeTemplateService productChargeTemplateService;

    @Override
    public ProductChargeTemplate create(ProductChargeTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(ProductChargeTemplate.class.getName(), postData);
        }
        if (StringUtils.isBlank(postData.getInvoiceSubCategory())) {
            missingParameters.add("invoiceSubCategory");
        }
        if (StringUtils.isBlank(postData.getTaxClassCode())) {
            missingParameters.add("taxClassCode");
        }

        handleMissingParametersAndValidate(postData);

        // check if code already exists
        if (productChargeTemplateService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(ProductChargeTemplate.class, postData.getCode());
        }

        ProductChargeTemplate chargeTemplate = dtoToEntity(postData, null);

        productChargeTemplateService.create(chargeTemplate);

        return chargeTemplate;
    }

    @Override
    public ProductChargeTemplate update(ProductChargeTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (postData.getInvoiceSubCategory() != null && StringUtils.isBlank(postData.getInvoiceSubCategory())) {
            missingParameters.add("invoiceSubCategory");
        }
        if (postData.getTaxClassCode() != null && StringUtils.isBlank(postData.getTaxClassCode())) {
            missingParameters.add("taxClassCode");
        }

        handleMissingParametersAndValidate(postData);

        // check if code already exists
        ProductChargeTemplate chargeTemplate = productChargeTemplateService.findByCode(postData.getCode());
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(ProductChargeTemplate.class, postData.getCode());
        }

        chargeTemplate = dtoToEntity(postData, chargeTemplate);

        chargeTemplate = productChargeTemplateService.update(chargeTemplate);

        return chargeTemplate;
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
    private ProductChargeTemplate dtoToEntity(ProductChargeTemplateDto postData, ProductChargeTemplate chargeTemplate) throws MeveoApiException, BusinessException {

        boolean isNew = chargeTemplate == null;

        if (isNew) {
            chargeTemplate = new ProductChargeTemplate();
            chargeTemplate.setCode(postData.getCode());
        } else {
            chargeTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        }

        super.dtoToEntity(postData, chargeTemplate, isNew);

        return chargeTemplate;
    }

    @Override
    public ProductChargeTemplateDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("usageChargeTemplateCode");
            handleMissingParameters();
        }

        // check if code already exists
        ProductChargeTemplate chargeTemplate = productChargeTemplateService.findByCode(code, Arrays.asList("invoiceSubCategory"));
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(UsageChargeTemplateDto.class, code);
        }

        ProductChargeTemplateDto result = new ProductChargeTemplateDto(chargeTemplate, entityToDtoConverter.getCustomFieldsDTO(chargeTemplate, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));

        return result;
    }

    public List<ProductChargeTemplateDto> list() {
        List<ProductChargeTemplate> listProductChargeTemplate = productChargeTemplateService.list();
        List<ProductChargeTemplateDto> dtos = new ArrayList<ProductChargeTemplateDto>();
        if (listProductChargeTemplate != null) {
            for (ProductChargeTemplate productChargeTemplate : listProductChargeTemplate) {
                dtos.add(new ProductChargeTemplateDto(productChargeTemplate, null));
            }
        }
        return dtos;
    }
}