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
import org.meveo.model.communication.contact.Contact;
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
import org.meveo.service.intcrm.impl.ContactService;
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
	
	@Inject
	private CustomerService customerService;

	@JpaAmpNewTx
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, String parameter) {

		Provider provider = providerService.findById(appProvider.getId());
		GdprConfiguration gdprConfiguration = provider.getGdprConfigurationNullSafe();

		try {
			if (gdprConfiguration.isDeleteSubscription()) {
				List<Subscription> inactiveSubscriptions = subscriptionService.listInactiveSubscriptions(gdprConfiguration.getInactiveSubscriptionLife());
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

			if(gdprConfiguration.isDeleteCustomerProspect()) {
				List<Customer> oldCustomerProspects = customerService.listInactiveProspect(gdprConfiguration.getCustomerProspectLife());
				log.debug("Found {} old customer prospects", oldCustomerProspects.size());
				if (!oldCustomerProspects.isEmpty()) {
					customerService.bulkDelete(oldCustomerProspects);
				}
			}

			// TODO: check for mailing
			
		} catch (Exception e) {
			log.error("Failed to run GDPR data erasure job", e);
			result.registerError(e.getMessage());
		}
	}
}
