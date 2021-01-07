package org.meveo.api.catalog;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.service.catalog.impl.PricePlanMatrixColumnService;
import org.meveo.service.catalog.impl.PricePlanMatrixLineService;
import org.meveo.service.catalog.impl.PricePlanMatrixValueService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class PricePlanMatrixLineApi extends BaseApi {

    @Inject
    private PricePlanMatrixLineService pricePlanMatrixLineService;

    public PricePlanMatrixLineDto addPricePlanMatrixLine(PricePlanMatrixLineDto dtoData) throws MeveoApiException, BusinessException {

        checkMissingParameters(dtoData);

        return pricePlanMatrixLineService.createPricePlanMatrixLine(dtoData);
    }

    public PricePlanMatrixLineDto updatePricePlanMatrixLine(PricePlanMatrixLineDto pricePlanMatrixLineDto) {

        checkMissingParameters(pricePlanMatrixLineDto);


        return pricePlanMatrixLineService.updatePricePlanMatrixLine(pricePlanMatrixLineDto);
    }

    private void checkMissingParameters(PricePlanMatrixLineDto dtoData) {
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
