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
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BaseEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ISearchable;
import org.meveo.model.ObservableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.UnitOfMeasure;
import org.meveo.model.cpq.commercial.OrderInfo;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.rating.EDR;
import org.meveo.model.tax.TaxClass;

/**
 * Rated transaction - usually corresponds 1-1 to Wallet operation.
 * <p>
 * Starting from version 7.0 a RatedTransaction can be linked to several WalletOperation.
 * </p>
 *
 * @see WalletOperation
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @author Khalid HORRI
 * @lastModifiedVersion 7.1
 */
@Entity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "RatedTransaction")
@Table(name = "billing_rated_transaction")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "billing_rated_transaction_seq"), @Parameter(name = "increment_size", value = "500") })
@NamedQueries({ @NamedQuery(name = "RatedTransaction.listInvoiced", query = "SELECT r FROM RatedTransaction r where r.wallet=:wallet and r.status<>'OPEN' order by usageDate desc "),

        @NamedQuery(name = "RatedTransaction.listToInvoiceByOrderNumberInvoiceableData", query = "SELECT new org.meveo.service.billing.impl.InvoiceableData(r.id, r.billingAccount.id, r.userAccount.id, r.seller.id, r.subscription.id, r.wallet.id, r.taxClass.id, r.orderNumber, r.unitAmountWithoutTax, r.unitAmountWithTax, r.unitAmountTax, r.amountWithoutTax, r.amountWithTax, r.amountTax, r.tax.id, r.taxPercent, case when w.walletTemplate is not null then true else false end, r.invoiceSubCategory.id) FROM RatedTransaction r left join r.wallet w where r.status='OPEN' AND r.orderNumber=:orderNumber AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate)  order by r.billingAccount.id "),
        @NamedQuery(name = "RatedTransaction.listToInvoiceBySubscriptionInvoiceableData", query = "SELECT  new org.meveo.service.billing.impl.InvoiceableData(r.id, r.billingAccount.id, r.userAccount.id, r.seller.id, r.subscription.id, r.wallet.id, r.taxClass.id, r.orderNumber, r.unitAmountWithoutTax, r.unitAmountWithTax, r.unitAmountTax, r.amountWithoutTax, r.amountWithTax, r.amountTax, r.tax.id, r.taxPercent, case when w.walletTemplate is not null then true else false end, r.invoiceSubCategory.id) FROM RatedTransaction r left join r.wallet w where r.subscription.id=:subscriptionId AND r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) "),
        @NamedQuery(name = "RatedTransaction.listToInvoiceByBillingAccountInvoiceableData", query = "SELECT  new org.meveo.service.billing.impl.InvoiceableData(r.id, r.billingAccount.id, r.userAccount.id, r.seller.id, r.subscription.id, r.wallet.id, r.taxClass.id, r.orderNumber, r.unitAmountWithoutTax, r.unitAmountWithTax, r.unitAmountTax, r.amountWithoutTax, r.amountWithTax, r.amountTax, r.tax.id, r.taxPercent, case when w.walletTemplate is not null then true else false end, r.invoiceSubCategory.id) FROM RatedTransaction r left join r.wallet w where r.billingAccount.id=:billingAccountId AND r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) "),

        @NamedQuery(name = "RatedTransaction.listToInvoiceByBillingAccountAndIDs", query = "SELECT r FROM RatedTransaction r where r.billingAccount.id=:billingAccountId AND r.status='OPEN' AND id in (:listOfIds) "),

        @NamedQuery(name = "RatedTransaction.sumInvoiceableByServiceWithMinAmountBySubscription", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax),  r.serviceInstance.id FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) "
                + " and r.subscription=:subscription and r.serviceInstance.minimumAmountEl is not null GROUP BY  r.serviceInstance.id"),
        @NamedQuery(name = "RatedTransaction.sumInvoiceableByServiceWithMinAmountByBA", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax),  r.serviceInstance.id  FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) "
                + " and r.billingAccount=:billingAccount and r.serviceInstance.minimumAmountEl is not null GROUP BY  r.serviceInstance.id"),

        @NamedQuery(name = "RatedTransaction.sumInvoiceableBySubscriptionWithMinAmountBySubscription", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax),  r.subscription.id FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) "
                + " and r.subscription=:subscription and r.subscription.minimumAmountEl is not null GROUP BY  r.subscription.id"),
        @NamedQuery(name = "RatedTransaction.sumInvoiceableBySubscriptionWithMinAmountByBA", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax),  r.subscription.id  FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) "
                + " and r.billingAccount=:billingAccount and r.subscription.minimumAmountEl is not null GROUP BY  r.subscription.id"),

        @NamedQuery(name = "RatedTransaction.sumTotalInvoiceableBySubscription", query = "SELECT new org.meveo.model.billing.Amounts(sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax)) FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) and r.subscription=:subscription"),
        @NamedQuery(name = "RatedTransaction.sumTotalInvoiceableBySubscriptionExcludePrepaidWO", query = "SELECT new org.meveo.model.billing.Amounts(sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax)) FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) "
                + " and r.subscription=:subscription AND r.wallet.id NOT IN (:walletsIds)"),
        @NamedQuery(name = "RatedTransaction.sumTotalInvoiceableBySubscriptionInBatch", query = "SELECT new org.meveo.admin.async.AmountsToInvoice(r.subscription.id, sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax)) FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) AND r.subscription.billingCycle=:billingCycle group by r.subscription.id"),

        @NamedQuery(name = "RatedTransaction.sumTotalInvoiceableByBA", query = "SELECT new org.meveo.model.billing.Amounts(sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax)) FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) and r.billingAccount=:billingAccount"),
        @NamedQuery(name = "RatedTransaction.sumTotalInvoiceableByBAExcludePrepaidWO", query = "SELECT new org.meveo.model.billing.Amounts(sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax)) FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) "
                + " and r.billingAccount=:billingAccount AND r.wallet.id NOT IN (:walletsIds)"),
        @NamedQuery(name = "RatedTransaction.sumTotalInvoiceableByBAInBatch", query = "SELECT new org.meveo.admin.async.AmountsToInvoice(r.billingAccount.id, sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax)) FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate  and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) AND r.billingAccount.billingCycle=:billingCycle group by r.billingAccount.id"),
        @NamedQuery(name = "RatedTransaction.sumTotalInvoiceableByBAInBatchLimitByNextInvoiceDate", query = "SELECT new org.meveo.admin.async.AmountsToInvoice(r.billingAccount.id, sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax)) FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) AND r.billingAccount.billingCycle=:billingCycle and :startDate<=r.billingAccount.nextInvoiceDate AND r.billingAccount.nextInvoiceDate<:endDate group by r.billingAccount.id"),

        @NamedQuery(name = "RatedTransaction.sumTotalInvoiceableByOrderNumber", query = "SELECT new org.meveo.model.billing.Amounts(sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax)) FROM RatedTransaction r WHERE r.status='OPEN' AND r.orderNumber=:orderNumber AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate)"),
        @NamedQuery(name = "RatedTransaction.sumTotalInvoiceableByOrderNumberExcludePrpaidWO", query = "SELECT new org.meveo.model.billing.Amounts(sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax)) FROM RatedTransaction r WHERE r.status='OPEN' AND r.orderNumber=:orderNumber AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) AND r.wallet.id NOT IN (:walletsIds)"),

        @NamedQuery(name = "RatedTransaction.sumInvoiceableWithMinAmountByBA", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax),  r.billingAccount.id, r.seller.id FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) and r.billingAccount=:billingAccount GROUP BY r.seller.id, r.billingAccount.id"),

        @NamedQuery(name = "RatedTransaction.sumInvoiceableWithMinAmountByUA", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax),  r.userAccount.id, r.seller.id FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) and r.billingAccount=:billingAccount and r.userAccount.minimumAmountEl is not null GROUP BY r.seller.id, r.userAccount.id"),

        @NamedQuery(name = "RatedTransaction.sumInvoiceableWithMinAmountByCA", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax), r.billingAccount.customerAccount.id, r.seller.id FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) and r.billingAccount.customerAccount=:customerAccount GROUP BY r.seller.id, r.billingAccount.customerAccount.id"),

        @NamedQuery(name = "RatedTransaction.sumInvoiceableWithMinAmountByCustomer", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax), r.billingAccount.customerAccount.customer.id, r.seller.id FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) and r.billingAccount.customerAccount.customer=:customer GROUP BY  r.seller.id, r.billingAccount.customerAccount.customer.id"),

        @NamedQuery(name = "RatedTransaction.sumInvoiceableForBAWithMinAmountBySubscription", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax),   r.billingAccount.id, r.seller.id FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) and r.subscription=:subscription and r.billingAccount.minimumAmountEl is not null GROUP BY  r.seller.id, r.billingAccount.id"),

        @NamedQuery(name = "RatedTransaction.sumInvoiceableForUAWithMinAmountBySubscription", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax),   r.userAccount.id, r.seller.id FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) and r.subscription=:subscription and r.userAccount.minimumAmountEl is not null GROUP BY  r.seller.id, r.userAccount.id"),

        @NamedQuery(name = "RatedTransaction.sumInvoiceableForCAWithMinAmountBySubscription", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax),  r.billingAccount.customerAccount.id, r.seller.id FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) and r.subscription=:subscription and r.billingAccount.customerAccount.minimumAmountEl is not null GROUP BY r.seller.id, r.billingAccount.customerAccount.id"),

        @NamedQuery(name = "RatedTransaction.sumInvoiceableForCustomerWithMinAmountBySubscription", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax), r.billingAccount.customerAccount.customer.id, r.seller.id FROM RatedTransaction r WHERE r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) and r.subscription=:subscription and r.billingAccount.customerAccount.customer.minimumAmountEl is not null GROUP BY r.seller.id, r.billingAccount.customerAccount.customer.id"),

        @NamedQuery(name = "RatedTransaction.cancelByWOIds", query = "UPDATE RatedTransaction r SET r.status=org.meveo.model.billing.RatedTransactionStatusEnum.CANCELED, r.updated = :now  WHERE id IN (SELECT wo.ratedTransaction.id FROM WalletOperation wo WHERE wo.id IN :woIds)"),
        @NamedQuery(name = "RatedTransaction.getListByInvoiceAndSubCategory", query = "select r from RatedTransaction r where r.invoice=:invoice and r.invoiceSubCategory=:invoiceSubCategory "),

        @NamedQuery(name = "RatedTransaction.unInvoiceByInvoice", query = "update RatedTransaction r set r.status='OPEN', r.updated = :now, r.billingRun= null, r.invoice=null, r.invoiceAgregateF=null where r.status=org.meveo.model.billing.RatedTransactionStatusEnum.BILLED and r.invoice=:invoice"),
        @NamedQuery(name = "RatedTransaction.unInvoiceByBR", query = "update RatedTransaction r set r.status='OPEN', r.updated = :now, r.billingRun= null, r.invoice=null, r.invoiceAgregateF=null where r.status=org.meveo.model.billing.RatedTransactionStatusEnum.BILLED and r.billingRun=:billingRun \r\n"
        		+ "AND r.invoice.id in (select r2.invoice.id from RatedTransaction r2 where r2.invoice.status <> org.meveo.model.billing.InvoiceStatusEnum.VALIDATED)"),

        @NamedQuery(name = "RatedTransaction.deleteSupplementalRTByInvoice", query = "DELETE from RatedTransaction r WHERE r.type = 'MINIMUM' and r.status=org.meveo.model.billing.RatedTransactionStatusEnum.BILLED and r.invoice=:invoice"),
        @NamedQuery(name = "RatedTransaction.deleteSupplementalRTByBR", query = "DELETE from RatedTransaction r WHERE r.type = 'MINIMUM' and r.status=org.meveo.model.billing.RatedTransactionStatusEnum.BILLED and r.billingRun=:billingRun AND r.invoice.id in (select r2.invoice.id from RatedTransaction r2 where r2.invoice.status <> org.meveo.model.billing.InvoiceStatusEnum.VALIDATED)"),

        @NamedQuery(name = "RatedTransaction.countNotInvoicedOpenByBA", query = "SELECT count(r) FROM RatedTransaction r WHERE r.billingAccount=:billingAccount AND r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) "),

        @NamedQuery(name = "RatedTransaction.countNotInvoicedByBA", query = "SELECT count(*) FROM RatedTransaction r WHERE r.status <> org.meveo.model.billing.RatedTransactionStatusEnum.BILLED AND r.billingAccount=:billingAccount"),
        @NamedQuery(name = "RatedTransaction.countNotInvoicedByUA", query = "SELECT count(*) FROM RatedTransaction r WHERE r.status <> org.meveo.model.billing.RatedTransactionStatusEnum.BILLED AND r.userAccount=:userAccount"),
        @NamedQuery(name = "RatedTransaction.countNotInvoicedByCA", query = "SELECT count(*) FROM RatedTransaction r WHERE r.status <> org.meveo.model.billing.RatedTransactionStatusEnum.BILLED AND r.billingAccount.customerAccount=:customerAccount"),

        @NamedQuery(name = "RatedTransaction.countNotBilledRTBySubscription", query = "SELECT count(*) FROM RatedTransaction r WHERE r.status='OPEN' AND r.subscription=:subscription"),
        @NamedQuery(name = "RatedTransaction.moveNotBilledRTToUA", query = "UPDATE RatedTransaction r SET r.wallet=:newWallet, r.userAccount=:newUserAccount, r.billingAccount=:newBillingAccount WHERE r.status='OPEN' AND r.subscription=:subscription"),
        @NamedQuery(name = "RatedTransaction.moveAndRerateNotBilledRTToUA", query = "UPDATE RatedTransaction r SET r.status='RERATED', r.wallet=:newWallet, r.userAccount=:newUserAccount, r.billingAccount=:newBillingAccount WHERE r.id IN (SELECT o.ratedTransaction.id FROM WalletOperation o WHERE o.status='TO_RERATE' AND o.subscription=:subscription) OR (r.status='OPEN' AND r.subscription=:subscription)"),

        @NamedQuery(name = "RatedTransaction.cancelByRTIds", query = "UPDATE RatedTransaction r set r.status=org.meveo.model.billing.RatedTransactionStatusEnum.CANCELED, r.updated = :now, r.invoice=null, r.invoiceAgregateF=null where r.id IN :rtIds"),
        @NamedQuery(name = "RatedTransaction.findByWalletOperationId", query = "SELECT wo.ratedTransaction FROM WalletOperation wo WHERE wo.id=:walletOperationId"),

        @NamedQuery(name = "RatedTransaction.massUpdateWithInvoiceInfo", query = "UPDATE RatedTransaction r set r.status=org.meveo.model.billing.RatedTransactionStatusEnum.BILLED, r.updated = :now , r.invoiceAgregateF=:invoiceAgregateF, r.billingRun=:billingRun, r.invoice=:invoice where r.status='OPEN' and r.id in :ids"),
        @NamedQuery(name = "RatedTransaction.updateWithInvoiceInfo", query = "UPDATE RatedTransaction r set r.status=org.meveo.model.billing.RatedTransactionStatusEnum.BILLED, r.updated = :now, r.invoiceAgregateF=:invoiceAgregateF, r.billingRun=:billingRun, r.invoice=:invoice, r.unitAmountWithoutTax=:unitAmountWithoutTax, r.unitAmountWithTax=:unitAmountWithTax, r.unitAmountTax=:unitAmountTax, r.amountWithoutTax=:amountWithoutTax, r.amountWithTax=:amountWithTax, r.amountTax=:amountTax, r.tax=:tax, r.taxPercent=:taxPercent where r.status='OPEN' and r.id=:id"),

        @NamedQuery(name = "RatedTransaction.listNotOpenedBetweenTwoDates", query = "SELECT r FROM RatedTransaction r where r.status!='OPEN' AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate AND r.id>:lastId order by r.id "),
        @NamedQuery(name = "RatedTransaction.listBetweenTwoDatesByStatus", query = "SELECT r FROM RatedTransaction r where r.status in (:status) AND :firstTransactionDate<=r.usageDate AND r.usageDate<=:lastTransactionDate AND r.id>:lastId order by r.id "),
        @NamedQuery(name = "RatedTransaction.deleteNotOpenBetweenTwoDates", query = "delete FROM RatedTransaction r where r.status<>'OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate "),
        @NamedQuery(name = "RatedTransaction.deleteByLastTransactionDateAndStatus", query = "delete FROM RatedTransaction r where r.status in (:status) AND r.usageDate<=:lastTransactionDate "),
        @NamedQuery(name = "RatedTransaction.deleteBetweenTwoDatesByStatus", query = "delete FROM RatedTransaction r where r.status in (:status) AND :firstTransactionDate<=r.usageDate AND r.usageDate<=:lastTransactionDate "),

        @NamedQuery(name = "RatedTransaction.listByInvoice", query = "SELECT r FROM RatedTransaction r where r.invoice=:invoice and r.status='BILLED' order by r.usageDate"),
        @NamedQuery(name = "RatedTransaction.listByInvoiceNotFree", query = "SELECT r FROM RatedTransaction r where r.invoice=:invoice and r.amountWithoutTax<>0 and r.status='BILLED' order by r.usageDate"),
        @NamedQuery(name = "RatedTransaction.listByInvoiceSubCategoryAggr", query = "SELECT r FROM RatedTransaction r where r.invoice=:invoice and r.invoiceAgregateF=:invoiceAgregateF and r.status='BILLED' order by r.usageDate"),
        @NamedQuery(name = "RatedTransaction.deleteInvoiceAggrByInvoice", query = "UPDATE RatedTransaction r set r.invoiceAgregateF=null where r.invoice.id=:invoiceId"),
        @NamedQuery(name = "RatedTransaction.listAllByInvoice", query = "SELECT r FROM RatedTransaction r where r.invoice=:invoice order by r.usageDate"),
        @NamedQuery(name = "RatedTransaction.listToInvoiceByOrderNumber", query = "SELECT r FROM RatedTransaction r left join fetch r.wallet where r.status='OPEN' AND r.orderNumber=:orderNumber AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate)  order by r.billingAccount.id "),
        @NamedQuery(name = "RatedTransaction.listToInvoiceBySubscription", query = "SELECT r FROM RatedTransaction r left join fetch r.wallet where r.subscription.id=:subscriptionId AND r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) "),
        @NamedQuery(name = "RatedTransaction.listToInvoiceByBillingAccount", query = "SELECT r FROM RatedTransaction r left join fetch r.wallet where r.billingAccount.id=:billingAccountId AND r.status='OPEN' AND :firstTransactionDate<=r.usageDate AND r.usageDate<:lastTransactionDate and (r.invoicingDate is NULL or r.invoicingDate<:invoiceUpToDate) "),
        @NamedQuery(name = "RatedTransaction.moveToQuarantineBRByInvoiceIds", query = "update RatedTransaction r set r.billingRun= :billingRun where r.invoice.id IN (:invoiceIds)"),

        @NamedQuery(name = "RatedTransaction.sumPositiveRTByBillingRun", query = "select sum(r.amountWithoutTax), sum(r.amountWithTax), r.invoice.id, r.billingAccount.id, r.billingAccount.customerAccount.id, r.billingAccount.customerAccount.customer.id "
                + "FROM RatedTransaction r where r.billingRun.id=:billingRunId and r.amountWithoutTax > 0 and r.status='BILLED' group by r.invoice.id, r.billingAccount.id, r.billingAccount.customerAccount.id, r.billingAccount.customerAccount.customer.id"),
        @NamedQuery(name = "RatedTransaction.unInvoiceByInvoiceIds", query = "update RatedTransaction r set r.status='OPEN', r.updated = :now, r.billingRun= null, r.invoice=null, r.invoiceAgregateF=null where r.status=org.meveo.model.billing.RatedTransactionStatusEnum.BILLED and r.invoice.id IN (:invoiceIds)"),
        @NamedQuery(name = "RatedTransaction.deleteSupplementalRTByInvoiceIds", query = "DELETE from RatedTransaction r WHERE r.type='MINIMUM' and r.invoice.id IN (:invoicesIds)"),
        @NamedQuery(name = "RatedTransaction.detachRTsFromSubscription", query = "UPDATE RatedTransaction set serviceInstance = null where serviceInstance.id IN (SELECT id from ServiceInstance where subscription=:subscription)"),
        @NamedQuery(name = "RatedTransaction.detachRTsFromInvoice", query = "UPDATE RatedTransaction set invoice = null, invoiceAgregateF = null where invoiceAgregateF.id IN (SELECT id from SubCategoryInvoiceAgregate where invoice=:invoice)"),
        @NamedQuery(name = "RatedTransaction.invalidateRTByInvoice", query = "UPDATE RatedTransaction r set r.invoice=null, r.status='OPEN' WHERE r.invoice=:invoice"),
        @NamedQuery(name = "RatedTransaction.markAsProcessed", query = "UPDATE RatedTransaction rt set rt.status='PROCESSED' WHERE rt.id in (:listOfIds)"),
        @NamedQuery(name = "RatedTransaction.sumTotalInvoiceableByRtIdInBatch", query = "SELECT new org.meveo.admin.async.AmountsToInvoice(r.billingAccount.id, sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax)) FROM RatedTransaction r WHERE r.status='OPEN' AND r.id in (:ids) group by r.billingAccount.id"),
        @NamedQuery(name = "RatedTransaction.BillingAccountByRTIds", query = "SELECT distinct rt.billingAccount FROM RatedTransaction rt WHERE rt.id in (:ids)"),
        @NamedQuery(name = "RatedTransaction.linkRTWithInvoiceLine", query = "UPDATE RatedTransaction rt set rt.status='PROCESSED', rt.invoiceLine.id = :il WHERE rt.id in :ids"),
        @NamedQuery(name = "RatedTransaction.linkRTWithInvoice", query = "UPDATE RatedTransaction rt set rt.invoice = :invoice, rt.billingRun = :billingRun, rt.status = 'BILLED', rt.updated = :now WHERE rt.invoiceLine.id in :ids"),
        @NamedQuery(name = "RatedTransaction.detachFromInvoiceLines", query = "UPDATE RatedTransaction rt set rt.invoiceLine = null, rt.status = 'OPEN' WHERE rt.invoiceLine.id in :ids"),
        @NamedQuery(name = "RatedTransaction.detachFromInvoices", query = "UPDATE RatedTransaction r SET r.status='OPEN', r.updated = :now, r.billingRun= null, r.invoice=null, r.invoiceAgregateF=null WHERE r.invoice.id IN :ids"),
        @NamedQuery(name = "RatedTransaction.detachRTFromSubCat", query = "UPDATE RatedTransaction r SET r.invoiceAgregateF = null WHERE r.id IN (:rtIds)")})

@NamedNativeQueries({
        @NamedNativeQuery(name = "RatedTransaction.massUpdateWithInvoiceInfoFromPendingTable", query = "update {h-schema}billing_rated_transaction rt set status='BILLED', updated=now(), aggregate_id_f=pending.aggregate_id_f, billing_run_id=pending.billing_run_id, invoice_id=pending.invoice_id from {h-schema}billing_rated_transaction_pending pending where status='OPEN' and rt.id=pending.id"),
        @NamedNativeQuery(name = "RatedTransaction.massUpdateWithInvoiceInfoFromPendingTableOracle", query = "update {h-schema}billing_rated_transaction rt set (status, updated, aggregate_id_f, billing_run_id, invoice_id) =  (select 'BILLED', now(), pending.aggregate_id_f, pending.billing_run_id, pending.invoice_id from {h-schema}billing_rated_transaction_pending pending where pending.id=rt.id) where status='OPEN' and rt.id in (select id from {h-schema}billing_rated_transaction_pending pending where pending.id=rt.id)"),
        @NamedNativeQuery(name = "RatedTransaction.deletePendingTable", query = "delete from {h-schema}billing_rated_transaction_pending") })

public class RatedTransaction extends BaseEntity implements ISearchable, ICustomFieldEntity, IInvoiceable {

    private static final long serialVersionUID = 1L;

    /**
     * Wallet instance associated to rated transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private WalletInstance wallet;

    /**
     * Billing account associated to rated transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account__id", nullable = false)
    @NotNull
    private BillingAccount billingAccount;
    
    /**
     * Origin Billing account associated to rated transaction
     */
    @ManyToOne
    @JoinColumn(name = "origin_billing_account")
    private BillingAccount originBillingAccount;
    
    public BillingAccount getOriginBillingAccount() {
        return originBillingAccount;
    }

    public void setOriginBillingAccount(BillingAccount originBillingAccount) {
        this.originBillingAccount = originBillingAccount;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    /**
     * User account associated to rated transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    /**
     * Seller associated to operation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @NotNull
    private Seller seller;

    /**
     * Operation date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "usage_date")
    private Date usageDate;

    /**
     * Associated Invoice subcategory
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_sub_category_id")
    private InvoiceSubCategory invoiceSubCategory;

    /**
     * Operation code - corresponds in majority of cases to charge code
     */
    @Column(name = "code", length = 255)
    @Size(max = 255)
    private String code;

    /**
     * Reject Reason
     */
    @Column(name = "reject_reason")
    @Size(max = 4000)
    private String rejectReason;

    /**
     * Description - corresponds in majority of cases to charge description
     */
    @Size(max = 4000)
    @Column(name = "description")
    private String description;

    /**
     * Input unit description
     */
    @Column(name = "unity_description", length = 20)
    @Size(max = 20)
    private String unityDescription;

    /**
     * Rating unit description
     */
    @Column(name = "rating_unit_description", length = 20)
    @Size(max = 20)
    private String ratingUnitDescription;

    /**
     * Unit price without tax
     */
    @Column(name = "unit_amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal unitAmountWithoutTax;

    /**
     * Unit price with tax
     */
    @Column(name = "unit_amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal unitAmountWithTax;

    /**
     * Unit price tax amount
     */
    @Column(name = "unit_amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal unitAmountTax;

    /**
     * Quantity
     */
    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal quantity;

    /**
     * Total amount without tax
     */
    @Column(name = "amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountWithoutTax;

    /**
     * Total amount with tax
     */
    @Column(name = "amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountWithTax;

    /**
     * Total tax amount
     */
    @Column(name = "amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountTax;

    /**
     * Do not trigger invoicing
     */
    @Type(type = "numeric_boolean")
    @Column(name = "do_not_trigger_invoicing")
    private boolean doNotTriggerInvoicing = false;

    /**
     * Additional parameter used in rating
     */
    @Column(name = "parameter_1", length = 255)
    @Size(max = 255)
    private String parameter1;

    /**
     * Additional parameter used in rating
     */
    @Column(name = "parameter_2", length = 255)
    @Size(max = 255)
    private String parameter2;

    /**
     * Additional parameter used in rating
     */
    @Column(name = "parameter_3", length = 255)
    @Size(max = 255)
    private String parameter3;

    /**
     * Operation start date. Used in cases when operation corresponds to a period.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate;

    /**
     * Operation end date. Used in cases when operation corresponds to a period.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;

    /**
     * Additional parameter used in rating
     */
    @Size(max = 4000)
    @Column(name = "parameter_extra")
    private String parameterExtra;

    /**
     * Order number in cases when operation was originated from an order
     */
    @Column(name = "order_number", length = 100)
    @Size(max = 100)
    private String orderNumber;

    /**
     * Price plan applied during rating
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priceplan_id")
    private PricePlanMatrix priceplan;

    /**
     * EDR that produced this operation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edr_id")
    private EDR edr;

    /**
     * Adjusted rated transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adjusted_rated_tx")
    private RatedTransaction adjustedRatedTx;

    /**
     * Associated Subscription when operation is tied to subscription.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    /**
     * Associated Charge instance when operation is tied to charge instance
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_instance_id")
    private ChargeInstance chargeInstance;

    /**
     * Tax applied
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tax_id", nullable = false)
    @NotNull
    private Tax tax;

    /**
     * Tax percent applied
     */
    @Column(name = "tax_percent", precision = NB_PRECISION, scale = NB_DECIMALS, nullable = false)
    @NotNull
    private BigDecimal taxPercent;

    /**
     * Offer template
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private OfferTemplate offerTemplate;

    /**
     * Service instance that Wallet operation is applied to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_instance_id")
    private ServiceInstance serviceInstance;

    /**
     * Billing run that invoiced this Rated transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_run_id")
    private BillingRun billingRun;

    /**
     * Invoice that invoiced this Rated transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    /**
     * Subcategory invoice aggregate that Rated transaction was invoiced under
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aggregate_id_f")
    private SubCategoryInvoiceAgregate invoiceAgregateF;

    /**
     * Status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull
    private RatedTransactionStatusEnum status = RatedTransactionStatusEnum.OPEN;

    /**
     * Last record/entity update timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated")
    private Date updated;

    /**
     * Input quantity
     */
    @Column(name = "input_quantity", precision = BaseEntity.NB_PRECISION, scale = BaseEntity.NB_DECIMALS)
    private BigDecimal inputQuantity;

    /**
     * Raw rating amount without tax from Price plan. Might differ from amountWitouttax when minimumAmount is set on a price plan.
     */
    @Column(name = "raw_amount_without_tax", precision = 23, scale = 12)
    @Digits(integer = 23, fraction = 12)
    private BigDecimal rawAmountWithoutTax;

    /**
     * Raw rating amount with tax from Price plan. Might differ from amountWitouttax when minimumAmount is set on a price plan.
     */
    @Column(name = "raw_amount_with_tax", precision = 23, scale = 12)
    @Digits(integer = 23, fraction = 12)
    private BigDecimal rawAmountWithTax;

    /**
     * Charge tax class
     **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_class_id", nullable = false)
    private TaxClass taxClass;

    /**
     * Was tax recalculated (changed) during invoicing
     */
    @Transient
    private boolean taxRecalculated;

    /**
     * input_unit_unitOfMeasure
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "input_unitofmeasure")
    private UnitOfMeasure inputUnitOfMeasure;

    /**
     * rating_unit_unitOfMeasure
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_unitofmeasure")
    private UnitOfMeasure ratingUnitOfMeasure;

    /**
     * Sorting index
     */
    @Column(name = "sort_index")
    private Integer sortIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private RatedTransactionTypeEnum type;

    /**
     * Accounting code
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;

    /**
     * Date past which a charge can be included in the invoice. Allows to exclude charges from the current billing cycle by specifying a future date.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "invoicing_date")
    private Date invoicingDate;

    /**
     * Custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values", columnDefinition = "jsonb")
    private CustomFieldValues cfValues;

    /**
     * Accumulated custom field values in JSON format
     */
//    @Type(type = "cfjson")
//    @Column(name = "cf_values_accum", columnDefinition = "TEXT")
    @Transient
    private CustomFieldValues cfAccumulatedValues;

    /**
     * Unique identifier - UUID
     */
    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_article_id")
    private AccountingArticle accountingArticle;

    @Embedded
    private OrderInfo infoOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_line_id")
    private InvoiceLine invoiceLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_plan_id")
    private DiscountPlan discountPlan;    
    
    @Column(name = "discounted_ratedtransaction_id")
    private Long discountedRatedTransaction;
    
    @Column(name = "discount_value")
	private BigDecimal discountValue;
    
    @Enumerated(EnumType.STRING)
	@Column(name = "discount_plan_type", length = 50)
	private DiscountPlanItemTypeEnum discountPlanType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_plan_item_id")
    private DiscountPlanItem discountPlanItem;
    
    @ManyToOne
    @JoinColumn(name = "rules_contract_id")
    private Contract rulesContract;
    
    /**
  	 * 
  	 *filled only for price lines related to applied discounts, and contains the application sequence composed by the concatenation of the DP sequence and DPI sequence
  	 */
  	@Column(name = "sequence")
  	private Integer sequence;
    
    public RatedTransaction() {
        super();
    }

    /**
     * Constructor
     *
     * @param usageDate Operation date
     * @param unitAmountWithoutTax Unit amount without tax
     * @param unitAmountWithTax Unit amount with tax
     * @param unitAmountTax Unit amount tax
     * @param quantity Rating quantity
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     * @param amountTax Amount tax
     * @param status Status
     * @param wallet Wallet on which operation is performed
     * @param billingAccount Billing account
     * @param userAccount User account
     * @param invoiceSubCategory Invoice subcategory
     * @param parameter1 Parameter 1
     * @param parameter2 Parameter 2
     * @param parameter3 Parameter 3
     * @param parameterExtra Extra parameter
     * @param orderNumber Order number
     * @param subscription Subscription
     * @param inputUnitDescription Input unit description
     * @param ratingUnitDescription Rating unit description
     * @param priceplan Price plan applied
     * @param offerTemplate Offer template
     * @param edr Related EDR
     * @param code Charge/Operation code
     * @param description Charge/Operation description
     * @param startDate Date period that transaction covers - start date
     * @param endDate Date period that transaction covers - end date
     * @param seller Seller
     * @param tax Tax
     * @param taxPercent Tax percent
     * @param serviceInstance Service instance associated
     * @param taxClass Tax class
     * @param accountingCode Accounting code
     * @param type Rated transaction type
     * @param chargeInstance Charge instance
     * @param invoicingDate Date until which invoicing should be postponed to
     */
    public RatedTransaction(Date usageDate, BigDecimal unitAmountWithoutTax, BigDecimal unitAmountWithTax, BigDecimal unitAmountTax, BigDecimal quantity, BigDecimal amountWithoutTax, BigDecimal amountWithTax,
            BigDecimal amountTax, RatedTransactionStatusEnum status, WalletInstance wallet, BillingAccount billingAccount, UserAccount userAccount, InvoiceSubCategory invoiceSubCategory, String parameter1,
            String parameter2, String parameter3, String parameterExtra, String orderNumber, Subscription subscription, String inputUnitDescription, String ratingUnitDescription, PricePlanMatrix priceplan,
            OfferTemplate offerTemplate, EDR edr, String code, String description, Date startDate, Date endDate, Seller seller, Tax tax, BigDecimal taxPercent, ServiceInstance serviceInstance, TaxClass taxClass,
            AccountingCode accountingCode, RatedTransactionTypeEnum type, ChargeInstance chargeInstance, Date invoicingDate) {

        super();

        this.code = code;
        this.type = type;
        this.description = description;
        this.usageDate = usageDate;
        this.unitAmountWithoutTax = unitAmountWithoutTax;
        this.unitAmountWithTax = unitAmountWithTax;
        this.unitAmountTax = unitAmountTax;
        this.quantity = quantity;
        this.amountWithoutTax = amountWithoutTax;
        this.amountWithTax = amountWithTax;
        this.amountTax = amountTax;
        this.status = status;
        this.wallet = wallet;
        this.billingAccount = billingAccount;
        this.userAccount = userAccount;
        this.invoiceSubCategory = invoiceSubCategory;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.parameter3 = parameter3;
        this.parameterExtra = parameterExtra;
        this.orderNumber = orderNumber;
        this.subscription = subscription;
        this.priceplan = priceplan;
        this.offerTemplate = offerTemplate;
        this.edr = edr;
        this.startDate = startDate;
        this.endDate = endDate;
        this.seller = seller;
        this.tax = tax;
        this.taxPercent = taxPercent;
        this.unityDescription = inputUnitDescription;
        this.ratingUnitDescription = ratingUnitDescription;
        this.serviceInstance = serviceInstance;
        this.updated = new Date();
        this.taxClass = taxClass;
        this.chargeInstance = chargeInstance;
        if (accountingCode == null && this.invoiceSubCategory != null) {
            this.accountingCode = invoiceSubCategory.getAccountingCode();
        } else {
            this.accountingCode = accountingCode;
        }
        this.invoicingDate = invoicingDate;
    }

    /**
     * Constructor
     * 
     * @param walletOperation WalletOperation to convert to rated transaction
     */
    public RatedTransaction(WalletOperation walletOperation) {

        super();
        this.code = walletOperation.getCode();
        this.description = walletOperation.getDescription();
        this.chargeInstance = walletOperation.getChargeInstance();
        this.usageDate = walletOperation.getOperationDate();
        this.unitAmountWithoutTax = walletOperation.getUnitAmountWithoutTax();
        this.unitAmountWithTax = walletOperation.getUnitAmountWithTax();
        this.unitAmountTax = walletOperation.getUnitAmountTax();
        this.quantity = walletOperation.getQuantity();
        this.amountWithoutTax = walletOperation.getAmountWithoutTax();
        this.amountWithTax = walletOperation.getAmountWithTax();
        this.inputQuantity = walletOperation.getInputQuantity();
        this.rawAmountWithTax = walletOperation.getRawAmountWithTax();
        this.rawAmountWithoutTax = walletOperation.getRawAmountWithoutTax();
        this.amountTax = walletOperation.getAmountTax();
        this.wallet = walletOperation.getWallet();
        this.userAccount = walletOperation.getUserAccount();
        this.billingAccount = walletOperation.getBillingAccount();
        this.seller = walletOperation.getSeller();
        this.invoiceSubCategory = walletOperation.getInvoiceSubCategory();
        this.parameter1 = walletOperation.getParameter1();
        this.parameter2 = walletOperation.getParameter2();
        this.parameter3 = walletOperation.getParameter3();
        this.parameterExtra = walletOperation.getParameterExtra();
        this.orderNumber = walletOperation.getOrderNumber();
        this.subscription = walletOperation.getSubscription();
        this.priceplan = walletOperation.getPriceplan();
        this.offerTemplate = walletOperation.getOfferTemplate();
        this.edr = walletOperation.getEdr();
        this.startDate = walletOperation.getStartDate();
        this.endDate = walletOperation.getEndDate();
        this.tax = walletOperation.getTax();
        this.taxPercent = walletOperation.getTaxPercent();
        this.serviceInstance = walletOperation.getServiceInstance();
        this.status = RatedTransactionStatusEnum.OPEN;
        this.updated = new Date();
        this.taxClass = walletOperation.getTaxClass();
        this.inputUnitOfMeasure = walletOperation.getInputUnitOfMeasure();
        this.ratingUnitOfMeasure = walletOperation.getRatingUnitOfMeasure();
        this.accountingCode = walletOperation.getAccountingCode();
        this.accountingArticle = walletOperation.getAccountingArticle();
        this.infoOrder = walletOperation.getOrderInfo();
        this.invoicingDate = walletOperation.getInvoicingDate();
        this.unityDescription = walletOperation.getInputUnitDescription();
        this.ratingUnitDescription = walletOperation.getRatingUnitDescription();
        this.sortIndex = walletOperation.getSortIndex();
        this.cfValues = walletOperation.getCfValues();
        this.discountPlan = walletOperation.getDiscountPlan();
        this.rulesContract = walletOperation.getRulesContract();
    }

    public WalletInstance getWallet() {
        return wallet;
    }

    public void setWallet(WalletInstance wallet) {
        this.wallet = wallet;
    }

    @Override
    public Long getWalletId() {
        return wallet != null ? wallet.getId() : null;
    }

    public Date getUsageDate() {
        return usageDate;
    }

    public void setUsageDate(Date usageDate) {
        this.usageDate = usageDate;
    }

    public InvoiceSubCategory getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    @Override
    public Long getInvoiceSubCategoryId() {
        return invoiceSubCategory.getId();
    }

    public BigDecimal getUnitAmountWithoutTax() {
        return unitAmountWithoutTax;
    }

    public void setUnitAmountWithoutTax(BigDecimal unitAmountWithoutTax) {
        this.unitAmountWithoutTax = unitAmountWithoutTax;
    }

    public BigDecimal getUnitAmountWithTax() {
        return unitAmountWithTax;
    }

    public void setUnitAmountWithTax(BigDecimal unitAmountWithTax) {
        this.unitAmountWithTax = unitAmountWithTax;
    }

    public BigDecimal getUnitAmountTax() {
        return unitAmountTax;
    }

    public void setUnitAmountTax(BigDecimal unitAmountTax) {
        this.unitAmountTax = unitAmountTax;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public BigDecimal getAmountTax() {
        return amountTax;
    }

    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    public boolean isDoNotTriggerInvoicing() {
        return doNotTriggerInvoicing;
    }

    public void setDoNotTriggerInvoicing(boolean doNotTriggerInvoicing) {
        this.doNotTriggerInvoicing = doNotTriggerInvoicing;
    }

    /**
     * @return Billing account associated to rated transaction
     */
    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    /**
     * @param billingAccount Billing account associated to rated transaction
     */
    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    @Override
    public Long getBillingAccountId() {
        return billingAccount.getId();
    }

    /**
     * @return User account associated to rated transaction
     */
    public UserAccount getUserAccount() {
        return userAccount;
    }

    /**
     * @param userAccount User account associated to rated transaction
     */
    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public Long getUserAccountId() {
        return userAccount != null ? userAccount.getId() : null;
    }

    /**
     * @return Seller associated to rated transaction
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * @param seller Seller associated to rated transaction
     */
    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    @Override
    public Long getSellerId() {
        return seller.getId();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnityDescription() {
        return unityDescription;
    }

    public void setUnityDescription(String unityDescription) {
        this.unityDescription = unityDescription;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public String getParameter3() {
        return parameter3;
    }

    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setParameterExtra(String parameterExtra) {
        this.parameterExtra = parameterExtra;
    }

    public String getParameterExtra() {
        return parameterExtra;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public PricePlanMatrix getPriceplan() {
        return priceplan;
    }

    public void setPriceplan(PricePlanMatrix priceplan) {
        this.priceplan = priceplan;
    }

    public EDR getEdr() {
        return edr;
    }

    public void setEdr(EDR edr) {
        this.edr = edr;
    }

    public RatedTransaction getAdjustedRatedTx() {
        return adjustedRatedTx;
    }

    public void setAdjustedRatedTx(RatedTransaction adjustedRatedTx) {
        this.adjustedRatedTx = adjustedRatedTx;
    }

    public void recompute() {
        recompute(false);
    }

    // recompute given unit amount and quantity
    public void recompute(boolean isEnterprise) {
        unitAmountTax = unitAmountWithTax.subtract(unitAmountWithoutTax);
        amountWithoutTax = unitAmountWithoutTax.multiply(quantity);

        if (!isEnterprise) {
            amountWithTax = unitAmountWithTax.multiply(quantity);
        }
    }

    public OfferTemplate getOfferTemplate() {
        return offerTemplate;
    }

    public void setOfferTemplate(OfferTemplate offerTemplate) {
        this.offerTemplate = offerTemplate;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof RatedTransaction)) {
            return false;
        }

        RatedTransaction other = (RatedTransaction) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
        }
        return false;
    }

    public String getRatingUnitDescription() {
        return ratingUnitDescription;
    }

    public void setRatingUnitDescription(String ratingUnitDescription) {
        this.ratingUnitDescription = ratingUnitDescription;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public Long getSubscriptionId() {
        return subscription != null ? subscription.getId() : null;
    }

    public ChargeInstance getChargeInstance() {
        return chargeInstance;
    }

    public void setChargeInstance(ChargeInstance chargeInstance) {
        this.chargeInstance = chargeInstance;
    }

    /**
     * @return Tax applied
     */
    public Tax getTax() {
        return tax;
    }

    /**
     * @param tax Tax applied
     */
    public void setTax(Tax tax) {
        this.tax = tax;
    }

    @Override
    public Long getTaxId() {
        return tax.getId();
    }

    /**
     * @return Tax percent applied
     */
    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    /**
     * @param taxPercent Tax percent applied
     */
    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
    }

    public void resetAmounts() {
        unitAmountWithoutTax = BigDecimal.ZERO;
        unitAmountWithTax = BigDecimal.ZERO;
        unitAmountTax = BigDecimal.ZERO;
        amountWithoutTax = BigDecimal.ZERO;
        amountWithTax = BigDecimal.ZERO;
        amountTax = BigDecimal.ZERO;
    }

    public BigDecimal getIsEnterpriseAmount(boolean isEnterprise) {
        return isEnterprise ? getAmountWithoutTax() : getAmountWithTax();
    }

    public BigDecimal getIsEnterpriseUnitAmount(boolean isEnterprise) {
        return isEnterprise ? getUnitAmountWithoutTax() : getUnitAmountWithTax();
    }

    public void setIsEnterpriseAmount(boolean isEnterprise, BigDecimal amount) {
        if (isEnterprise) {
            setAmountWithoutTax(amount);

        } else {
            setAmountWithTax(amount);
        }
    }

    public void setIsEnterpriseUnitAmount(boolean isEnterprise, BigDecimal amount) {
        if (isEnterprise) {
            setUnitAmountWithoutTax(amount);

        } else {
            setUnitAmountWithTax(amount);
        }
    }

    /**
     * @return Service instance that Wallet operation is applied to
     */
    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    /**
     * @param serviceInstance Service instance that Wallet operation is applied to
     */
    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    /**
     * @return Is this a prepaid transaction
     */
    public boolean isPrepaid() {
        return wallet != null && wallet.isPrepaid();
    }

    /**
     * @return Was tax explicitly overridden during rating instead of calculated by a lookup in tax mapping table based on account and charge attributes. An absence of tax class with a presence of tax means tax was
     *         overridden.
     */
    public boolean isTaxOverriden() {
        return taxClass == null;
    }

    /**
     * @return Was tax recalculated (changed) during invoicing
     */
    public boolean isTaxRecalculated() {
        return taxRecalculated;
    }

    /**
     * @param taxRecalculated Was tax recalculated (changed) during invoicing
     */
    public void setTaxRecalculated(boolean taxRecalculated) {
        this.taxRecalculated = taxRecalculated;
    }

    /**
     * @return Billing run that invoiced this Rated transaction
     */
    public BillingRun getBillingRun() {
        return billingRun;
    }

    /**
     * @param billingRun Billing run that invoiced this Rated transaction
     */
    public void setBillingRun(BillingRun billingRun) {
        this.billingRun = billingRun;
    }

    /**
     * @return Invoice that invoiced this Rated transaction
     */
    public Invoice getInvoice() {
        return invoice;
    }

    /**
     * @param invoice Invoice that invoiced this Rated transaction
     */
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    /**
     * @return Subcategory invoice aggregate that Rated transaction was invoiced under
     */
    public SubCategoryInvoiceAgregate getInvoiceAgregateF() {
        return invoiceAgregateF;
    }

    /**
     * @param invoiceAgregateF Subcategory invoice aggregate that Rated transaction was invoiced under
     */
    public void setInvoiceAgregateF(SubCategoryInvoiceAgregate invoiceAgregateF) {
        this.invoiceAgregateF = invoiceAgregateF;
    }

    /**
     * @return Processing status
     */
    public RatedTransactionStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status Processing status
     */
    public void setStatus(RatedTransactionStatusEnum status) {
        this.status = status;
    }

    /**
     * @return Last status change date
     */
    public Date getUpdated() {
        return updated;
    }

    /**
     * @param updated Last status change date
     */
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public BigDecimal getInputQuantity() {
        return inputQuantity;
    }

    public void setInputQuantity(BigDecimal inputQuantity) {
        this.inputQuantity = inputQuantity;
    }

    public BigDecimal getRawAmountWithoutTax() {
        return rawAmountWithoutTax;
    }

    public void setRawAmountWithoutTax(BigDecimal rawAmountWithoutTax) {
        this.rawAmountWithoutTax = rawAmountWithoutTax;
    }

    public BigDecimal getRawAmountWithTax() {
        return rawAmountWithTax;
    }

    public void setRawAmountWithTax(BigDecimal rawAmountWithTax) {
        this.rawAmountWithTax = rawAmountWithTax;
    }

    /**
     * Change status and update a last updated timestamp
     * 
     * @param status Processing status
     */
    public void changeStatus(RatedTransactionStatusEnum status) {
        this.status = status;
        this.updated = new Date();
    }

    /**
     * @return Charge tax class
     */
    public TaxClass getTaxClass() {
        return taxClass;
    }

    /**
     * @param taxClass Charge tax class
     */
    public void setTaxClass(TaxClass taxClass) {
        this.taxClass = taxClass;
    }

    @Override
    public Long getTaxClassId() {
        return taxClass != null ? taxClass.getId() : null;
    }

    public UnitOfMeasure getInputUnitOfMeasure() {
        return inputUnitOfMeasure;
    }

    public void setInputUnitOfMeasure(UnitOfMeasure inputUnitOfMeasure) {
        this.inputUnitOfMeasure = inputUnitOfMeasure;
    }

    public UnitOfMeasure getRatingUnitOfMeasure() {
        return ratingUnitOfMeasure;
    }

    public void setRatingUnitOfMeasure(UnitOfMeasure ratingUnitOfMeasure) {
        this.ratingUnitOfMeasure = ratingUnitOfMeasure;
    }

    /**
     * @return Accounting code
     */
    public AccountingCode getAccountingCode() {
        return accountingCode;
    }

    /**
     * @param accountingCode Accounting code
     */
    public void setAccountingCode(AccountingCode accountingCode) {
        this.accountingCode = accountingCode;
    }

    public AccountingArticle getAccountingArticle() {
        return accountingArticle;
    }

    public void setAccountingArticle(AccountingArticle accountingArticle) {
        this.accountingArticle = accountingArticle;
    }

    /**
     * @return Date past which a charge can be included in the invoice. Allows to exclude charges from the current billing cycle by specifying a future date.
     */
    public Date getInvoicingDate() {
        return invoicingDate;
    }

    /**
     * @param invoicingDate Date past which a charge can be included in the invoice. Allows to exclude charges from the current billing cycle by specifying a future date.
     */
    public void setInvoicingDate(Date invoicingDate) {
        this.invoicingDate = invoicingDate;
    }

    /**
     * Gets the sorting index.
     *
     * @return the sorting index
     */
    public Integer getSortIndex() {
        return sortIndex;
    }

    /**
     * Sets the sorting index.
     *
     * @param sortIndex the sorting index
     */
    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    public RatedTransactionTypeEnum getType() {
        return type;
    }

    public void setType(RatedTransactionTypeEnum type) {
        this.type = type;
    }

    @Override
    public CustomFieldValues getCfValues() {
        return cfValues;
    }

    @Override
    public void setCfValues(CustomFieldValues cfValues) {
        this.cfValues = cfValues;
    }

    @Override
    public CustomFieldValues getCfAccumulatedValues() {
        return cfAccumulatedValues;
    }

    @Override
    public void setCfAccumulatedValues(CustomFieldValues cfAccumulatedValues) {
        this.cfAccumulatedValues = cfAccumulatedValues;
    }

    @Override
    public String getUuid() {
        setUUIDIfNull(); // setting uuid if null to be sure that the existing code expecting uuid not null will not be impacted
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Set a new UUID value.
     *
     * @return Old UUID value
     */
    @Override
    public String clearUuid() {
        String oldUuid = uuid;
        uuid = UUID.randomUUID().toString();
        return oldUuid;
    }

    /**
     * Get an array of parent custom field entity in case custom field values should be inherited from a parent entity.
     *
     * @return An entity
     */
    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return new ICustomFieldEntity[0];
    }

    /**
     * setting uuid if null
     */
    @PrePersist
    public void setUUIDIfNull() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }

    /**
     * @return the infoOrder
     */
    public OrderInfo getOrderInfo() {
        return infoOrder;
    }

    /**
     * @param infoOrder the infoOrder to set
     */
    public void setOrderInfo(OrderInfo infoOrder) {
        this.infoOrder = infoOrder;
    }

    public InvoiceLine getInvoiceLine() {
        return invoiceLine;
    }

    public void setInvoiceLine(InvoiceLine invoiceLine) {
        this.invoiceLine = invoiceLine;
    }

	public DiscountPlan getDiscountPlan() {
		return discountPlan;
	}

	public void setDiscountPlan(DiscountPlan discountPlan) {
		this.discountPlan = discountPlan;
	}

	public Long getDiscountedRatedTransaction() {
		return discountedRatedTransaction;
	}

	public void setDiscountedRatedTransaction(Long discountedRatedTransaction) {
		this.discountedRatedTransaction = discountedRatedTransaction;
	}

	public BigDecimal getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(BigDecimal discountValue) {
		this.discountValue = discountValue;
	}

	public DiscountPlanItemTypeEnum getDiscountPlanType() {
		return discountPlanType;
	}

	public void setDiscountPlanType(DiscountPlanItemTypeEnum discountPlanType) {
		this.discountPlanType = discountPlanType;
	}

	public DiscountPlanItem getDiscountPlanItem() {
		return discountPlanItem;
	}

	public void setDiscountPlanItem(DiscountPlanItem discountPlanItem) {
		this.discountPlanItem = discountPlanItem;
	}

	public OrderInfo getInfoOrder() {
		return infoOrder;
	}

	public void setInfoOrder(OrderInfo infoOrder) {
		this.infoOrder = infoOrder;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

    public Contract getRulesContract() {
        return rulesContract;
    }

    public void setRulesContract(Contract rulesContract) {
        this.rulesContract = rulesContract;
    }	
	
}