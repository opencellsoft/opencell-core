package org.meveo.apiv2.standardReport.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.AgedReceivableDto;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.apiv2.standardReport.AgedReceivables;
import org.meveo.apiv2.standardReport.ImmutableAgedReceivable;
import org.meveo.apiv2.standardReport.ImmutableAgedReceivables;
import org.meveo.apiv2.standardReport.resource.StandardReportResource;
import org.meveo.apiv2.standardReport.service.StandardReportApiService;
import org.meveo.model.shared.DateUtils;

public class StandardReportResourceImpl implements StandardReportResource {

    @Inject
    private StandardReportApiService standardReportApiService;
    private AgedReceivableMapper agedReceivableMapper = new AgedReceivableMapper();



    
    @Override
    public Response getAgedReceivables(Long offset, Long limit, String sort, String orderBy, String customerAccountCode,
                                     Date startDate, Request request) {
    	if (startDate == null) {
    		startDate = new Date();
		}
    	List<Object[]> agedBalanceList =
                standardReportApiService.list(offset, limit, sort, orderBy, customerAccountCode, startDate);
    	List<AgedReceivableDto> agedReceivablesList= agedReceivableMapper.toEntityList(agedBalanceList);
    	EntityTag etag = new EntityTag(Integer.toString(agedReceivablesList.hashCode()));
        CacheControl cc = new CacheControl();
        cc.setMaxAge(1000);
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
        if (builder != null) {
            builder.cacheControl(cc);
            return builder.build();
        }
        ImmutableAgedReceivable[] agedReceivablesData = agedReceivablesList
                .stream()
                .map(AgedReceivableDto -> agedReceivableMapper.toResourceAgedReceivable(agedReceivableMapper.toResource(AgedReceivableDto)))
                .toArray(ImmutableAgedReceivable[]::new);
        Long count = Long.valueOf(standardReportApiService.getCountAgedReceivables(customerAccountCode));
        AgedReceivables agedReceivables = ImmutableAgedReceivables.builder().addData(agedReceivablesData).startDate(DateUtils.formatDateWithPattern(startDate, "dd/MM/yyyy").toString()).offset(offset).limit(limit).total(count)
                .build().withLinks(new LinkGenerator.PaginationLinkGenerator(StandardReportResource.class)
                        .offset(offset).limit(limit).total(count).build());
        return Response.ok().cacheControl(cc).tag(etag).entity(agedReceivables).build();
    }
    
}