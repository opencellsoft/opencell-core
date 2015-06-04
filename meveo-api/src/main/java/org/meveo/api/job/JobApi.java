package org.meveo.api.job;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.TimerInfoDto;
import org.meveo.service.job.TimerEntityService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class JobApi extends BaseApi {

	@Inject
	private TimerEntityService timerEntityService;

	public void executeTimer(TimerInfoDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getTimerName())) {
			try {
				timerEntityService.executeAPITimer(postData,currentUser);
			} catch (Exception e) {
				log.error("Failed to execute api timer ",e);
			}
		} else {
			if (StringUtils.isBlank(postData.getTimerName())) {
				missingParameters.add("timerName");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
}
