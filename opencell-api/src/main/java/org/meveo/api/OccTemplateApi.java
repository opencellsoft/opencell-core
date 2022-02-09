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
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.OccTemplateDto;
import org.meveo.api.dto.payment.AccountingSchemeDto;
import org.meveo.api.dto.response.GetOccTemplatesResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.payments.AccountingScheme;
import org.meveo.model.payments.Journal;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.billing.impl.AccountingCodeService;
import org.meveo.service.payments.impl.AccountingSchemeService;
import org.meveo.service.payments.impl.JournalReportService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Stateless
public class OccTemplateApi extends BaseApi {

    @Inject
    private OCCTemplateService occTemplateService;

    @Inject
    private AccountingCodeService accountingCodeService;

    @Inject
    private JournalReportService journalService;

    @Inject
    private AccountingSchemeService accountingSchemeService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public void create(OccTemplateDto postData) throws MeveoApiException, BusinessException {
        validateRequiredParams(postData);

        if (occTemplateService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(OCCTemplate.class, postData.getCode());
        }

        OCCTemplate occTemplate = new OCCTemplate();
        occTemplate.setCode(postData.getCode());
        occTemplate.setDescription(postData.getDescription());
        validateAndSetAccountingCode(postData, occTemplate);
        occTemplate.setAccountCodeClientSide(postData.getAccountCodeClientSide());
        occTemplate.setOccCategory(postData.getOccCategory());
        if (!StringUtils.isBlank(postData.getJournalCode())) {
            Journal journal = journalService.findByCode(postData.getJournalCode());
            if (journal == null) {
                throw new EntityDoesNotExistsException(Journal.class, postData.getJournalCode());
            }
            occTemplate.setJournal(journal);
        }

        if (postData.getAccountingScheme() != null) {
            AccountingScheme accountingScheme = createOrUpdateAccountingScheme(postData);
            occTemplate.setAccountingScheme(accountingScheme);
        }
        occTemplateService.create(occTemplate);
    }

    public void update(OccTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getAccountingCode()) && StringUtils.isBlank(postData.getAccountCode())) {
            missingParameters.add("accountCode / accountingCode");
        }
        if (StringUtils.isBlank(postData.getOccCategory())) {
            missingParameters.add("occCategory");
        }

        handleMissingParametersAndValidate(postData);

        OCCTemplate occTemplate = occTemplateService.findByCode(postData.getCode());
        if (occTemplate == null) {
            throw new EntityDoesNotExistsException(OCCTemplate.class, postData.getCode());
        }
        occTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        occTemplate.setDescription(postData.getDescription());
        validateAndSetAccountingCode(postData, occTemplate);
        occTemplate.setAccountCodeClientSide(postData.getAccountCodeClientSide());
        occTemplate.setOccCategory(postData.getOccCategory());

        if (!StringUtils.isBlank(postData.getJournalCode())) {
            Journal journal = journalService.findByCode(postData.getJournalCode());
            if (journal == null) {
                throw new EntityDoesNotExistsException(Journal.class, postData.getJournalCode());
            }
            occTemplate.setJournal(journal);
        }

        if (postData.getAccountingScheme() == null) {
            // delete old one
            AccountingScheme accountingScheme = occTemplate.getAccountingScheme();
            if (accountingScheme != null) {
                accountingSchemeService.remove(accountingScheme);
            }
            occTemplate.setAccountingScheme(null);
        } else {
            AccountingScheme accountingScheme = createOrUpdateAccountingScheme(postData);
            occTemplate.setAccountingScheme(accountingScheme);
        }

        occTemplateService.update(occTemplate);

    }

    public OccTemplateDto find(String code) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("occTemplateCode");
            handleMissingParameters();
        }

        OCCTemplate occTemplate = occTemplateService.findByCode(code);
        if (occTemplate == null) {
            throw new EntityDoesNotExistsException(OCCTemplate.class, code);
        }

        return new OccTemplateDto(occTemplate);

    }

    /**
     * @param code account operation code
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void remove(String code) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("occTemplateCode");
            handleMissingParameters();
        }

        OCCTemplate occTemplate = occTemplateService.findByCode(code);
        if (occTemplate == null) {
            throw new EntityDoesNotExistsException(OCCTemplate.class, code);
        }

        occTemplateService.remove(occTemplate);
    }

    /**
     * create or update occ template based on occ template code.
     *
     * @param postData posted data.
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void createOrUpdate(OccTemplateDto postData) throws MeveoApiException, BusinessException {

        if (!StringUtils.isBlank(postData.getCode()) && occTemplateService.findByCode(postData.getCode()) != null) {
            update(postData);
        } else {
            create(postData);
        }
    }

    /**
     * retrieve a list of occ templates.
     *
     * @param pagingAndFiltering pagingAndFiltering
     * @return OCCTemplateDto.
     * @throws MeveoApiException meveo api exception.
     */
    public GetOccTemplatesResponseDto list(PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
        PaginationConfiguration paginationConfiguration = toPaginationConfiguration("id", PagingAndFiltering.SortOrder.DESCENDING, null, pagingAndFiltering, OCCTemplate.class);

        Long totalCount = occTemplateService.count(paginationConfiguration);

        GetOccTemplatesResponseDto result = new GetOccTemplatesResponseDto();

        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<OCCTemplate> occTemplates = occTemplateService.list(paginationConfiguration);
            if (occTemplates != null) {
                result.getOccTemplates().setOccTemplate(occTemplates.stream().map(OccTemplateDto::new).collect(Collectors.toList()));
            }
        }
        return result;
    }

    /**
     * create or update an accounting scheme.
     *
     * @param dto an accounting scheme DTO
     * @return an accounting scheme
     */
    private AccountingScheme createOrUpdateAccountingScheme(OccTemplateDto dto) {

        AccountingSchemeDto accSchemeDto = dto.getAccountingScheme();
        ScriptInstance scriptInstance = scriptInstanceService.findByCode(accSchemeDto.getScriptCode());

        if (scriptInstance == null)
            throw new EntityDoesNotExistsException(ScriptInstance.class, accSchemeDto.getScriptCode());
        else if ("FILE_ACCOUNTING_SCHEMES".equals(scriptInstance.getScriptInstanceCategory().getCode())) {
            throw new MissingParameterException("the script with code=" + accSchemeDto.getScriptCode() + " does not belong to category ‘File accounting schemes’");
        }

        String code = accSchemeDto.getCode();
        if (StringUtils.isNotBlank(code)) {
            AccountingScheme accountingScheme = accountingSchemeService.findByCode(code);

            if (accountingScheme == null) {
                createNewAccountingScheme(accSchemeDto, scriptInstance, code);
            } else {
                updateTheAccountingScheme(accSchemeDto, scriptInstance, accountingScheme);
            }
        } else if (StringUtils.isNotBlank(accSchemeDto.getLongDescription())) {
            code = dto.getCode() + "-" + getGenericCode(OCCTemplate.class.getSimpleName());
            createNewAccountingScheme(accSchemeDto, scriptInstance, code);
        }
        return accountingSchemeService.findByCode(code);
    }

    private void updateTheAccountingScheme(AccountingSchemeDto accSchemeDto, ScriptInstance scriptInstance, AccountingScheme accountingScheme) {
        accountingScheme.setDescription(accSchemeDto.getDescription());
        accountingScheme.setLongDescription(accSchemeDto.getLongDescription());
        accountingScheme.setScriptInstance(scriptInstance);
        accountingSchemeService.update(accountingScheme);
    }

    private void createNewAccountingScheme(AccountingSchemeDto accSchemeDto, ScriptInstance scriptInstance, String code) {
        AccountingScheme accountingScheme;
        accountingScheme = new AccountingScheme();
        accountingScheme.setCode(code);
        accountingScheme.setDescription(accSchemeDto.getDescription());
        accountingScheme.setLongDescription(accSchemeDto.getLongDescription());
        accountingScheme.setScriptInstance(scriptInstance);
        accountingSchemeService.create(accountingScheme);
    }

    private void validateRequiredParams(OccTemplateDto postData) {
        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(OCCTemplate.class.getName(), postData);
        }
        if (StringUtils.isBlank(postData.getAccountingCode()) && StringUtils.isBlank(postData.getAccountCode())) {
            missingParameters.add("accountCode / accountingCode");
        }
        if (StringUtils.isBlank(postData.getOccCategory())) {
            missingParameters.add("occCategory");
        }
        if (postData.getAccountingScheme() != null && StringUtils.isNotBlank(postData.getAccountingScheme().getScriptCode())) {
            missingParameters.add("accountingScheme -> scriptCode");
        }

        handleMissingParametersAndValidate(postData);
    }

    private void validateAndSetAccountingCode(OccTemplateDto postData, OCCTemplate occTemplate) {
        if (!StringUtils.isBlank(postData.getAccountingCode())) {
            AccountingCode accountingCode = accountingCodeService.findByCode(postData.getAccountingCode());
            if (accountingCode == null) {
                throw new EntityDoesNotExistsException(AccountingCode.class, postData.getAccountingCode());
            }
            occTemplate.setAccountingCode(accountingCode);
        } else {
            if (!StringUtils.isBlank(postData.getAccountCode())) {
                AccountingCode accountingCode = accountingCodeService.findByCode(postData.getAccountCode());
                if (accountingCode == null) {
                    throw new EntityDoesNotExistsException(AccountingCode.class, postData.getAccountCode());
                }
                occTemplate.setAccountingCode(accountingCode);
            }
        }
    }
}