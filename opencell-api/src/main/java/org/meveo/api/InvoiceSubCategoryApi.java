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

import java.util.Arrays;
import java.util.function.BiFunction;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.InvoiceSubCategoryDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.service.billing.impl.AccountingCodeService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.payments.impl.OCCTemplateService;

/**
 * CRUD API for managing {@link InvoiceSubCategory}.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 **/
@Stateless
public class InvoiceSubCategoryApi extends BaseCrudApi<InvoiceSubCategory, InvoiceSubCategoryDto> {

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private InvoiceCategoryService invoiceCategoryService;

    @Inject
    private AccountingCodeService accountingCodeService;

    /** The occ template service. */
    @Inject
    private OCCTemplateService occTemplateService;

    public InvoiceSubCategory create(InvoiceSubCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(InvoiceSubCategory.class.getName(), postData);
        }
        if (StringUtils.isBlank(postData.getInvoiceCategory())) {
            missingParameters.add("invoiceCategory");
        }

        handleMissingParametersAndValidate(postData);

        if (invoiceSubCategoryService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(InvoiceSubCategory.class, postData.getCode());
        }

        // check if invoice cat exists
        InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(postData.getInvoiceCategory());
        if (invoiceCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceCategory.class, postData.getInvoiceCategory());
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

        InvoiceSubCategory invoiceSubCategory = new InvoiceSubCategory();
        invoiceSubCategory.setInvoiceCategory(invoiceCategory);
        invoiceSubCategory.setCode(postData.getCode());
        invoiceSubCategory.setDescription(postData.getDescription());
        invoiceSubCategory.setOccTemplate(occTemplate);
        invoiceSubCategory.setOccTemplateNegative(occTemplateNegative);
        if (!StringUtils.isBlank(postData.getAccountingCode())) {
            AccountingCode accountingCode = accountingCodeService.findByCode(postData.getAccountingCode());
            if (accountingCode == null) {
                throw new EntityDoesNotExistsException(AccountingCode.class, postData.getAccountingCode());
            }
            invoiceSubCategory.setAccountingCode(accountingCode);
        }
        invoiceSubCategory.setSortIndex(postData.getSortIndex());
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), invoiceSubCategory, true, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        invoiceSubCategory.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));

        invoiceSubCategoryService.create(invoiceSubCategory);
        
        return invoiceSubCategory;
    }

    public InvoiceSubCategory update(InvoiceSubCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getInvoiceCategory())) {
            missingParameters.add("invoiceCategory");
        }

        handleMissingParametersAndValidate(postData);

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(postData.getCode());
        if (invoiceSubCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceSubCategory.class, postData.getCode());
        }

        // check if invoice cat exists
        InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(postData.getInvoiceCategory());
        if (invoiceCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceCategory.class, postData.getInvoiceCategory());
        }
        invoiceSubCategory.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        invoiceSubCategory.setInvoiceCategory(invoiceCategory);
        invoiceSubCategory.setDescription(postData.getDescription());
        if (!StringUtils.isBlank(postData.getAccountingCode())) {
            AccountingCode accountingCode = accountingCodeService.findByCode(postData.getAccountingCode());
            if (accountingCode == null) {
                throw new EntityDoesNotExistsException(AccountingCode.class, postData.getAccountingCode());
            }
            invoiceSubCategory.setAccountingCode(accountingCode);
        }

        if (postData.getLanguageDescriptions() != null) {
            invoiceSubCategory.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), invoiceSubCategory.getDescriptionI18n()));
        }

        OCCTemplate occTemplate = null;
        if (!StringUtils.isBlank(postData.getOccTemplateCode())) {
            occTemplate = occTemplateService.findByCode(postData.getOccTemplateCode());
            if (occTemplate == null) {
                throw new EntityDoesNotExistsException(OCCTemplate.class, postData.getOccTemplateCode());
            }
            invoiceSubCategory.setOccTemplate(occTemplate);
        }

        OCCTemplate occTemplateNegative = null;
        if (!StringUtils.isBlank(postData.getOccTemplateNegativeCode())) {
            occTemplateNegative = occTemplateService.findByCode(postData.getOccTemplateNegativeCode());
            if (occTemplateNegative == null) {
                throw new EntityDoesNotExistsException(OCCTemplate.class, postData.getOccTemplateNegativeCode());
            }
            invoiceSubCategory.setOccTemplateNegative(occTemplateNegative);
        }
        if (postData.getSortIndex() != null) {
            invoiceSubCategory.setSortIndex(postData.getSortIndex());
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), invoiceSubCategory, false, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        invoiceSubCategory = invoiceSubCategoryService.update(invoiceSubCategory);
        
        return invoiceSubCategory;
    }

    public InvoiceSubCategoryDto find(String code) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("invoiceSubCategoryCode");
            handleMissingParameters();
        }

        InvoiceSubCategoryDto result = new InvoiceSubCategoryDto();

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(code, Arrays.asList("invoiceCategory"));
        if (invoiceSubCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceSubCategory.class, code);
        }

        result = new InvoiceSubCategoryDto(invoiceSubCategory, entityToDtoConverter.getCustomFieldsDTO(invoiceSubCategory, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));

        return result;
    }

    public void remove(String code) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("invoiceSubCategoryCode");
            handleMissingParameters();
        }

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(code);
        if (invoiceSubCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceSubCategory.class, code);
        }

        invoiceSubCategoryService.remove(invoiceSubCategory);

    }

    /**
     * Create or update invoice subcategory based on code.
     * 
     * @param postData posted data to API
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public InvoiceSubCategory createOrUpdate(InvoiceSubCategoryDto postData) throws MeveoApiException, BusinessException {
        if (postData.getCode() != null && invoiceSubCategoryService.findByCode(postData.getCode()) != null) {
            return update(postData);
        } else {
            return create(postData);
        }
    }
    
    @Override
    protected BiFunction<InvoiceSubCategory, CustomFieldsDto, InvoiceSubCategoryDto> getEntityToDtoFunction() {
        return InvoiceSubCategoryDto::new;
    }
}