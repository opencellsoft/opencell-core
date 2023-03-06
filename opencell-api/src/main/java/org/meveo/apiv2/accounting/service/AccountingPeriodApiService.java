
package org.meveo.apiv2.accounting.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.accounting.AccountingPeriod;
import org.meveo.model.accounting.AccountingPeriodActionLevelEnum;
import org.meveo.service.accounting.impl.AccountingPeriodService;

import static java.util.Optional.ofNullable;

public class AccountingPeriodApiService  implements ApiService<AccountingPeriod> {
	
    @Inject
    private AccountingPeriodService accountingPeriodService;

	@Override
	public List<AccountingPeriod> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getCount(String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<AccountingPeriod> findById(Long id) {
		// TODO Auto-generated method stub
		return ofNullable(accountingPeriodService.findById(id));
	}

	@Override
	public AccountingPeriod create(AccountingPeriod baseEntity) {
		accountingPeriodService.create(baseEntity);
		return baseEntity;
	}

	@Override
	public Optional<AccountingPeriod> update(Long id, AccountingPeriod baseEntity) {
		return Optional.of(accountingPeriodService.update(baseEntity));
	}

	@Override
	public Optional<AccountingPeriod> patch(Long id, AccountingPeriod baseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<AccountingPeriod> delete(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<AccountingPeriod> findByCode(String code) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param entity
	 * @param isUseSubAccountingPeriods
	 * @return
	 */
	public AccountingPeriod create(AccountingPeriod entity, Boolean isUseSubAccountingPeriods) {
		return accountingPeriodService.create(entity, isUseSubAccountingPeriods);
	}

	/**
	 * @param fiscalYear
	 * @return
	 */
	public Optional<AccountingPeriod> findByFiscalYear(String fiscalYear) {
		return Optional.ofNullable(accountingPeriodService.findByAccountingPeriodYear(fiscalYear));
	}

	/**
	 * @param entity old accounting period
	 * @param newValue the new accounting period
	 */
	public Optional<AccountingPeriod> update(AccountingPeriod entity, AccountingPeriod newValue) {
		return Optional.of(accountingPeriodService.update(entity, newValue));
		
	}

	/**
	 * @return
	 */
	public Optional<AccountingPeriod> findOpenAccountingPeriod() {
		return Optional.of(accountingPeriodService.findLastAccountingPeriod());
	}

	/**
	 * @return
	 */
	public Optional<AccountingPeriod> generateNextAP() {
		return Optional.of(accountingPeriodService.generateNextAP());
	}

	/**
	 * Update the status of a fiscal year
	 * @param entity {@link AccountingPeriod}
	 * @param status Status
	 * @param fiscalYear Fiscal Year
	 * @return {@link AccountingPeriod}
	 */
	public AccountingPeriod updateStatus(AccountingPeriod entity, String status, String fiscalYear) {
		return accountingPeriodService.updateStatus(entity, status, fiscalYear);

	}
	
	public AccountingPeriod updateStatus(AccountingPeriod entity, String status, String fiscalYear, AccountingPeriodActionLevelEnum level) {
		return accountingPeriodService.updateStatus(entity, status, fiscalYear, level);

	}
}