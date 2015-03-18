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
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.subscription.Subscriptions;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.SubscriptionService;
import org.slf4j.Logger;

@Stateless
public class ExportSubscriptionsJobBean {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");

	@Inject
	private Logger log;

	@Inject
	private SubscriptionService subscriptionService;

	Subscriptions subscriptions;
	ParamBean param = ParamBean.getInstance();

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	public void execute(JobExecutionResultImpl result, String parameter, User currentUser) {
		Provider provider = currentUser.getProvider();

		String exportDir = param.getProperty("providers.rootDir", "/tmp/meveo/") + File.separator + provider.getCode()
				+ File.separator + "exports" + File.separator + "subscriptions" + File.separator;
		log.info("exportDir=" + exportDir);
		File dir = new File(exportDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String timestamp = sdf.format(new Date());
		List<org.meveo.model.billing.Subscription> subs = subscriptionService.list();
		subscriptions = new Subscriptions(subs, param.getProperty("connectorCRM.dateFormat", "yyyy-MM-dd"));
		try {
			JAXBUtils.marshaller(subscriptions, new File(dir + File.separator + "SUB_" + timestamp + ".xml"));
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

}
