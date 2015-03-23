package org.meveo.api.job;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.job.ExecuteJobDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobCategoryEnum;
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

	public void executeJob(ExecuteJobDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getJobCategory()) && !StringUtils.isBlank(postData.getJobName())) {
			try {
				JobCategoryEnum category = JobCategoryEnum.valueOf(postData.getJobCategory());
				timerEntityService.executeViaJob(category, postData.getJobName(), currentUser);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		} else {
			if (StringUtils.isBlank(postData.getJobCategory())) {
				missingParameters.add("jobCategory");
			}
			if (StringUtils.isBlank(postData.getJobName())) {
				missingParameters.add("jobName");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
}
