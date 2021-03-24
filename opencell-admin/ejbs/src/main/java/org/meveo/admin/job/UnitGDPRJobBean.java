/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.meveo.admin.job;

import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.Customer;
import org.meveo.model.order.Order;
import org.meveo.model.payments.AccountOperation;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.order.OrderService;
import org.meveo.service.payments.impl.AccountOperationService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

/**
 * Unit GDPR Job bean
 *
 * @author MBoukayoua
 */
@Stateless
public class UnitGDPRJobBean {

    @Inject
    private RatedTransactionService ratedTransactionService;

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

    /**
     * Remove subscription entity in a separate Tx
     *
     * @param subscription subscription to remove
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void subscriptionRemove(Subscription subscription) {
        ratedTransactionService.detachRTsFromSubscription(subscription);
        subscriptionService.remove(subscription);
    }

    /**
     * Remove order entity in a separate Tx
     *
     * @param order order to remove
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void orderRemove(Order order) {
        orderService.remove(order);
    }

    /**
     * Remove invoice entity in a separate Tx
     *
     * @param invoice invoice to remove
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void invoiceRemove(Invoice invoice) {
        ratedTransactionService.detachRTsFromInvoice(invoice);
        invoiceService.remove(invoice);
    }

    /**
     * Remove account operation entity in a separate Tx
     *
     * @param accountOperation ao to remove
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void accountOperationRemove(AccountOperation accountOperation) {
        accountOperationService.remove(accountOperation);
    }

    /**
     * Remove account operation entity in a separate Tx
     *
     * @param contact ao to remove
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void customerRemove(Customer contact) {
        customerService.remove(contact);
    }
}
