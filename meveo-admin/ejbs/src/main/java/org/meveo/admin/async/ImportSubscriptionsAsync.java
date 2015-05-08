/**
 * 
 */
package org.meveo.admin.async;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.job.importexport.ImportSubscriptionsJobBean;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;

/**
 * @author anasseh
 *
 */

@Stateless
public class ImportSubscriptionsAsync {

    @Inject
    private ImportSubscriptionsJobBean importSubscriptionsJobBean;

	@Asynchronous
	public void launchAndForget(JobExecutionResultImpl result, User currentUser) {
		importSubscriptionsJobBean.execute(result, currentUser);
	}	
}
