/**
 * 
 */
package org.meveo.admin.async;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.job.FlatFileProcessingJobBean;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.ScriptInstance;
import org.slf4j.Logger;

/**
 * @author anasseh
 * 
 */

@Stateless
public class FlatFileProcessingAsync {

    @Inject
    private FlatFileProcessingJobBean flatFileProcessingJobBean;

    @Inject
    protected Logger log;

    @Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<File> files, JobExecutionResultImpl result, String parameter, User currentUser,String mappingConf,ScriptInstance scriptInstanceFlow) {
        for (File file : files) {
        	flatFileProcessingJobBean.execute(result, parameter, currentUser, file, mappingConf,scriptInstanceFlow);
        }

        return new AsyncResult<String>("OK");
    }
}