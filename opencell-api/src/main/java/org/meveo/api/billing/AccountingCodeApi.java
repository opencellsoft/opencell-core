package org.meveo.api.billing;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.billing.AccountingCodeDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.AccountingCode;
import org.meveo.service.billing.impl.AccountingCodeService;

/**
 * @author Edward P. Legaspi
 * @created 23 Feb 2018
 **/
@Stateless
public class AccountingCodeApi extends BaseApi {

    @Inject
    private AccountingCodeService accountingCodeService;

    public void create(AccountingCodeDto postData) throws BusinessException, MissingParameterException, EntityAlreadyExistsException {
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

    public AccountingCode toAccountingCode(AccountingCodeDto source, AccountingCode target) {
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

}
