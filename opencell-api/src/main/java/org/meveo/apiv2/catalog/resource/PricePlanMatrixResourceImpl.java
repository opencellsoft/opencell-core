package org.meveo.apiv2.catalog.resource;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.meveo.api.catalog.PricePlanMatrixLineApi;
import org.meveo.api.dto.response.catalog.ImportResultResponseDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixLinesDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.catalog.ImportPricePlanVersionsDto;
import org.meveo.apiv2.catalog.PricePlanMLinesDTO;
import org.meveo.apiv2.catalog.service.PricePlanMatrixApiService;
import org.meveo.apiv2.generic.core.mapper.JsonGenericMapper;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.service.catalog.impl.PricePlanMatrixLineService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;


@Stateless
@Interceptors({ WsRestApiInterceptor.class })
public class PricePlanMatrixResourceImpl implements PricePlanMatrixResource {

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

    @Inject
    private PricePlanMatrixApiService pricePlanMatrixApiService;

    @Inject
    private PricePlanMatrixLineApi pricePlanMatrixLineApi;

    @Inject
    private PricePlanMatrixLineService pricePlanMatrixLineService;

    @Override
    public Response importPricePlanMatrixLines(PricePlanMLinesDTO pricePlanMLinesDTO) {
        String data = new String(pricePlanMLinesDTO.getData());
        PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMLinesDTO.getPricePlanMatrixCode(),
            pricePlanMLinesDTO.getPricePlanMatrixVersion());
        if (pricePlanMatrixVersion == null) {
            throw new NotFoundException(
                "pricePlanMatrixVersion not found with code" + pricePlanMLinesDTO.getPricePlanMatrixCode() + "and version" + pricePlanMLinesDTO.getPricePlanMatrixVersion());
        }
        PricePlanMatrixLinesDto pricePlanMatrixLinesDto = pricePlanMatrixApiService.updatePricePlanMatrixLines(pricePlanMLinesDTO, data, pricePlanMatrixVersion);
        pricePlanMatrixLineApi.updatePricePlanMatrixLines(pricePlanMLinesDTO.getPricePlanMatrixCode(), pricePlanMLinesDTO.getPricePlanMatrixVersion(), pricePlanMatrixLinesDto);

        return Response.ok().build();
    }

    @Override
    public Response importPricePlanMatrixVersions(ImportPricePlanVersionsDto importPricePlanVersionsDto) {

        ImportResultResponseDto result = new ImportResultResponseDto();

        result.setImportResultDtos(pricePlanMatrixApiService.importPricePlanMatrixVersions(importPricePlanVersionsDto));
        
        return Response.ok().entity(result).build();
    }

    @Override
    public Response exportPricePlanMatrixVersions(Map<String, Object> payload) {
        List<Long> ids;
        if(payload.get("ids") == null || (ids = toLong(payload.get("ids"))).isEmpty()){
            throw new BadRequestException("ids of the price plan matrix version is required.");
        }
        if(payload.get("format") == null){
            throw new BadRequestException("format of the price plan matrix version is required.");
        }
        String typeFile = "" + payload.get("format");
        typeFile = typeFile.toUpperCase();
        if(!typeFile.equals("CSV") && !typeFile.equals("EXCEL")){
            throw new BadRequestException("format of the price plan matrix version can be only equals (CSV or EXCEL).");
        }
        String filePath = pricePlanMatrixVersionService.export(ids, typeFile);
        if(StringUtils.isBlank(filePath)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"actionStatus\":{\"status\":\"FAILED\",\"message\": \"there was a problem during export operation\"}}")
                    .build();
        }
        return Response.ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"\"}, \"data\":{ \"filePath\":\""+ filePath +"\"}}")
                .build();
    }

    private List<Long> toLong(Object ids) {
        return ((List<Integer>) ids).stream()
                .map(integer -> integer.longValue())
                .collect(Collectors.toList());
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
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
        fields.add("cfValues");

        response.put("data", mapper.readValue(mapper.toJson(fields,
                PricePlanMatrixLine.class, pricePlanMatrixLines, null), List.class));
        response.put("limit", limit);
        response.put("offset", offset);
        response.put("sortBy", sortBy);
        response.put("order", order);
        return response;
    }

}