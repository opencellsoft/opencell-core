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
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.generic.exception.ConflictException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.catalog.TradingPricePlanMatrixLine;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.PricePlanMatrixColumnService;
import org.meveo.service.catalog.impl.PricePlanMatrixLineService;
import org.meveo.service.catalog.impl.PricePlanMatrixValueService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.meveo.service.crm.impl.ProviderService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.*;

@Stateless
public class PricePlanMatrixLineApi extends BaseApi {

    @Inject
    private PricePlanMatrixLineService pricePlanMatrixLineService;

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

    @Inject
    private PricePlanMatrixColumnService pricePlanMatrixColumnService;

    @Inject
    private ProviderService providerService;

    @Inject
    private PricePlanMatrixValueService pricePlanMatrixValueService;

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
        updateWithoutDeletePricePlanMatrixLines(ppmVersion, dtoData);
        pricePlanMatrixVersionService.updatePricePlanMatrixVersion(ppmVersion);

        return new GetPricePlanVersionResponseDto(ppmVersion);
    }

    private void updateWithoutDeletePricePlanMatrixLines(PricePlanMatrixVersion ppmVersion,
                                                        PricePlanMatrixLinesDto dtoData) throws MeveoApiException, BusinessException {
        pricePlanMatrixLineService.checkDuplicatePricePlanMatrixValues(dtoData.getPricePlanMatrixLines());
        Provider provider = providerService.getProvider();
        for (PricePlanMatrixLineDto pricePlanMatrixLineDto : dtoData.getPricePlanMatrixLines()) {
            PricePlanMatrixLine pricePlanMatrixLine = new PricePlanMatrixLine();
            if(pricePlanMatrixLineDto.getPpmLineId() != null){
                pricePlanMatrixLine = pricePlanMatrixLineService.findById(pricePlanMatrixLineDto.getPpmLineId());
                if (pricePlanMatrixLine == null) {
                    throw new EntityDoesNotExistsException(PricePlanMatrixLine.class, pricePlanMatrixLineDto.getPpmLineId());
                }
                pricePlanMatrixLineService.converterPricePlanMatrixLineFromDto(ppmVersion, pricePlanMatrixLineDto, pricePlanMatrixLine);
                Set<PricePlanMatrixValue> pricePlanMatrixValues =
                        pricePlanMatrixLineService.getPricePlanMatrixValues(pricePlanMatrixLineDto, pricePlanMatrixLine);
                pricePlanMatrixValues.stream().forEach(ppmv -> pricePlanMatrixValueService.create(ppmv));
                pricePlanMatrixLine.getPricePlanMatrixValues().clear();
                pricePlanMatrixLine.getPricePlanMatrixValues().addAll(pricePlanMatrixValues);
                Set<TradingPricePlanMatrixLine> tradingPricePlanMatrixLines =
                        pricePlanMatrixLineService.getTradingPricePlanMatrixLine(pricePlanMatrixLineDto, pricePlanMatrixLine, provider);
                pricePlanMatrixLine.getTradingPricePlanMatrixLines().clear();
                pricePlanMatrixLine.getTradingPricePlanMatrixLines().addAll(tradingPricePlanMatrixLines);
                pricePlanMatrixLine.setRatingAccuracy(pricePlanMatrixLine.getPricePlanMatrixValues().size());
                if(pricePlanMatrixLineDto.getCustomFields() != null) {
                    setCustomFields(pricePlanMatrixLineDto, pricePlanMatrixLine);
                }
                pricePlanMatrixLineService.update(pricePlanMatrixLine);
            }
            else {
                pricePlanMatrixLineService.converterPricePlanMatrixLineFromDto(ppmVersion, pricePlanMatrixLineDto, pricePlanMatrixLine);
                pricePlanMatrixLine.setPricePlanMatrixValues(pricePlanMatrixLineService.getPricePlanMatrixValues(pricePlanMatrixLineDto, pricePlanMatrixLine));
                pricePlanMatrixLine.setRatingAccuracy(pricePlanMatrixLine.getPricePlanMatrixValues().size());
                pricePlanMatrixLine.setTradingPricePlanMatrixLines(pricePlanMatrixLineService.getTradingPricePlanMatrixLine(pricePlanMatrixLineDto, pricePlanMatrixLine, provider));
                setCustomFields(pricePlanMatrixLineDto, pricePlanMatrixLine);
                pricePlanMatrixLineService.create(pricePlanMatrixLine);

                pricePlanMatrixLineDto.setPpmLineId(pricePlanMatrixLine.getId());

                ppmVersion.getLines().add(pricePlanMatrixLine);
            }

        }
    }

    private void setCustomFields(PricePlanMatrixLineDto pricePlanMatrixLineDto, PricePlanMatrixLine pricePlanMatrixLine) {
        try {
            populateCustomFields(pricePlanMatrixLineDto.getCustomFields(),
                    pricePlanMatrixLine, true, true);

        } catch (MissingParameterException | InvalidParameterException exception) {
            log.error("Failed to associate custom field instance to an entity: {}", exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            log.error("Failed to associate custom field instance to an entity", exception);
            throw exception;
        }
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
