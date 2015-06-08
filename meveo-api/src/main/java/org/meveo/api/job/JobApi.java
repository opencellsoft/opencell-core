package org.meveo.api.job;

import javax.ejb.Stateless;
import javax.inject.Inject;

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
	 * @param timerInfoDTO, timerInfoDTO.getTimerName() contains the code of JobInstance
	 * @param currentUser
	 * @throws MeveoApiException
	 */

	public void executeJob(JobInstanceInfoDto timerInfoDTO, User currentUser) throws MeveoApiException {
		if (StringUtils.isBlank(timerInfoDTO.getTimerName())) {
			missingParameters.add("timerName");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}else{
			try {
				jobInstanceService.executeAPITimer(timerInfoDTO, currentUser);
			} catch (Exception e) {
				log.error("Failed to execute api timer ",e);
			}
		} 
	}
}

