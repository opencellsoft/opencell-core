package org.meveo.apiv2.finance.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.finance.ImmutableTrialBalancesResult;
import org.meveo.apiv2.finance.ReportingPeriodEnum;
import org.meveo.apiv2.finance.TrialBalance;
import org.meveo.apiv2.finance.TrialBalancesResult;
import org.meveo.apiv2.finance.resource.ReportingResource;
import org.meveo.apiv2.finance.service.ReportingApiService;
import org.meveo.model.report.query.SortOrderEnum;
import org.meveo.model.shared.DateUtils;

public class ReportingResourceImpl implements ReportingResource {

	
	@Inject
	private ReportingApiService reportingApiService;

	@Override
	public Response getTrialBalances(ReportingPeriodEnum period, Date startDate, Date endDate, String sortBy, SortOrderEnum sortOrder, Long offset, Long limit, Request request) {
		int balancesCount = reportingApiService.count(period, startDate, endDate);
		if (balancesCount == 0) {
			return Response.noContent().build();
		}
		
		List<TrialBalance> trialBalances = reportingApiService.list(period, startDate, endDate, sortBy, sortOrder, offset, limit);
		return buildTrialBlancesResponse(trialBalances, period, startDate, endDate, offset, limit, balancesCount);
	}
	
	private Response buildTrialBlancesResponse(List<TrialBalance> trialBalances, ReportingPeriodEnum period, Date startDate, Date endDate, long offset, long limit, long total) {
		TrialBalancesResult trialBalancesResult = ImmutableTrialBalancesResult.builder()
				.balances(trialBalances)
				.period((startDate == null || endDate == null) ? period.name() : DateUtils.formatAsDate(startDate) + " => " + DateUtils.formatAsDate(endDate))
				.offset(offset)
				.limit(limit)
				.total(total)
				.build();

		return Response.ok().entity(trialBalancesResult).build();
	}
	

}
