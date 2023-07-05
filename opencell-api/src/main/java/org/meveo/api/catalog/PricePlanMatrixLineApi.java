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
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.service.catalog.impl.PricePlanMatrixLineService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;

@Stateless
public class PricePlanMatrixLineApi extends BaseApi {

    @Inject
    private PricePlanMatrixLineService pricePlanMatrixLineService;

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

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
}
