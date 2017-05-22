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

import org.meveo.admin.job.UnitPaymentCardJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;

/**
 * @author anasseh
 * 
 */

@Stateless
public class PaymentCardAsync {

    @Inject
    UnitPaymentCardJobBean unitPaymentCardJobBean;

    @Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<Long> ids, JobExecutionResultImpl result, String callingMode, boolean createAO, boolean matchingAO, String mappingConf, String outputDir, String recordVariableName) {
        for (Long id : ids) {
        	unitPaymentCardJobBean.execute(result, id);
        }
        return new AsyncResult<String>("OK");
    }
}
