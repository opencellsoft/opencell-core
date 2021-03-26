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

package org.meveo.api.billing;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.billing.AccountingCodeDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.billing.AccountingCodeListResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.AccountingCode;
import org.meveo.service.billing.impl.AccountingCodeService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API class for AccountingCode CRUD.
 *
 * @author Edward P. Legaspi
 * @since 5.0
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 **/
@Stateless
public class AccountingCodeApi extends BaseCrudApi<AccountingCode, AccountingCodeDto> {

    @Inject
    private AccountingCodeService accountingCodeService;

    @Override
    public AccountingCode create(AccountingCodeDto postData) throws BusinessException, MeveoApiException {
        if (postData.getChartOfAccountTypeEnum() == null) {
            missingParameters.add("chartOfAccountTypeEnum");
        }
        if (postData.getChartOfAccountViewTypeEnum() == null) {
            missingParameters.add("chartOfAccountViewTypeEnum");
        }

        handleMissingParameters();

        if (accountingCodeService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(AccountingCode.class, postData.getCode());
        }

        AccountingCode accountingCode = convertFromDto(postData, null);
        accountingCodeService.create(accountingCode);

        return accountingCode;
    }

    @Override
    public AccountingCode update(AccountingCodeDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (postData.getChartOfAccountTypeEnum() == null) {
            missingParameters.add("chartOfAccountTypeEnum");
        }
        if (postData.getChartOfAccountViewTypeEnum() == null) {
            missingParameters.add("chartOfAccountViewTypeEnum");
        }

        handleMissingParameters();

        AccountingCode accountingCode = accountingCodeService.findByCode(postData.getCode());
        if (accountingCode == null) {
            throw new EntityDoesNotExistsException(AccountingCode.class, postData.getCode());
        }

        convertFromDto(postData, accountingCode);
        accountingCode = accountingCodeService.update(accountingCode);

        return accountingCode;
    }

    public AccountingCodeDto find(String accountingCode) throws EntityDoesNotExistsException {
        if (StringUtils.isBlank(accountingCode)) {
            missingParameters.add("accountingCode");
        }
        AccountingCode ac = accountingCodeService.findByCode(accountingCode);
        if (ac == null) {
            throw new EntityDoesNotExistsException(AccountingCode.class, accountingCode);
        }

        return new AccountingCodeDto(ac);
    }

    public AccountingCodeListResponseDto list(PagingAndFiltering pagingAndFiltering) throws InvalidParameterException {
        PaginationConfiguration paginationConfiguration = toPaginationConfiguration("id", org.primefaces.model.SortOrder.ASCENDING, null, pagingAndFiltering, AccountingCode.class);

        Long totalCount = accountingCodeService.count(paginationConfiguration);

        AccountingCodeListResponseDto result = new AccountingCodeListResponseDto();

        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<AccountingCode> accountingCodes = accountingCodeService.list(paginationConfiguration);
            if (accountingCodes != null) {
                result.setAccountingCodes(accountingCodes.stream().map(p -> new AccountingCodeDto(p)).collect(Collectors.toList()));
            }
        }

        return result;
    }

    public AccountingCodeListResponseDto list() {
        AccountingCodeListResponseDto result = new AccountingCodeListResponseDto();
        result.setPaging( GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering() );

        List<AccountingCode> accountingCodes = accountingCodeService.list( GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration() );
        if (accountingCodes != null) {
            for (AccountingCode accountingCode : accountingCodes) {
                result.getAccountingCodes().add(new AccountingCodeDto(accountingCode));
            }
        }

        return result;
    }

    public AccountingCode convertFromDto(AccountingCodeDto dto, AccountingCode accountingCodeToUpdate) {

        AccountingCode accountingCode = accountingCodeToUpdate;
        if (accountingCode == null) {
            accountingCode = new AccountingCode();
            accountingCode.setCode(dto.getCode());
            if (dto.isDisabled() != null) {
                accountingCode.setDisabled(dto.isDisabled());
            }
        }

        accountingCode.setChartOfAccountTypeEnum(dto.getChartOfAccountTypeEnum());
        accountingCode.setChartOfAccountViewTypeEnum(dto.getChartOfAccountViewTypeEnum());
        accountingCode.setDescription(dto.getDescription());
        accountingCode.setNotes(dto.getNotes());
        accountingCode.setReportingAccount(dto.getReportingAccount());
        accountingCode.setMigrated(dto.isMigrated());

        return accountingCode;
    }
}
