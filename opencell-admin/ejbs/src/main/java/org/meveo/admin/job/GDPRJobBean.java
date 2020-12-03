package org.meveo.admin.job;

import org.meveo.admin.async.GDPRJobAsync;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.dwh.GdprConfiguration;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.order.Order;
import org.meveo.model.payments.AccountOperation;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.order.OrderService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

	@Inject
	private CustomerService customerService;

	@Inject
	private GDPRJobAsync gdprJobAsync;

	@JpaAmpNewTx
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, String parameter) {

		GdprConfiguration gdprConfiguration = providerService.getEntityManager()
				.createQuery("Select p.gdprConfiguration From Provider p Where p.id=:providerId", GdprConfiguration.class)
				.setParameter("providerId", appProvider.getId())
				.getResultList().stream().findFirst().orElse(null);

		if (gdprConfiguration == null) {
			log.warn("No GDPR Config found for provider[id={}], so no items will be processed!", appProvider.getId() );
			return;
		}

		Map<String, Future<int[]>> futures = new HashMap<>();
		try {
			if (gdprConfiguration.isDeleteSubscription()) {
				List<Subscription> inactiveSubscriptions = subscriptionService.listInactiveSubscriptions(gdprConfiguration.getInactiveSubscriptionLife());
				log.info("Found {} inactive subscriptions", inactiveSubscriptions.size());
				if (!inactiveSubscriptions.isEmpty()) {
					futures.put("inactive subscriptions", gdprJobAsync.subscriptionBulkDelete(inactiveSubscriptions));
				}
			}

			if (gdprConfiguration.isDeleteOrder()) {
				List<Order> inactiveOrders = orderService.listInactiveOrders(gdprConfiguration.getInactiveOrderLife());
				log.info("Found {} inactive orders", inactiveOrders.size());
				if (!inactiveOrders.isEmpty()) {
					futures.put("inactive orders", gdprJobAsync.orderBulkDelete(inactiveOrders));
				}
			}

			if (gdprConfiguration.isDeleteInvoice()) {
				List<Invoice> inactiveInvoices = invoiceService.listInactiveInvoice(gdprConfiguration.getInvoiceLife());
				log.info("Found {} inactive invoices", inactiveInvoices.size());
				if (!inactiveInvoices.isEmpty()) {
					futures.put("inactive invoices", gdprJobAsync.invoiceBulkDelete(inactiveInvoices));
				}
			}

			if (gdprConfiguration.isDeleteAccounting()) {
				List<AccountOperation> inactiveAccountOps = accountOperationService.listInactiveAccountOperations(gdprConfiguration.getAccountingLife());
				log.info("Found {} inactive accountOperations", inactiveAccountOps.size());
				if (!inactiveAccountOps.isEmpty()) {
					futures.put("inactive accountOperations", gdprJobAsync.accountOperationBulkDelete(inactiveAccountOps));
				}
			}

			if (gdprConfiguration.isDeleteAoCheckUnpaidLife()) {
				List<AccountOperation> unpaidAccountOperations = accountOperationService.listUnpaidAccountOperations(gdprConfiguration.getAoCheckUnpaidLife());
				log.info("Found {} unpaid accountOperations", unpaidAccountOperations.size());
				if (!unpaidAccountOperations.isEmpty()) {
					futures.put("unpaid accountOperations", gdprJobAsync.accountOperationBulkDelete(unpaidAccountOperations));
				}
			}

			if(gdprConfiguration.isDeleteCustomerProspect()) {
				List<Customer> oldCustomerProspects = customerService.listInactiveProspect(gdprConfiguration.getCustomerProspectLife());
				log.info("Found {} old customer prospects", oldCustomerProspects.size());
				if (!oldCustomerProspects.isEmpty()) {
					futures.put("old prospects", gdprJobAsync.customerBulkDelete(oldCustomerProspects));
				}
			}

			for (Map.Entry<String, Future<int[]>> entryFuture: futures.entrySet()) {
				try {
					String entity = entryFuture.getKey();
					int[] asyncResult = entryFuture.getValue().get();
					result.addNbItemsCorrectlyProcessed(asyncResult[0]);
					result.addNbItemsProcessedWithError(asyncResult[1]);
					result.addReport(String.format("%s=>[Items OKs=%d, Items KO=%d]", entity, asyncResult[0], asyncResult[1]));
				} catch (InterruptedException e) {
					// It was cancelled from outside - no interest
				} catch (ExecutionException e) {
					Throwable cause = e.getCause();
					if(result != null) {
						result.registerError(cause.getMessage());
						result.addReport(cause.getMessage());
					}
					log.error("Failed to execute async method", cause);
				}
			}
			if (result.getNbItemsProcessedWithError() > 0) {
				result.addReport("Please check logs for more details about KO items");
			}
			// TODO: check for mailing
			
		} catch (Exception e) {
			log.error("Failed to run GDPR data erasure job", e);
			result.registerError(e.getMessage());
		}
	}
}
