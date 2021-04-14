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

package org.meveo.api;

import java.util.function.BiFunction;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.InvoiceCategoryDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.payments.impl.OCCTemplateService;

/**
 * @author Edward P. Legaspi
 * 
 * @lastModifiedVersion 5.1
 **/
@Stateless
public class InvoiceCategoryApi extends BaseCrudApi<InvoiceCategory, InvoiceCategoryDto> {

    @Inject
    private InvoiceCategoryService invoiceCategoryService;

    /** The occ template service. */
    @Inject
    private OCCTemplateService occTemplateService;

    public InvoiceCategory create(InvoiceCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(InvoiceCategory.class.getName(), postData);
        }

        handleMissingParametersAndValidate(postData);

        if (invoiceCategoryService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(InvoiceCategory.class, postData.getCode());
        }

        OCCTemplate occTemplate = null;
        if (!StringUtils.isBlank(postData.getOccTemplateCode())) {
            occTemplate = occTemplateService.findByCode(postData.getOccTemplateCode());
            if (occTemplate == null) {
                throw new EntityDoesNotExistsException(OCCTemplate.class, postData.getOccTemplateCode());
            }
        }

        OCCTemplate occTemplateNegative = null;
        if (!StringUtils.isBlank(postData.getOccTemplateNegativeCode())) {
            occTemplateNegative = occTemplateService.findByCode(postData.getOccTemplateNegativeCode());
            if (occTemplateNegative == null) {
                throw new EntityDoesNotExistsException(OCCTemplate.class, postData.getOccTemplateNegativeCode());
            }
        }

        InvoiceCategory invoiceCategory = new InvoiceCategory();
        invoiceCategory.setCode(postData.getCode());
        invoiceCategory.setDescription(postData.getDescription());
        invoiceCategory.setOccTemplate(occTemplate);
        invoiceCategory.setOccTemplateNegative(occTemplateNegative);
        invoiceCategory.setSortIndex(postData.getSortIndex());

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), invoiceCategory, true, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        invoiceCategory.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));

        invoiceCategoryService.create(invoiceCategory);

        return invoiceCategory;
    }

    public InvoiceCategory update(InvoiceCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(postData.getCode());
        if (invoiceCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceCategory.class, postData.getCode());
        }
        invoiceCategory.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        invoiceCategory.setDescription(postData.getDescription());

        if (postData.getLanguageDescriptions() != null) {
            invoiceCategory.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), invoiceCategory.getDescriptionI18n()));
        }

        OCCTemplate occTemplate = null;
        if (!StringUtils.isBlank(postData.getOccTemplateCode())) {
            occTemplate = occTemplateService.findByCode(postData.getOccTemplateCode());
            if (occTemplate == null) {
                throw new EntityDoesNotExistsException(OCCTemplate.class, postData.getOccTemplateCode());
            }
            invoiceCategory.setOccTemplate(occTemplate);
        }

        OCCTemplate occTemplateNegative = null;
        if (!StringUtils.isBlank(postData.getOccTemplateNegativeCode())) {
            occTemplateNegative = occTemplateService.findByCode(postData.getOccTemplateNegativeCode());
            if (occTemplateNegative == null) {
                throw new EntityDoesNotExistsException(OCCTemplate.class, postData.getOccTemplateNegativeCode());
            }
            invoiceCategory.setOccTemplateNegative(occTemplateNegative);
        }
        if (postData.getSortIndex() != null) {
            invoiceCategory.setSortIndex(postData.getSortIndex());
        }
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), invoiceCategory, false, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        invoiceCategory = invoiceCategoryService.update(invoiceCategory);
        return invoiceCategory;
    }

    public InvoiceCategoryDto find(String code) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("invoiceCategoryCode");
            handleMissingParameters();
        }

        InvoiceCategoryDto result = new InvoiceCategoryDto();

        InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(code);
        if (invoiceCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceCategory.class, code);
        }

        result = new InvoiceCategoryDto(invoiceCategory, entityToDtoConverter.getCustomFieldsDTO(invoiceCategory, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));

        return result;
    }

    public void remove(String code) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("invoiceCategoryCode");
            handleMissingParameters();
        }

        InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(code);
        if (invoiceCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceCategory.class, code);
        }

        invoiceCategoryService.remove(invoiceCategory);
    }

    /**
     * Creates or updates invoice category based on the code. If passed invoice category is not yet existing, it will be created else will be updated.
     * 
     * @param postData posted data.
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public InvoiceCategory createOrUpdate(InvoiceCategoryDto postData) throws MeveoApiException, BusinessException {

        if(!StringUtils.isBlank(postData.getCode())
                && invoiceCategoryService.findByCode(postData.getCode()) != null) {
            return update(postData);
        } else {
            return create(postData);

        }
    }
    
    @Override
    protected BiFunction<InvoiceCategory, CustomFieldsDto, InvoiceCategoryDto> getEntityToDtoFunction() {
        return InvoiceCategoryDto::new;
    }

}