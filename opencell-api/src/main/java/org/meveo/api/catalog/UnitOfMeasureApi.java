package org.meveo.api.catalog;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.UnitOfMeasureDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.UnitOfMeasure;
import org.meveo.service.catalog.impl.UnitOfMeasureService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mounir Bahije
 */
@Stateless
public class UnitOfMeasureApi extends BaseCrudApi<UnitOfMeasure, UnitOfMeasureDto> {

    @Inject
    private UnitOfMeasureService unitOfMeasureService;

    @Override
    public UnitOfMeasure create(UnitOfMeasureDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        if (unitOfMeasureService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(UnitOfMeasure.class, postData.getCode());
        }

        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        unitOfMeasure.setCode(postData.getCode());
        unitOfMeasure.setDescription(postData.getDescription());
        unitOfMeasure.setSymbol(postData.getSymbol());

        unitOfMeasureService.create(unitOfMeasure);

        return unitOfMeasure;

    }

    @Override
    public UnitOfMeasure update(UnitOfMeasureDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        UnitOfMeasure unitOfMeasure = unitOfMeasureService.findByCode(postData.getCode());
        if (unitOfMeasure == null) {
            throw new EntityAlreadyExistsException(UnitOfMeasure.class, postData.getCode());
        }

        unitOfMeasure.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        unitOfMeasure.setDescription(postData.getDescription());
        unitOfMeasure.setSymbol(postData.getSymbol());

        unitOfMeasure = unitOfMeasureService.update(unitOfMeasure);
        return unitOfMeasure;
    }

    /**
     * 
     * @param code unitOfMeasure's code
     * @return found unitOfMeasure
     * @throws MeveoApiException meveo api exception.
     */
    public UnitOfMeasureDto find(String code) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        UnitOfMeasureDto unitOfMeasureDto = null;

        UnitOfMeasure unitOfMeasure = unitOfMeasureService.findByCode(code);

        if (unitOfMeasure == null) {
            throw new EntityDoesNotExistsException(UnitOfMeasure.class, code);
        }

        unitOfMeasureDto = new UnitOfMeasureDto(unitOfMeasure);

        return unitOfMeasureDto;

    }

    /**
     * 
     * 
     * @return list of unitOfMeasures
     * @throws MeveoApiException meveo api exception
     */
    public List<UnitOfMeasureDto> list() throws MeveoApiException {
        List<UnitOfMeasureDto> unitOfMeasureDtos = new ArrayList<UnitOfMeasureDto>();

        List<UnitOfMeasure> unitOfMeasures = unitOfMeasureService.list();
        if (unitOfMeasures != null && !unitOfMeasures.isEmpty()) {
            for (UnitOfMeasure unitOfMeasure  : unitOfMeasures) {
                UnitOfMeasureDto unitOfMeasureDto = new UnitOfMeasureDto(unitOfMeasure);
                unitOfMeasureDtos.add(unitOfMeasureDto);
            }
        }

        return unitOfMeasureDtos;
    }

    /**
     * 
     * @param unitOfMeasureId unitOfMeasure's id
     * 
     * @return unitOfMeasureDto for given id
     * @throws MeveoApiException meveo api exception.
     */
    public UnitOfMeasureDto findById(String unitOfMeasureId) throws MeveoApiException {
        UnitOfMeasureDto unitOfMeasureDto = null;

        if (!StringUtils.isBlank(unitOfMeasureId)) {
            try {
                long id = Integer.parseInt(unitOfMeasureId);
                UnitOfMeasure unitOfMeasure = unitOfMeasureService.findById(id);
                if (unitOfMeasure == null) {
                    throw new EntityDoesNotExistsException(UnitOfMeasure.class, id);
                }
                unitOfMeasureDto = new UnitOfMeasureDto(unitOfMeasure);

            } catch (NumberFormatException nfe) {
                throw new MeveoApiException("Passed unitOfMeasureId is invalid.");
            }

        }

        return unitOfMeasureDto;
    }

}
