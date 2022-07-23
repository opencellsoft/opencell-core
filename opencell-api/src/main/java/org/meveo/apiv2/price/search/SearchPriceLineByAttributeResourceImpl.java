package org.meveo.apiv2.price.search;


import org.hibernate.criterion.MatchMode;
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

public class SearchPriceLineByAttributeResourceImpl implements SearchPriceLineByAttributeResource {
    @Inject
    private PricePlanMatrixLineService pricePlanMatrixLineService;

    @Override
    public Response search(Map<String, Object> searchInfo) {
        Query query = pricePlanMatrixLineService.getEntityManager().createQuery(buildQuery(searchInfo), PricePlanMatrixLine.class);
        injectParamsIntoQuery(searchInfo, query);
        List<PricePlanMatrixLine> resultList = query.getResultList();
        return  Response.ok().entity(buildResponse(resultList,
                        (Integer) searchInfo.getOrDefault("limit", 10),
                        (Integer) searchInfo.getOrDefault("offset", 0),
                        (String) searchInfo.getOrDefault("sortBy","id"),
                        (String) searchInfo.getOrDefault("order","ASC")))
                .build();
    }

    private void injectParamsIntoQuery(Map<String, Object> searchInfo, Query query) {
        query.setParameter("description", MatchMode.ANYWHERE.toMatchString(((String) searchInfo.getOrDefault("description", "")).toLowerCase()));
        if(searchInfo.containsKey("pricePlanMatrixVersion") && ((Map) searchInfo.get("pricePlanMatrixVersion")).containsKey("id")){
            query.setParameter("pricePlanMatrixVersionId", Long.valueOf(((Map) searchInfo.get("pricePlanMatrixVersion")).getOrDefault("id", 1l)+""));
        }
        if(searchInfo.containsKey("priceWithoutTax")){
            query.setParameter("priceWithoutTax", BigDecimal.valueOf(Double.valueOf(searchInfo.getOrDefault("priceWithoutTax", 0.0)+"")));
        }
    }

    private String buildQuery(Map<String, Object> searchInfo) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT distinct ppml FROM PricePlanMatrixLine ppml");
        queryString.append(" LEFT JOIN FETCH ppml.pricePlanMatrixValues ppmv ");
        queryString.append(" WHERE (LOWER(ppml.description) LIKE :description OR ppml.description is null) ");
        if(searchInfo.containsKey("pricePlanMatrixVersion") && ((Map) searchInfo.get("pricePlanMatrixVersion")).containsKey("id")){
            queryString.append(" AND ppml.pricePlanMatrixVersion.id = :pricePlanMatrixVersionId ");
        }
        if(searchInfo.containsKey("priceWithoutTax")){
            queryString.append(" AND ppml.priceWithoutTax = :priceWithoutTax ");
        }
        if(searchInfo.containsKey("attributes") && !((List)searchInfo.get("attributes")).isEmpty()){
            queryString.append(" AND ppmv.id IN ");
            queryString.append("(SELECT ppmv2.id FROM PricePlanMatrixValue ppmv2");
            queryString.append(" JOIN PricePlanMatrixColumn ppmc ON ppmv2.pricePlanMatrixColumn=ppmc");
            queryString.append(" WHERE ");
            queryString.append(appendAttributesToQuery((List<Map<String, Object>>) searchInfo.getOrDefault("attributes", Collections.EMPTY_LIST)));
            queryString.append(")");
        }
        queryString.append(" ORDER BY ppml." + searchInfo.getOrDefault("sortBy","id"));
        queryString.append(" ");
        queryString.append(searchInfo.getOrDefault("order","ASC"));

        return queryString.toString();
    }

    private String appendAttributesToQuery(List<Map<String, Object>> attributesSearch) {
        return attributesSearch.stream()
                .map(stringObjectMap -> "(LOWER(ppmc.code)='"
                        + stringObjectMap.get("column").toString().toLowerCase()
                        + "' AND "
                        + resolveType((String) stringObjectMap.get("type"), stringObjectMap.get("value"))
                        +")")
                .collect(Collectors.joining(" OR "));
    }

    private String resolveType(String type, Object value) {
        switch(type.toLowerCase()){
            case "string":
                return "(LOWER(ppmv2.stringValue) LIKE '"+value.toString().toLowerCase()+"' OR ppmv2.stringValue IS NULL)";
            case "long":
                return "(ppmv2.longValue = " + value + " OR ppmv2.long_value IS NULL)";
            case "double":
                return "(ppmv2.doubleValue = " + Double.valueOf(value.toString())+ " OR ppmv2.doubleValue IS NULL)";
            case "boolean":
                return "(ppmv2.booleanValue = " + Boolean.valueOf(value.toString())+ " OR ppmv2.booleanValue IS NULL)";
           case "date":
                return "(ppmv2.dateValue = " + parseDate(value)+ " OR ppmv2.dateValue IS NULL)";
           default:
                return "stringValue = ''";
        }
    }

    private Date parseDate(Object value) {
        if(value instanceof String) {
            try {
                return ((String) value).matches("^\\d{4}-\\d{2}-\\d{2}$") ? new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(value)) : new SimpleDateFormat("dd/MM/yyyy").parse(String.valueOf(value));
            } catch (ParseException e) {
                throw new IllegalArgumentException("date attribute has not a valid filter value, hint : yyyy-MM-dd or dd/MM/yyyy");
            }
        }
        return new Date((Long) value);
    }

    private Map<String, Object> buildResponse(List<PricePlanMatrixLine> resultList, int limit, int offset, String sortBy, String order) {
        Map<String, Object> response = new HashMap<>();
        response.put("total", resultList.size());
        List<PricePlanMatrixLine> pricePlanMatrixLines = ((resultList.size() + offset) <= limit) ? resultList : resultList.subList(offset, limit + offset);
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
        fields.add("priority");

        response.put("data", mapper.readValue(mapper.toJson(fields, PricePlanMatrixLine.class, pricePlanMatrixLines, null), List.class));
        response.put("limit", limit);
        response.put("offset", offset);
        response.put("sortBy", sortBy);
        response.put("order", order);
        return response;
    }
}
