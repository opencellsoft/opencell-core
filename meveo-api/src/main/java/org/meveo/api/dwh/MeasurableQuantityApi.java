package org.meveo.api.dwh;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.services.dwh.MeasurableQuantityService;

/**
 * @author Andrius Karpavicius
 **/
@Stateless
public class MeasurableQuantityApi extends BaseApi {

    @Inject
    private MeasurableQuantityService measurableQuantityService;

    public void create(MeasurableQuantityDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        if (measurableQuantityService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(MeasurableQuantity.class, postData.getCode());
        }

        MeasurableQuantity measurableQuantity = fromDTO(postData, currentUser, null);
        measurableQuantityService.create(measurableQuantity, currentUser);

    }

    public void update(MeasurableQuantityDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("measurableQuantityCode");
            handleMissingParameters();
        }

        MeasurableQuantity measurableQuantity = measurableQuantityService.findByCode(postData.getCode(), currentUser.getProvider());
        if (measurableQuantity == null) {
            throw new EntityDoesNotExistsException(MeasurableQuantity.class, postData.getCode());
        }

        measurableQuantity = fromDTO(postData, currentUser, measurableQuantity);
        measurableQuantityService.update(measurableQuantity, currentUser);

    }

    public MeasurableQuantityDto find(String measurableQuantityCode, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(measurableQuantityCode)) {
            missingParameters.add("measurableQuantityCode");
            handleMissingParameters();
        }

        MeasurableQuantity measurableQuantity = measurableQuantityService.findByCode(measurableQuantityCode, currentUser.getProvider());
        if (measurableQuantity == null) {
            throw new EntityDoesNotExistsException(MeasurableQuantity.class, measurableQuantityCode);
        }

        MeasurableQuantityDto result = new MeasurableQuantityDto(measurableQuantity);

        return result;
    }

    public void remove(String measurableQuantityCode, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(measurableQuantityCode)) {
            missingParameters.add("measurableQuantityCode");
            handleMissingParameters();
        }

        MeasurableQuantity measurableQuantity = measurableQuantityService.findByCode(measurableQuantityCode, currentUser.getProvider());
        if (measurableQuantity == null) {
            throw new EntityDoesNotExistsException(MeasurableQuantity.class, measurableQuantityCode);
        }

        measurableQuantityService.remove(measurableQuantity, currentUser);
    }

    public void createOrUpdate(MeasurableQuantityDto postData, User currentUser) throws MeveoApiException, BusinessException {
        MeasurableQuantity measurableQuantity = measurableQuantityService.findByCode(postData.getCode(), currentUser.getProvider());
        if (measurableQuantity == null) {
            // create
            create(postData, currentUser);
        } else {
            // update
            update(postData, currentUser);
        }
    }

    public List<MeasurableQuantityDto> list(String measurableQuantityCode, User currentUser) {

        List<MeasurableQuantity> measurableQuantities = null;
        if (StringUtils.isBlank(measurableQuantityCode)) {
            measurableQuantities = measurableQuantityService.list(currentUser.getProvider());
        } else {
            measurableQuantities = measurableQuantityService.findByCodeLike(measurableQuantityCode, currentUser.getProvider());
        }

        List<MeasurableQuantityDto> measurableQuantityDtos = new ArrayList<MeasurableQuantityDto>();

        for (MeasurableQuantity measurableQuantity : measurableQuantities) {
            measurableQuantityDtos.add(new MeasurableQuantityDto(measurableQuantity));
        }

        return measurableQuantityDtos;
    }

    private MeasurableQuantity fromDTO(MeasurableQuantityDto dto, User currentUser, MeasurableQuantity mqToUpdate) {

        MeasurableQuantity mq = new MeasurableQuantity();
        if (mqToUpdate != null) {
            mq = mqToUpdate;
        }

        mq.setCode(dto.getCode());
        mq.setDescription(dto.getDescription());
        mq.setTheme(dto.getTheme());
        mq.setDimension1(dto.getDimension1());
        mq.setDimension2(dto.getDimension2());
        mq.setDimension3(dto.getDimension3());
        mq.setDimension4(dto.getDimension4());
        mq.setEditable(dto.isEditable());
        mq.setAdditive(dto.isAdditive());
        mq.setSqlQuery(dto.getSqlQuery());
        mq.setMeasurementPeriod(dto.getMeasurementPeriod());
        mq.setLastMeasureDate(dto.getLastMeasureDate());

        return mq;
    }
}