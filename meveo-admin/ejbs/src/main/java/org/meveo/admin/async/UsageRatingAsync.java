/**
 * 
 */
package org.meveo.admin.async;

import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.job.UnitUsageRatingJobBean;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;

/**
 * @author anasseh
 *
 */

@Stateless
public class UsageRatingAsync {
	
	@Inject
	UnitUsageRatingJobBean unitUsageRatingJobBean;

	@Asynchronous
	public void launchAndForget(List<Long> ids,JobExecutionResultImpl result,User currentUser) {
		
		for (Long id : ids) {
			unitUsageRatingJobBean.execute(result, currentUser, id);
		}	
	}
}
