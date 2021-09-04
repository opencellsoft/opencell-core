package org.meveo.apiv2.accounting.resource.impl;

import java.util.Optional;

import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.accounting.ImmutableAccountingPeriod;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.accounting.AccountingOperationAction;
import org.meveo.model.accounting.AccountingPeriod;
import org.meveo.model.accounting.AccountingPeriodForceEnum;
import org.meveo.model.accounting.CustomLockOption;
import org.meveo.model.accounting.RegularUserLockOption;
import org.meveo.model.accounting.SubAccountingPeriodTypeEnum;

public class AccountingPeriodMapper extends ResourceMapper<org.meveo.apiv2.accounting.AccountingPeriod, AccountingPeriod> {

	@Override
	public org.meveo.apiv2.accounting.AccountingPeriod toResource(AccountingPeriod entity) {
		try {
			ImmutableAccountingPeriod resource = (ImmutableAccountingPeriod) initResource(ImmutableAccountingPeriod.class, entity);
			return ImmutableAccountingPeriod.builder().from(resource).id(entity.getId()).fiscalYear(entity.getAccountingPeriodYear()).subAccountingPeriodType(entity.getSubAccountingPeriodType().toString())
					.build();
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}
	

	@Override
	public AccountingPeriod toEntity(org.meveo.apiv2.accounting.AccountingPeriod resource) {
		return toEntity(null,resource);
	}


	/**
	 * @param accountingPeriod
	 * @param accountingPeriodResource
	 * @return
	 */
	public AccountingPeriod toEntity(AccountingPeriod toUpdate,
			org.meveo.apiv2.accounting.AccountingPeriod resource) {
		try {
			AccountingPeriod accountingPeriod = Optional.ofNullable(toUpdate).orElse(new AccountingPeriod());
			if(toUpdate==null) {
				Optional.ofNullable(resource.getFiscalYear()).ifPresent(accountingPeriod::setAccountingPeriodYear);
				Optional.ofNullable(resource.getEndDate()).ifPresent(accountingPeriod::setEndDate);
			}
			if(toUpdate==null || !toUpdate.isUseSubAccountingCycles()) {
				accountingPeriod.setUseSubAccountingCycles(resource.getUseSubAccountingPeriods());
			}
			
			Optional.ofNullable(resource.getCustomLockNumberDays()).ifPresent(accountingPeriod::setCustomLockNumberDays);
			Optional.ofNullable(resource.getCustomLockOption()).ifPresent(s->accountingPeriod.setCustomLockOption(CustomLockOption.valueOf(resource.getCustomLockOption())));
			Optional.ofNullable(resource.getSubAccountingPeriodType()).ifPresent(s->accountingPeriod.setSubAccountingPeriodType(SubAccountingPeriodTypeEnum.valueOf(resource.getSubAccountingPeriodType())));
			Optional.ofNullable(resource.getAccountingOperationAction()).ifPresent(s->accountingPeriod.setAccountingOperationAction(AccountingOperationAction.valueOf(resource.getAccountingOperationAction())));
			Optional.ofNullable(resource.getRegularUserLockOption()).ifPresent(s->accountingPeriod.setRegularUserLockOption(RegularUserLockOption.valueOf(resource.getRegularUserLockOption())));
			Optional.ofNullable(resource.getForceOption()).ifPresent(s->accountingPeriod.setForceOption(AccountingPeriodForceEnum.valueOf(resource.getForceOption())));
			Optional.ofNullable(resource.getForceCustomDay()).ifPresent(accountingPeriod::setForceCustomDay);
			return accountingPeriod;
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}

}