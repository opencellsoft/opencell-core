/**
 * 
 */
package org.meveo.admin.async;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.job.importexport.ImportCustomersJobBean;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;

/**
 * @author anasseh
 * 
 */

@Stateless
public class ImportCustomersAsync {

    @Inject
    private ImportCustomersJobBean importCustomersJobBean;

    @Asynchronous
    public Future<String> launchAndForget(JobExecutionResultImpl result, User currentUser) {
        importCustomersJobBean.execute(result, currentUser);

        return new AsyncResult<String>("OK");
    }
}