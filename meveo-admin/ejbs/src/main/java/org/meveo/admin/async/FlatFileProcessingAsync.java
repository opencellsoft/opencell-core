/**
 * 
 */
package org.meveo.admin.async;

import java.io.File;
import java.util.List;
import java.util.Map;
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
    public Future<String> launchAndForget(List<File> files, JobExecutionResultImpl result, String inputDir, User currentUser,String mappingConf,String scriptInstanceFlowCode, String recordVariableName, Map<String, Object> context, String originFilename) {
        for (File file : files) {
        	flatFileProcessingJobBean.execute(result, inputDir, currentUser, file, mappingConf,scriptInstanceFlowCode,recordVariableName,context,originFilename);
        }

        return new AsyncResult<String>("OK");
    }
}