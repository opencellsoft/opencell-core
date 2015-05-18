/**
 * 
 */
package org.meveo.admin.async;

import java.io.File;
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.job.MediationJobBean;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.slf4j.Logger;

/**
 * @author anasseh
 *
 */

@Stateless
public class MediationAsync {

	@Inject
	private MediationJobBean mediationJobBean;

	@Inject
	protected Logger log;

	@Asynchronous
	public void launchAndForget(List<File> files,JobExecutionResultImpl result, String parameter, User currentUser){
		for(File file: files){
			mediationJobBean.execute(result, parameter, currentUser,file);	
		}
	}

}
