package org.meveo.service.script;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.job.JobInstanceService;

public class CancelInvalidJobsScript extends Script {

	private static final long serialVersionUID = 1L;

	private JobInstanceService jobInstanceService = (JobInstanceService) getServiceInterface("JobInstanceService");

	@Override
	public void execute(Map<String, Object> methodContext) throws BusinessException {

		jobInstanceService.cancelInvalidJobsFromCache();

	}

}
