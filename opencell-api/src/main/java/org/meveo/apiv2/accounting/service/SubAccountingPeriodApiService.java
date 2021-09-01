
package org.meveo.apiv2.accounting.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.accounting.AccountingPeriod;
import org.meveo.model.accounting.SubAccountingPeriod;
import org.meveo.service.accounting.impl.SubAccountingPeriodService;

public class SubAccountingPeriodApiService  implements ApiService<SubAccountingPeriod> {
	
    @Inject
    private SubAccountingPeriodService subAccountingPeriodService;


	@Override
	public Long getCount(String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubAccountingPeriod create(SubAccountingPeriod baseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param entity
	 */
	public Optional<SubAccountingPeriod> update(SubAccountingPeriod entity) {
		return Optional.of(subAccountingPeriodService.update(entity));
		
	}

	@Override
	public Optional<SubAccountingPeriod> patch(Long id, SubAccountingPeriod baseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SubAccountingPeriod> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<SubAccountingPeriod> findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<SubAccountingPeriod> delete(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<SubAccountingPeriod> findByCode(String code) {
		// TODO Auto-generated method stub
		return null;
	}

	public Optional<SubAccountingPeriod> findByNumber(Integer number) {
		return Optional.of(subAccountingPeriodService.findByNumber(number));
	}

	@Override
	public Optional<SubAccountingPeriod> update(Long id, SubAccountingPeriod baseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateSubAccountingAllUsersStatus(String fiscalYear, String status,
			SubAccountingPeriod subAccountingPeriod) {
		subAccountingPeriodService.updateSubAccountingAllUsersStatus(fiscalYear, status, subAccountingPeriod);
	}
	
	public void updateSubAccountingRegularUsersStatus(String fiscalYear, String status,
			SubAccountingPeriod subAccountingPeriod) {
		subAccountingPeriodService.updateSubAccountingRegularUsersStatus(fiscalYear, status, subAccountingPeriod);
	}



}