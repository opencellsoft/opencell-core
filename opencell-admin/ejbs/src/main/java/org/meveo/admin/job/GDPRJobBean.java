package org.meveo.admin.job;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.Provider;
import org.meveo.model.dwh.GdprConfiguration;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.order.Order;
import org.meveo.model.payments.AccountOperation;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.order.OrderService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 */
@Stateless
public class GDPRJobBean extends BaseJobBean {

	@Inject
	private Logger log;

	@Inject
	@CurrentUser
	private MeveoUser currentUser;

	@Inject
	@ApplicationProvider
	private Provider appProvider;

	@Inject
	private ProviderService providerService;

	@Inject
	private SubscriptionService subscriptionService;

	@Inject
	private OrderService orderService;

	@Inject
	private InvoiceService invoiceService;

	@Inject
	private AccountOperationService accountOperationService;

	@JpaAmpNewTx
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, String parameter) {

		MeveoUser lastCurrentUser = currentUser.unProxy();

		Provider provider = providerService.findById(appProvider.getId());
		GdprConfiguration gdprConfiguration = provider.getGdprConfigurationNullSafe();

		try {
			if (gdprConfiguration.isDeleteSubscription()) {
				List<Subscription> inactiveSubscriptions = subscriptionService.listInactiveSubscriptions(gdprConfiguration.getInactiveOrderLife());
				log.debug("Found {} inactive subscriptions", inactiveSubscriptions.size());
				if (!inactiveSubscriptions.isEmpty()) {
					subscriptionService.bulkDelete(inactiveSubscriptions);
				}
			}

			if (gdprConfiguration.isDeleteOrder()) {
				List<Order> inactiveOrders = orderService.listInactiveOrders(gdprConfiguration.getInactiveOrderLife());
				log.debug("Found {} inactive orders", inactiveOrders.size());
				if (!inactiveOrders.isEmpty()) {
					orderService.bulkDelete(inactiveOrders);
				}
			}

			if (gdprConfiguration.isDeleteInvoice()) {
				List<Invoice> inactiveInvoices = invoiceService.listInactiveInvoice(gdprConfiguration.getInvoiceLife());
				log.debug("Found {} inactive invoices", inactiveInvoices.size());
				if (!inactiveInvoices.isEmpty()) {
					invoiceService.bulkDelete(inactiveInvoices);
				}
			}

			if (gdprConfiguration.isDeleteAccounting()) {
				List<AccountOperation> inactiveAccountOps = accountOperationService.listInactiveAccountOperations(gdprConfiguration.getAccountingLife());
				log.debug("Found {} inactive accountOperations", inactiveAccountOps.size());
				if (!inactiveAccountOps.isEmpty()) {
					accountOperationService.bulkDelete(inactiveAccountOps);
				}
			}

			if (gdprConfiguration.isDeleteAoCheckUnpaidLife()) {
				List<AccountOperation> unpaidAccountOperations = accountOperationService.listUnpaidAccountOperations(gdprConfiguration.getAoCheckUnpaidLife());
				log.debug("Found {} unpaid accountOperations", unpaidAccountOperations.size());
				if (!unpaidAccountOperations.isEmpty()) {
					accountOperationService.bulkDelete(unpaidAccountOperations);
				}
			}

			// TODO: check for old customer prospect

			// TODO: check for mailing
		} catch (Exception e) {
			log.error("Failed to run GDPR data erasure job", e);
			result.registerError(e.getMessage());
		}
	}
}
