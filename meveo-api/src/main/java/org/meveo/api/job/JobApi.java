package org.meveo.api.job;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.job.TimerInfoDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.service.job.TimerEntityService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class JobApi extends BaseApi {

	@Inject
	private Logger log;

	@Inject
	private TimerEntityService timerEntityService;

	public void executeTimer(TimerInfoDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getTimerName())) {
			try {
				timerEntityService.executeAPITimer(postData.getTimerName(),currentUser);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		} else {
			if (StringUtils.isBlank(postData.getTimerName())) {
				missingParameters.add("timerName");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
}
