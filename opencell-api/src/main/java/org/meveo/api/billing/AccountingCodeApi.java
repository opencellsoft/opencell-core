package org.meveo.api.billing;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

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
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.Subscription;
import org.meveo.service.billing.impl.AccountingCodeService;

/**
 * API class for AccountingCode CRUD.
 * 
 * @author Edward P. Legaspi
 * @since 5.0
 * @lastModifiedVersion 5.0
 **/
@Stateless
public class AccountingCodeApi extends BaseCrudApi<AccountingCode, AccountingCodeDto> {

    @Inject
    private AccountingCodeService accountingCodeService;

    @Override
    public AccountingCode create(AccountingCodeDto postData) throws BusinessException, MeveoApiException {
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
        PaginationConfiguration paginationConfiguration = toPaginationConfiguration("id", org.primefaces.model.SortOrder.ASCENDING, null, pagingAndFiltering, Subscription.class);

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
