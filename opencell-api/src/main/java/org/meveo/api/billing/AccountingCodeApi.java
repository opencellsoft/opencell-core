package org.meveo.api.billing;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.billing.AccountingCodeDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.billing.AccountingCodeListResponse;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.Subscription;
import org.meveo.service.billing.impl.AccountingCodeService;

/**
 * @author Edward P. Legaspi
 * @created 23 Feb 2018
 **/
@Stateless
public class AccountingCodeApi extends BaseApi {

    @Inject
    private AccountingCodeService accountingCodeService;

    public void create(AccountingCodeDto postData) throws BusinessException, MeveoApiException {
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

        AccountingCode accountingCode = toAccountingCode(postData, null);
        accountingCodeService.create(accountingCode);
    }

    public void update(AccountingCodeDto postData) throws MeveoApiException, BusinessException {
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

        toAccountingCode(postData, accountingCode);
        accountingCodeService.update(accountingCode);
    }

    public void createOrUpdate(AccountingCodeDto postData) throws MeveoApiException, BusinessException {
        if (accountingCodeService.findByCode(postData.getCode()) != null) {
            update(postData);
        } else {
            create(postData);
        }
    }

    public AccountingCodeDto find(String accountingCode) throws EntityDoesNotExistsException {
        if (StringUtils.isBlank(accountingCode)) {
            missingParameters.add("accountingCode");
        }
        AccountingCode ac = accountingCodeService.findByCode(accountingCode);
        if (ac == null) {
            throw new EntityDoesNotExistsException(AccountingCode.class, accountingCode);
        }

        return fromAccountingCode(ac, null);
    }

    public AccountingCodeListResponse list(PagingAndFiltering pagingAndFiltering) throws InvalidParameterException {
        PaginationConfiguration paginationConfiguration = toPaginationConfiguration("id", org.primefaces.model.SortOrder.ASCENDING, null, pagingAndFiltering, Subscription.class);

        Long totalCount = accountingCodeService.count(paginationConfiguration);

        AccountingCodeListResponse result = new AccountingCodeListResponse();

        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<AccountingCode> accountingCodes = accountingCodeService.list(paginationConfiguration);
            if (accountingCodes != null) {
                result.setAccountingCodes(accountingCodes.stream().map(p -> fromAccountingCode(p, null)).collect(Collectors.toList()));
            }
        }

        return result;
    }

    public void remove(String accountingCode) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(accountingCode)) {
            missingParameters.add("accountingCode");
        }

        handleMissingParameters();

        AccountingCode ac = accountingCodeService.findByCode(accountingCode);
        if (ac == null) {
            throw new EntityDoesNotExistsException(AccountingCode.class, accountingCode);
        }
        accountingCodeService.remove(ac);
    }

    public static AccountingCode toAccountingCode(AccountingCodeDto source, AccountingCode target) {
        if (target == null) {
            target = new AccountingCode();
            target.setCode(source.getCode());
        }

        target.setChartOfAccountTypeEnum(source.getChartOfAccountTypeEnum());
        target.setChartOfAccountViewTypeEnum(source.getChartOfAccountViewTypeEnum());
        target.setDescription(source.getDescription());
        target.setNotes(source.getNotes());
        target.setReportingAccount(source.getReportingAccount());
        target.setDisabled(source.isDisabled());

        return target;
    }

    public static AccountingCodeDto fromAccountingCode(AccountingCode source, AccountingCodeDto target) {
        if (target == null) {
            target = new AccountingCodeDto();
        }

        target.setCode(source.getCode());
        target.setChartOfAccountTypeEnum(source.getChartOfAccountTypeEnum());
        target.setChartOfAccountViewTypeEnum(source.getChartOfAccountViewTypeEnum());
        target.setDescription(source.getDescription());
        target.setNotes(source.getNotes());
        target.setReportingAccount(source.getReportingAccount());
        target.setDisabled(source.isDisabled());

        return target;
    }

}
