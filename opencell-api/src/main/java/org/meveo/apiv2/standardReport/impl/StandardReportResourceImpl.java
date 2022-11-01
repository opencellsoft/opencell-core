package org.meveo.apiv2.standardReport.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.AgedReceivableDto;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.apiv2.standardReport.AgedReceivables;
import org.meveo.apiv2.standardReport.ImmutableAgedReceivable;
import org.meveo.apiv2.standardReport.ImmutableAgedReceivables;
import org.meveo.apiv2.standardReport.resource.StandardReportResource;
import org.meveo.apiv2.standardReport.service.StandardReportApiService;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.DateUtils;
import org.meveo.util.ApplicationProvider;

public class StandardReportResourceImpl implements StandardReportResource {

    @Inject
    private StandardReportApiService standardReportApiService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    private AgedReceivableMapper agedReceivableMapper = new AgedReceivableMapper();
    
    @Override
    public Response getAgedReceivables(Long offset, Long limit, String sort, String orderBy, String customerAccountCode,
                                       Date startDate, Date startDueDate, Date endDueDate, String customerAccountDescription,
                                       String sellerDescription, String sellerCode,
                                       String invoiceNumber,
                                       Integer stepInDays, Integer numberOfPeriods, String tradingCurrency, String functionalCurrency, Request request) {
    	if (startDate == null) {
    		startDate = new Date();
		}
    	List<Object[]> agedBalanceList =
                standardReportApiService.list(offset, limit, sort, orderBy, customerAccountCode, startDate,
                        startDueDate, endDueDate, customerAccountDescription, sellerDescription, sellerCode, invoiceNumber, stepInDays, numberOfPeriods, tradingCurrency, functionalCurrency);
        agedReceivableMapper.setAppProvider(appProvider);
    	List<AgedReceivableDto> agedReceivablesList = (stepInDays == null && numberOfPeriods == null)
                ? agedReceivableMapper.toEntityList(agedBalanceList) : agedReceivableMapper.buildDynamicResponse(agedBalanceList, numberOfPeriods);
        ImmutableAgedReceivable[] agedReceivablesData = agedReceivablesList
                .stream()
                .map(agedReceivableDto ->
                        agedReceivableMapper.toResourceAgedReceivable(agedReceivableMapper.toResource(agedReceivableDto)))
                .toArray(ImmutableAgedReceivable[]::new);
        Long count = functionalCurrency!= null && !functionalCurrency.equals(appProvider.getCurrency().getCurrencyCode()) ? 0: standardReportApiService.getCountAgedReceivables(customerAccountCode);
        AgedReceivables agedReceivables = ImmutableAgedReceivables.builder()
                .addData(agedReceivablesData)
                .startDate(DateUtils.formatDateWithPattern(startDate, "dd/MM/yyyy"))
                .offset(offset)
                .limit(limit)
                .total(count)
                .build().withLinks(new LinkGenerator.PaginationLinkGenerator(StandardReportResource.class)
                        .offset(offset).limit(limit).total(count).build());
        return Response.ok().entity(agedReceivables).build();
    }
}
