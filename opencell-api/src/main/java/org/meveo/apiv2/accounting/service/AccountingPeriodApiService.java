
package org.meveo.apiv2.accounting.service;

import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.accounting.AccountingPeriod;
import org.meveo.service.accounting.impl.AccountingPeriodService;

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


}