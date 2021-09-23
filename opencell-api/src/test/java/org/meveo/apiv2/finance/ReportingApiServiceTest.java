package org.meveo.apiv2.finance;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.finance.service.ReportingApiService;
import org.meveo.apiv2.generic.services.GenericApiLoadService;
import org.meveo.model.report.query.SortOrderEnum;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReportingApiServiceTest {

	@Spy
	@InjectMocks
	private ReportingApiService reportingApiService;

	@Spy
	private GenericApiLoadService loadService;

	@Before
	public void setup() {
		doReturn(buildSomeBalances()).when(loadService).findAggregatedPaginatedRecords(any(), any(), any());
	}

	@Test
	public void getTrialBalances() {
		List<TrialBalance> balances = reportingApiService.list(ReportingPeriodEnum.CURRENT_YEAR, null, null, "accountingCode.code", SortOrderEnum.DESCENDING, 0L, 2L);
		assertEquals(2, balances.size());
		
		assertEquals("411000000", balances.get(0).getAccountingCode());
		assertEquals(new BigDecimal("81808.4"), balances.get(0).getInitialBalance());
		assertEquals(new BigDecimal("2500"), balances.get(0).getCurrentCreditBalance());
		assertEquals(BigDecimal.ZERO, balances.get(0).getCurrentDebitBalance());
		assertEquals(new BigDecimal("79308.4"), balances.get(0).getClosingBalance());

		assertEquals("512010000", balances.get(1).getAccountingCode());
		assertEquals(new BigDecimal("-1234"), balances.get(1).getInitialBalance());
		assertEquals(BigDecimal.ZERO, balances.get(1).getCurrentCreditBalance());
		assertEquals(BigDecimal.ZERO, balances.get(1).getCurrentDebitBalance());
		assertEquals(new BigDecimal("-1234"), balances.get(1).getClosingBalance());
	}

	private List<Map<String, Object>> buildSomeBalances() {
		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put("accountingCode", "411000000");
		entry.put("accountingLabel", "Gross receivables");
		entry.put("initialBalanceDebit", new BigDecimal("81808.4"));
		entry.put("initialBalanceCredit", BigDecimal.ZERO);
		entry.put("currentDebitBalance", BigDecimal.ZERO);
		entry.put("currentCreditBalance", new BigDecimal("2500"));
		result.add(entry);
		
		entry = new HashMap<>();
		entry.put("accountingCode", "512010000");
		entry.put("accountingLabel", "Cash Deposit - BNP #123456");
		entry.put("initialBalanceDebit", new BigDecimal("0"));
		entry.put("initialBalanceCredit", new BigDecimal("1234"));
		entry.put("currentDebitBalance", new BigDecimal("0"));
		entry.put("currentCreditBalance", new BigDecimal("0"));
		result.add(entry);
		return result;
	}

}
