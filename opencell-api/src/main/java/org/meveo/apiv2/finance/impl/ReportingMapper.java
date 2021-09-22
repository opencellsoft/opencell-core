package org.meveo.apiv2.finance.impl;

import java.util.List;
import java.math.BigDecimal;

import org.meveo.model.BaseEntity;
import org.meveo.apiv2.finance.TrialBalance;
import org.meveo.apiv2.finance.ReportingPeriodEnum;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.payments.AccountOperation;
import org.meveo.apiv2.finance.TrialBalancesResult;
import org.meveo.apiv2.finance.ImmutableTrialBalance;

public class ReportingMapper extends ResourceMapper<TrialBalance, AccountOperation> {

	public TrialBalance toTrialBalance(List<Object> record, String accountingCode, String accountingLabel, BigDecimal initialBalance, 
			BigDecimal currentCreditBalance, BigDecimal currentDebitBalance, BigDecimal closingBalance) {
		return ImmutableTrialBalance.builder()
				.accountingCode(accountingCode)
				.accountingLabel(accountingLabel)
				.initialBalance(initialBalance)
				.currentCreditBalance(currentCreditBalance)
				.currentDebitBalance(currentDebitBalance)
				.closingBalance(closingBalance)
				.build();
	}

	@Override
	protected TrialBalance toResource(AccountOperation entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AccountOperation toEntity(TrialBalance resource) {
		// TODO Auto-generated method stub
		return null;
	}
}
