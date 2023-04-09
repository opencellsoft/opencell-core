package org.meveo.apiv2.price.search;

import org.hibernate.criterion.MatchMode;
import org.meveo.api.catalog.PricePlanMatrixLineApi;
import org.meveo.apiv2.generic.core.mapper.JsonGenericMapper;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.service.catalog.impl.PricePlanMatrixLineService;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Deprecated
public class SearchPriceLineByAttributeResourceImpl implements SearchPriceLineByAttributeResource {
    @Inject
    private PricePlanMatrixLineApi pricePlanMatrixLineApi;

    @Override
    public Response search(Map<String, Object> searchInfo) {
        List<PricePlanMatrixLine> resultList = pricePlanMatrixLineApi.search(searchInfo);
        return  Response.ok().entity(buildResponse(resultList,
                        (Integer) searchInfo.getOrDefault("limit", 10),
                        (Integer) searchInfo.getOrDefault("offset", 0),
                        (String) searchInfo.getOrDefault("sortBy","id"),
                        (String) searchInfo.getOrDefault("order","ASC")))
                .build();
    }

    private Map<String, Object> buildResponse(List<PricePlanMatrixLine> resultList, int limit, int offset, String sortBy, String order) {
        Map<String, Object> response = new HashMap<>();
        response.put("total", resultList.size());
        List<PricePlanMatrixLine> pricePlanMatrixLines = new ArrayList<>();

        if(offset + limit <= resultList.size()) {
        	pricePlanMatrixLines = resultList.subList(offset, limit + offset);        	
        } 
        
        if(offset + limit > resultList.size()) {
        	pricePlanMatrixLines = resultList.subList(offset, resultList.size());        	
        } 
        
        for(PricePlanMatrixLine pricePlanMatrixLine: pricePlanMatrixLines) {
           PricePlanMatrixVersion pricePlanMatrixVersion = new PricePlanMatrixVersion();
           pricePlanMatrixVersion.setId(pricePlanMatrixLine.getPricePlanMatrixVersion().getId());
           pricePlanMatrixLine.setPricePlanMatrixVersion(pricePlanMatrixVersion);
        }
        JsonGenericMapper mapper = JsonGenericMapper.Builder.getBuilder()
                .build();
        Set<String> fields = new HashSet<>();
        fields.add("id");
        fields.add("description");
        fields.add("priceWithoutTax");
        fields.add("pricePlanMatrixVersion");
        fields.add("pricePlanMatrixValues");
        fields.add("priceEL");
        fields.add("priority");

        response.put("data", mapper.readValue(mapper.toJson(fields, PricePlanMatrixLine.class, pricePlanMatrixLines, null), List.class));
        response.put("limit", limit);
        response.put("offset", offset);
        response.put("sortBy", sortBy);
        response.put("order", order);
        return response;
    }
}
