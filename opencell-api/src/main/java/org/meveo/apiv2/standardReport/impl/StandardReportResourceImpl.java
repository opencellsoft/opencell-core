package org.meveo.apiv2.standardReport.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.AgedReceivableDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.apiv2.generic.GenericFieldDetails;
import org.meveo.apiv2.generic.GenericPagingAndFiltering;
import org.meveo.apiv2.generic.services.GenericFileExportManager;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.apiv2.standardReport.*;
import org.meveo.apiv2.standardReport.resource.StandardReportResource;
import org.meveo.apiv2.standardReport.service.StandardReportApiService;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.DateUtils;
import org.meveo.util.ApplicationProvider;

public class StandardReportResourceImpl implements StandardReportResource {

    private static final String FR_DATE_FORMAT = "dd/MM/yyyy";
    private static final String EN_DATE_FORMAT = "MM/dd/yyyy";

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
    public Response exportAgedReceivables(String fileFormat, String locale, GenericPagingAndFiltering input, @Context Request request) {
        String filePath = null;
        SimpleDateFormat format = new SimpleDateFormat(FR_DATE_FORMAT);

        if(!fileFormat.equalsIgnoreCase("csv") && !fileFormat.equalsIgnoreCase("excel") && !fileFormat.equalsIgnoreCase("pdf")){
            throw new BadRequestException("Accepted formats for export are (CSV, pdf or EXCEL).");
        }

        if (org.meveo.commons.utils.StringUtils.isBlank(locale)) {
            locale = "EN";
        }

        List<Object[]> agedBalanceList = null;
        List<AgedReceivableDto> agedReceivablesList = null;
        agedReceivableMapper.setAppProvider(appProvider);

        if("EN".equalsIgnoreCase(locale)) {
            format = new SimpleDateFormat(EN_DATE_FORMAT);
        }

        try {
            if(input.getFilters() != null) {
                agedBalanceList = standardReportApiService.list(
                        input.getOffset(),
                        input.getLimit(),
                        input.getSortOrder(),
                        input.getSortBy(),
                        (String) input.getFilters().get("customerAccountCode"),
                        input.getFilters().get("startDate") != null ? format.parse((String) input.getFilters().get("startDate")) : new Date(),
                        input.getFilters().get("startDueDate") != null ? format.parse((String) input.getFilters().get("startDueDate")) : null,
                        input.getFilters().get("endDueDate") != null ? format.parse((String) input.getFilters().get("endDueDate")) : null,
                        (String) input.getFilters().get("customerAccountDescription"),
                        (String) input.getFilters().get("sellerDescription"),
                        (String) input.getFilters().get("sellerCode"),
                        (String) input.getFilters().get("invoiceNumber"),
                        (Integer) input.getFilters().get("stepInDays"),
                        (Integer) input.getFilters().get("numberOfPeriods"),
                        (String) input.getFilters().get("tradingCurrency"),
                        (String) input.getFilters().get("functionalCurrency"));
                // Convert List of Object to a list of Aged Receivable Dto
                agedReceivablesList = (input.getFilters().get("stepInDays") == null && input.getFilters().get("numberOfPeriods") == null)
                        ? agedReceivableMapper.buildEntityList(agedBalanceList)
                        : agedReceivableMapper.buildDynamicResponse(agedBalanceList, input.getFilters().get("numberOfPeriods") != null ? (Integer) input.getFilters().get("numberOfPeriods") : 0);

            } else {
                agedBalanceList = standardReportApiService.getAll();
                agedReceivablesList = agedReceivableMapper.fromListObjectToListEntity(agedBalanceList);
            }

            filePath = genericExportManager.exportAgedTrialBalance("AgedReceivableDto", fileFormat, input.getGenericFieldDetails(), agedReceivablesList,
                    input.getGenericFieldDetails().stream().map(GenericFieldDetails::getName).collect(Collectors.toList()), locale);
        } catch(ParseException ex) {
            throw new BusinessApiException("Error occurred when listing aged balance report : " + ex.getMessage());
        }

        return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"\"}, \"data\":{ \"filePath\":\""+ filePath +"\"}}").build();
    }
}
