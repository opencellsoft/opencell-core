package org.meveo.api.job;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobInstanceInfoDto;
import org.meveo.service.job.JobInstanceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class JobApi extends BaseApi {

	@Inject
	private JobInstanceService jobInstanceService;

	/**
	 * 
	 * @param timerInfoDTO
	 *            , timerInfoDTO.getTimerName() contains the code of JobInstance
	 * @param currentUser
	 * @throws Exception
	 */

	public Long executeJob(JobInstanceInfoDto timerInfoDTO, User currentUser) throws MeveoApiException {
		if (StringUtils.isBlank(timerInfoDTO.getCode()) && StringUtils.isBlank(timerInfoDTO.getTimerName())) {
			missingParameters.add("timerName or code");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		} else {
			try {
				return jobInstanceService.executeAPITimer(timerInfoDTO, currentUser);
			} catch (BusinessException e) {
				throw new MeveoApiException(e.getMessage());
			}
		}
	}

}
