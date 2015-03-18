package org.meveo.admin.job.importexport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.xml.bind.JAXBException;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.AccountImportHisto;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.account.BillingAccounts;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.BillingAccountService;
import org.slf4j.Logger;

@Stateless
public class ExportAccountsJobBean {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");

	@Inject
	private Logger log;

	@Inject
	private BillingAccountService billingAccountService;

	BillingAccounts billingAccounts;
	ParamBean param = ParamBean.getInstance();

	int nbBillingAccounts;

	int nbUserAccounts;
	AccountImportHisto accountImportHisto;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	public void execute(JobExecutionResultImpl result, String parameter, User currentUser) {
		Provider provider = currentUser.getProvider();

		String exportDir = param.getProperty("providers.rootDir", "/tmp/meveo/") + File.separator + provider.getCode()
				+ File.separator + "exports" + File.separator + "accounts" + File.separator;
		log.info("exportDir=" + exportDir);
		File dir = new File(exportDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String timestamp = sdf.format(new Date());
		List<org.meveo.model.billing.BillingAccount> bas = billingAccountService.list();
		billingAccounts = new BillingAccounts(bas, param.getProperty("connectorCRM.dateFormat", "yyyy-MM-dd"));
		try {
			JAXBUtils.marshaller(billingAccounts, new File(dir + File.separator + "ACCOUNT_" + timestamp + ".xml"));
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

}
