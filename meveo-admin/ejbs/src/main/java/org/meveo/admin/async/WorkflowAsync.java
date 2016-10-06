/**
 * 
 */
package org.meveo.admin.async;

import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.job.UnitWorkflowJobBean;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.wf.Workflow;

/**
 * @author anasseh
 * 
 */

@Stateless
public class WorkflowAsync {

    @Inject
    UnitWorkflowJobBean unitWorkflowJobBean;

    @Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<BusinessEntity> entities,Workflow workflow, JobExecutionResultImpl result, User currentUser) {
        for (BusinessEntity entity  : entities) {
        	unitWorkflowJobBean.execute(result, currentUser, entity,workflow);
        }
        return new AsyncResult<String>("OK");
    }
}
