package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;
import org.meveo.api.dto.response.catalog.GetPricePlanVersionResponseDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixLinesDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.generic.exception.ConflictException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.ColumnTypeEnum;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.service.catalog.impl.PricePlanMatrixColumnService;
import org.meveo.service.catalog.impl.PricePlanMatrixLineService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.*;

@Stateless
public class PricePlanMatrixLineApi extends BaseApi {

    @Inject
    private PricePlanMatrixLineService pricePlanMatrixLineService;

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

    @Inject
    private PricePlanMatrixColumnService pricePlanMatrixColumnService;

    public PricePlanMatrixLineDto addPricePlanMatrixLine(String pricePlanMatrixCode, int version, PricePlanMatrixLineDto dtoData) throws MeveoApiException, BusinessException {

        checkCommunMissingParameters(pricePlanMatrixCode, version, dtoData);
        dtoData.setPricePlanMatrixCode(pricePlanMatrixCode);
        dtoData.setPricePlanMatrixVersion(version);

        return pricePlanMatrixLineService.createPricePlanMatrixLine(dtoData);
    }
    
    public GetPricePlanVersionResponseDto addPricePlanMatrixLines(String pricePlanMatrixCode, int pricePlanMatrixVersion, PricePlanMatrixLinesDto dtoData) throws MeveoApiException, BusinessException {

        pricePlanMatrixLineService.checkDuplicatePricePlanMatrixValues(dtoData.getPricePlanMatrixLines());

        for (PricePlanMatrixLineDto pricePlanMatrixLineDto:dtoData.getPricePlanMatrixLines()) {
            addPricePlanMatrixLine(pricePlanMatrixCode, pricePlanMatrixVersion, pricePlanMatrixLineDto);
        }
        PricePlanMatrixVersion ppmVersion= pricePlanMatrixLineService.getPricePlanMatrixVersion(pricePlanMatrixCode, pricePlanMatrixVersion);
        return new GetPricePlanVersionResponseDto(ppmVersion);
    }

    public GetPricePlanVersionResponseDto updatePricePlanMatrixLines(String pricePlanMatrixCode, int pricePlanMatrixVersion, PricePlanMatrixLinesDto dtoData) throws MeveoApiException, BusinessException {
        PricePlanMatrixVersion ppmVersion = pricePlanMatrixLineService.getPricePlanMatrixVersion(pricePlanMatrixCode, pricePlanMatrixVersion);
        pricePlanMatrixLineService.updatePricePlanMatrixLines(ppmVersion, dtoData);
        pricePlanMatrixVersionService.updatePricePlanMatrixVersion(ppmVersion);

        return new GetPricePlanVersionResponseDto(ppmVersion);
    }

    public GetPricePlanVersionResponseDto updateWithoutDeletePricePlanMatrixLines(String pricePlanMatrixCode, int pricePlanMatrixVersion, PricePlanMatrixLinesDto dtoData) throws MeveoApiException, BusinessException {
        PricePlanMatrixVersion ppmVersion = pricePlanMatrixLineService.getPricePlanMatrixVersion(pricePlanMatrixCode, pricePlanMatrixVersion);
        pricePlanMatrixLineService.updateWithoutDeletePricePlanMatrixLines(ppmVersion, dtoData);
        pricePlanMatrixVersionService.updatePricePlanMatrixVersion(ppmVersion);

        return new GetPricePlanVersionResponseDto(ppmVersion);
    }
    
    public PricePlanMatrixLineDto updatePricePlanMatrixLine(String pricePlanMatrixCode, int version, PricePlanMatrixLineDto pricePlanMatrixLineDto) {

        if(StringUtils.isBlank(pricePlanMatrixLineDto.getPpmLineId()))
            missingParameters.add("ppmLineId");
        checkCommunMissingParameters(pricePlanMatrixCode, version, pricePlanMatrixLineDto);


        return pricePlanMatrixLineService.updatePricePlanMatrixLine(pricePlanMatrixLineDto);
    }

    private void checkCommunMissingParameters(String pricePlanMatrixCode, int version, PricePlanMatrixLineDto dtoData) {
        if(StringUtils.isBlank(pricePlanMatrixCode)){
            missingParameters.add("pricePlanMatrixCode");
        }
        if(StringUtils.isBlank(version)){
            missingParameters.add("pricePlanMatrixVersion");
        }

        dtoData.getPricePlanMatrixValues().stream()
                .forEach(value -> {
                    if(StringUtils.isBlank(value.getPpmColumnCode())){
                        missingParameters.add("pricePlanMatrixValues.ppmColumnCode");
                    }
                });
        if(!StringUtils.isBlank(dtoData.getPriceWithoutTax()) && !StringUtils.isBlank(dtoData.getValue())){
            throw new BusinessApiException("Property 'priceWithoutTax' is deprecated, please use only property 'value'");
        }

        handleMissingParametersAndValidate(dtoData);
    }

    public void remove(Long ppmLineId) {
        if(StringUtils.isBlank(ppmLineId))
            missingParameters.add("pricePlanMatrixLineId");
        handleMissingParameters();
        PricePlanMatrixLine ppmLine = pricePlanMatrixLineService.findById(ppmLineId);
        if(ppmLine == null){
            throw new EntityDoesNotExistsException(PricePlanMatrixLine.class, ppmLineId);
        }
        ppmLine.getPricePlanMatrixVersion().getLines().remove(ppmLine);
        pricePlanMatrixVersionService.update(ppmLine.getPricePlanMatrixVersion());
    }

    public void remove(PricePlanMatrixLinesDto pricePlanMatrixLinesDto) {
        for (PricePlanMatrixLineDto pricePlanMatrixLineDto : pricePlanMatrixLinesDto.getPricePlanMatrixLines()) {
            PricePlanMatrixLine ppmLine = pricePlanMatrixLineService.findById(pricePlanMatrixLineDto.getPpmLineId());
            if (ppmLine == null) {
                throw new EntityDoesNotExistsException(PricePlanMatrixLine.class, pricePlanMatrixLineDto.getPpmLineId());
            }
            pricePlanMatrixLineService.remove(ppmLine);
        }
    }
    
    public PricePlanMatrixLineDto load(Long ppmLineId){
        if(StringUtils.isBlank(ppmLineId))
            missingParameters.add("pricePlanMatrixLineId");
        handleMissingParameters();
        return pricePlanMatrixLineService.load(ppmLineId);
    }

    public List<PricePlanMatrixLine> search(Map<String, Object> searchInfo) {
        for (Map<String, Object> e : (List<Map<String, Object>>) searchInfo.getOrDefault("attributes", EMPTY_LIST)) {

            if(!e.containsKey("column")) {
                throw new MissingParameterException("column");
            }

            String columnCode = (String) e.get("column");
            PricePlanMatrixColumn column = pricePlanMatrixColumnService.findByCode(columnCode);

            if(column == null) {
                throw new EntityDoesNotExistsException(PricePlanMatrixColumn.class, columnCode);
            }

            String operator = (String) e.getOrDefault("operator", "=");

            if(!isOperatorCompatible(column.getAttribute().getAttributeType(), operator)) {
                throw new ConflictException(String.format("The given operator %s is not compatible with the type of the attribute %s", operator, column.getAttribute().getCode()));
            }
        }
        return pricePlanMatrixLineService.search(searchInfo);
    }

    private boolean isOperatorCompatible(AttributeTypeEnum type, String operator) {
        switch (type) {
            case BOOLEAN: {
                return Arrays.asList("=", "!=").contains(operator.toLowerCase());
            }
            case PHONE:
            case EMAIL:
            case TEXT:
            case LIST_TEXT: {
                return Arrays.asList("=", "!=", "like").contains(operator.toLowerCase());
            }
            case NUMERIC:
            case INTEGER:
            case DATE:
            case LIST_NUMERIC: {
                return Arrays.asList("=", "!=", "<", ">", "<=", ">=", "between").contains(operator.toLowerCase());
            }
            case LIST_MULTIPLE_TEXT:
            case LIST_MULTIPLE_NUMERIC: {
                return Arrays.asList("=", "!=", "like", "in").contains(operator.toLowerCase());
            }
            default:
                return false;
        }
    }
}
