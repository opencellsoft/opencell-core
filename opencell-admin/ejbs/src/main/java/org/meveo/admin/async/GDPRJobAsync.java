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

package org.meveo.admin.async;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.UnitGDPRJobBean;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.Customer;
import org.meveo.model.order.Order;
import org.meveo.model.payments.AccountOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mboukayoua
 */
@Stateless
public class GDPRJobAsync {

    /** The log. */
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Inject
    private UnitGDPRJobBean unitGDPRJobBean;

    /**
     * Bulk subscription delete.
     *
     * @param inactiveSubscriptions
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<int[]> subscriptionBulkDelete(List<Subscription> inactiveSubscriptions) {
        int[] result = {0,0};
        for (Subscription subscription : inactiveSubscriptions) {
            try {
                unitGDPRJobBean.subscriptionRemove(subscription);
                result[0]++;
            } catch (Exception e) {
                log.error("Error on removing Subscription[id={}]", subscription.getId(), e);
                result[1]++;
            }
        }
        return new AsyncResult<>(result);
    }

    /**
     * Bulk order delete.
     *
     * @param inactiveOrders
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<int[]> orderBulkDelete(List<Order> inactiveOrders) {
        int[] result = {0,0};
        for (Order order : inactiveOrders) {
            try {
                unitGDPRJobBean.orderRemove(order);
                result[0]++;
            } catch (Exception e) {
                log.error("Error on removing order[id={}]", order.getId(), e);
                result[1]++;
            }
        }
        return new AsyncResult<>(result);
    }

    /**
     * Bulk invoice delete.
     *
     * @param inactiveInvoices
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<int[]> invoiceBulkDelete(List<Invoice> inactiveInvoices) {
        int[] result = {0,0};
        for (Invoice invoice : inactiveInvoices) {
            try {
                unitGDPRJobBean.invoiceRemove(invoice);
                result[0]++;
            } catch (Exception e) {
                log.error("Error on removing invoice[id={}]", invoice.getId(), e);
                result[1]++;
            }
        }
        return new AsyncResult<>(result);
    }

    /**
     * Bulk ao delete.
     *
     * @param inactiveAccountOps the inactive account ops
     * @throws BusinessException the business exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<int[]> accountOperationBulkDelete(List<AccountOperation> inactiveAccountOps) {
        int[] result = {0,0};
        for (AccountOperation ao : inactiveAccountOps) {
            try {
                unitGDPRJobBean.accountOperationRemove(ao);
                result[0]++;
            } catch (Exception e) {
                log.error("Error on removing AccountOperation[id={}]", ao.getId(),  e);
                result[1]++;
            }
        }
        return new AsyncResult<>(result);
    }

    /**
     * bulk contact delete
     *
     * @param oldCustomerProspects
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<int[]> customerBulkDelete(List<Customer> inactiveProspects) {
        int[] result = {0,0};
        for (Customer customer : inactiveProspects) {
            try {
                unitGDPRJobBean.customerRemove(customer);
                result[0]++;
            } catch (Exception e) {
                log.error("Error on removing customer[id={}]", customer.getId(), e);
                result[1]++;
            }
        }
        return new AsyncResult<>(result);
    }
}
