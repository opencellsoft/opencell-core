package org.meveo.admin.job;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.communication.postalmail.GdprConfiguration;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.util.ApplicationProvider;

/**
 * @author Edward P. Legaspi
 */
@Stateless
public class GDPRJobBean extends BaseJobBean {

	@Inject
	@CurrentUser
	private MeveoUser currentUser;

	@Inject
	@ApplicationProvider
	private Provider appProvider;

	private ProviderService providerService;

	@JpaAmpNewTx
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, String parameter) {
		Date now = new Date();

		MeveoUser lastCurrentUser = currentUser.unProxy();
		Provider provider = providerService.findById(appProvider.getId());
		GdprConfiguration gdprConfiguration = provider.getGdprConfigurationNullSafe();

		// TODO: check for inactive subscription
		

		// TODO: check for inactive order

		// TODO: check for old invoice

		// TODO: check for old accounting

		// TODO: check for old customer prospect

		// TODO: check for mailing

		// TODO: check for unpaid ao
	}
}
