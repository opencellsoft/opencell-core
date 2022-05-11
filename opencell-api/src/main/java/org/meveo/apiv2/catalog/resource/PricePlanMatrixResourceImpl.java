package org.meveo.apiv2.catalog.resource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.meveo.api.catalog.PricePlanMatrixLineApi;
import org.meveo.api.dto.response.catalog.ImportResultResponseDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixLinesDto;
import org.meveo.apiv2.catalog.ImportPricePlanVersionsDto;
import org.meveo.apiv2.catalog.PricePlanMLinesDTO;
import org.meveo.apiv2.catalog.service.PricePlanMatrixApiService;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.communication.FormatEnum;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;


@Stateless
public class PricePlanMatrixResourceImpl implements PricePlanMatrixResource {

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

    @Inject
    private PricePlanMatrixApiService pricePlanMatrixApiService;

    @Inject
    private PricePlanMatrixLineApi pricePlanMatrixLineApi;

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
        String filePath = pricePlanMatrixVersionService.export(ids, FormatEnum.CSV);
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
}