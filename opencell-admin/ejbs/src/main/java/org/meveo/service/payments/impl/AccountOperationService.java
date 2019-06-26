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
package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.DiscriminatorValue;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.Seller;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;

/**
 * AccountOperation service implementation.
 * 
 * @author anasseh
 * @lastModifiedVersion 5.3 
 */
@Stateless
public class AccountOperationService extends PersistenceService<AccountOperation> {

    /**
     * Gets the account operations.
     *
     * @param date date
     * @param operationCode code of operation.
     * @return list of account operations.
     */
    @SuppressWarnings("unchecked")
    public List<AccountOperation> getAccountOperations(Date date, String operationCode) {
        Query query = getEntityManager().createQuery("from " + getEntityClass().getSimpleName() + " a where a.occCode=:operationCode and  a.transactionDate=:date")
            .setParameter("date", date).setParameter("operationCode", operationCode);

        return query.getResultList();
    }

    /**
     * Gets the account operation.
     *
     * @param amount account operation account
     * @param customerAccount customer account
     * @param transactionType transaction type.
     * @return account operation.
     */
    @SuppressWarnings("unchecked")
    public AccountOperation getAccountOperation(BigDecimal amount, CustomerAccount customerAccount, String transactionType) {

        Query query = getEntityManager()
            .createQuery("from " + getEntityClass().getSimpleName() + " a where a.amount=:amount and  a.customerAccount=:customerAccount and  a.type=:transactionType")
            .setParameter("amount", amount).setParameter("transactionType", transactionType).setParameter("customerAccount", customerAccount);
        List<AccountOperation> accountOperations = query.getResultList();

        return accountOperations.size() > 0 ? accountOperations.get(0) : null;
    }

    /**
     * Find by reference.
     *
     * @param reference reference of account operation.
     * @return account operation.
     */
    public AccountOperation findByReference(String reference) {
        try {
            QueryBuilder qb = new QueryBuilder(AccountOperation.class, "a");
            qb.addCriterion("reference", "=", reference, false);
            return (AccountOperation) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException ne) {
            return null;
        }
    }

    /**
     * Set the discriminatorValue value, so it would be available in the list of entities right away.
     * 
     * @param aop account operation.
     * @return id of account operation.
     * @throws BusinessException business exception.
     */
    public Long createAndReturnId(AccountOperation aop) throws BusinessException {

        if (aop.getClass().isAnnotationPresent(DiscriminatorValue.class)) {
            aop.setType(aop.getClass().getAnnotation(DiscriminatorValue.class).value());
        }

        super.create(aop);
        return aop.getId();

    }

    /**
     * Gets the a os to pay.
     *
     * @param paymentMethodEnum the payment method enum
     * @param fromDueDate the from due date
     * @param toDueDate the to due date
     * @param opCatToProcess the op cat to process
     * @param customerAccountId the customer account id
     * @return the a os to pay
     */
    @SuppressWarnings("unchecked")
    public List<AccountOperation> getAOsToPayOrRefund(PaymentMethodEnum paymentMethodEnum, Date fromDueDate, Date toDueDate, OperationCategoryEnum opCatToProcess,
            Long customerAccountId) {
        try {
            return (List<AccountOperation>) getEntityManager().createNamedQuery("AccountOperation.listAoToPayOrRefund").setParameter("paymentMethodIN", paymentMethodEnum)
                .setParameter("caIdIN", customerAccountId).setParameter("fromDueDateIN", fromDueDate).setParameter("toDueDateIN", toDueDate)
                .setParameter("opCatToProcessIN", opCatToProcess).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Gets the a os to pay.
     *
     * @param paymentMethodEnum the payment method enum
     * @param fromDueDate the from due date
     * @param toDueDate the to due date
     * @param opCatToProcess the op cat to process
     * @param seller the seller
     * @return the a os to pay
     */
    @SuppressWarnings("unchecked")
    public List<AccountOperation> getAOsToPayOrRefund(PaymentMethodEnum paymentMethodEnum, Date fromDueDate, Date toDueDate, OperationCategoryEnum opCatToProcess, Seller seller) {
        try {
            String queryName = "AccountOperation.listAoToPayOrRefundWithoutCA";
            if (seller != null) {
                queryName = "AccountOperation.listAoToPayOrRefundWithoutCAbySeller";
            }
            Query query = getEntityManager().createNamedQuery(queryName).setParameter("paymentMethodIN", paymentMethodEnum).setParameter("fromDueDateIN", fromDueDate)
                .setParameter("toDueDateIN", toDueDate).setParameter("opCatToProcessIN", opCatToProcess);

            if (seller != null) {
                query.setParameter("sellerIN", seller);
            }
            return (List<AccountOperation>) query.getResultList();

        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Return all AccountOperation with now - invoiceDate date &gt; n years.
     * 
     * @param nYear age of the account operation
     * @return Filtered list of account operations
     */
    @SuppressWarnings("unchecked")
    public List<AccountOperation> listInactiveAccountOperations(int nYear) {
        QueryBuilder qb = new QueryBuilder(AccountOperation.class, "e");
        Date higherBound = DateUtils.addYearsToDate(new Date(), -1 * nYear);

        qb.addCriterionDateRangeToTruncatedToDay("transactionDate", higherBound);

        return (List<AccountOperation>) qb.getQuery(getEntityManager()).getResultList();
    }

    /**
     * Return all unpaid AccountOperation with now - invoiceDate date &gt; n years.
     * 
     * @param nYear age of the account operation
     * @return Filtered list of account operations
     */
    @SuppressWarnings("unchecked")
    public List<AccountOperation> listUnpaidAccountOperations(int nYear) {
        QueryBuilder qb = new QueryBuilder(AccountOperation.class, "e");
        Date higherBound = DateUtils.addYearsToDate(new Date(), -1 * nYear);

        qb.addCriterionDateRangeToTruncatedToDay("transactionDate", higherBound);
        qb.startOrClause();
        qb.addCriterionEnum("matchingStatus", MatchingStatusEnum.L);
        qb.addCriterionEnum("matchingStatus", MatchingStatusEnum.P);
        qb.endOrClause();

        return (List<AccountOperation>) qb.getQuery(getEntityManager()).getResultList();
    }

    /**
     * Bulk delete.
     *
     * @param inactiveAccountOps the inactive account ops
     * @throws BusinessException the business exception
     */
    public void bulkDelete(List<AccountOperation> inactiveAccountOps) throws BusinessException {
        for (AccountOperation e : inactiveAccountOps) {
            remove(e);
        }
    }

    /**
     * Count unmatched AOs by CA.
     * 
     * @param customerAccount Customer Account.
     * @return count of unmatched AOs.
     */
    public Long countUnmatchedAOByCA(CustomerAccount customerAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("AccountOperation.countUnmatchedAOByCA").setParameter("customerAccount", customerAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countUnmatchedAOs by CA", e);
            return null;
        }
    }

    /**
     * Find by order number.
     *
     * @param orderNumber The order number
     * @return account operation.
     */
    public AccountOperation findByOrderNumber(String orderNumber) {
        try {
            QueryBuilder qb = new QueryBuilder(AccountOperation.class, "a");
            qb.addCriterion("orderNumber", "=", orderNumber, false);
            return (AccountOperation) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException ne) {
            return null;
        }
    }
}