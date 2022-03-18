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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.hibernate.Session;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.EntityManagerProvider;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.crm.Provider;
import org.meveo.model.dwh.GdprConfiguration;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobExecutionResultStatusEnum;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 */
@Stateless
public class GDPRJobBean extends BaseJobBean {

    private static final long serialVersionUID = -6589293878357797288L;

    @Inject
    private Logger log;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    @Inject
    private SubscriptionService subscriptionService;

    @EJB
    private GDPRJobBean thisNewTx;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, String parameter) {

        GdprConfiguration gdprConfiguration = appProvider.getGdprConfiguration();

        if (gdprConfiguration == null) {
            jobExecutionResult.addReport("GDPR Config isn't set yet");
            return;
        }

        try {
            if (gdprConfiguration.isDeleteSubscription()) {
                long count = thisNewTx.removeSubscriptions(gdprConfiguration, jobExecutionResult);
                jobExecutionResult.addNbItemsCorrectlyProcessed(count);
                jobExecutionResult.close();
                jobExecutionResult.setStatus(JobExecutionResultStatusEnum.COMPLETED);
                jobExecutionResultService.persistResult(jobExecutionResult);

                jobExecutionResult = new JobExecutionResultImpl(jobExecutionResult.getJobInstance(), jobExecutionResult.getJobLauncherEnum());
            }

            if (gdprConfiguration.isDeleteOrder()) {
                long count = thisNewTx.removeOrders(gdprConfiguration, jobExecutionResult);
                jobExecutionResult.addNbItemsCorrectlyProcessed(count);
                jobExecutionResult.close();
                jobExecutionResult.setStatus(JobExecutionResultStatusEnum.COMPLETED);
                jobExecutionResultService.persistResult(jobExecutionResult);

                jobExecutionResult = new JobExecutionResultImpl(jobExecutionResult.getJobInstance(), jobExecutionResult.getJobLauncherEnum());
            }

            if (gdprConfiguration.isDeleteInvoice()) {
                long count = thisNewTx.removeInvoices(gdprConfiguration, jobExecutionResult);
                jobExecutionResult.addNbItemsCorrectlyProcessed(count);
                jobExecutionResult.close();
                jobExecutionResult.setStatus(JobExecutionResultStatusEnum.COMPLETED);
                jobExecutionResultService.persistResult(jobExecutionResult);
                jobExecutionResult = new JobExecutionResultImpl(jobExecutionResult.getJobInstance(), jobExecutionResult.getJobLauncherEnum());
            }

            if (gdprConfiguration.isDeleteAccounting() || gdprConfiguration.isDeleteAoCheckUnpaidLife()) {
                long count = thisNewTx.removeAccountOperations(gdprConfiguration, jobExecutionResult);
                jobExecutionResult.addNbItemsCorrectlyProcessed(count);
                jobExecutionResult.close();
                jobExecutionResult.setStatus(JobExecutionResultStatusEnum.COMPLETED);
                jobExecutionResultService.persistResult(jobExecutionResult);
                jobExecutionResult = new JobExecutionResultImpl(jobExecutionResult.getJobInstance(), jobExecutionResult.getJobLauncherEnum());
            }

            if (gdprConfiguration.isDeleteCustomerProspect()) {
                long count = thisNewTx.removeCustomers(gdprConfiguration, jobExecutionResult);
                jobExecutionResult.addNbItemsCorrectlyProcessed(count);
                jobExecutionResult.close();
                jobExecutionResult.setStatus(JobExecutionResultStatusEnum.COMPLETED);
                jobExecutionResultService.persistResult(jobExecutionResult);
                jobExecutionResult = new JobExecutionResultImpl(jobExecutionResult.getJobInstance(), jobExecutionResult.getJobLauncherEnum());
            }

        } catch (Exception e) {
            log.error("Failed to run GDPR data erasure job", e);
            jobExecutionResult.registerError(e.getMessage());
        }
    }

    /**
     * Remove inactive subscriptions
     *
     * @param gdprConfiguration GDPR configuration
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long removeSubscriptions(GdprConfiguration gdprConfiguration, JobExecutionResultImpl jobExecutionResult) {

        String providerCode = currentUser.getProviderCode();
        final String schemaPrefix = providerCode != null ? EntityManagerProvider.convertToSchemaName(providerCode) + "." : "";

        final long[] recordCount = new long[] { 0L };

        Session hibernateSession = subscriptionService.getEntityManager().unwrap(Session.class);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                try (Statement statement = connection.createStatement()) {
                    statement.execute("SET session_replication_role = replica");
                    statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_subscriptions");
                    statement.execute("create materialized view " + schemaPrefix + "mview_gdpr_subscriptions (id) as (select id from " + schemaPrefix
                            + "billing_subscription s where s.status not in ('CREATED', 'ACTIVE') and s.termination_date<=now() - interval '" + gdprConfiguration.getInactiveSubscriptionLife() + " year')");
                    statement.execute("create index mview_gdpr_subscriptions_pk on " + schemaPrefix + "mview_gdpr_subscriptions (id)");

                    ResultSet resultset = statement.executeQuery("select count(*) from " + schemaPrefix + "mview_gdpr_subscriptions");
                    resultset.next();
                    recordCount[0] = resultset.getLong(1);

                } catch (Exception e) {
                    log.error("Failed to remove subscriptions in GDPR", e);
                    throw new BusinessException(e);
                }
            }
        });

        jobExecutionResult.addReport("Inactive subscriptions");
        jobExecutionResult.addNbItemsToProcess(recordCount[0]);
        jobExecutionResultService.persistResult(jobExecutionResult);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                try (Statement statement = connection.createStatement()) {

                    if (recordCount[0] > 0) {

                        statement.execute("update " + schemaPrefix + "billing_rated_transaction set service_instance_id=null, subscription_id=null, charge_instance_id=null where subscription_id in (select id from "
                                + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("update " + schemaPrefix + "billing_invoice_line set subscription_id=null where subscription_id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("update " + schemaPrefix + "billing_invoice set subscription_id=null where subscription_id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("update " + schemaPrefix + "ord_order_item set subscription_id=null where subscription_id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("update " + schemaPrefix + "billing_subscription set next_version=null where next_version in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("update " + schemaPrefix + "billing_subscription set previous_version=null where previous_version in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("delete from " + schemaPrefix + "billing_wallet_operation where subscription_id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("delete from " + schemaPrefix + "billing_reservation where subscription_id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("delete from " + schemaPrefix + "ar_matching_amount where account_operation_id in (select ao.id from " + schemaPrefix + "ar_account_operation ao join " + schemaPrefix
                                + "mview_gdpr_subscriptions gs on ao.subscription_id=gs.id)");
                        statement.execute("delete from " + schemaPrefix + "ar_account_operation where subscription_id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");

                        statement.execute("delete from " + schemaPrefix + "billing_chrginst_wallet where chrg_instance_id in (select ci.id from " + schemaPrefix + "billing_charge_instance ci join " + schemaPrefix
                                + "mview_gdpr_subscriptions gs on ci.subscription_id=gs.id)");
                        statement.execute("delete from " + schemaPrefix + "billing_charge_instance where subscription_id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("delete from " + schemaPrefix + "billing_service_instance where subscription_id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("delete from " + schemaPrefix + "billing_product_instance where subscription_id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("delete from " + schemaPrefix + "medina_access where subscription_id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("delete from " + schemaPrefix + "rating_cdr where header_edr_id in (select e.id from " + schemaPrefix + "rating_edr e join " + schemaPrefix
                                + "mview_gdpr_subscriptions gs on e.subscription_id=gs.id)");
                        statement.execute("delete from " + schemaPrefix + "rating_edr where subscription_id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("delete from " + schemaPrefix + "billing_counter where subscription_id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("delete from " + schemaPrefix + "billing_discount_plan_instance where subscription_id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("delete from " + schemaPrefix + "dunning_document where billing_subscription_id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("delete from " + schemaPrefix + "cpq_attribute_instance where subscription_id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("delete from " + schemaPrefix + "billing_subscription where id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
                        statement.execute("delete from " + schemaPrefix + "audit_field_changes_history where entity_class='org.meveo.model.billing.Subscription' and id in (select id from " + schemaPrefix
                                + "mview_gdpr_subscriptions)");

                    }
                    statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_subscriptions");
                    statement.execute("SET session_replication_role = DEFAULT");

                } catch (Exception e) {
                    log.error("Failed to remove subscriptions in GDPR", e);
                    throw new BusinessException(e);
                }
            }
        });
        return recordCount[0];
    }

    /**
     * Remove old orders
     *
     * @param gdprConfiguration GDPR configuration
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long removeOrders(GdprConfiguration gdprConfiguration, JobExecutionResultImpl jobExecutionResult) {

        String providerCode = currentUser.getProviderCode();
        final String schemaPrefix = providerCode != null ? EntityManagerProvider.convertToSchemaName(providerCode) + "." : "";

        final long[] recordCount = new long[] { 0L };

        Session hibernateSession = subscriptionService.getEntityManager().unwrap(Session.class);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                try (Statement statement = connection.createStatement()) {
                    statement.execute("SET session_replication_role = replica");
                    statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_orders");
                    statement.execute("create materialized view " + schemaPrefix + "mview_gdpr_orders (id) as (select id from " + schemaPrefix + "ord_order o where o.order_date<=now() - interval '"
                            + gdprConfiguration.getInactiveOrderLife() + " year')");
                    statement.execute("create index mview_gdpr_orders_pk on " + schemaPrefix + "mview_gdpr_orders (id)");

                    ResultSet resultset = statement.executeQuery("select count(*) from " + schemaPrefix + "mview_gdpr_orders");
                    resultset.next();
                    recordCount[0] = resultset.getLong(1);

                } catch (Exception e) {
                    log.error("Failed to remove subscriptions in GDPR", e);
                    throw new BusinessException(e);
                }
            }
        });

        jobExecutionResult.addReport("Inactive orders");
        jobExecutionResult.addNbItemsToProcess(recordCount[0]);
        jobExecutionResultService.persistResult(jobExecutionResult);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                try (Statement statement = connection.createStatement()) {

                    if (recordCount[0] > 0) {

                        statement.execute("update " + schemaPrefix + "billing_invoice set order_id=null where order_id in (select id from " + schemaPrefix + "mview_gdpr_orders)");

                        statement.execute("delete from " + schemaPrefix + "order_article_line where order_id in (select co.id from " + schemaPrefix + "cpq_commercial_order co join " + schemaPrefix
                                + "mview_gdpr_orders go on co.order_parent_id=go.id)");
                        statement.execute("delete from " + schemaPrefix + "cpq_order_lot where order_id in (select co.id from " + schemaPrefix + "cpq_commercial_order co join " + schemaPrefix
                                + "mview_gdpr_orders go on co.order_parent_id=go.id)");
                        statement.execute("delete from " + schemaPrefix + "cpq_order_offer where order_id in (select co.id from " + schemaPrefix + "cpq_commercial_order co join " + schemaPrefix
                                + "mview_gdpr_orders go on co.order_parent_id=go.id)");
                        statement.execute("delete from " + schemaPrefix + "cpq_order_product where order_id in (select co.id from " + schemaPrefix + "cpq_commercial_order co join " + schemaPrefix
                                + "mview_gdpr_orders go on co.order_parent_id=go.id)");
                        statement.execute("delete from " + schemaPrefix + "cpq_commercial_order where order_parent_id in (select id from " + schemaPrefix + "mview_gdpr_orders)");

                        statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_payment_token");
                        statement.execute("create materialized view " + schemaPrefix + "mview_gdpr_payment_token (id) as (select payment_method_id from " + schemaPrefix + "ord_order o join " + schemaPrefix
                                + "mview_gdpr_orders go on o.id=go.id)");
                        statement.execute("create index mview_gdpr_payment_token_pk on " + schemaPrefix + "mview_gdpr_payment_token (id)");

                        statement.execute("delete from " + schemaPrefix + "ord_order_history where order_item_id in (select oi.id from " + schemaPrefix + "ord_order_item oi join " + schemaPrefix
                                + "mview_gdpr_orders go on oi.order_id=go.id)");
                        statement.execute("delete from " + schemaPrefix + "ord_item_offerings where order_item_id in (select oi.id from " + schemaPrefix + "ord_order_item oi join " + schemaPrefix
                                + "mview_gdpr_orders go on oi.order_id=go.id)");
                        statement.execute("delete from " + schemaPrefix + "ord_order_item where order_id in (select id from " + schemaPrefix + "mview_gdpr_orders)");
                        statement.execute("delete from " + schemaPrefix + "ord_order where id in (select id from " + schemaPrefix + "mview_gdpr_orders)");

                        statement.execute("delete from " + schemaPrefix + "ar_payment_token where id in (select id from " + schemaPrefix + "mview_gdpr_payment_token)");
                        statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_payment_token");
                    }

                    statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_orders");
                    statement.execute("SET session_replication_role = DEFAULT");

                } catch (Exception e) {
                    log.error("Failed to remove orders in GDPR", e);
                    throw new BusinessException(e);
                }
            }
        });
        return recordCount[0];
    }

    /**
     * Remove old invoices
     *
     * @param gdprConfiguration GDPR configuration
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long removeInvoices(GdprConfiguration gdprConfiguration, JobExecutionResultImpl jobExecutionResult) {

        String providerCode = currentUser.getProviderCode();
        final String schemaPrefix = providerCode != null ? EntityManagerProvider.convertToSchemaName(providerCode) + "." : "";

        final long[] recordCount = new long[] { 0L };

        Session hibernateSession = subscriptionService.getEntityManager().unwrap(Session.class);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                try (Statement statement = connection.createStatement()) {
                    statement.execute("SET session_replication_role = replica");
                    statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_invoices");
                    statement.execute("create materialized view " + schemaPrefix + "mview_gdpr_invoices (id) as (select id from " + schemaPrefix + "billing_invoice i where i.invoice_date<=now() - interval '"
                            + gdprConfiguration.getInvoiceLife() + " year')");
                    statement.execute("create index mview_gdpr_invoices_pk on " + schemaPrefix + "mview_gdpr_invoices (id)");

                    ResultSet resultset = statement.executeQuery("select count(*) from " + schemaPrefix + "mview_gdpr_invoices");
                    resultset.next();
                    recordCount[0] = resultset.getLong(1);

                } catch (Exception e) {
                    log.error("Failed to remove subscriptions in GDPR", e);
                    throw new BusinessException(e);
                }
            }
        });

        jobExecutionResult.addReport("Invoices");
        jobExecutionResult.addNbItemsToProcess(recordCount[0]);
        jobExecutionResultService.persistResult(jobExecutionResult);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                try (Statement statement = connection.createStatement()) {

                    if (recordCount[0] > 0) {

                        statement.execute("update " + schemaPrefix + "billing_invoice set recorded_invoice_id=null where id in (select id from " + schemaPrefix + "mview_gdpr_invoices)");

                        statement.execute("delete from " + schemaPrefix + "billing_wallet_operation where status<>'OPEN' and rated_transaction_id in (select id from " + schemaPrefix
                                + "billing_rated_transaction where status='BILLED' and invoice_id in (select id from " + schemaPrefix + "mview_gdpr_invoices))");
                        statement.execute("delete from " + schemaPrefix + "billing_rated_transaction  where status='BILLED' and invoice_id in (select id from " + schemaPrefix + "mview_gdpr_invoices)");

                        statement.execute("delete from " + schemaPrefix + "ar_matching_amount where account_operation_id in (select id from " + schemaPrefix
                                + "ar_account_operation where transaction_type='IC' and recorded_invoice_id in (select ao.id from " + schemaPrefix + "ar_account_operation ao join " + schemaPrefix
                                + "mview_gdpr_invoices gi on ao.invoice_id=gi.id where transaction_type='I'))");
                        statement.execute("delete from " + schemaPrefix + "ar_account_operation where transaction_type='IC' and recorded_invoice_id in (select ao.id from " + schemaPrefix + "ar_account_operation ao join "
                                + schemaPrefix + "mview_gdpr_invoices gi on ao.invoice_id=gi.id where transaction_type='I')");

                        statement.execute("delete from " + schemaPrefix + "ar_matching_amount where account_operation_id in (select ao.id from " + schemaPrefix + "ar_account_operation ao join " + schemaPrefix
                                + "mview_gdpr_invoices gi on ao.invoice_id=gi.id where transaction_type='I')");
                        statement.execute("delete from " + schemaPrefix + "ar_account_operation where transaction_type='I' and invoice_id in (select id from " + schemaPrefix + "mview_gdpr_invoices)");

                        statement.execute("delete from " + schemaPrefix + "billing_invoice_line where invoice_id in (select id from " + schemaPrefix + "mview_gdpr_invoices)");

                        statement.execute("delete from " + schemaPrefix + "billing_invoice_agregate where type='F' and invoice_id in (select id from " + schemaPrefix + "mview_gdpr_invoices)");
                        statement.execute("delete from " + schemaPrefix + "billing_invoice_agregate where invoice_id in (select id from " + schemaPrefix + "mview_gdpr_invoices)");
                        statement.execute("delete from " + schemaPrefix + "billing_invoice where id in (select id from " + schemaPrefix + "mview_gdpr_invoices)");

                    }

                    statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_invoices");
                    statement.execute("SET session_replication_role = DEFAULT");

                } catch (Exception e) {
                    log.error("Failed to remove invoices in GDPR", e);
                    throw new BusinessException(e);
                }
            }
        });
        return recordCount[0];
    }

    /**
     * Remove old or old and unpaid account operations
     *
     * @param gdprConfiguration GDPR configuration
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long removeAccountOperations(GdprConfiguration gdprConfiguration, JobExecutionResultImpl jobExecutionResult) {

        String providerCode = currentUser.getProviderCode();
        final String schemaPrefix = providerCode != null ? EntityManagerProvider.convertToSchemaName(providerCode) + "." : "";

        final long[] recordCount = new long[] { 0L };

        Session hibernateSession = subscriptionService.getEntityManager().unwrap(Session.class);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                String where = null;
                if (gdprConfiguration.isDeleteAccounting()) {
                    where = "(ao.transaction_date<=now() - interval '" + gdprConfiguration.getAccountingLife() + " year')";
                }
                if (gdprConfiguration.isDeleteAoCheckUnpaidLife()) {
                    where = (where != null ? where + " or " : "") + "(ao.transaction_date<=now() - interval '" + gdprConfiguration.getAoCheckUnpaidLife()
                            + " year' and matching_status='O' and transaction_type in ('I','OCC'))";
                }

                try (Statement statement = connection.createStatement()) {
                    statement.execute("SET session_replication_role = replica");
                    statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_aos");
                    statement.execute("create materialized view " + schemaPrefix + "mview_gdpr_aos (id) as (select id from " + schemaPrefix + "ar_account_operation ao where " + where + ")");
                    statement.execute("create index mview_gdpr_aos_pk on " + schemaPrefix + "mview_gdpr_aos (id)");

                    ResultSet resultset = statement.executeQuery("select count(*) from " + schemaPrefix + "mview_gdpr_aos");
                    resultset.next();
                    recordCount[0] = resultset.getLong(1);

                } catch (Exception e) {
                    log.error("Failed to remove subscriptions in GDPR", e);
                    throw new BusinessException(e);
                }
            }
        });

        jobExecutionResult.addReport("Account operations");
        jobExecutionResult.addNbItemsToProcess(recordCount[0]);
        jobExecutionResultService.persistResult(jobExecutionResult);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                try (Statement statement = connection.createStatement()) {

                    if (recordCount[0] > 0) {

                        statement.execute("update " + schemaPrefix + "billing_invoice set recorded_invoice_id=null where recorded_invoice_id in (select id from " + schemaPrefix + "mview_gdpr_aos)");

                        statement.execute("delete from " + schemaPrefix + "ar_matching_amount where account_operation_id in (select ao.id from " + schemaPrefix + "ar_account_operation ao join " + schemaPrefix
                                + "mview_gdpr_aos go on ao.recorded_invoice_id=go.id where transaction_type='IC')");
                        statement.execute("delete from " + schemaPrefix + "ar_account_operation where transaction_type='IC' and recorded_invoice_id in (select id from " + schemaPrefix + "mview_gdpr_aos)");

                        statement.execute("delete from " + schemaPrefix + "ar_matching_amount where account_operation_id in (select id from " + schemaPrefix + "mview_gdpr_aos)");
                        statement.execute("delete from " + schemaPrefix + "ar_account_operation where id in (select id from " + schemaPrefix + "mview_gdpr_aos)");
                    }

                    statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_aos");
                    statement.execute("SET session_replication_role = DEFAULT");

                } catch (Exception e) {
                    log.error("Failed to remove Account operations in GDPR", e);
                    throw new BusinessException(e);
                }
            }
        });
        return recordCount[0];
    }

    /**
     * Remove old or old and unpaid account operations
     *
     * @param gdprConfiguration GDPR configuration
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long removeCustomers(GdprConfiguration gdprConfiguration, JobExecutionResultImpl jobExecutionResult) {

        String providerCode = currentUser.getProviderCode();
        final String schemaPrefix = providerCode != null ? EntityManagerProvider.convertToSchemaName(providerCode) + "." : "";

        final long[] recordCount = new long[] { 0L };

        Session hibernateSession = subscriptionService.getEntityManager().unwrap(Session.class);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                try (Statement statement = connection.createStatement()) {
                    statement.execute("SET session_replication_role = replica");
                    statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_customers");
                    statement.execute("create materialized view " + schemaPrefix + "mview_gdpr_customers (id) as (select c.id from " + schemaPrefix + "crm_customer c left join " + schemaPrefix
                            + "ar_customer_account ca on ca.customer_id=c.id left join " + schemaPrefix + "billing_billing_account ba on ba.customer_account_id=ca.id left join " + schemaPrefix
                            + "billing_user_account ua on ua.billing_account_id=ba.id left join " + schemaPrefix + "billing_invoice i on i.billing_account_id=ba.id left join " + schemaPrefix
                            + "billing_subscription sub on sub.user_account_id=ua.id where sub.id is null and i.id is null and c.created<=now() - interval '" + gdprConfiguration.getCustomerProspectLife() + " year')");
                    statement.execute("create index mview_gdpr_aos_pk on " + schemaPrefix + "mview_gdpr_customers (id)");

                    ResultSet resultset = statement.executeQuery("select count(*) from " + schemaPrefix + "mview_gdpr_customers");
                    resultset.next();
                    recordCount[0] = resultset.getLong(1);

                } catch (Exception e) {
                    log.error("Failed to remove subscriptions in GDPR", e);
                    throw new BusinessException(e);
                }
            }
        });

        jobExecutionResult.addReport("Prospect customers");
        jobExecutionResult.addNbItemsToProcess(recordCount[0]);
        jobExecutionResultService.persistResult(jobExecutionResult);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                try (Statement statement = connection.createStatement()) {

                    if (recordCount[0] > 0) {

                        statement.execute("update " + schemaPrefix + "crm_customer set minimum_target_account_id=null where id in (select id from " + schemaPrefix + "mview_gdpr_customers)");
                        statement.execute("update " + schemaPrefix + "billing_user_account set wallet_id=null where billing_account_id in (select ba.id from " + schemaPrefix + "billing_billing_account ba join "
                                + schemaPrefix + "ar_customer_account ca on ba.customer_account_id=ca.id join " + schemaPrefix + "mview_gdpr_customers gc on ca.customer_id=gc.id)");

                        statement.execute("delete from " + schemaPrefix + "billing_wallet where user_account_id in (select ua.id from " + schemaPrefix + "billing_user_account ua join " + schemaPrefix
                                + "billing_billing_account ba on ua.billing_account_id=ba.id join " + schemaPrefix + "ar_customer_account ca on ba.customer_account_id=ca.id join " + schemaPrefix
                                + "mview_gdpr_customers gc on ca.customer_id=gc.id)");

                        statement.execute("delete from " + schemaPrefix + "billing_user_account where billing_account_id in (select ba.id from " + schemaPrefix + "billing_billing_account ba join " + schemaPrefix
                                + "ar_customer_account ca on ba.customer_account_id=ca.id join " + schemaPrefix + "mview_gdpr_customers gc on ca.customer_id=gc.id)");

                        statement.execute("delete from " + schemaPrefix + "billing_discount_plan_instance where id in (select ba.id from " + schemaPrefix + "billing_billing_account ba join " + schemaPrefix
                                + "ar_customer_account ca on ba.customer_account_id=ca.id join " + schemaPrefix + "mview_gdpr_customers gc on ca.customer_id=gc.id)");

                        statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_payment_token");
                        statement.execute("create materialized view " + schemaPrefix + "mview_gdpr_payment_token (id) as (select ba.payment_method_id from " + schemaPrefix + "billing_billing_account ba join "
                                + schemaPrefix + "ar_customer_account ca on ba.customer_account_id=ca.id join " + schemaPrefix + "mview_gdpr_customers gc on ca.customer_id=gc.id)");
                        statement.execute("create index mview_gdpr_payment_token_pk on " + schemaPrefix + "mview_gdpr_payment_token (id)");
                        statement.execute("delete from " + schemaPrefix + "billing_billing_account where customer_account_id in (select ca.id from " + schemaPrefix + "ar_customer_account ca join " + schemaPrefix
                                + "mview_gdpr_customers gc on ca.customer_id=gc.id)");
                        statement.execute("delete from " + schemaPrefix + "ar_payment_token where id in (select id from " + schemaPrefix + "mview_gdpr_payment_token)");
                        statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_payment_token");

                        statement.execute("delete from " + schemaPrefix + "com_message where contact_id in (select com.id from " + schemaPrefix + "com_contact com join " + schemaPrefix
                                + "ar_customer_account ca on com.address_book_id=ca.crm_address_book_id join " + schemaPrefix + "mview_gdpr_customers gc on ca.customer_id=gc.id)");
                        statement.execute("delete from " + schemaPrefix + "com_contact where address_book_id in (select ca.crm_address_book_id from " + schemaPrefix + "ar_customer_account ca join " + schemaPrefix
                                + "mview_gdpr_customers gc on ca.customer_id=gc.id)");
                        statement.execute("delete from " + schemaPrefix + "crm_address_book where id in (select ca.crm_address_book_id from " + schemaPrefix + "ar_customer_account ca join " + schemaPrefix
                                + "mview_gdpr_customers gc on ca.customer_id=gc.id)");
                        statement.execute("delete from " + schemaPrefix + "ar_payment_token where customer_account_id in (select ca.id from " + schemaPrefix + "ar_customer_account ca join " + schemaPrefix
                                + "mview_gdpr_customers gc on ca.customer_id=gc.id)");

                        statement.execute("delete from " + schemaPrefix + "ar_matching_amount where account_operation_id in (select ao.id from " + schemaPrefix + "ar_account_operation ao join " + schemaPrefix
                                + "ar_customer_account ca on ao.customer_account_id=ca.id join " + schemaPrefix + "mview_gdpr_customers gc on ca.customer_id=gc.id)");
                        statement.execute("delete from " + schemaPrefix + "ar_account_operation where customer_account_id in (select ca.id from " + schemaPrefix + "ar_customer_account ca join " + schemaPrefix
                                + "mview_gdpr_customers gc on ca.customer_id=gc.id)");
                        statement.execute("delete from " + schemaPrefix + "dunning_document where customer_account_id in (select ca.id from " + schemaPrefix + "ar_customer_account ca join " + schemaPrefix
                                + "mview_gdpr_customers gc on ca.customer_id=gc.id)");

                        statement.execute("delete from " + schemaPrefix + "ar_customer_account where customer_id in (select id from " + schemaPrefix + "mview_gdpr_customers)");

                        statement.execute("delete from " + schemaPrefix + "com_message where contact_id in (select com.id from " + schemaPrefix + "com_contact com join " + schemaPrefix
                                + "crm_customer c on com.address_book_id=c.address_book_id join " + schemaPrefix + "mview_gdpr_customers gc on c.id=gc.id)");
                        statement.execute("delete from " + schemaPrefix + "com_contact where address_book_id in (select c.address_book_id from " + schemaPrefix + "crm_customer c join " + schemaPrefix
                                + "mview_gdpr_customers gc on c.id=gc.id)");
                        statement.execute("delete from " + schemaPrefix + "crm_address_book where id in (select c.address_book_id from " + schemaPrefix + "crm_customer c join " + schemaPrefix
                                + "mview_gdpr_customers gc on c.id=gc.id)");

                        statement.execute("delete from " + schemaPrefix + "crm_additional_details where id in (select c.additional_details_id from " + schemaPrefix + "crm_customer c join " + schemaPrefix
                                + "mview_gdpr_customers gc on c.id=gc.id)");

                        statement.execute("delete from " + schemaPrefix + "crm_customer where id in (select id from " + schemaPrefix + "mview_gdpr_customers)");
                    }

                    statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_customers");
                    statement.execute("SET session_replication_role = DEFAULT");

                } catch (Exception e) {
                    log.error("Failed to remove customers in GDPR", e);
                    throw new BusinessException(e);
                }
            }
        });
        return recordCount[0];
    }
}