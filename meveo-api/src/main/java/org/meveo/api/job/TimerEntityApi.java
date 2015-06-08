package org.meveo.api.job;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.job.TimerEntityService;

@Stateless
public class TimerEntityApi extends BaseApi {

	@Inject
	private TimerEntityService timerEntityService;



	public void create(TimerEntityDto timerEntityDto, User currentUser) throws MeveoApiException {
		if ( StringUtils.isBlank(timerEntityDto.getCode()) || StringUtils.isBlank(timerEntityDto.getHour()) 
				||StringUtils.isBlank(timerEntityDto.getMinute()) || StringUtils.isBlank(timerEntityDto.getSecond())
				|| StringUtils.isBlank(timerEntityDto.getYear()) || StringUtils.isBlank(timerEntityDto.getMonth()) 
				|| StringUtils.isBlank(timerEntityDto.getDayOfMonth())|| StringUtils.isBlank(timerEntityDto.getDayOfWeek())) {

			if (StringUtils.isBlank(timerEntityDto.getHour())) {
				missingParameters.add("hour");
			}
			if (StringUtils.isBlank(timerEntityDto.getMinute())) {
				missingParameters.add("minute");
			}
			if (StringUtils.isBlank(timerEntityDto.getSecond())) {
				missingParameters.add("second");
			}
			if (StringUtils.isBlank(timerEntityDto.getYear())) {
				missingParameters.add("year");
			}
			if (StringUtils.isBlank(timerEntityDto.getMonth())) {
				missingParameters.add("month");
			}
			if (StringUtils.isBlank(timerEntityDto.getDayOfMonth())) {
				missingParameters.add("dayOfMonth");
			}
			if (StringUtils.isBlank(timerEntityDto.getDayOfWeek())) {
				missingParameters.add("dayOfWeek");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());

		}

		Provider provider = currentUser.getProvider();

		if (timerEntityService.findByCode(timerEntityDto.getCode(), provider) != null) {
			throw new EntityAlreadyExistsException(TimerEntity.class, timerEntityDto.getCode());
		}

		TimerEntity timerEntity = new TimerEntity();
		timerEntity.setCode(timerEntityDto.getCode());
		timerEntity.setDescription(timerEntityDto.getDescription());
		timerEntity.setHour(timerEntityDto.getHour());
		timerEntity.setMinute(timerEntityDto.getMinute());
		timerEntity.setSecond(timerEntityDto.getSecond());
		timerEntity.setYear(timerEntityDto.getYear());
		timerEntity.setMonth(timerEntityDto.getMonth());
		timerEntity.setDayOfMonth(timerEntityDto.getDayOfMonth());
		timerEntity.setDayOfWeek(timerEntityDto.getDayOfWeek());

		timerEntityService.create(timerEntity, currentUser, provider);
	}
}