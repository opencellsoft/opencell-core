package org.meveo.admin.job;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.ParamBean;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.XMLInvoiceCreator;
import org.slf4j.Logger;

@Stateless
public class XMLInvoiceGenerationJobBean {

	@Inject
	private Logger log;

	@Inject
	private BillingRunService billingRunService;

	@Inject
	private XMLInvoiceCreator xmlInvoiceCreator;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, String parameter, User currentUser) {
		Provider provider = currentUser.getProvider();
		List<BillingRun> billingRuns = new ArrayList<BillingRun>();

		if (parameter != null && parameter.trim().length() > 0) {
			try {
				billingRuns.add(billingRunService.getBillingRunById(Long.parseLong(parameter), provider));
			} catch (Exception e) {
				log.error(e.getMessage());
				result.registerError(e.getMessage());
			}
		} else {
			billingRuns = billingRunService.getValidatedBillingRuns(provider);
		}

		log.info("billingRuns to process={}", billingRuns.size());

		ParamBean param = ParamBean.getInstance();
		String invoicesDir = param.getProperty("providers.rootDir", "/tmp/meveo");

		for (BillingRun billingRun : billingRuns) {
			try {
				File billingRundir = new File(invoicesDir + File.separator + provider.getCode() + File.separator
						+ "invoices" + File.separator + "xml" + File.separator + billingRun.getId());
				billingRundir.mkdirs();

				for (Invoice invoice : billingRun.getInvoices()) {
					long startDate = System.currentTimeMillis();

					Future<Boolean> xmlCreated = xmlInvoiceCreator.createXMLInvoice(invoice, billingRundir);
					xmlCreated.get();

					log.info("Invoice creation delay :" + (System.currentTimeMillis() - startDate)
							+ ", xmlCreated={1} " + xmlCreated.get() + "");
				}

				billingRun.setXmlInvoiceGenerated(true);
				billingRun.updateAudit(currentUser);
			} catch (Exception e) {
				log.error(e.getMessage());
				result.registerError(e.getMessage());
			}
		}
	}
}
