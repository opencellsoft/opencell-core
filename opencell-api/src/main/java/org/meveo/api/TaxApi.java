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

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.TaxesDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.Tax;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.billing.impl.AccountingCodeService;
import org.meveo.service.catalog.impl.TaxService;

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

    public ActionStatus create(TaxDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        if (StringUtils.isBlank(postData.getPercent())) {
            missingParameters.add("percent");
        }

        handleMissingParametersAndValidate(postData);

        ActionStatus result = new ActionStatus();

        // check if tax exists
        if (taxService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(Tax.class, postData.getCode());
        }

        Tax tax = new Tax();
        tax.setCode(postData.getCode());
        tax.setDescription(postData.getDescription());
        tax.setPercent(postData.getPercent());
        if (!StringUtils.isBlank(postData.getAccountingCode())) {
            AccountingCode accountingCode = accountingCodeService.findByCode(postData.getAccountingCode());
            if (accountingCode == null) {
                throw new EntityDoesNotExistsException(AccountingCode.class, postData.getAccountingCode());
            }
            tax.setAccountingCode(accountingCode);
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

        return result;
    }

    public ActionStatus update(TaxDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        if (StringUtils.isBlank(postData.getPercent())) {
            missingParameters.add("percent");
        }

        handleMissingParametersAndValidate(postData);

        ActionStatus result = new ActionStatus();

        // check if tax exists
        Tax tax = taxService.findByCode(postData.getCode());
        if (tax == null) {
            throw new EntityDoesNotExistsException(Tax.class, postData.getCode());
        }
        tax.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        tax.setDescription(postData.getDescription());
        tax.setPercent(postData.getPercent());
        if (!StringUtils.isBlank(postData.getAccountingCode())) {
            AccountingCode accountingCode = accountingCodeService.findByCode(postData.getAccountingCode());
            if (accountingCode == null) {
                throw new EntityDoesNotExistsException(AccountingCode.class, postData.getAccountingCode());
            }
            tax.setAccountingCode(accountingCode);
        }

        if (postData.getLanguageDescriptions() != null) {
            tax.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), tax.getDescriptionI18n()));
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

        tax = taxService.update(tax);

        return result;
    }

    public TaxDto find(String taxCode) throws MeveoApiException {

        if (StringUtils.isBlank(taxCode)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        TaxDto result = new TaxDto();

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

    public void createOrUpdate(TaxDto postData) throws MeveoApiException, BusinessException {
        Tax tax = taxService.findByCode(postData.getCode());

        if (tax == null) {
            create(postData);
        } else {
            update(postData);
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
}
