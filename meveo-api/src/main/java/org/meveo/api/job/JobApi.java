package org.meveo.api.job;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.TimerInfoDto;
import org.meveo.service.job.TimerEntityService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class JobApi extends BaseApi {


	@Inject
	private TimerEntityService timerEntityService;

	public void executeTimer(TimerInfoDto timerInfoDTO, User currentUser) throws MeveoApiException,Exception {
		if (StringUtils.isBlank(timerInfoDTO.getTimerName())) {
			missingParameters.add("timerName");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		} else {
			try {
				timerEntityService.executeAPITimer(timerInfoDTO, currentUser);
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}
		}
	}
}