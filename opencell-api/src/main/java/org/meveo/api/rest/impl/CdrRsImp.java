package org.meveo.api.rest.impl;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.FilterCDRDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.rest.CdrRs;
import org.meveo.model.rating.CDR;
import org.meveo.service.medina.impl.CDRService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mohamed CHAOUKI
 **/

public class CdrRsImp extends BaseRs implements CdrRs {
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";

    @Inject
    private CDRService cdrService;

    @Override
    public List<CDR> listCDRs(FilterCDRDto filterCDRDto, int size, int page) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        Map<String, Object> filters = new HashMap<>();
        if (filterCDRDto.getQuantity() != null)
            filters.put("quantity", BigDecimal.valueOf(filterCDRDto.getQuantity()));
        if (filterCDRDto.getAccessCode() != null) filters.put("accessCode", filterCDRDto.getAccessCode());
        if (filterCDRDto.getParameter1() != null) filters.put("parameter1", filterCDRDto.getParameter1());
        if (filterCDRDto.getStartDate() != null && filterCDRDto.getEndDate() != null) {
            filters.put("fromRange eventDate", dateFormat.parse(filterCDRDto.getStartDate()));
            filters.put("toRange eventDate", dateFormat.parse(filterCDRDto.getEndDate()));
        }


        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(size * page, size, filters, null, null, "id", PagingAndFiltering.SortOrder.ASCENDING);

        return cdrService.list(paginationConfiguration);
    }
}
