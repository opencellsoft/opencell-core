/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.GDPRJobAsync;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.ServiceInstance;
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
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.job.JobExecutionService;
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
    private RatedTransactionService ratedTransactionService;

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

	@Inject
    protected JobExecutionService jobExecutionService;

	@JpaAmpNewTx
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, String parameter) {

		GdprConfiguration gdprConfiguration = providerService.getEntityManager()
				.createQuery("Select p.gdprConfiguration From Provider p Where p.id=:providerId", GdprConfiguration.class)
				.setParameter("providerId", appProvider.getId())
				.getSingleResult();

		List<Future<int[]>> futures = new ArrayList<>();
		try {
			if (gdprConfiguration.isDeleteSubscription()) {
				List<Subscription> inactiveSubscriptions = subscriptionService.listInactiveSubscriptions(gdprConfiguration.getInactiveOrderLife());
				log.debug("Found {} inactive subscriptions", inactiveSubscriptions.size());
				if (!inactiveSubscriptions.isEmpty()) {
					futures.add(gdprJobAsync.subscriptionBulkDelete(inactiveSubscriptions));
				}
			}

			if (gdprConfiguration.isDeleteOrder()) {
				List<Order> inactiveOrders = orderService.listInactiveOrders(gdprConfiguration.getInactiveOrderLife());
				log.debug("Found {} inactive orders", inactiveOrders.size());
				if (!inactiveOrders.isEmpty()) {
					futures.add(gdprJobAsync.orderBulkDelete(inactiveOrders));
				}
			}

			if (gdprConfiguration.isDeleteInvoice()) {
				List<Invoice> inactiveInvoices = invoiceService.listInactiveInvoice(gdprConfiguration.getInvoiceLife());
				log.debug("Found {} inactive invoices", inactiveInvoices.size());
				if (!inactiveInvoices.isEmpty()) {
					futures.add(gdprJobAsync.invoiceBulkDelete(inactiveInvoices));
				}
			}

			if (gdprConfiguration.isDeleteAccounting()) {
				List<AccountOperation> inactiveAccountOps = accountOperationService.listInactiveAccountOperations(gdprConfiguration.getAccountingLife());
				log.debug("Found {} inactive accountOperations", inactiveAccountOps.size());
				if (!inactiveAccountOps.isEmpty()) {
					futures.add(gdprJobAsync.accountOperationBulkDelete(inactiveAccountOps));
				}
			}

			if (gdprConfiguration.isDeleteAoCheckUnpaidLife()) {
				List<AccountOperation> unpaidAccountOperations = accountOperationService.listUnpaidAccountOperations(gdprConfiguration.getAoCheckUnpaidLife());
				log.debug("Found {} unpaid accountOperations", unpaidAccountOperations.size());
				if (!unpaidAccountOperations.isEmpty()) {
					futures.add(gdprJobAsync.accountOperationBulkDelete(unpaidAccountOperations));
				}
			}

			if(gdprConfiguration.isDeleteCustomerProspect()) {
				List<Customer> oldCustomerProspects = customerService.listInactiveProspect(gdprConfiguration.getCustomerProspectLife());
				log.debug("Found {} old customer prospects", oldCustomerProspects.size());
				if (!oldCustomerProspects.isEmpty()) {
					futures.add(gdprJobAsync.contactBulkDelete(oldCustomerProspects));
				}
			}

			for (Future<int[]> future: futures) {
				try {
					int[] asyncResult = future.get();
					result.addNbItemsCorrectlyProcessed(asyncResult[0]);
					result.addNbItemsProcessedWithError(asyncResult[1]);
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
				result.addReport("Many items are treated with errors. Please check logs for more details");
			}
			// TODO: check for mailing
			
		} catch (Exception e) {
			log.error("Failed to run GDPR data erasure job", e);
			result.registerError(e.getMessage());
		}
	}

	/**
     * Bulk delete prospects.
     *
     * @param inactiveProspects the inactive prospects
     * @param result job execution stats
     */
	private void bulkProspectDelete(List<Customer> inactiveProspects, JobExecutionResultImpl result) {
        for (Customer inactiveProspect : inactiveProspects) {
            try {
                customerService.remove(inactiveProspect);
                result.setNbItemsCorrectlyProcessed(result.getNbItemsCorrectlyProcessed() + 1);
            } catch(Exception e) {
                result.setNbItemsProcessedWithError(result.getNbItemsProcessedWithError() + 1);
                result.addReport(e.getMessage());
                jobExecutionService.registerError(result, e.getMessage());
            }
        }
    }

    /**
     * Bulk delete accountOperations.
     *
     * @param inactiveAccountOps the inactive account ops
     * @param result job execution stats
     */
    private void bulkAODelete(List<AccountOperation> inactiveAccountOps, JobExecutionResultImpl result) {
        for (AccountOperation inactiveAccountOp : inactiveAccountOps) {
            try {
                accountOperationService.remove(inactiveAccountOp);
                result.setNbItemsCorrectlyProcessed(result.getNbItemsCorrectlyProcessed() + 1);
            } catch(Exception e) {
                result.setNbItemsProcessedWithError(result.getNbItemsProcessedWithError() + 1);
                result.addReport(e.getMessage());
                jobExecutionService.registerError(result, e.getMessage());
            }
        }
    }

    /**
     * Bulk delete invoices.
     *
     * @param inactiveInvoices the inactive invoices
     * @param result job execution stats
     */
    private void bulkInvoiceDelete(List<Invoice> inactiveInvoices, JobExecutionResultImpl result) {
        for (Invoice inactiveInvoice : inactiveInvoices) {
            try {
                ratedTransactionService.detachRTsFromInvoice(inactiveInvoice);
                invoiceService.remove(inactiveInvoice);
                result.setNbItemsCorrectlyProcessed(result.getNbItemsCorrectlyProcessed() + 1);
            } catch(Exception e) {
                result.setNbItemsProcessedWithError(result.getNbItemsProcessedWithError() + 1);
                result.addReport(e.getMessage());
                jobExecutionService.registerError(result, e.getMessage());
            }
        }
    }

    /**
     * Bulk delete orders.
     *
     * @param inactiveOrders the inactive orders
     * @param result job execution stats
     */
    private void bulkOrderDelete(List<Order> inactiveOrders, JobExecutionResultImpl result) {
        for (Order inactiveOrder : inactiveOrders) {
            try {
                orderService.remove(inactiveOrder);
                result.setNbItemsCorrectlyProcessed(result.getNbItemsCorrectlyProcessed() + 1);
            } catch(Exception e) {
                result.setNbItemsProcessedWithError(result.getNbItemsProcessedWithError() + 1);
                result.addReport(e.getMessage());
                jobExecutionService.registerError(result, e.getMessage());
            }
        }
    }

    /**
     * Bulk delete subscriptions.
     *
     * @param inactiveSubscriptions the inactive subscriptions
     * @param result job execution stats
     */
    private void bulkSubscriptionDelete(List<Subscription> inactiveSubscriptions, JobExecutionResultImpl result) {
        for (Subscription inactiveSubscription : inactiveSubscriptions) {
            try {
                ratedTransactionService.detachRTsFromSubscription(inactiveSubscription);
                subscriptionService.remove(inactiveSubscription);
                result.setNbItemsCorrectlyProcessed(result.getNbItemsCorrectlyProcessed() + 1);
            } catch(Exception e) {
                result.setNbItemsProcessedWithError(result.getNbItemsProcessedWithError() + 1);
                result.addReport(e.getMessage());
                jobExecutionService.registerError(result, e.getMessage());
            }
        }
    }


}
