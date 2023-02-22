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

import static java.util.Optional.ofNullable;
import static java.math.BigDecimal.ZERO;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.TaxesDto;
import org.meveo.api.dto.response.GetTaxesResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.*;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UntdidTaxationCategory;
import org.meveo.model.billing.UntdidVatex;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.billing.impl.AccountingCodeService;
import org.meveo.service.billing.impl.UntdidTaxationCategoryService;
import org.meveo.service.billing.impl.UntdidVatexService;
import org.meveo.service.catalog.impl.TaxService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD API for managing {@link Tax}.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 **/
@Stateless
public class TaxApi extends BaseApi {

    @Inject
    private TaxService taxService;
    
    @Inject
    private AccountingCodeService accountingCodeService;
    
    @Inject
    private UntdidTaxationCategoryService untdidTaxationCategoryService;
    
    @Inject
    private UntdidVatexService untdidVatexService;
    
    private static final String SUBTAXES_MESSAGE_EXCEPTION = "SubTaxes must contain at least two taxes";

    public Tax create(TaxDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(Tax.class.getName(), postData);
        }
        validateTaxInput(postData);
        
        // check if tax exists
        if (taxService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(Tax.class, postData.getCode());
        }
        Tax tax = new Tax();
        tax.setCode(postData.getCode());
        tax.setDescription(postData.getDescription());
        tax.setComposite(postData.getComposite());
        if(tax.isComposite()) {
            List<Tax> subTaxes = toEntity(postData.getSubTaxes());
            validateSubTaxes(subTaxes);
            tax.setPercent(subTaxes.stream().map(Tax::getPercent).reduce(ZERO, BigDecimal::add));
            tax.setSubTaxes(subTaxes);
        } else {
            tax.setPercent(postData.getPercent());
        }
        if (!StringUtils.isBlank(postData.getAccountingCode())) {
            AccountingCode accountingCode = accountingCodeService.findByCode(postData.getAccountingCode());
            if (accountingCode == null) {
                throw new EntityDoesNotExistsException(AccountingCode.class, postData.getAccountingCode());
            }
            tax.setAccountingCode(accountingCode);
        }
        if (!StringUtils.isBlank(postData.getTaxationCategory())) {
            UntdidTaxationCategory untdidTaxationCategory = untdidTaxationCategoryService.getByCode(postData.getTaxationCategory());
            if (untdidTaxationCategory == null) {
                throw new EntityDoesNotExistsException(UntdidTaxationCategory.class, postData.getTaxationCategory());
            }
            tax.setUntdidTaxationCategory(untdidTaxationCategory);
        }
        else {
        	//taxationCategory, defaut=(S,”Standard rate”)
        	UntdidTaxationCategory untdidTaxationCategory = untdidTaxationCategoryService.getByCode("S");
        	tax.setUntdidTaxationCategory(untdidTaxationCategory);
        }
        if (!StringUtils.isBlank(postData.getVatex())) {
            UntdidVatex untdidVatex = untdidVatexService.getByCode(postData.getVatex());
            if (untdidVatex == null) {
                throw new EntityDoesNotExistsException(UntdidVatex.class, postData.getVatex());
            }
            tax.setUntdidVatex(untdidVatex);
        }
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), tax, true, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        tax.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));

        taxService.create(tax);
        
        return tax;
    }

    private void validateTaxInput(TaxDto postData) {
        if(postData.getComposite()) {
            if(postData.getSubTaxes() == null || postData.getSubTaxes().size() < 2) {
                throw new BadRequestException(SUBTAXES_MESSAGE_EXCEPTION);
            }
        } else {
            if (StringUtils.isBlank(postData.getPercent())) {
                throw new BadRequestException("Percent is required");
            }
            if(postData.getPercent().compareTo(ZERO) < 0
                    || postData.getPercent().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new BadRequestException("Percent must be between 0 and 100");
            }
            if(postData.getSubTaxes() != null && !postData.getSubTaxes().isEmpty()) {
                throw new BadRequestException("Sub taxes must be empty if composition not enabled");
            }
        }
    }

    private List<Tax> toEntity(List<TaxDto> subTaxesInput) {
        List<Tax> subTaxes = new ArrayList<>();
        for (TaxDto subTaxDto : subTaxesInput) {
            Tax subTax = ofNullable(taxService.findById(subTaxDto.getId()))
                                .orElseThrow(()
                                        -> new NotFoundException("Sub tax id : " + subTaxDto.getId() + " does not exists"));
            subTaxes.add(subTax);
        }
        return subTaxes;
    }

    private void validateSubTaxes(List<Tax> subTaxes) {
        for (Tax subTax : subTaxes) {
            if(subTax.isComposite()) {
                throw new BadRequestException("Sub tax id : " + subTax.getId() + " must not be composite");
            }
        }
    }

    public Tax update(TaxDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        if (StringUtils.isBlank(postData.getPercent())) {
            missingParameters.add("percent");
        }

        handleMissingParametersAndValidate(postData);

        // check if tax exists
        Tax tax = taxService.findByCode(postData.getCode());
        if (tax == null) {
            throw new EntityDoesNotExistsException(Tax.class, postData.getCode());
        }
        tax.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        tax.setDescription(postData.getDescription());
        tax.setComposite(postData.getComposite());
        if(tax.isComposite()) {
        	//Check if the subTaxes are null or size lower than 2
        	if(postData.getSubTaxes() == null || postData.getSubTaxes().size() < 2) {
                throw new BadRequestException(SUBTAXES_MESSAGE_EXCEPTION);
            } else {
            	List<Tax> subTaxes = toEntity(postData.getSubTaxes());
            	if(subTaxes.stream().anyMatch(st -> st.getId().equals(postData.getId()))) {
            		throw new BadRequestException("A tax cannot be sub-tax of itself");
            	}
                validateSubTaxes(subTaxes);
                tax.setPercent(subTaxes.stream().map(Tax::getPercent).reduce(ZERO, BigDecimal::add));
                tax.setSubTaxes(subTaxes);
            }
        } else {
            if (tax.getSubTaxes() != null && !tax.getSubTaxes().isEmpty()) {
                tax.getSubTaxes().clear();
            }
            tax.setPercent(postData.getPercent());
        }
        if (!StringUtils.isBlank(postData.getAccountingCode())) {
            AccountingCode accountingCode = accountingCodeService.findByCode(postData.getAccountingCode());
            if (accountingCode == null) {
                throw new EntityDoesNotExistsException(AccountingCode.class, postData.getAccountingCode());
            }
            tax.setAccountingCode(accountingCode);
        } else {
        	tax.setAccountingCode(null);
        }

        if (postData.getLanguageDescriptions() != null) {
            tax.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), tax.getDescriptionI18n()));
        }
        if (!StringUtils.isBlank(postData.getTaxationCategory())) {
            UntdidTaxationCategory untdidTaxationCategory = untdidTaxationCategoryService.getByCode(postData.getTaxationCategory());
            if (untdidTaxationCategory == null) {
                throw new EntityDoesNotExistsException(UntdidTaxationCategory.class, postData.getTaxationCategory());
            }
            tax.setUntdidTaxationCategory(untdidTaxationCategory);
        }
        if (!StringUtils.isBlank(postData.getVatex())) {
            UntdidVatex untdidVatex = untdidVatexService.getByCode(postData.getVatex());
            if (untdidVatex == null) {
                throw new EntityDoesNotExistsException(UntdidVatex.class, postData.getVatex());
            }
            tax.setUntdidVatex(untdidVatex);
        }
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), tax, true, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        taxService.update(tax);

        return tax;
    }

    public TaxDto find(String taxCode) throws MeveoApiException {

        if (StringUtils.isBlank(taxCode)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        TaxDto result;

        Tax tax = taxService.findByCode(taxCode);
        if (tax == null) {
            throw new EntityDoesNotExistsException(Tax.class, taxCode);
        }

        result = new TaxDto(tax, entityToDtoConverter.getCustomFieldsDTO(tax, CustomFieldInheritanceEnum.INHERIT_NO_MERGE), false);

        return result;
    }

    public ActionStatus remove(String taxCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(taxCode)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        ActionStatus result = new ActionStatus();

        Tax tax = taxService.findByCode(taxCode);
        if (tax == null) {
            throw new EntityDoesNotExistsException(Tax.class, taxCode);
        }

        taxService.remove(tax);
        return result;
    }

    public TaxDto createOrUpdate(TaxDto postData) throws MeveoApiException, BusinessException {
        if(!StringUtils.isBlank(postData.getCode()) && taxService.findByCode(postData.getCode()) != null) {
            return new TaxDto(update(postData));
        } else {
            return new TaxDto(create(postData));
        }
    }

    public TaxesDto list() throws MeveoApiException {
        TaxesDto taxesDto = new TaxesDto();

        List<Tax> taxes = taxService.list();
        if (taxes != null && !taxes.isEmpty()) {
            for (Tax tax : taxes) {
                TaxDto taxDto = new TaxDto(tax, entityToDtoConverter.getCustomFieldsDTO(tax, CustomFieldInheritanceEnum.INHERIT_NO_MERGE), false);
                taxesDto.getTax().add(taxDto);
            }
        }

        return taxesDto;
    }

    public GetTaxesResponse list(PagingAndFiltering pagingAndFiltering) {
        GetTaxesResponse result = new GetTaxesResponse();
        result.setPaging( pagingAndFiltering );

        List<Tax> taxes = taxService.list( GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration() );
        if (taxes != null) {
            for (Tax tax : taxes) {
                result.getTaxesDto().getTax().add(new TaxDto(tax, entityToDtoConverter.getCustomFieldsDTO(tax, CustomFieldInheritanceEnum.INHERIT_NO_MERGE), false));
            }
        }

        return result;
    }
}
