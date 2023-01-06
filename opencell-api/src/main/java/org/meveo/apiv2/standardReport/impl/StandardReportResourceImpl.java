package org.meveo.apiv2.standardReport.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.AgedReceivableDto;
import org.meveo.apiv2.generic.GenericFieldDetails;
import org.meveo.apiv2.generic.services.GenericFileExportManager;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.apiv2.standardReport.AgedReceivables;
import org.meveo.apiv2.standardReport.AgedReceivablesDto;
import org.meveo.apiv2.standardReport.ImmutableAgedReceivable;
import org.meveo.apiv2.standardReport.ImmutableAgedReceivables;
import org.meveo.apiv2.standardReport.resource.StandardReportResource;
import org.meveo.apiv2.standardReport.service.StandardReportApiService;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.DateUtils;
import org.meveo.util.ApplicationProvider;

public class StandardReportResourceImpl implements StandardReportResource {

    private static final String PDF_TYPE = "pdf";
    private static final String EXCEL_TYPE = "excel";
    private static final String CSV_TYPE = "csv";

    @Inject
    private StandardReportApiService standardReportApiService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    private AgedReceivableMapper agedReceivableMapper = new AgedReceivableMapper();

    @Inject
    private GenericFileExportManager genericExportManager;
    
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
		Long count = functionalCurrency != null && !functionalCurrency.equals(appProvider.getCurrency().getCurrencyCode())
						? 0
						: standardReportApiService.getCountAgedReceivables(customerAccountCode,
								customerAccountDescription, sellerCode, sellerDescription, invoiceNumber,
								tradingCurrency, startDueDate, endDueDate, startDate);
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

    @Override
    public Response exportAgedReceivables(String fileFormat, String locale, AgedReceivablesDto input, @Context Request request) {
        if(!fileFormat.equalsIgnoreCase("csv") && !fileFormat.equalsIgnoreCase("excel") && !fileFormat.equalsIgnoreCase("pdf")){
            throw new BadRequestException("Accepted formats for export are (CSV, pdf or EXCEL).");
        }

        if (org.meveo.commons.utils.StringUtils.isBlank(locale)) {
            locale = "EN";
        }

        // Get the list of aged balance as array
        List<Object[]> agedBalanceList = standardReportApiService.getAll();
        agedReceivableMapper.setAppProvider(appProvider);

        // Convert List of Object to a list of Aged Receivable Dto
        List<AgedReceivableDto> agedReceivablesList = agedReceivableMapper.fromListObjectToListEntity(agedBalanceList);

        String filePath = genericExportManager.exportAgedTrialBalance("AgedReceivableDto", fileFormat, input.getSearchConfig().getGenericFieldDetails(), agedReceivablesList,
                input.getSearchConfig().getGenericFieldDetails().stream().map(GenericFieldDetails::getName).collect(Collectors.toList()), locale);

        return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"\"}, \"data\":{ \"filePath\":\""+ filePath +"\"}}").build();
    }
}
