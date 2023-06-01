package org.meveo.service.billing.impl;

import static java.util.Arrays.asList;
import static org.meveo.model.billing.BillingEntityTypeEnum.BILLINGACCOUNT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.AggregationConfiguration;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.IBillableEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.DateAggregationOption;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.Subscription;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.crm.Provider;
import org.meveo.model.order.Order;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.util.ApplicationProvider;

public class InvoiceLineAggregationService implements Serializable {

    private static final long serialVersionUID = 4394465445595777997L;

    private static final String QUERY_FILTER = "a.status = 'OPEN' AND :firstTransactionDate <= a.usageDate AND (a.invoicingDate is NULL or a.invoicingDate < :invoiceUpToDate) AND a.accountingArticle.ignoreAggregation = false ";

    @Inject
    @Named
    private NativePersistenceService nativePersistenceService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    public String getAggregationQuery(AggregationConfiguration aggregationConfiguration, BillingRun billingRun, Class<? extends IBillableEntity> billableEntityClass) {

        BillingCycle billingCycle = billingRun.getBillingCycle();
        Map<String, Object> filter = billingRun.getBillingCycle() != null ? billingRun.getBillingCycle().getFilters() : billingRun.getFilters();
        if (filter == null && billingRun.getBillingCycle() != null) {
            filter = new HashMap<>();
            filter.put("billingAccount.billingCycle.id", billingRun.getBillingCycle().getId());
        }
        if (filter == null) {
            throw new BusinessException("No filter found for billingRun " + billingRun.getId());
        }
        
        if (billingCycle != null && !billingCycle.isDisableAggregation()) {
            aggregationConfiguration = new AggregationConfiguration(billingCycle);
        }
        if (aggregationConfiguration != null) {
            String usageDateAggregation = getUsageDateAggregation(aggregationConfiguration.getDateAggregationOption());
            String unitAmount = appProvider.isEntreprise() ? "unitAmountWithoutTax" : "unitAmountWithTax";
            String unitAmountField = aggregationConfiguration.isAggregationPerUnitAmount() ? "SUM(a.unitAmountWithoutTax)" : unitAmount;

            boolean incrementalInvoiceLines = billingRun.getIncrementalInvoiceLines();
            List<String> fieldToFetch = buildFieldList(usageDateAggregation, unitAmountField, aggregationConfiguration.isIgnoreSubscriptions(), aggregationConfiguration.isIgnoreOrders(), true,
                aggregationConfiguration.isUseAccountingArticleLabel(), aggregationConfiguration.getType(), incrementalInvoiceLines);

            Map<String, String> mapToInvoiceLineTable = buildMapToInvoiceLineTable(aggregationConfiguration);
            String query = buildFetchQuery(new PaginationConfiguration(filter, fieldToFetch, mapToInvoiceLineTable.keySet()), getEntityCondition(billableEntityClass), billingRun.getLastTransactionDate(),
                incrementalInvoiceLines, mapToInvoiceLineTable, billingRun.getId());

            if (incrementalInvoiceLines) {
                query = query.replace("a.ivl.", "ivl.");
                query = query + ", ivl.id";
            }

            return query;

        } else {
            List<String> fieldToFetch = buildFieldList(null, null, aggregationConfiguration.isIgnoreSubscriptions(), aggregationConfiguration.isIgnoreOrders(), false,
                aggregationConfiguration.isUseAccountingArticleLabel(), aggregationConfiguration.getType(), billingRun.getIncrementalInvoiceLines());
            String query = buildFetchQuery(new PaginationConfiguration(filter, fieldToFetch, null), getEntityCondition(billableEntityClass), billingRun.getLastTransactionDate(), billingRun.getIncrementalInvoiceLines(),
                null, billingRun.getId());

            return query;
        }
    }

    private String getEntityCondition(Class<? extends IBillableEntity> billableEntityClass) {
        String entityCondition = "";
        if (billableEntityClass.equals(Subscription.class)) {
            entityCondition = " a.subscription.id= :subscriptionId";
        } else if (billableEntityClass.equals(BillingAccount.class)) {
            entityCondition = " a.billingAccount.id = :baId";
        } else if (billableEntityClass.equals(Order.class)) {
            entityCondition = " a.orderNumber = :orderNumber";
        } else if (billableEntityClass.equals(CommercialOrder.class)) {
            entityCondition = " a.orderNumber = :orderNumber";
        }
        return entityCondition;
    }

    private String getUsageDateAggregation(DateAggregationOption dateAggregationOption) {
        return getUsageDateAggregation(dateAggregationOption, "usageDate ");
    }

    private String getUsageDateAggregation(DateAggregationOption dateAggregationOption, String usageDateColumn) {
        return this.getUsageDateAggregation(dateAggregationOption, usageDateColumn, "a");
    }

    private String getUsageDateAggregation(DateAggregationOption dateAggregationOption, String usageDateColumn, String alias) {
        switch (dateAggregationOption) {
        case MONTH_OF_USAGE_DATE:
            return " TO_CHAR(" + alias + "." + usageDateColumn + ", 'YYYY-MM') ";
        case DAY_OF_USAGE_DATE:
            return " TO_CHAR(" + alias + "." + usageDateColumn + ", 'YYYY-MM-DD') ";
        case WEEK_OF_USAGE_DATE:
            return " TO_CHAR(" + alias + "." + usageDateColumn + ", 'YYYY-WW') ";
        case NO_DATE_AGGREGATION:
            return usageDateColumn;
        }
        return usageDateColumn;
    }

    private List<String> buildFieldList(String usageDateAggregation, String unitAmountField, boolean ignoreSubscription, boolean ignoreOrder, boolean withAggregation, boolean useAccountingArticleLabel,
            BillingEntityTypeEnum type, boolean incrementalInvoiceLines) {

        List<String> fieldToFetch;
        if (withAggregation) {
            fieldToFetch = new ArrayList<>(asList("string_agg_long(a.id) as rated_transaction_ids", "billingAccount.id as billing_account__id", "SUM(a.quantity) as quantity",
                unitAmountField + " as unit_amount_without_tax", "SUM(a.amountWithoutTax) as sum_without_tax", "SUM(a.amountWithTax) as sum_with_tax", "offerTemplate.id as offer_id",
                usageDateAggregation + " as usage_date", "min(a.startDate) as start_date", "max(a.endDate) as end_date", "taxPercent as tax_percent", "tax.id as tax_id",
                "infoOrder.productVersion.id as product_version_id", "accountingArticle.id as article_id", "discountedRatedTransaction as discounted_ratedtransaction_id"));
        } else {
            fieldToFetch = new ArrayList<>(asList("CAST(a.id as string) as rated_transaction_ids", "billingAccount.id as billing_account__id", "description as label", "quantity AS quantity",
                "amountWithoutTax as sum_without_tax", "amountWithTax as sum_with_tax", "offerTemplate.id as offer_id", "serviceInstance.id as service_instance_id", "startDate as start_date", "endDate as end_date",
                "orderNumber as order_number", "taxPercent as tax_percent", "tax.id as tax_id", "infoOrder.order.id as order_id", "infoOrder.productVersion.id as product_version_id",
                "infoOrder.orderLot.id as order_lot_id", "chargeInstance.id as charge_instance_id", "accountingArticle.id as article_id", "discountedRatedTransaction as discounted_ratedtransaction_id"));
        }

        if (incrementalInvoiceLines) {
            fieldToFetch.add("ivl.id as invoice_line_id");
            fieldToFetch.add("ivl.amountWithoutTax as amount_without_tax");
            fieldToFetch.add("ivl.amountWithTax as amount_with_tax");
            fieldToFetch.add("ivl.taxRate as tax_rate");
            fieldToFetch.add("ivl.quantity as accumulated_quantity");
            fieldToFetch.add("ivl.validity.from as begin_date");
            fieldToFetch.add("ivl.validity.to as end_date");
        }

        if (BILLINGACCOUNT != type || !ignoreSubscription) {
            fieldToFetch.add("subscription.id as subscription_id");
            fieldToFetch.add("serviceInstance.id as service_instance_id");
        }
        if (!ignoreOrder) {
            fieldToFetch.add("subscription.order.id as commercial_order_id");
            fieldToFetch.add("orderNumber as order_number");
            fieldToFetch.add("infoOrder.order.id as order_id");
        }
        if (!useAccountingArticleLabel) {
            fieldToFetch.add("description as label");
        }
        return fieldToFetch;
    }

    private String buildFetchQuery(PaginationConfiguration searchConfig, String entityCondition, Date lastTransactionDate, boolean incrementalInvoiceLines, Map<String, String> mapToInvoiceLineTable, Long billingRunId) {
        String extraCondition = entityCondition + (lastTransactionDate != null ? " AND a.usageDate < :lastTransactionDate AND " : " AND ") + QUERY_FILTER;

        StringBuilder leftJoinClauseBd = new StringBuilder();
        if (incrementalInvoiceLines) {
            String aliasInvoiceLineTable = "ivl";
            leftJoinClauseBd.append("LEFT JOIN InvoiceLine ").append(aliasInvoiceLineTable).append(" ON ");
            Iterator<String> itr = searchConfig.getGroupBy().iterator();
            String groupByInRT;
            String leftJoinInIL = "";
            String testNullCondition = "";
            while (itr.hasNext()) {
                groupByInRT = itr.next();
                leftJoinInIL = mapToInvoiceLineTable.get(groupByInRT);

                if (checkAggFunctions(groupByInRT.toUpperCase().trim())) {
                    testNullCondition = groupByInRT + " IS NULL AND " + leftJoinInIL + " IS NULL OR ";

                    leftJoinClauseBd.append("(").append(testNullCondition).append(groupByInRT).append("=").append(leftJoinInIL).append(")");
                } else {
                    testNullCondition = "a." + groupByInRT + " IS NULL AND " + aliasInvoiceLineTable + "." + leftJoinInIL + " IS NULL OR ";

                    if (groupByInRT.equals("description")) {
                        leftJoinClauseBd.append("(").append(testNullCondition).append("a.description=ivl.label OR a.accountingArticle.description=ivl.label)");
                    } else if (groupByInRT.equals("taxPercent")) {
                        leftJoinClauseBd.append("(").append(testNullCondition).append("a.taxPercent=ivl.taxRate OR a.tax.percent=ivl.taxRate)");
                    } else {
                        leftJoinClauseBd.append("(").append(testNullCondition).append("a.").append(groupByInRT).append("=").append(aliasInvoiceLineTable).append(".").append(leftJoinInIL).append(")");
                    }
                }

                if (itr.hasNext())
                    leftJoinClauseBd.append(" AND ");
            }

            leftJoinClauseBd.append("AND ivl.billingRun.id = ").append(billingRunId).append(" ");
        }

        QueryBuilder queryBuilder = nativePersistenceService.getAggregateQuery("RatedTransaction", searchConfig, null, extraCondition, leftJoinClauseBd.toString());
        return queryBuilder.getQueryAsString();
    }

    private Map<String, String> buildMapToInvoiceLineTable(AggregationConfiguration aggregationConfiguration) {
        Map<String, String> mapToInvoiceLineTable = new HashMap<>() {
            {
                put("billingAccount.id", "billingAccount.id");
                put("offerTemplate", "offerTemplate");
                put("taxPercent", "taxRate");
                put("tax.id", "tax.id");
                put("infoOrder.productVersion.id", "productVersion.id");
                put("accountingArticle.id", "accountingArticle.id");
                put("discountedRatedTransaction", "discountedInvoiceLine");
            }
        };

        String usageDateAggregation = getUsageDateAggregation(aggregationConfiguration.getDateAggregationOption());
        mapToInvoiceLineTable.put(usageDateAggregation, usageDateAggregation.replace("a.", "ivl.").replace("usageDate", "valueDate"));

        boolean ignoreSubscription = BILLINGACCOUNT == aggregationConfiguration.getType() && aggregationConfiguration.isIgnoreSubscriptions();
        if (!ignoreSubscription) {
            mapToInvoiceLineTable.put("subscription.id", "subscription.id");
            mapToInvoiceLineTable.put("serviceInstance", "serviceInstance");
        }

        if (!aggregationConfiguration.isIgnoreOrders()) {
            mapToInvoiceLineTable.put("subscription.order.id", "subscription.order.id");
            mapToInvoiceLineTable.put("infoOrder.order.id", "commercialOrder.id");
            mapToInvoiceLineTable.put("orderNumber", "orderNumber");
        }

        if (!aggregationConfiguration.isAggregationPerUnitAmount()) {
            if (appProvider.isEntreprise()) {
                mapToInvoiceLineTable.put("unitAmountWithoutTax", "unitPrice");
            } else {
                mapToInvoiceLineTable.put("unitAmountWithTax", "unitPrice");
            }
        }

        if (!aggregationConfiguration.isUseAccountingArticleLabel()) {
            mapToInvoiceLineTable.put("description", "label");
        }

        return mapToInvoiceLineTable;
    }

    private boolean checkAggFunctions(String field) {
        return field.startsWith("SUM(") || field.startsWith("COUNT(") || field.startsWith("AVG(") || field.startsWith("MAX(") || field.startsWith("MIN(") || field.startsWith("COALESCE(SUM(")
                || field.startsWith("STRING_AGG_LONG") || field.startsWith("TO_CHAR(") || field.startsWith("CAST(");
    }
}