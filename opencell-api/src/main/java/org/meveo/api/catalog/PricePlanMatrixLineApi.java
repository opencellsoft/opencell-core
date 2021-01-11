package org.meveo.api.catalog;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.service.catalog.impl.PricePlanMatrixLineService;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class PricePlanMatrixLineApi extends BaseApi {

    @Inject
    private PricePlanMatrixLineService pricePlanMatrixLineService;

    public PricePlanMatrixLineDto addPricePlanMatrixLine(PricePlanMatrixLineDto dtoData) throws MeveoApiException, BusinessException {

        checCommunMissingParameters(dtoData);

        return pricePlanMatrixLineService.createPricePlanMatrixLine(dtoData);
    }

    public PricePlanMatrixLineDto updatePricePlanMatrixLine(PricePlanMatrixLineDto pricePlanMatrixLineDto) {

        if(StringUtils.isBlank(pricePlanMatrixLineDto.getPpmLineId()))
            missingParameters.add("ppmLineId");
        checCommunMissingParameters(pricePlanMatrixLineDto);


        return pricePlanMatrixLineService.updatePricePlanMatrixLine(pricePlanMatrixLineDto);
    }

    private void checCommunMissingParameters(PricePlanMatrixLineDto dtoData) {
        if(StringUtils.isBlank(dtoData.getPricePlanMatrixCode())){
            missingParameters.add("pricePlanMatrixCode");
        }
        if(StringUtils.isBlank(dtoData.getPricePlanMatrixVersion())){
            missingParameters.add("pricePlanMatrixVersion");
        }

        dtoData.getPricePlanMatrixValues().stream()
                .forEach(value -> {
                    if(StringUtils.isBlank(value.getPpmColumnCode())){
                        missingParameters.add("pricePlanMatrixValues.ppmColumnCode");
                    }
                });

        handleMissingParametersAndValidate(dtoData);
    }

}
