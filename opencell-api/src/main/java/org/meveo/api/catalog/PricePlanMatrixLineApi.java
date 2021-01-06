package org.meveo.api.catalog;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.BaseCrudApi;
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
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class PricePlanMatrixLineApi extends BaseApi {

    @Inject
    private PricePlanMatrixLineService pricePlanMatrixLineService;

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

    @Inject
    private PricePlanMatrixColumnService pricePlanMatrixColumnService;


    public PricePlanMatrixLine addPricePlanMatrixLine(PricePlanMatrixLineDto dtoData) throws MeveoApiException, BusinessException {

        checkMissingParameters(dtoData);

        PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(dtoData.getPricePlanMatrixCode(), dtoData.getPricePlanMatrixVersion());
        if(pricePlanMatrixVersion == null){
            throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, dtoData.getPricePlanMatrixCode(), "pricePlanMatrixCode", "" + dtoData.getPricePlanMatrixVersion(), "currentVersion");
        }

        PricePlanMatrixLine pricePlanMatrixLine = new PricePlanMatrixLine();
        pricePlanMatrixLine.setPricetWithoutTax(dtoData.getPricetWithoutTax());
        pricePlanMatrixLine.setPricePlanMatrixVersion(pricePlanMatrixVersion);



        List<PricePlanMatrixValue> pricePlanMatrixValues = dtoData.getPricePlanMatrixValues()
                .stream()
                .map(value -> {
                    PricePlanMatrixColumn pricePlanMatrixColumn = loadEntityByCode(pricePlanMatrixColumnService, value.getPpmColumnCode(), PricePlanMatrixColumn.class);
                    PricePlanMatrixValue pricePlanMatrixValue = new PricePlanMatrixValue();
                    pricePlanMatrixValue.setPricePlanMatrixColumn(pricePlanMatrixColumn);
                    pricePlanMatrixValue.setDoubleValue(value.getDoubleValue());
                    pricePlanMatrixValue.setLongValue(value.getLongValue());
                    pricePlanMatrixValue.setStringValue(value.getStringValue());
                    pricePlanMatrixValue.setDateValue(value.getDateValue());
                    pricePlanMatrixValue.setFromDoubleValue(value.getFromDoubleValue());
                    pricePlanMatrixValue.setFromDateValue(value.getFromDateValue());
                    pricePlanMatrixValue.setToDoubleValue(value.getToDoubleValue());
                    pricePlanMatrixValue.setToDateValue(value.getToDateValue());
                    return pricePlanMatrixValue;
                }).collect(Collectors.toList());

        return pricePlanMatrixLineService.addPricePlanMatrixLine(pricePlanMatrixLine, pricePlanMatrixValues);
    }

    public PricePlanMatrixLine updatePricePlanMatrixLine(PricePlanMatrixLineDto pricePlanMatrixLineDto) {
        return null;
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
