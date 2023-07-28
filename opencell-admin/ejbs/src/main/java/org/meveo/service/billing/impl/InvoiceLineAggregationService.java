package org.meveo.service.billing.impl;

import static java.util.Arrays.asList;
import static org.meveo.model.billing.BillingEntityTypeEnum.BILLINGACCOUNT;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.query.Query;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.AggregationConfiguration;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.DateAggregationOption;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.base.PersistenceService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvoiceLineAggregationService implements Serializable {

    private static final long serialVersionUID = 4394465445595777997L;

    private static final String QUERY_FILTER = "a.status = 'OPEN' AND (a.invoicingDate is NULL or a.invoicingDate < :invoiceUpToDate) AND a.accountingArticle.ignoreAggregation = false ";

    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(InvoiceLineAggregationService.class);

    @Inject
    @Named
    private NativePersistenceService nativePersistenceService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    /**
     * Create a query object for IL aggregation lookup
     * 
     * @param aggregationQuery Aggregation query
     * @param billingRun Billing run
     * @param statelessSession Stateless session for query creation
     * @param maxId Max RT id to lookup
     * @param incrementalInvoiceLines Shall Invoice lines be created in incremental mode
     * @param aggregationConfiguration
     * @return A hibernate query
     */
    @SuppressWarnings("rawtypes")
    public RTtoILAggregationQuery getILDetailsQuery(BillingRun billingRun, AggregationConfiguration aggregationConfiguration, StatelessSession statelessSession, Long maxId, boolean incrementalInvoiceLines) {

        // Get a basic RT to IL aggregation query

        BillingCycle billingCycle = billingRun.getBillingCycle();
        Map<String, Object> bcFilter = billingCycle != null ? billingCycle.getFilters() : billingRun.getFilters();
        if (bcFilter == null && billingRun.getBillingCycle() != null) {
            bcFilter = new HashMap<>();
            bcFilter.put("billingAccount.billingCycle.id", billingRun.getBillingCycle().getId());
        }
        if (bcFilter == null) {
            throw new BusinessException("No filter found for billingRun " + billingRun.getId());
        }

        String aggregationQuery = getAggregationJPAQuery(aggregationConfiguration, billingRun, bcFilter);
        List<String> aggregationFields = parseQueryFieldNames(aggregationQuery);

        EntityManager em = emWrapper.getEntityManager();

        // Pass parameters to the aggregation query
        Map<String, Object> params = new HashMap<String, Object>();
        // params.put("firstTransactionDate", new Date(0));
        if (billingRun.getLastTransactionDate() != null) {
            params.put("lastTransactionDate", billingRun.getLastTransactionDate());
        }
        params.put("invoiceUpToDate", billingRun.getInvoiceDate());
        params.put("maxId", maxId);

        // In incremental mode aggregated RT information is written to a materialized view and then joined with a IL table
        if (incrementalInvoiceLines) {

            String sql = PersistenceService.getNativeQueryFromJPA(em.createQuery(aggregationQuery), params);

            Session hibernateSession = em.unwrap(Session.class);

            String viewName = getMaterializedAggregationViewName(billingRun.getId());
            String materializedViewFields = StringUtils.concatenate(",", aggregationFields);
            hibernateSession.doWork(new org.hibernate.jdbc.Work() {

                @Override
                public void execute(Connection connection) throws SQLException {

                    try (Statement statement = connection.createStatement()) {
                        log.info("Dropping and rereating materialized view {} with fields {} and request {}: ", viewName, materializedViewFields, sql);
                        statement.execute("drop materialized view if exists " + viewName);
                        statement.execute("create materialized view " + viewName + "(" + materializedViewFields + ") as " + sql);
                        statement.execute("create index idx__" + viewName + " ON " + viewName + " USING btree (billing_account__id, offer_id, article_id, tax_id) ");
                    } catch (Exception e) {
                        log.error("Failed to drop/create the materialized view " + viewName, e.getMessage());
                        throw new BusinessException(e);
                    }
                }
            });

            Long ilCount = ((BigInteger) em.createNativeQuery("select count(*) from " + InvoiceLineAggregationService.getMaterializedAggregationViewName(billingRun.getId())).getSingleResult()).longValue();

            aggregationQuery = buildJoinWithILQuery(billingRun.getId(), aggregationFields, aggregationConfiguration);
            aggregationFields = parseQueryFieldNames(aggregationQuery);

            org.hibernate.query.Query query = statelessSession.createNativeQuery(aggregationQuery);

            return new RTtoILAggregationQuery(query, aggregationFields, ilCount);

            // A simple JPA query
        } else {

            String sql = PersistenceService.getNativeQueryFromJPA(em.createQuery(aggregationQuery), params);
            sql = sql.substring(0, sql.toLowerCase().indexOf(" order by "));
            sql = "select count(*) from (select billing_account__id " + sql.substring(sql.toLowerCase().indexOf("from")) + ") as b";

            Long ilCount = ((BigInteger) em.createNativeQuery(sql).getSingleResult()).longValue();

            org.hibernate.query.Query query = statelessSession.createQuery(aggregationQuery);
            for (Entry<String, Object> paramInfo : params.entrySet()) {
                if (paramInfo.getValue() != null && paramInfo.getValue().getClass().isEnum()) {
                    query.setParameter(paramInfo.getKey(), paramInfo.getValue().toString());
                } else {
                    query.setParameter(paramInfo.getKey(), paramInfo.getValue());
                }
            }

            return new RTtoILAggregationQuery(query, aggregationFields, ilCount);
        }
    }

    /**
     * Get Rated transaction to Invoice line aggregation JPA query
     * 
     * @param aggregationConfiguration Aggregation configuration
     * @param billingRun Billing run
     * @param bcFilter Additional filter of Billing cycle
     * @return A JPA query to aggregate Rated transactions
     */
    private String getAggregationJPAQuery(AggregationConfiguration aggregationConfiguration, BillingRun billingRun, Map<String, Object> bcFilter) {

        String query = buildAggregationQuery(billingRun, aggregationConfiguration, bcFilter);

        query = "select " + billingRun.getId() + " as billing_run_id, " + query.substring(query.toLowerCase().indexOf("select") + 6);

        return query;
    }

    private String getUsageDateAggregationFunction(DateAggregationOption dateAggregationOption, String usageDateColumn, String alias) {
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

    /**
     * Get a list of fields to retrieve
     * 
     * @param billingRun Billing run
     * @param usageDateAggregationFunction Usage date aggregation function
     * @param unitAmountAggregationFunction Unit amount field aggregation function
     * @param doNotAggregateBySubscription True if no aggregation should be done by subscription
     * @param doNotAggregateByOrder True if no aggregation should be done by order
     * @param withAggregation Apply aggregation. If False, one RatedTransaction will result in one InvoiceLine
     * @param aggregateByDescription Aggregate by description
     * @param type Billing entity type
     * @return A list of fields
     */
    private List<String> buildAggregationFieldList(String usageDateAggregationFunction, String unitAmountAggregationFunction, boolean doNotAggregateBySubscription, boolean doNotAggregateByOrder, boolean withAggregation,
            boolean useAccountingArticleLabel, BillingEntityTypeEnum type) {

        List<String> fieldToFetch;
        if (withAggregation) {
            fieldToFetch = new ArrayList<>(
                asList("string_agg_long(a.id) as rated_transaction_ids", "billingAccount.id as billing_account__id", "SUM(a.quantity) as quantity", unitAmountAggregationFunction + " as unit_amount_without_tax",
                    "SUM(a.amountWithoutTax) as sum_without_tax", "SUM(a.amountWithTax) as sum_with_tax", "offerTemplate.id as offer_id", usageDateAggregationFunction + " as usage_date", "min(a.startDate) as start_date",
                    "max(a.endDate) as end_date", "taxPercent as tax_percent", "tax.id as tax_id", "infoOrder.productVersion.id as product_version_id", "accountingArticle.id as article_id",
                    "discountedRatedTransaction as discounted_ratedtransaction_id", "discountPlanType as discount_plan_type", "discountValue as discount_value", "count(a.id) as rt_count"));

            if (!doNotAggregateByOrder) {
                fieldToFetch.add("infoOrder.order.id as commercial_order_id");
                fieldToFetch.add("orderNumber as order_number");
                fieldToFetch.add("infoOrder.order.id as order_id");
            }
            if (useAccountingArticleLabel) {
                fieldToFetch.add("accountingArticle.description as label");
            } else {
                fieldToFetch.add("description as label");
            }

        } else {
            fieldToFetch = new ArrayList<>(
                asList("CAST(a.id as string) as rated_transaction_ids", "billingAccount.id as billing_account__id", "description as label", "quantity AS quantity", "amountWithoutTax as sum_without_tax",
                    "amountWithTax as sum_with_tax", "offerTemplate.id as offer_id", "serviceInstance.id as service_instance_id", "startDate as start_date", "endDate as end_date", "orderNumber as order_number",
                    "infoOrder.order.id as commercial_order_id", "taxPercent as tax_percent", "tax.id as tax_id", "infoOrder.order.id as order_id", "infoOrder.productVersion.id as product_version_id",
                    "infoOrder.orderLot.id as order_lot_id", "chargeInstance.id as charge_instance_id", "accountingArticle.id as article_id", "discountedRatedTransaction as discounted_ratedtransaction_id"));
        }

        if (BILLINGACCOUNT != type || !doNotAggregateBySubscription) {
            fieldToFetch.add("subscription.id as subscription_id");
            fieldToFetch.add("serviceInstance.id as service_instance_id");
        }

        return fieldToFetch;
    }

    /**
     * Construct RT to IL aggregation query
     * 
     * @param billingRun Billing run
     * @param aggregationConfiguration
     * @param bcFilter Additional filters of billing cycle
     * @return JPA query
     */
    private String buildAggregationQuery(BillingRun billingRun, AggregationConfiguration aggregationConfiguration, Map<String, Object> bcFilter) {

        String usageDateAggregationFunction = getUsageDateAggregationFunction(aggregationConfiguration.getDateAggregationOption(), "usageDate", "a");
        String unitAmount = appProvider.isEntreprise() ? "unitAmountWithoutTax" : "unitAmountWithTax";
        String unitAmountAggregationFunction = aggregationConfiguration.isAggregationPerUnitAmount() ? "SUM(a.unitAmountWithoutTax)" : unitAmount;

        List<String> fieldToFetch = buildAggregationFieldList(usageDateAggregationFunction, unitAmountAggregationFunction, aggregationConfiguration.isIgnoreSubscriptions(), aggregationConfiguration.isIgnoreOrders(),
            true, aggregationConfiguration.isUseAccountingArticleLabel(), aggregationConfiguration.getType());

        Set<String> groupBy = getAggregationQueryGroupBy(aggregationConfiguration);

        PaginationConfiguration searchConfig = new PaginationConfiguration(null, null, bcFilter, null, fieldToFetch, groupBy, (Set<String>) null, "billingAccount.id", SortOrder.ASCENDING);

        String extraCondition = (billingRun.getLastTransactionDate() != null ? " a.usageDate < :lastTransactionDate and a.id<=:maxId and " : " and a.id<:maxId and ") + QUERY_FILTER;

        QueryBuilder queryBuilder = nativePersistenceService.getAggregateQuery("RatedTransaction", searchConfig, null, extraCondition, null);
        return queryBuilder.getQueryAsString();
    }

    private String buildJoinWithILQuery(Long billingRunId, List<String> aggregationFields, AggregationConfiguration aggregationConfiguration) {

        StringBuilder sql = new StringBuilder("select ");
        String viewName = getMaterializedAggregationViewName(billingRunId);
        for (String field : aggregationFields) {
            sql.append("agr.").append(field).append(" as ").append(field).append(',');
        }
        sql.append(
            "ivl.id as invoice_line_id, ivl.amount_without_tax as amount_without_tax, ivl.amount_with_tax as amount_with_tax, ivl.tax_rate as tax_rate,ivl.quantity as accumulated_quantity,ivl.begin_date as begin_date,ivl.end_date as finish_date,ivl.unit_price as unit_price ")
            .append(" from ").append(viewName).append(" agr LEFT JOIN billing_invoice_line ivl ON ivl.billing_run_id=").append(billingRunId);

        Map<String, String> joinCriteria = getIncrementalJoinCriteria(aggregationConfiguration);

        for (String joinField : joinCriteria.keySet()) {
            if (aggregationFields.contains(joinField)) {
                sql.append(" and ").append(joinCriteria.get(joinField));
            }
        }

        sql.append(" and agr.discounted_ratedtransaction_id is null and agr.discount_value is null and ivl.discount_value is null and ivl.discounted_invoice_line is null and ivl.status='OPEN'");

        sql.append(" ORDER BY agr.billing_account__id");
        return sql.toString();

    }

    private Set<String> getAggregationQueryGroupBy(AggregationConfiguration aggregationConfiguration) {

        Set<String> groupBy = new LinkedHashSet<String>();
        groupBy.add("billingAccount.id");
        groupBy.add("offerTemplate");
        groupBy.add("accountingArticle.id");
        groupBy.add("tax.id");
        groupBy.add("taxPercent");
        groupBy.add("infoOrder.productVersion.id");
        groupBy.add("discountedRatedTransaction");
        groupBy.add("discountValue");
        groupBy.add("discountPlanType");
        if (aggregationConfiguration.getType() == BillingEntityTypeEnum.ORDER) {
            groupBy.add("orderNumber");
        } else if (aggregationConfiguration.getType() == BillingEntityTypeEnum.SUBSCRIPTION) {
            groupBy.add("subscription.id");
        }

        String usageDateAggregationFunction = getUsageDateAggregationFunction(aggregationConfiguration.getDateAggregationOption(), "usageDate", "a");
        groupBy.add(usageDateAggregationFunction);

        if (!(BILLINGACCOUNT == aggregationConfiguration.getType() && aggregationConfiguration.isIgnoreSubscriptions())) {
            groupBy.add("subscription.id");
            groupBy.add("serviceInstance");
        }

        if (!aggregationConfiguration.isIgnoreOrders()) {
            groupBy.add("infoOrder.order.id");
            groupBy.add("orderNumber");
        }

        if (!aggregationConfiguration.isAggregationPerUnitAmount()) {
            if (appProvider.isEntreprise()) {
                groupBy.add("unitAmountWithoutTax");
            } else {
                groupBy.add("unitAmountWithTax");
            }
        }

        if (aggregationConfiguration.isUseAccountingArticleLabel()) {
            groupBy.add("accountingArticle.description");
        } else {
            groupBy.add("description");
        }

        return groupBy;
    }

    /**
     * Get a list of join fields between RT aggregation materialized view and IL table
     * 
     * @param aggregationConfiguration Aggregation configuration
     * @return
     */
    private Map<String, String> getIncrementalJoinCriteria(AggregationConfiguration aggregationConfiguration) {

        // DO NOT Change the order - indexes are build according to the order
        Map<String, String> mapToInvoiceLineTable = new LinkedHashMap<>();

        mapToInvoiceLineTable.put("billing_account__id", "agr.billing_account__id = ivl.billing_account_id");
        mapToInvoiceLineTable.put("offer_id", "agr.offer_id = ivl.offer_template_id");
        mapToInvoiceLineTable.put("article_id", "agr.article_id = ivl.accounting_article_id");
        mapToInvoiceLineTable.put("tax_id", "agr.tax_id = ivl.tax_id");
        mapToInvoiceLineTable.put("tax_percent", "agr.tax_percent =  tax_rate");
        mapToInvoiceLineTable.put("product_version_id", "((agr.product_version_id is null and ivl.product_version_id is null) or agr.product_version_id = ivl.product_version_id)");

        String usageDateAggregation = getUsageDateAggregationFunction(aggregationConfiguration.getDateAggregationOption(), "value_date", "ivl");
        mapToInvoiceLineTable.put("usage_date", "((agr.usage_date is null and ivl.value_date is null) or  agr.usage_date =" + usageDateAggregation + ")");

        mapToInvoiceLineTable.put("subscription_id", "((agr.subscription_id is null and  ivl.subscription_id is null) or agr.subscription_id = ivl.subscription_id)");
        mapToInvoiceLineTable.put("service_instance_id", "((agr.service_instance_id is null and ivl.service_instance_id is null) or agr.service_instance_id = ivl.service_instance_id)");
        mapToInvoiceLineTable.put("order_id", "((agr.order_id is null and ivl.commercial_order_id is null) or agr.order_id =  ivl.commercial_order_id)");
        mapToInvoiceLineTable.put("order_number", "((agr.order_number is null and ivl.order_number is null) or agr.order_number = ivl.order_number)");
        if (appProvider.isEntreprise()) {
            mapToInvoiceLineTable.put("unit_amount_without_tax", "((agr.unit_amount_without_tax is null or ivl.unit_price is null) or agr.unit_amount_without_tax = ivl.unit_price)");
        } else {
            mapToInvoiceLineTable.put("unit_amount_with_tax", "((agr.unit_amount_with_tax is null or ivl.unit_price is null) or agr.unit_amount_with_tax = ivl.unit_price)");
        }
        mapToInvoiceLineTable.put("label", "((agr.label is null and ivl.label is null) or agr.label = ivl.label)");

        return mapToInvoiceLineTable;
    }

    /**
     * Get a Invoice line aggregation data materialized view name
     * 
     * @param billingRunId Billing run id
     * @return A materialized view name in format: "billing_il_job_&lt;billingRun Id&gt;"
     */
    public static String getMaterializedAggregationViewName(long billingRunId) {
        return "billing_il_job_" + billingRunId;
    }

    /**
     * Get a list of fieldnames from a query
     * 
     * @param query Sql query
     * @return
     */
    private static List<String> parseQueryFieldNames(String query) {

        List<String> fieldNames = new ArrayList<String>();

        // Get only field part
        query = query.substring(0, query.toLowerCase().indexOf(" from"));

        Pattern pattern = Pattern.compile(" as (\\w*)[ ,]?");
        Matcher matcher = pattern.matcher(query);

        while (matcher.find()) {
            fieldNames.add(matcher.group(1));
        }

        return fieldNames;
    }

    /**
     * A query for aggregate RTs to IL
     */
    public class RTtoILAggregationQuery {

        @SuppressWarnings("rawtypes")
        private org.hibernate.query.Query query;
        private List<String> fieldNames;
        private Long numberOfIL;

        /**
         * Constructor
         * 
         * @param query Aggregation query
         * @param fieldNames Query fieldnames
         * @param numberOfIL Number of Invoices lines that will result in aggregation
         */
        @SuppressWarnings("rawtypes")
        public RTtoILAggregationQuery(Query query, List<String> fieldNames, Long numberOfIL) {
            this.query = query;
            this.fieldNames = fieldNames;
            this.numberOfIL = numberOfIL;
        }

        /**
         * 
         * @return
         */
        @SuppressWarnings("rawtypes")
        public org.hibernate.query.Query getQuery() {
            return query;
        }

        /**
         * @return Query fieldnames
         */
        public List<String> getFieldNames() {
            return fieldNames;
        }

        /**
         * @return Number of Invoices lines that will result in aggregation
         */
        public Long getNumberOfIL() {
            return numberOfIL;
        }
    }
}