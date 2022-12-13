package org.meveo.apiv2.finance.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.finance.ReportingPeriodEnum;
import org.meveo.apiv2.finance.TrialBalance;
import org.meveo.apiv2.finance.impl.ReportingMapper;
import org.meveo.apiv2.generic.services.GenericApiLoadService;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.DatePeriod;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.report.query.SortOrderEnum;
import org.meveo.model.shared.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportingApiService implements ApiService<AccountOperation> {

	private static final Logger log = LoggerFactory.getLogger(ReportingApiService.class);

	@Inject
	private GenericApiLoadService loadService;
	
	private static final String BALANCE_CRITERIA = "COALESCE(SUM(CASE WHEN (accountingDate >= '%s' AND  accountingDate < '%s' AND transactionCategory = '%s') THEN COALESCE(amountWithoutTax, amount) END), 0)";

	
	public List<TrialBalance> list(ReportingPeriodEnum period, String codeOrLabel, Date startDate, Date endDate, String sortBy, SortOrderEnum sortOrder, Long offset, Long limit) {

		Set<String> fetchFieldsAliasSet = new LinkedHashSet<>();
		fetchFieldsAliasSet.addAll(Arrays.asList("accountingCode", "accountingLabel", "initialBalanceDebit", "initialBalanceCredit", "currentDebitBalance", "currentCreditBalance"));

		PaginationConfiguration paginationConfiguration = getSearchConfig(period, codeOrLabel, startDate, endDate, sortBy, sortOrder, offset, limit);
		List<Map<String, Object>> res = loadService.findAggregatedPaginatedRecords(AccountOperation.class, paginationConfiguration, fetchFieldsAliasSet);
		ReportingMapper mapper = new ReportingMapper();
		List<TrialBalance> trialBalances = new ArrayList<>();
		for (Map<String, Object> balance : res) {
			String accountingCode = (String) balance.getOrDefault("accountingCode", "");
			String accountingLabel = (String) balance.getOrDefault("accountingLabel", "");
			BigDecimal initialBalanceDebit = (BigDecimal) balance.getOrDefault("initialBalanceDebit", 0);
			BigDecimal initialBalanceCredit = (BigDecimal) balance.getOrDefault("initialBalanceCredit", 0);
			BigDecimal currentDebitBalance = (BigDecimal) balance.getOrDefault("currentDebitBalance", 0);
			BigDecimal currentCreditBalance = (BigDecimal) balance.getOrDefault("currentCreditBalance", 0);
			
			BigDecimal initialBalance = initialBalanceDebit.subtract(initialBalanceCredit);
			BigDecimal closingBalance = currentDebitBalance.subtract(currentCreditBalance).add(initialBalance);
			
			trialBalances.add(mapper.toTrialBalance(accountingCode, accountingLabel, initialBalance, currentCreditBalance, currentDebitBalance, closingBalance));
		}
		return trialBalances;
	}

	public int count(ReportingPeriodEnum period, String codeOrLabel, Date startDate, Date endDate) {
		PaginationConfiguration paginationConfiguration = getSearchConfig(period, codeOrLabel, startDate, endDate, null, null, null, null);
		return loadService.getAggregatedRecordsCount(AccountOperation.class, paginationConfiguration);
	}

	private PaginationConfiguration getSearchConfig(ReportingPeriodEnum period, String codeOrLabel, Date startDate, Date endDate, String sortBy, SortOrderEnum sortOrder, Long offset, Long limit) {
		
		DatePeriod targetPeriod = getTargetPeriod(period, startDate, endDate);
		
		Date reportStartDate = targetPeriod.getFrom();
		Date reportEndDate = targetPeriod.getTo();
		LocalDate earliestDate = LocalDate.ofEpochDay(365);
		LocalDate reportEndDateInclusive = Instant.ofEpochMilli(DateUtils.addDaysToDate(reportEndDate, 1).getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		if (limit != null) {
			log.info("Computing Trial Balances during {} => {} ...", reportStartDate, reportEndDate);
		}
		
		Set<String> fetchFieldsSet = new LinkedHashSet<>();
		Map<String, Object> filters = new HashMap<>();
		filters.put("toRange accountingDate", reportEndDateInclusive);
		if(codeOrLabel != null && !codeOrLabel.isEmpty()){
			filters.put("SQL", "(a.accountingCode.code like '" + codeOrLabel + "%' OR a.accountingCode.description like '" + codeOrLabel + "%')");
		}

		String initalBalanceDebit = String.format(BALANCE_CRITERIA, earliestDate, reportStartDate, "DEBIT");
		String initalBalanceCredit = String.format(BALANCE_CRITERIA, earliestDate, reportStartDate, "CREDIT");
		String currentBalanceDebit = String.format(BALANCE_CRITERIA, reportStartDate, reportEndDateInclusive, "DEBIT");
		String currentBalanceCredit = String.format(BALANCE_CRITERIA, reportStartDate, reportEndDateInclusive, "CREDIT");
		fetchFieldsSet.addAll(Arrays.asList("accountingCode.code", "accountingCode.description", initalBalanceDebit, initalBalanceCredit, currentBalanceDebit, currentBalanceCredit));

		return new PaginationConfiguration(offset == null ? null : offset.intValue(), limit == null ? null : limit.intValue(), filters, null, new ArrayList<>(fetchFieldsSet), sortBy, sortOrder == null ? null : sortOrder.name());
	}
	
	private DatePeriod getTargetPeriod(ReportingPeriodEnum period, Date startDate, Date endDate) {
		
		DatePeriod targetPeriod = new DatePeriod(startDate, endDate);
		if (startDate == null || endDate == null) {
			switch (period) {
			case LAST_MONTH:
				targetPeriod = getMonthPeriod(LocalDate.now().minusMonths(1));
				break;
			case CURRENT_MONTH:
				targetPeriod = getMonthPeriod(LocalDate.now());
				break;
			case LAST_QUARTER:
				targetPeriod = getQuarterPeriod(LocalDate.now().minusMonths(3));
				break;
			case CURRENT_QUARTER:
				targetPeriod = getQuarterPeriod(LocalDate.now());
				break;
			case CURRENT_YEAR:
				targetPeriod = getCurrentYearPeriod();
				break;
				
			default:
				break;
			}
		}
		return targetPeriod;
	}
	
	private DatePeriod getMonthPeriod(LocalDate date) {
		LocalDate monthStart = date.withDayOfMonth(1);
		LocalDate monthEnd = date.withDayOfMonth(date.lengthOfMonth());
		return new DatePeriod(java.sql.Date.valueOf(monthStart), java.sql.Date.valueOf(monthEnd));
	}
	
	private DatePeriod getQuarterPeriod(LocalDate date) {
		LocalDate quarterStart = date.with(date.getMonth().firstMonthOfQuarter()).with(TemporalAdjusters.firstDayOfMonth());
		LocalDate quarterEnd = quarterStart.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
		return new DatePeriod(java.sql.Date.valueOf(quarterStart), java.sql.Date.valueOf(quarterEnd));
	}

	private DatePeriod getCurrentYearPeriod() {
		LocalDate now = LocalDate.now();
		LocalDate currentYearStart = now.withMonth(1).withDayOfMonth(1);
		LocalDate currentYearEnd = now.withMonth(12).with(TemporalAdjusters.lastDayOfMonth());
		return new DatePeriod(java.sql.Date.valueOf(currentYearStart), java.sql.Date.valueOf(currentYearEnd));
	}
	
	@Override
	public List<AccountOperation> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		return Collections.emptyList();
	}

	@Override
	public Long getCount(String filter) {
		return null;
	}

	@Override
	public Optional<AccountOperation> findById(Long id) {
		return Optional.empty();
	}

	@Override
	public AccountOperation create(AccountOperation baseEntity) {
		return null;
	}

	@Override
	public Optional<AccountOperation> update(Long id, AccountOperation baseEntity) {
		return Optional.empty();
	}

	@Override
	public Optional<AccountOperation> patch(Long id, AccountOperation baseEntity) {
		return Optional.empty();
	}

	@Override
	public Optional<AccountOperation> delete(Long id) {
		return Optional.empty();
	}

	@Override
	public Optional<AccountOperation> findByCode(String code) {
		return Optional.empty();
	}

}
