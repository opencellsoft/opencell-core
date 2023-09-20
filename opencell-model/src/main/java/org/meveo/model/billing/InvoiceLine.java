package org.meveo.model.billing;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.LAZY;
import static org.meveo.model.billing.InvoiceLineStatusEnum.OPEN;
import static org.meveo.model.billing.AdjustmentStatusEnum.NOT_ADJUSTED;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.persistence.PreUpdate;
import javax.persistence.PrePersist;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.model.*;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.commercial.OrderLot;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.offer.QuoteOffer;

/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@Entity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "InvoiceLine")
@Table(name = "billing_invoice_line")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_invoice_line_seq"), @Parameter(name = "increment_size", value = "5000")})
@NamedQueries({
		@NamedQuery(name = "InvoiceLine.listToInvoiceByBillingAccountAndIDs", query = "FROM InvoiceLine il where il.billingAccount.id=:billingAccountId AND il.status='OPEN' AND id in (:listOfIds) "),
		@NamedQuery(name = "InvoiceLine.InvoiceLinesByInvoiceID", query = "FROM InvoiceLine il WHERE il.invoice.id =:invoiceId"),
		@NamedQuery(name = "InvoiceLine.InvoiceLinesByBRs", query = "FROM InvoiceLine il WHERE il.billingRun IN (:BillingRus)"),
        @NamedQuery(name = "InvoiceLine.findByCommercialOrder", query = "select il from InvoiceLine il where il.commercialOrder = :commercialOrder"),
		@NamedQuery(name = "InvoiceLine.InvoiceLinesByBRID", query = "FROM InvoiceLine il WHERE il.billingRun.id = :billingRunId"),
		@NamedQuery(name = "InvoiceLine.AddInvoice", query = "UPDATE InvoiceLine il SET il.invoice = :inv WHERE il.id in (:ids)"),
		@NamedQuery(name = "InvoiceLine.listToInvoiceByOrderNumber", query = "FROM InvoiceLine il where il.status='OPEN' AND il.orderNumber=:orderNumber AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate order by il.billingAccount.id "),
		@NamedQuery(name = "InvoiceLine.listToInvoiceBySubscription", query = "FROM InvoiceLine il where il.subscription.id=:subscriptionId AND il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate "),
		@NamedQuery(name = "InvoiceLine.listToInvoiceByBillingAccount", query = "FROM InvoiceLine il where il.billingAccount.id=:billingAccountId AND il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate"),
		@NamedQuery(name = "InvoiceLine.listToInvoiceByOrderNumberAndBR", query = "FROM InvoiceLine il where il.status='OPEN' AND il.orderNumber=:orderNumber AND il.billingRun.id=:billingRunId order by il.billingAccount.id "),
		@NamedQuery(name = "InvoiceLine.listToInvoiceBySubscriptionAndBR", query = "FROM InvoiceLine il where il.subscription.id=:subscriptionId AND il.status='OPEN' AND il.billingRun.id=:billingRunId"),
		@NamedQuery(name = "InvoiceLine.listToInvoiceByBillingAccountAndBR", query = "FROM InvoiceLine il where il.billingAccount.id=:billingAccountId AND il.status='OPEN'  AND il.billingRun.id=:billingRunId "),
		@NamedQuery(name = "InvoiceLine.updateWithInvoice", query = "UPDATE InvoiceLine il set il.status=org.meveo.model.billing.InvoiceLineStatusEnum.BILLED, il.auditable.updated = :now , il.billingRun=:billingRun, il.invoice=:invoice, il.invoiceAggregateF=:invoiceAgregateF where il.id in :ids"),
		@NamedQuery(name = "InvoiceLine.updateWithInvoiceInfo", query = "UPDATE InvoiceLine il set il.status=org.meveo.model.billing.InvoiceLineStatusEnum.BILLED, il.billingRun=:billingRun, il.auditable.updated = :now, il.invoice=:invoice, il.amountWithoutTax=:amountWithoutTax, il.amountWithTax=:amountWithTax, il.amountTax=:amountTax, il.tax=:tax, il.taxRate=:taxPercent, il.invoiceAggregateF=:invoiceAgregateF where il.id=:id"),
		@NamedQuery(name = "InvoiceLine.sumTotalInvoiceableByOrderNumber", query = "SELECT new org.meveo.model.billing.Amounts(sum(il.amountWithoutTax), sum(il.amountWithTax), sum(il.amountTax)) FROM InvoiceLine il WHERE il.status='OPEN' AND il.orderNumber=:orderNumber AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate "),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableByServiceWithMinAmountBySubscription", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.serviceInstance.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate AND il.subscription=:subscription AND il.serviceInstance.minimumAmountEl is not null GROUP BY il.serviceInstance.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableByServiceWithMinAmountByBA", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.serviceInstance.id  FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate AND il.billingAccount=:billingAccount AND il.serviceInstance.minimumAmountEl is not null GROUP BY il.serviceInstance.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableBySubscriptionWithMinAmountBySubscription", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.subscription.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate AND il.subscription=:subscription AND il.subscription.minimumAmountEl is not null GROUP BY il.subscription.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableBySubscriptionWithMinAmountByBA", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.subscription.id  FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate AND il.billingAccount=:billingAccount AND il.subscription.minimumAmountEl is not null GROUP BY il.subscription.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableForUAWithMinAmountBySubscription", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.subscription.userAccount.id, il.subscription.seller.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.subscription=:subscription and il.subscription.userAccount.minimumAmountEl is not null GROUP BY il.subscription.seller.id, il.subscription.userAccount.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableForBAWithMinAmountBySubscription", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.billingAccount.id, il.subscription.seller.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.subscription=:subscription and il.billingAccount.minimumAmountEl is not null GROUP BY il.subscription.seller.id, il.billingAccount.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableWithMinAmountByBA", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.billingAccount.id, il.subscription.seller.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.billingAccount=:billingAccount GROUP BY il.subscription.seller.id, il.billingAccount.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableWithMinAmountByUA", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.billingAccount.id, il.subscription.seller.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.billingAccount=:billingAccount and il.subscription.userAccount.minimumAmountEl is not null GROUP BY il.subscription.seller.id, il.billingAccount.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableForCAWithMinAmountBySubscription", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.billingAccount.customerAccount.id, il.subscription.seller.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.subscription=:subscription and il.billingAccount.customerAccount.minimumAmountEl is not null GROUP BY il.subscription.seller.id, il.billingAccount.customerAccount.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableWithMinAmountByCA", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.billingAccount.customerAccount.id, il.subscription.seller.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.billingAccount.customerAccount=:customerAccount GROUP BY il.subscription.seller.id, il.billingAccount.customerAccount.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableForCustomerWithMinAmountBySubscription", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.billingAccount.customerAccount.customer.id, il.subscription.seller.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.subscription=:subscription and il.billingAccount.customerAccount.customer.minimumAmountEl is not null GROUP BY il.subscription.seller.id, il.billingAccount.customerAccount.customer.id"),
		@NamedQuery(name = "InvoiceLine.sumInvoiceableWithMinAmountByCustomer", query = "SELECT sum(il.amountWithoutTax), sum(il.amountWithTax), il.billingAccount.customerAccount.customer.id, il.subscription.seller.id FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.billingAccount.customerAccount.customer=:customer GROUP BY il.subscription.seller.id, il.billingAccount.customerAccount.customer.id"),
		@NamedQuery(name = "InvoiceLine.sumTotalInvoiceableBySubscription", query = "SELECT new org.meveo.model.billing.Amounts(sum(il.amountWithoutTax), sum(il.amountWithTax), sum(il.amountTax)) FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.subscription=:subscription"),
		@NamedQuery(name = "InvoiceLine.sumTotalInvoiceableByBA", query = "SELECT new org.meveo.model.billing.Amounts(sum(il.amountWithoutTax), sum(il.amountWithTax), sum(il.amountTax)) FROM InvoiceLine il WHERE il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate and il.billingAccount=:billingAccount"),
		@NamedQuery(name = "InvoiceLine.sumPositiveILByBillingRun", query = "select sum(il.amountWithoutTax), sum(il.amountWithTax),il.subscription.id, il.commercialOrder.id, il.invoice.id, il.billingAccount.id, il.billingAccount.customerAccount.id, il.billingAccount.customerAccount.customer.id FROM InvoiceLine il where il.billingRun.id=:billingRunId and il.amountWithoutTax > 0 and il.status='BILLED' group by il.subscription.id, il.commercialOrder.id, il.invoice.id, il.billingAccount.id, il.billingAccount.customerAccount.id, il.billingAccount.customerAccount.customer.id"),
		@NamedQuery(name = "InvoiceLine.unInvoiceByInvoiceIds", query = "update InvoiceLine il set il.status='OPEN', il.auditable.updated = :now , il.billingRun= null, il.invoice=null, il.invoiceAggregateF = null where il.invoice.id IN (:invoiceIds) and orderOffer is not null"),
		@NamedQuery(name = "InvoiceLine.cancelForRemoveByInvoiceIds", query = "update InvoiceLine il set il.status='CANCELED', il.auditable.updated = :now, il.invoice=null, il.invoiceAggregateF=null WHERE il.invoice.id IN (:invoicesIds)"),
		@NamedQuery(name = "InvoiceLine.cancelByInvoiceIds", query = "update InvoiceLine il set il.status='CANCELED', il.auditable.updated = :now, il.invoice=null WHERE il.invoice.id IN (:invoicesIds) and orderOffer is null"),
	    @NamedQuery(name = "InvoiceLine.listToInvoiceByCommercialOrder", query = "FROM InvoiceLine il where il.commercialOrder.id=:commercialOrderId AND il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate "),
		@NamedQuery(name = "InvoiceLine.listToInvoiceByCommercialOrderAndBR", query = "FROM InvoiceLine il where il.commercialOrder.id=:commercialOrderId AND il.status='OPEN' AND il.billingRun.id=:billingRunId"),
		@NamedQuery(name = "InvoiceLine.BillingAccountByILIds",
				query = "SELECT ba FROM BillingAccount ba WHERE ba.id IN (SELECT distinct il.billingAccount.id FROM InvoiceLine il WHERE il.id in (:ids))"),
		@NamedQuery(name = "InvoiceLine.listByInvoice", query = "SELECT il FROM InvoiceLine il where il.invoice=:invoice and il.status='BILLED' order by il.valueDate"),
		@NamedQuery(name = "InvoiceLine.listByInvoiceNotFree", query = "SELECT il FROM InvoiceLine il where il.invoice=:invoice and il.amountWithoutTax<>0 and il.status='BILLED' order by il.valueDate"),
		@NamedQuery(name = "InvoiceLine.sumTotalInvoiceableByQuote", query = "SELECT new org.meveo.model.billing.Amounts(sum(il.amountWithoutTax), sum(il.amountWithTax), sum(il.amountTax)) FROM InvoiceLine il WHERE il.status='OPEN' AND il.quote.id=:quoteId AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate "),
		@NamedQuery(name = "InvoiceLine.listToInvoiceByQuote", query = "FROM InvoiceLine il where il.quote.id=:quoteId AND il.status='OPEN' AND :firstTransactionDate<=il.valueDate AND il.valueDate<:lastTransactionDate "),
		@NamedQuery(name = "InvoiceLine.listToInvoiceByQuoteAndBR", query = "FROM InvoiceLine il where il.quote.id=:quoteId AND il.status='OPEN' AND il.billingRun.id=:billingRunId"),
		@NamedQuery(name = "InvoiceLine.findByQuote", query = "select il from InvoiceLine il where il.quote =:quote"),
        @NamedQuery(name = "InvoiceLine.deleteInvoiceAggrByInvoiceAgregate", query = "UPDATE InvoiceLine il set il.invoiceAggregateF=null where il.invoiceAggregateF in (Select ia.id from InvoiceAgregate ia where ia.invoice.id =:invoiceId)"),
		@NamedQuery(name = "InvoiceLine.deleteInvoiceAggrByInvoice", query = "UPDATE InvoiceLine il set il.invoiceAggregateF=null where il.invoice.id=:invoiceId"),
		@NamedQuery(name = "InvoiceLine.listByBillingRun", query = "SELECT il.id FROM InvoiceLine il WHERE il.billingRun.id =:billingRunId"),
		@NamedQuery(name = "InvoiceLine.deleteByBillingRun", query = "DELETE from InvoiceLine il WHERE il.billingRun.id =:billingRunId"),
		@NamedQuery(name = "InvoiceLine.listByBillingRunNotValidatedInvoices", query = "SELECT il.id FROM InvoiceLine il left join il.invoice i WHERE il.billingRun.id =:billingRunId and (i.id is null or i.status <> 'VALIDATED')"),
		@NamedQuery(name = "InvoiceLine.getBAsHavingOpenILsByBR", query = "SELECT DISTINCT il.billingAccount.id FROM InvoiceLine il WHERE il.billingRun.id=:billingRunId AND il.status='OPEN'"),
		@NamedQuery(name = "InvoiceLine.deleteByBillingRunNotValidatedInvoices", query = "DELETE from InvoiceLine il WHERE il.billingRun.id =:billingRunId AND (il.invoice.id IS NULL OR il.invoice.id in (select il2.invoice.id from InvoiceLine il2 where il2.invoice.status <> org.meveo.model.billing.InvoiceStatusEnum.VALIDATED))"),
		@NamedQuery(name = "InvoiceLine.listDiscountLines", query = "SELECT il.id from InvoiceLine il WHERE il.discountedInvoiceLine.id = :invoiceLineId "),
		@NamedQuery(name = "InvoiceLine.findByInvoiceAndIds", query = "SELECT il from InvoiceLine il WHERE il.invoice = :invoice and il.id in (:invoiceLinesIds)"),
		@NamedQuery(name = "InvoiceLine.updateTaxForRateTaxMode", query = "UPDATE InvoiceLine il SET il.tax= null WHERE il.id in (:invoiceLinesIds)"),
        @NamedQuery(name = "InvoiceLine.moveToQuarantineBRByInvoiceIds", query = "update InvoiceLine il set il.billingRun=:billingRun where il.invoice.id in (:invoiceIds)"),
        @NamedQuery(name = "InvoiceLine.listByAssociatedInvoice", query = "SELECT il.id FROM InvoiceLine il where il.invoice.id in (:invoiceIds)"),
        @NamedQuery(name = "InvoiceLine.sumAmountByOpenOrderNumberAndBA", query = "SELECT SUM(il.amountWithTax) FROM InvoiceLine il WHERE il.status = 'BILLED' AND il.openOrderNumber = :openOrderNumber AND il.billingAccount.id = :billingAccountId"),
		@NamedQuery(name = "InvoiceLine.linkToInvoice", query = "UPDATE InvoiceLine il set il.status=org.meveo.model.billing.InvoiceLineStatusEnum.BILLED, il.invoice=:invoice, il.invoiceAggregateF=:invoiceAgregateF where il.id in :ids"),
        @NamedQuery(name = "InvoiceLine.getInvoicingItems", query = 
    	"select il.billingAccount.id, il.accountingArticle.invoiceSubCategory.id, il.userAccount.id, il.tax.id, sum(il.amountWithoutTax), sum(il.amountWithTax), sum(il.amountTax), count(il.id), (string_agg(cast(il.id as text),',')) "
    		+ " FROM InvoiceLine il "
    		+ " WHERE il.billingRun.id=:billingRunId AND il.billingAccount.id IN (:ids) AND il.status='OPEN' "
    		+ " group by il.billingAccount.id, il.accountingArticle.invoiceSubCategory.id, il.userAccount.id, il.tax.id "
    		+ " order by il.billingAccount.id"),
        @NamedQuery(name = "InvoiceLine.findByIdsAndAdjustmentStatus", query = "SELECT il from InvoiceLine il left join fetch il.invoice i left join fetch i.invoiceType WHERE adjustment_status = :status and il.id in (:invoiceLinesIds)"),
        @NamedQuery(name = "InvoiceLine.findByIdsAndOtherAdjustmentStatus", query = "SELECT il from InvoiceLine il  left join fetch il.invoice i left join fetch i.invoiceType WHERE adjustment_status <> :status and il.id in (:invoiceLinesIds)"),
        @NamedQuery(name = "InvoiceLine.findByAdjustmentStatus", query = "SELECT il from InvoiceLine il left join fetch il.invoice WHERE adjustment_status = :status"),
        @NamedQuery(name = "InvoiceLine.findByIdsAndInvoiceType", query = "SELECT il from InvoiceLine il left join fetch il.invoice i left join fetch i.invoiceType WHERE i.invoiceType.code = :invoiceType and il.id in (:invoiceLinesIds)"),
        @NamedQuery(name = "InvoiceLine.updateForAdjustment", query = "UPDATE InvoiceLine il set adjustment_status=:status, il.auditable.updated = :now  where il.id in :ids"),
		@NamedQuery(name = "InvoiceLine.getMaxIlAmountAdj", query = "SELECT bli.id.id, bli.linkedInvoiceValue.id, il.accountingArticle.id, il.tax.id, il.taxRate, il.taxMode,   "
                + " (SUM(il.amountWithoutTax) - COALESCE(SUM(ilAdj.amountWithoutTax), 0)) AS amountWithoutTax, "
                + " (SUM(il.amountTax) - COALESCE(SUM(ilAdj.amountTax), 0)) AS amountTax, "
                + " (SUM(il.amountWithTax) - COALESCE(SUM(ilAdj.amountWithTax), 0)) AS amountWithTax "
                + " FROM InvoiceLine il LEFT JOIN LinkedInvoice bli ON (bli.id.id = il.invoice.id AND bli.type IS NULL) "
                + " LEFT JOIN Invoice adj ON (bli.linkedInvoiceValue.id = adj.id AND adj.status not in ('REJECTED', 'CANCELED')) "
                + " LEFT JOIN InvoiceLine ilAdj ON (adj.id = ilAdj.invoice.id AND il.accountingArticle.id = ilAdj.accountingArticle.id "
                + " AND il.tax.id = ilAdj.tax.id AND il.taxRate = ilAdj.taxRate AND il.taxMode = ilAdj.taxMode) WHERE bli.id.id in (:invoiceId) "
                + " GROUP BY bli.id.id, il.accountingArticle.id, il.tax.id, il.taxRate, il.taxMode, bli.linkedInvoiceValue.id "),
		@NamedQuery(name = "InvoiceLine.sumAmountsDiscountByBillingAccount", query = "select sum(il.amountWithoutTax), sum(il.amountWithTax), il.subscription.id, il.commercialOrder.id ,il.invoice.id ,il.billingAccount.id,  il.billingAccount.customerAccount.id, il.billingAccount.customerAccount.customer.id"
                + " from  InvoiceLine il  where il.billingRun.id=:billingRunId and il.discountPlanItem is not null group by il.subscription.id, il.commercialOrder.id , il.invoice.id, il.billingAccount.id, il.billingAccount.customerAccount.id, il.billingAccount.customerAccount.customer.id"),
		@NamedQuery(name = "InvoiceLine.getAdjustmentAmount", query = "SELECT SUM(li.amount) FROM Invoice i JOIN i.linkedInvoices li WHERE i.id= :ID_INVOICE and li.type = 'ADJUSTMENT'"),
		@NamedQuery(name = "InvoiceLine.updateByIncrementalMode", query = "UPDATE InvoiceLine il SET " +
				"il.amountWithoutTax=il.amountWithoutTax+:deltaAmountWithoutTax, il.amountWithTax=il.amountWithTax+:deltaAmountWithTax, " +
				"il.amountTax=il.amountTax+:deltaAmountTax, il.quantity=il.quantity+:deltaQuantity, il.validity.from=:beginDate, " +
				"il.validity.to=:endDate, il.auditable.updated=:now, il.unitPrice=:unitPrice WHERE il.id=:id"),
		@NamedQuery(name = "InvoiceLine.updateStatusInvoiceLine", query = "UPDATE InvoiceLine il SET " +
				"il.status =: statusToUpdate WHERE il.id =: id"),
		@NamedQuery(name = "InvoiceLine.cancelInvoiceLineByWoIds", query = "UPDATE InvoiceLine il SET il.auditable.updated = :now, il.status = org.meveo.model.billing.InvoiceLineStatusEnum.CANCELED WHERE il.status = org.meveo.model.billing.InvoiceLineStatusEnum.OPEN AND il.id in (SELECT wo.ratedTransaction.invoiceLine.id FROM WalletOperation wo WHERE wo.id IN :woIds)"),
		@NamedQuery(name = "InvoiceLine.sumAmountsPerBR", query = "SELECT SUM(il.amountWithoutTax), SUM(il.amountTax), SUM(il.amountWithTax) FROM InvoiceLine il WHERE il.billingRun.id =:billingRunId"),
        @NamedQuery(name = "InvoiceLine.countDistinctBAByBR", query = "select count(distinct il.billingAccount) from InvoiceLine il where billingRun.id=:brId")

	})

@NamedNativeQueries({
    @NamedNativeQuery(name = "InvoiceLine.massUpdateWithDiscountedIL", query = "update {h-schema}billing_invoice_line il set discounted_invoice_line=discountRT.invoice_line_id, updated=now() from {h-schema}billing_rated_transaction discountRT, {h-schema}billing_rated_transaction discountedRT where discountRT.id=discountedRT.discounted_ratedtransaction_id and il.id=discountedRT.invoice_line_id and discountedRT.status='BILLED' and il.status='OPEN' and il.billing_run_id=:brId and discountedRT.discounted_ratedtransaction_id is not null and il.discounted_invoice_line is null and il.discount_plan_type is not null and discountedRT.id>=:minId and discountedRT.id<=:maxId"),
    @NamedNativeQuery(name = "InvoiceLine.massUpdateWithDiscountedILOracle", query = "UPDATE (SELECT il.discounted_invoice_line, il.discount_plan_type, il.updated FROM {h-schema}billing_invoice_line il, {h-schema}billing_rated_transaction discountRT, {h-schema}billing_rated_transaction discountedRT where discountRT.id=discountedRT.discounted_ratedtransaction_id and il.id=discountedRT.invoice_line_id and discountedRT.status='BILLED' and il.status='OPEN' and il.billing_run_id=:brId and discountedRT.discounted_ratedtransaction_id is not null and discounted_invoice_line is null and discount_plan_type is not null and discountedRT.id>=:minId and discountedRT.id<=:maxId) SET il.discounted_invoice_line=discountRT.invoice_line_id , updated=now()"),
})
    
public class InvoiceLine extends AuditableCFEntity {

	/**
     * 
     */
    private static final long serialVersionUID = 7347240213099322047L;

    @ManyToOne(fetch = LAZY)
	@JoinColumn(name = "invoice_id")
	private Invoice invoice;
	
	@Column(name = "prestation")
	@Size(max = 255)
	private String prestation;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "accounting_article_id", nullable = false)
	private AccountingArticle accountingArticle;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "offer_service_template_id")
	private OfferServiceTemplate offerServiceTemplate;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "service_template_id")
	private ServiceTemplate serviceTemplate;

    @Embedded
    @AttributeOverrides(value = { @AttributeOverride(name = "from", column = @Column(name = "begin_date")), @AttributeOverride(name = "to", column = @Column(name = "end_date")) })
    private DatePeriod validity = new DatePeriod();
    
    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal quantity;

    @Column(name = "unit_price", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal unitPrice;
    
    @Column(name = "discount_rate", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal discountRate = BigDecimal.ZERO;

    @Column(name = "amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal amountWithoutTax;

    @Column(name = "tax_rate", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal taxRate;

    @Column(name = "amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal amountWithTax;

    @Column(name = "amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal amountTax;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "discount_plan_id")
    private DiscountPlan discountPlan;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "tax_id")
    private Tax tax;
    
	@Column(name = "order_ref", length = 20)
	@Size(max = 20)
    private String orderRef;
	
	@Column(name = "access_point", length = 20)
	@Size(max = 20)
    private String accessPoint;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "commercial_order_id")
    private CommercialOrder commercialOrder;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "billing_run_id")
	private BillingRun billingRun;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "billing_account_id")
	private BillingAccount billingAccount;

	@Column(name = "value_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date valueDate;

	@Column(name = "order_number")
	private String orderNumber;

	@Column(name = "discount_amount", precision = NB_PRECISION, scale = NB_DECIMALS)
	@NotNull
	private BigDecimal discountAmount = BigDecimal.ZERO;

	@Column(name = "label")
	private String label;

	@Column(name = "raw_amount", precision = NB_PRECISION, scale = NB_DECIMALS)
	@NotNull
	private BigDecimal rawAmount = BigDecimal.ZERO;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "service_instance_id")
	private ServiceInstance serviceInstance;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "subscription_id")
	private Subscription subscription;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "offer_template_id")
	private OfferTemplate offerTemplate;
	
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "product_version_id")
	private ProductVersion productVersion;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "order_lot_id")
	private OrderLot orderLot;

	@Transient
	private boolean taxRecalculated;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	@NotNull
	private InvoiceLineStatusEnum status = OPEN;

	@OneToMany(mappedBy = "invoiceLine", fetch = LAZY)
	private List<RatedTransaction> ratedTransactions;

	@Column(name = "discount_value")
	private BigDecimal discountValue;
    
    @Enumerated(EnumType.STRING)
 	@Column(name = "discount_plan_type", length = 50)
 	private DiscountPlanItemTypeEnum discountPlanType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_plan_item_id")
    private DiscountPlanItem discountPlanItem;
    
    @Enumerated(EnumType.STRING)
	@Column(name = "adjustment_status", nullable = false)
	@NotNull
	private AdjustmentStatusEnum adjustmentStatus = NOT_ADJUSTED;
    
    /**
   	 * 
   	 *filled only for price lines related to applied discounts, and contains the application sequence composed by the concatenation of the DP sequence and DPI sequence
   	 */
   	@Column(name = "sequence")
   	private Integer sequence;

	/**
	 * Subcategory invoice aggregate that invoice line was invoiced under
	 */
	@ManyToOne(fetch = LAZY, cascade = PERSIST)
	@JoinColumn(name = "aggregate_id_f")
	private SubCategoryInvoiceAgregate invoiceAggregateF;
	
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "cpq_quote_id")
    private CpqQuote quote;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_offer_id")
	private QuoteOffer quoteOffer;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_offer_id")
	private OrderOffer orderOffer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "discounted_invoice_line")
	private InvoiceLine discountedInvoiceLine;
	
    /**
     * User account associated to invoice line
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "tax_mode", nullable = false)
    @NotNull
    private InvoiceLineTaxModeEnum taxMode = InvoiceLineTaxModeEnum.ARTICLE;

	/**
	 * Transactional unit price
	 */
	@Column(name = "transactional_unit_price", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal transactionalUnitPrice;

	/**
	 * Transactional amount without tax
	 */
	@Column(name = "transactional_amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal transactionalAmountWithoutTax;

	/**
	 * Transactional amount with tax
	 */
	@Column(name = "transactional_amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal transactionalAmountWithTax;

	/**
	 * Transactional amount tax
	 */
	@Column(name = "transactional_amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal transactionalAmountTax;

	/**
	 * Transactional discount amount
	 */
	@Column(name = "transactional_discount_amount", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal transactionalDiscountAmount = BigDecimal.ZERO;

	/**
	 * Transactional raw amount
	 */
	@Column(name = "transactional_raw_amount", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal transactionalRawAmount = BigDecimal.ZERO;

    @Type(type = "numeric_boolean")
    @Column(name = "use_specific_price_conversion")
    private boolean useSpecificPriceConversion;
    
    @Column(name = "conversion_from_billing_currency")
    @Type(type = "numeric_boolean")
    private boolean conversionFromBillingCurrency = false;
    
	/**
	 * Open Order Number
	 */
	@Column(name = "open_order_number")
	@Size(max = 255)
	private String openOrderNumber;
    
	public InvoiceLine() {
	}

	public InvoiceLine(Date valueDate, BigDecimal quantity, BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax, InvoiceLineStatusEnum status,
					   BillingAccount billingAccount, String label, Tax tax, BigDecimal taxRate, AccountingArticle accountingArticle) {
		this.label = label;
		this.valueDate = valueDate;
		this.quantity = quantity;
		this.amountWithoutTax = amountWithoutTax;
		this.unitPrice = quantity!=null && !BigDecimal.ZERO.equals(quantity)?amountWithoutTax.divide(quantity):amountWithoutTax;
		this.amountWithTax = amountWithTax;
		this.amountTax = amountTax;
		this.status = status;
		this.billingAccount = billingAccount;
		this.tax = tax;
		this.taxRate = taxRate;
		this.accountingArticle = accountingArticle;
	}

	public InvoiceLine(InvoiceLine copy, Invoice invoice) {
		this.invoice = invoice;
		this.prestation = copy.prestation;
		this.accountingArticle = copy.accountingArticle;
		this.offerServiceTemplate = copy.offerServiceTemplate;
		this.product = copy.product;
		this.serviceTemplate = copy.serviceTemplate;
		this.validity = copy.validity;
		this.quantity = copy.quantity;
		this.unitPrice = copy.unitPrice;
		this.discountRate = copy.discountRate;
		this.amountWithoutTax = copy.amountWithoutTax;
		this.taxRate = copy.taxRate;
		this.amountWithTax = copy.amountWithTax;
		this.amountTax = copy.amountTax;
		this.discountPlan = copy.discountPlan;
		this.tax = copy.tax;
		this.orderRef = copy.orderRef;
		this.accessPoint = copy.accessPoint;
		this.commercialOrder = copy.commercialOrder;
		this.billingRun = copy.billingRun;
		this.billingAccount = copy.billingAccount;
		this.valueDate = copy.valueDate;
		this.orderNumber = copy.orderNumber;
		this.discountAmount = copy.discountAmount;
		this.label = copy.label;
		this.rawAmount = copy.rawAmount;
		this.serviceInstance = copy.serviceInstance;
		this.subscription = copy.subscription;
		this.offerTemplate = copy.offerTemplate;
		this.productVersion = copy.productVersion;
		this.orderLot = copy.orderLot;
		this.taxRecalculated = copy.taxRecalculated;
		this.userAccount = copy.userAccount;
		this.taxMode = copy.taxMode;
		this.status = InvoiceLineStatusEnum.OPEN;
		this.adjustmentStatus = copy.adjustmentStatus;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public String getPrestation() {
		return prestation;
	}

	public void setPrestation(String prestation) {
		this.prestation = prestation;
	}

	public AccountingArticle getAccountingArticle() {
		return accountingArticle;
	}

	public void setAccountingArticle(AccountingArticle accountingArticle) {
		this.accountingArticle = accountingArticle;
	}

	public OfferServiceTemplate getOfferServiceTemplate() {
		return offerServiceTemplate;
	}

	public void setOfferServiceTemplate(OfferServiceTemplate offerServiceTemplate) {
		this.offerServiceTemplate = offerServiceTemplate;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public ServiceTemplate getServiceTemplate() {
		return serviceTemplate;
	}

	public void setServiceTemplate(ServiceTemplate serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
	}

	public DatePeriod getValidity() {
		return validity;
	}

	public void setValidity(DatePeriod validity) {
		this.validity = validity;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(BigDecimal discountRate) {
		this.discountRate = discountRate;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
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

	public DiscountPlan getDiscountPlan() {
		return discountPlan;
	}

	public void setDiscountPlan(DiscountPlan discountPlan) {
		this.discountPlan = discountPlan;
	}

	public Tax getTax() {
		return tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}

	public String getOrderRef() {
		return orderRef;
	}

	public void setOrderRef(String orderRef) {
		this.orderRef = orderRef;
	}

	public String getAccessPoint() {
		return accessPoint;
	}

	public void setAccessPoint(String accessPoint) {
		this.accessPoint = accessPoint;
	}

	public CommercialOrder getCommercialOrder() {
		return commercialOrder;
	}

	public void setCommercialOrder(CommercialOrder commercialOrder) {
		this.commercialOrder = commercialOrder;
	}

	public BillingRun getBillingRun() {
		return billingRun;
	}

	public void setBillingRun(BillingRun billingRun) {
		this.billingRun = billingRun;
	}

	public BillingAccount getBillingAccount() {
		return billingAccount;
	}

	public void setBillingAccount(BillingAccount billingAccount) {
		this.billingAccount = billingAccount;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public BigDecimal getRawAmount() {
		return rawAmount;
	}

	public void setRawAmount(BigDecimal rawAmount) {
		this.rawAmount = rawAmount;
	}

	public OfferTemplate getOfferTemplate() {
		return offerTemplate;
	}

	public void setOfferTemplate(OfferTemplate offerTemplate) {
		this.offerTemplate = offerTemplate;
	}

	public ProductVersion getProductVersion() {
		return productVersion;
	}

	public void setProductVersion(ProductVersion productVersion) {
		this.productVersion = productVersion;
	}

	public OrderLot getOrderLot() {
		return orderLot;
	}

	public void setOrderLot(OrderLot orderLot) {
		this.orderLot = orderLot;
	}

	public boolean isTaxOverridden() {
		return accountingArticle.getTaxClass() == null;
	}

	public boolean isTaxRecalculated() {
		return taxRecalculated;
	}

	public void setTaxRecalculated(boolean taxRecalculated) {
		this.taxRecalculated = taxRecalculated;
	}

	public void computeDerivedAmounts(boolean isEnterprise, int rounding, RoundingModeEnum roundingMode) {
		BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(amountWithoutTax, amountWithTax, taxRate, isEnterprise, rounding, roundingMode.getRoundingMode());
		amountWithoutTax = amounts[0];
		amountWithTax = amounts[1];
		amountTax = amounts[2];
	}

	public InvoiceLineStatusEnum getStatus() {
		return status;
	}

	public void setStatus(InvoiceLineStatusEnum status) {
		this.status = status;
	}

	public List<RatedTransaction> getRatedTransactions() {
		return ratedTransactions;
	}

	public void setRatedTransactions(List<RatedTransaction> ratedTransactions) {
		this.ratedTransactions = ratedTransactions;
	}

	public SubCategoryInvoiceAgregate getInvoiceAggregateF() {
		return invoiceAggregateF;
	}

	public void setInvoiceAggregateF(SubCategoryInvoiceAgregate invoiceAggregateF) {
		this.invoiceAggregateF = invoiceAggregateF;
	}

	public CpqQuote getQuote() {
		return quote;
	}

	public void setQuote(CpqQuote quote) {
		this.quote = quote;
	}

	public QuoteOffer getQuoteOffer() {
		return quoteOffer;
	}

	public void setQuoteOffer(QuoteOffer quoteOffer) {
		this.quoteOffer = quoteOffer;
	}

	public OrderOffer getOrderOffer() {
		return orderOffer;
	}

	public void setOrderOffer(OrderOffer orderOffer) {
		this.orderOffer = orderOffer;
	}

	public InvoiceLine getDiscountedInvoiceLine() {
		return discountedInvoiceLine;
	}

	public void setDiscountedInvoiceLine(InvoiceLine discountedInvoiceLine) {
		this.discountedInvoiceLine = discountedInvoiceLine;
	}

	/**
	 * @return the userAccount
	 */
	public UserAccount getUserAccount() {
		return userAccount;
	}

	/**
	 * @param userAccount the userAccount to set
	 */
	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
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
	
	public InvoiceLineTaxModeEnum getTaxMode() {
        return taxMode;
    }

    public void setTaxMode(InvoiceLineTaxModeEnum taxMode) {
        this.taxMode = taxMode;
    }

	public BigDecimal getTransactionalUnitPrice() {
		return transactionalUnitPrice;
	}

	public void setTransactionalUnitPrice(BigDecimal transactionalUnitPrice) {
		this.transactionalUnitPrice = transactionalUnitPrice;
	}

	public BigDecimal getTransactionalAmountWithoutTax() {
		return transactionalAmountWithoutTax;
	}

	public void setTransactionalAmountWithoutTax(BigDecimal transactionalAmountWithoutTax) {
		this.transactionalAmountWithoutTax = transactionalAmountWithoutTax;
	}

	public BigDecimal getTransactionalAmountWithTax() {
		return transactionalAmountWithTax;
	}

	public void setTransactionalAmountWithTax(BigDecimal transactionalAmountWithTax) {
		this.transactionalAmountWithTax = transactionalAmountWithTax;
	}

	public BigDecimal getTransactionalAmountTax() {
		return transactionalAmountTax;
	}

	public void setTransactionalAmountTax(BigDecimal transactionalAmountTax) {
		this.transactionalAmountTax = transactionalAmountTax;
	}

	public BigDecimal getTransactionalDiscountAmount() {
		return transactionalDiscountAmount;
	}

	public void setTransactionalDiscountAmount(BigDecimal transactionalDiscountAmount) {
		this.transactionalDiscountAmount = transactionalDiscountAmount;
	}

	public BigDecimal getTransactionalRawAmount() {
		return transactionalRawAmount;
	}

	public void setTransactionalRawAmount(BigDecimal transactionalRawAmount) {
		this.transactionalRawAmount = transactionalRawAmount;
	}

	public String getOpenOrderNumber() {
		return openOrderNumber;
	}

	public void setOpenOrderNumber(String openOrderNumber) {
		this.openOrderNumber = openOrderNumber;
	}
	
	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public AdjustmentStatusEnum getAdjustmentStatus() {
		return adjustmentStatus;
	}

	public void setAdjustmentStatus(AdjustmentStatusEnum adjustmentStatus) {
		this.adjustmentStatus = adjustmentStatus;
	}

    public boolean isUseSpecificPriceConversion() {
        return useSpecificPriceConversion;
    }

    public void setUseSpecificPriceConversion(boolean useSpecificPriceConversion) {
        this.useSpecificPriceConversion = useSpecificPriceConversion;
    }

	public boolean isConversionFromBillingCurrency() {
		return conversionFromBillingCurrency;
	}

	public void setConversionFromBillingCurrency(boolean conversionFromBillingCurrency) {
		this.conversionFromBillingCurrency = conversionFromBillingCurrency;
	}   
    
	@PrePersist
	@PreUpdate
	public void prePersistOrUpdate() {
		BigDecimal appliedRate = this.invoice != null ? this.invoice.getAppliedRate() : ONE;
		Integer decimalPalces = this.invoice != null ? this.invoice.getTradingCurrency().getDecimalPlaces() : BaseEntity.NB_DECIMALS;
		if (this.transactionalUnitPrice == null || (!this.useSpecificPriceConversion && !this.conversionFromBillingCurrency)) {
			setTransactionalAmountWithoutTax(toTransactional(amountWithoutTax, appliedRate, decimalPalces));
			setTransactionalAmountWithTax(toTransactional(amountWithTax, appliedRate, decimalPalces));
			setTransactionalAmountTax(toTransactional(amountTax, appliedRate, decimalPalces));
			setTransactionalDiscountAmount(toTransactional(discountAmount, appliedRate, decimalPalces));
			setTransactionalRawAmount(toTransactional(rawAmount, appliedRate, decimalPalces));
			setTransactionalUnitPrice(toTransactional(unitPrice, appliedRate, decimalPalces));
		} else if (this.useSpecificPriceConversion) {
			setAmountWithoutTax(toFunctional(transactionalAmountWithoutTax, appliedRate));
			setAmountWithTax(toFunctional(transactionalAmountWithTax, appliedRate));
			setAmountTax(toFunctional(transactionalAmountTax, appliedRate));
			setDiscountAmount(toFunctional(transactionalDiscountAmount, appliedRate));
			setRawAmount(toFunctional(transactionalRawAmount, appliedRate));
			setUnitPrice(toFunctional(transactionalUnitPrice, appliedRate));
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (!(obj instanceof InvoiceLine)) {
			return false;
		}

		InvoiceLine other = (InvoiceLine) obj;
		return getId() != null && other.getId() != null && getId().equals(other.getId());
	}
	
	@Override
	public int hashCode() {
		return 961 + ("InvoiceLine" + getId()).hashCode();
	}
	
	private BigDecimal toTransactional(BigDecimal amount, BigDecimal rate, Integer decimalPlaces) {
		return amount != null ? amount.multiply(rate).setScale(decimalPlaces, RoundingMode.HALF_UP) : ZERO;
	}

	private BigDecimal toFunctional(BigDecimal amount, BigDecimal rate) {
		return amount != null ? amount.divide(rate, BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP) : ZERO;
	}

}
