package org.meveo.api.dwh;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.model.dwh.MeasurementPeriodEnum;
import org.meveocrm.services.dwh.MeasurableQuantityService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class MeasurableQuantityApi extends BaseApi {

	@Inject
	private MeasurableQuantityService measurableQuantityService;

	public void create(MeasurableQuantityDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())) {
			if (measurableQuantityService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(MeasurableQuantity.class, postData.getCode());
			}

			MeasurableQuantity measurableQuantity = new MeasurableQuantity();
			measurableQuantity.setCode(postData.getCode());
			measurableQuantity.setAdditive(postData.isAdditive());
			measurableQuantity.setDescription(postData.getDescription());
			measurableQuantity.setDimension1(postData.getDimension1());
			measurableQuantity.setDimension2(postData.getDimension2());
			measurableQuantity.setDimension3(postData.getDimension3());
			measurableQuantity.setDimension4(postData.getDimension4());
			measurableQuantity.setLastMeasureDate(postData.getLastMeasureDate());
			try {
				measurableQuantity.setMeasurementPeriod(MeasurementPeriodEnum.valueOf(postData.getMeasurementPeriod()));
			} catch (IllegalArgumentException e) {
				log.warn("Invalid enum {} for value={}", MeasurementPeriodEnum.class, postData.getMeasurementPeriod());
			}
			measurableQuantity.setSqlQuery(postData.getSqlQuery());
			measurableQuantity.setTheme(postData.getTheme());

			measurableQuantityService.create(measurableQuantity, currentUser, currentUser.getProvider());
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public MeasurableQuantityDto find(String itemCode, User currentUser) {
		MeasurableQuantityDto result = new MeasurableQuantityDto();

		// TODO Manu: populate dto

		return result;
	}

	// TODO Manu - create remaining rud + list operations
}
