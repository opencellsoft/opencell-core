package org.meveo.admin.async;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.inject.Inject;

import org.meveo.admin.job.importexport.ImportCustomerBankDetailsJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;

public class ImportCustomerBankDetailsAsync {

    @Inject
    private ImportCustomerBankDetailsJobBean importCustomerBankDetailsJobBean;

    @Inject
    private CurrentUserProvider currentUserProvider;

    @Asynchronous
    public Future<String> launchAndForget(JobExecutionResultImpl result, MeveoUser lastCurrentUser) {
        currentUserProvider.reestablishAuthentication(lastCurrentUser);
        importCustomerBankDetailsJobBean.execute(result);
        return new AsyncResult<String>("OK");
    }
}