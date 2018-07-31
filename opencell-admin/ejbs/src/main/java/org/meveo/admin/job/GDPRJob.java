package org.meveo.admin.job;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

/**
 * Checks for data records that have exceeded the maximum storage duration
 * specified in GDPRConfiguration. This job runs monthly.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Stateless
public class GDPRJob extends Job {

	@Inject
	private GDPRJobBean gdprJobBean;

	@Override
	protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
		gdprJobBean.execute(result, jobInstance.getParametres());
	}

	@Override
	public JobCategoryEnum getJobCategory() {
		return JobCategoryEnum.DWH;
	}

}
