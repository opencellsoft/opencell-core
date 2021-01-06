package org.meveo.api.catalog;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.PricePlanMatrixVersionDto;
import org.meveo.api.dto.response.catalog.GetPricePlanVersionResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;

@Stateless
public class PricePlanMatrixVersionApi extends BaseCrudApi<PricePlanMatrixVersion, PricePlanMatrixVersionDto> {

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;
    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    @Override
    public PricePlanMatrixVersion create(PricePlanMatrixVersionDto pricePlanMatrixVersionDto) throws MeveoApiException, BusinessException {
        return createOrUpdate(pricePlanMatrixVersionDto);
    }

    @Override
    public PricePlanMatrixVersion update(PricePlanMatrixVersionDto dtoData) throws MeveoApiException, BusinessException {
        return createOrUpdate(dtoData);
    }

    public void removePricePlanMatrixVersion(String pricePlanMatrixCode, int currentVersion){
        if (StringUtils.isBlank(currentVersion)) {
            missingParameters.add("currentVersion");
        }
        if (StringUtils.isBlank(pricePlanMatrixCode)) {
            missingParameters.add("pricePlanMatrixCode");
        }

        try {
            PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(currentVersion, pricePlanMatrixCode);
            if(pricePlanMatrixVersion==null) {
                throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, pricePlanMatrixCode, "pricePlanMatrixCode", "" + currentVersion, "currentVersion");
            }
            pricePlanMatrixVersionService.removePriceMatrixVersion(pricePlanMatrixVersion);
        }catch (BusinessException exp){
            throw new MeveoApiException(exp);
        }
    }

    @Override
    public PricePlanMatrixVersion createOrUpdate(PricePlanMatrixVersionDto pricePlanMatrixVersionDto) {
        Boolean isMatrix = pricePlanMatrixVersionDto.getMatrix();
        int currentVersion = pricePlanMatrixVersionDto.getVersion();
        String pricePlanMatrixCode = pricePlanMatrixVersionDto.getPricePlanMatrixCode();

        if (StringUtils.isBlank(isMatrix)) {
            missingParameters.add("isMatrix");
        }
        if (StringUtils.isBlank(currentVersion)) {
            missingParameters.add("currentVersion");
        }
        if (StringUtils.isBlank(pricePlanMatrixCode)) {
            missingParameters.add("pricePlanMatrixCode");
        }
        handleMissingParametersAndValidate(pricePlanMatrixVersionDto);

        PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(currentVersion, pricePlanMatrixCode);

        if(pricePlanMatrixVersion == null){
            pricePlanMatrixVersion = populatePricePlanMatrixVersion(new PricePlanMatrixVersion(), pricePlanMatrixVersionDto, VersionStatusEnum.DRAFT, Calendar.getInstance().getTime());
            pricePlanMatrixVersionService.create(pricePlanMatrixVersion);
        } else {
            populatePricePlanMatrixVersion(pricePlanMatrixVersion, pricePlanMatrixVersionDto, pricePlanMatrixVersionDto.getStatusEnum(), pricePlanMatrixVersionDto.getStatusDate());
            pricePlanMatrixVersionService.updatePricePlanMatrixVersion(pricePlanMatrixVersion);
        }

        return pricePlanMatrixVersion;
    }

    private PricePlanMatrixVersion populatePricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion, PricePlanMatrixVersionDto pricePlanMatrixVersionDto, VersionStatusEnum status, Date statusTime) {
        PricePlanMatrix pricePlanMatrix = pricePlanMatrixService.findByCode(pricePlanMatrixVersionDto.getPricePlanMatrixCode());
        if(pricePlanMatrix == null)
            throw new EntityDoesNotExistsException(PricePlanMatrix.class, pricePlanMatrixVersionDto.getPricePlanMatrixCode());
        pricePlanMatrixVersion.setPricePlanMatrix(pricePlanMatrix);
        pricePlanMatrixVersion.setCurrentVersion(pricePlanMatrixVersionDto.getVersion());
        pricePlanMatrixVersion.setValidity(pricePlanMatrixVersionDto.getValidity());
        if(status != null)
            pricePlanMatrixVersion.setStatus(status);
        if(statusTime != null)
            pricePlanMatrixVersion.setStatusDate(statusTime);
        pricePlanMatrixVersion.setPriceWithoutTax(pricePlanMatrixVersionDto.getPriceWithoutTax());
        pricePlanMatrixVersion.setMatrix(pricePlanMatrixVersionDto.getMatrix());
        pricePlanMatrixVersion.setLabel(pricePlanMatrixVersionDto.getLabel());
        return pricePlanMatrixVersion;
    }

    public GetPricePlanVersionResponseDto updateProductVersionStatus(String pricePlanMatrixCode, int currentVersion, VersionStatusEnum status) {
        try {
            PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(currentVersion, pricePlanMatrixCode);
            if(pricePlanMatrixVersion==null) {
                throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class,pricePlanMatrixCode,"pricePlanMatrixCode",""+currentVersion,"currentVersdion");
            }
            pricePlanMatrixVersionService.updateProductVersionStatus(pricePlanMatrixVersion, status);
            return new GetPricePlanVersionResponseDto(pricePlanMatrixVersion);
        } catch (BusinessException e) {
            throw new MeveoApiException(e);
        }
    }

    public GetPricePlanVersionResponseDto duplicateProductVersion(String pricePlanMatrixCode, int currentVersion) {

        try {
            PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(currentVersion, pricePlanMatrixCode);
            if(pricePlanMatrixVersion==null) {
                throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class,pricePlanMatrixCode,"pricePlanMatrixCode",""+currentVersion,"currentVersion");
            }
            return new GetPricePlanVersionResponseDto(pricePlanMatrixVersionService.duplicate(pricePlanMatrixVersion));
        } catch (BusinessException e) {
            throw new MeveoApiException(e);
        }
    }
}
