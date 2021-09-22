package org.meveo.apiv2.finance.impl;

import java.math.BigDecimal;

import org.meveo.apiv2.finance.ImmutableTrialBalance;
import org.meveo.apiv2.finance.TrialBalance;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.payments.AccountOperation;

public class ReportingMapper extends ResourceMapper<TrialBalance, AccountOperation> {

	public TrialBalance toTrialBalance(String accountingCode, String accountingLabel, BigDecimal initialBalance, 
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
		return null;
	}

	@Override
	protected AccountOperation toEntity(TrialBalance resource) {
		return null;
	}
}
