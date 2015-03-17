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
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.customer.Sellers;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.crm.impl.CustomerService;
import org.slf4j.Logger;

@Stateless
public class ExportCustomersJobBean {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");

	@Inject
	private Logger log;

	@Inject
	private CustomerService customerService;

	Sellers sellers;
	ParamBean param = ParamBean.getInstance();

	int nbSellers;

	int nbCustomers;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	public void execute(JobExecutionResultImpl result, String parameter, User currentUser) {
		Provider provider = currentUser.getProvider();

		String exportDir = param.getProperty("providers.rootDir", "/tmp/meveo/") + File.separator + provider.getCode()
				+ File.separator + "exports" + File.separator + "customers" + File.separator;
		log.info("exportDir=" + exportDir);
		File dir = new File(exportDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String timestamp = sdf.format(new Date());
		List<Seller> sellersInDB = customerService.listSellersWithCustomers(provider);
		sellers = new Sellers(sellersInDB, provider.getCode());// ,param.getProperty("connectorCRM.dateFormat",
																// "yyyy-MM-dd"));
		for (org.meveo.model.jaxb.customer.Seller seller : sellers.getSeller()) {
			List<Customer> customers = customerService.listBySellerCode(provider, seller.getCode());
			seller.setCustomers(customers);
		}
		try {
			JAXBUtils.marshaller(sellers, new File(dir + File.separator + "CUSTOMER_" + timestamp + ".xml"));
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

}
