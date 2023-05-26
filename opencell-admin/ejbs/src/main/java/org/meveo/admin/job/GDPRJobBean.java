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
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.EntityManagerProvider;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.crm.Provider;
import org.meveo.model.dwh.GdprConfiguration;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.crm.impl.ProviderService;
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

    @EJB
    private GDPRJobBean thisNewTx;
    
	@JpaAmpNewTx
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl jobExecutionResult, String parameter) {

		GdprConfiguration gdprConfiguration = providerService.getEntityManager()
				.createQuery("Select p.gdprConfiguration From Provider p Where p.id=:providerId", GdprConfiguration.class)
				.setParameter("providerId", appProvider.getId())
				.getResultList().stream().findFirst().orElse(null);

		if (gdprConfiguration == null) {
			log.warn("No GDPR Config found for provider[id={}], so no items will be processed!", appProvider.getId() );
			return;
		}
		
		try {
          if (gdprConfiguration.isDeleteSubscription()) {
                long count = thisNewTx.removeSubscriptions(gdprConfiguration, jobExecutionResult);
                jobExecutionResult.addNbItemsCorrectlyProcessed(count);
                jobExecutionResult.close();
                jobExecutionResultService.persistResultJob(jobExecutionResult);
                jobExecutionResult = new JobExecutionResultImpl(jobExecutionResult.getJobInstance(), jobExecutionResult.getJobLauncherEnum());
            }
 
            if (gdprConfiguration.isDeleteOrder()) {
                long count = thisNewTx.removeOrders(gdprConfiguration, jobExecutionResult);
                jobExecutionResult.addNbItemsCorrectlyProcessed(count);
                jobExecutionResult.close();
                jobExecutionResultService.persistResultJob(jobExecutionResult);
                jobExecutionResult = new JobExecutionResultImpl(jobExecutionResult.getJobInstance(), jobExecutionResult.getJobLauncherEnum());
            }

            if (gdprConfiguration.isDeleteInvoice()) {
                long count = thisNewTx.removeInvoices(gdprConfiguration, jobExecutionResult);
                jobExecutionResult.addNbItemsCorrectlyProcessed(count);
                jobExecutionResult.close();
                jobExecutionResultService.persistResultJob(jobExecutionResult);
                jobExecutionResult = new JobExecutionResultImpl(jobExecutionResult.getJobInstance(), jobExecutionResult.getJobLauncherEnum());
            }
            
            if (gdprConfiguration.isDeleteAccounting() || gdprConfiguration.isDeleteAoCheckUnpaidLife()) {
                long count = thisNewTx.removeAccountOperations(gdprConfiguration, jobExecutionResult);
                jobExecutionResult.addNbItemsCorrectlyProcessed(count);
                jobExecutionResult.close();
                jobExecutionResultService.persistResultJob(jobExecutionResult);
                jobExecutionResult = new JobExecutionResultImpl(jobExecutionResult.getJobInstance(), jobExecutionResult.getJobLauncherEnum());
            }

            if (gdprConfiguration.isDeleteCustomerProspect()) {
                long count = thisNewTx.removeCustomers(gdprConfiguration, jobExecutionResult);
                jobExecutionResult.addNbItemsCorrectlyProcessed(count);
                jobExecutionResult.close();
                jobExecutionResultService.persistResultJob(jobExecutionResult);
                jobExecutionResult = new JobExecutionResultImpl(jobExecutionResult.getJobInstance(), jobExecutionResult.getJobLauncherEnum());
            }

        } catch (Exception e) {
            log.error("Failed to run GDPR data erasure job", e);
            jobExecutionResult.registerError(e.getMessage());
        }
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
					if (gdprConfiguration.isDeactivateDBTriggers()) {
						statement.execute("SET session_replication_role = replica");
					}
	                statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_customers");
	                statement.execute("create materialized view " + schemaPrefix + "mview_gdpr_customers (id) as (select c.id from " + schemaPrefix + "crm_customer c left join " + schemaPrefix
	                        + "ar_customer_account ca on ca.customer_id=c.id left join " + schemaPrefix + "billing_billing_account ba on ba.customer_account_id=ca.id left join " + schemaPrefix
	                        + "billing_user_account ua on ua.billing_account_id=ba.id left join " + schemaPrefix 
	                        + "billing_invoice i on i.billing_account_id=ba.id left join " + schemaPrefix
	                        + "account_entity cust on cust.id=c.id left join " + schemaPrefix
	                        + "billing_subscription sub on sub.user_account_id=ua.id where i.id is null and cust.created<=now() - interval '" + gdprConfiguration.getCustomerProspectLife() + " year')");
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
	    jobExecutionResultService.persistResultJob(jobExecutionResult);
	    hibernateSession.doWork(new org.hibernate.jdbc.Work() {
	
	        @Override
	        public void execute(Connection connection) throws SQLException {
	
	            try (Statement statement = connection.createStatement()) {
	
	                if (recordCount[0] > 0) {
	                    statement.execute("update " + schemaPrefix + "billing_user_account set wallet_id=null where billing_account_id in (select ba.id from " + schemaPrefix + "billing_billing_account ba join "
	                            + schemaPrefix + "ar_customer_account ca on ba.customer_account_id=ca.id join " + schemaPrefix + "mview_gdpr_customers gc on ca.customer_id=gc.id)");
	
	                    statement.execute("delete from " + schemaPrefix + "billing_wallet where user_account_id in (select ua.id from " + schemaPrefix + "billing_user_account ua join " + schemaPrefix
	                            + "billing_billing_account ba on ua.billing_account_id=ba.id join " + schemaPrefix + "ar_customer_account ca on ba.customer_account_id=ca.id join " + schemaPrefix
	                            + "mview_gdpr_customers gc on ca.customer_id=gc.id)");
	
	                    statement.execute("drop materialized view if exists " + schemaPrefix + "mview_ar_cust_token");
	                    statement.execute("create materialized view " + schemaPrefix + "mview_ar_cust_token (id) as (select id from " + schemaPrefix + "billing_user_account where billing_account_id in (select ba.id from " + schemaPrefix + "billing_billing_account ba join " + schemaPrefix + "ar_customer_account ca on ba.customer_account_id=ca.id join " + schemaPrefix + "mview_gdpr_customers gc on ca.customer_id=gc.id))");
	                    statement.execute("create index mview_ar_cust_token_pk on " + schemaPrefix + "mview_ar_cust_token (id)");	                    
	                    statement.execute("delete from " + schemaPrefix + "billing_user_account where billing_account_id in (select ba.id from " + schemaPrefix + "billing_billing_account ba join " + schemaPrefix
	                            + "ar_customer_account ca on ba.customer_account_id=ca.id join " + schemaPrefix + "mview_gdpr_customers gc on ca.customer_id=gc.id)");
	                    statement.execute("delete from " + schemaPrefix + "account_entity where id in (select id from " + schemaPrefix + "mview_ar_cust_token)");
	                    statement.execute("drop materialized view if exists " + schemaPrefix + "mview_ar_cust_token");
	                    statement.execute("delete from " + schemaPrefix + "account_entity where id in (select ba.id from " + schemaPrefix + "billing_billing_account ba join " + schemaPrefix
	                    		+ "ar_customer_account ca on ba.customer_account_id=ca.id join " + schemaPrefix + "mview_gdpr_customers gc on ca.customer_id=gc.id)");
       
	
	                    
	                    
	                    statement.execute("delete from " + schemaPrefix + "billing_discount_plan_instance where id in (select ba.id from " + schemaPrefix + "billing_billing_account ba join " + schemaPrefix
	                            + "ar_customer_account ca on ba.customer_account_id=ca.id join " + schemaPrefix + "mview_gdpr_customers gc on ca.customer_id=gc.id)");
	                    statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_payment_token");
	                    statement.execute("create materialized view " + schemaPrefix + "mview_gdpr_payment_token (id) as (select bi.payment_method_id from " + schemaPrefix + 
	                    		"billing_invoice bi join " + schemaPrefix + "billing_billing_account ba on ba.id=bi.billing_account_id join " + schemaPrefix + 
	                    		"ar_customer_account ca on ba.customer_account_id=ca.id join " + schemaPrefix + "mview_gdpr_customers gc on ca.customer_id=gc.id)");	                        
	                    statement.execute("create index mview_gdpr_payment_token_pk on " + schemaPrefix + "mview_gdpr_payment_token (id)");
	                    statement.execute("delete from " + schemaPrefix + "billing_billing_account where customer_account_id in (select ca.id from " + schemaPrefix + "ar_customer_account ca join " + schemaPrefix
	                            + "mview_gdpr_customers gc on ca.customer_id=gc.id)");
	                    statement.execute("delete from " + schemaPrefix + "account_entity where id in (select ca.id from " + schemaPrefix + "ar_customer_account ca join " + schemaPrefix
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
	                    statement.execute("delete from " + schemaPrefix + "account_entity where id in (select id from " + schemaPrefix + "mview_gdpr_customers)");
	                    statement.execute("delete from " + schemaPrefix + "crm_customer where id in (select id from " + schemaPrefix + "mview_gdpr_customers)");
	                }
	
	                statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_customers");
					if (gdprConfiguration.isDeactivateDBTriggers()) {
						statement.execute("SET session_replication_role = DEFAULT");
					}
	
	            } catch (Exception e) {
	                log.error("Failed to remove customers in GDPR", e);
	                throw new BusinessException(e);
	            }
	        }
	    });
	    return recordCount[0];
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
					if (gdprConfiguration.isDeactivateDBTriggers()) {
						statement.execute("SET session_replication_role = replica");
					}
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
	    jobExecutionResultService.persistResultJob(jobExecutionResult);
	
	    hibernateSession.doWork(new org.hibernate.jdbc.Work() {	
	        @Override
	        public void execute(Connection connection) throws SQLException {	
	            try (Statement statement = connection.createStatement()) {	
	                if (recordCount[0] > 0) {
	                    statement.execute("update " + schemaPrefix + "billing_rated_transaction set service_instance_id=null, subscription_id=null, charge_instance_id=null where subscription_id in (select id from "
	                            + schemaPrefix + "mview_gdpr_subscriptions)");
	                    statement.execute("update " + schemaPrefix + "billing_invoice set subscription_id=null where subscription_id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
	                    statement.execute("update " + schemaPrefix + "ord_order_item set subscription_id=null where subscription_id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
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
	                    statement.execute("delete from " + schemaPrefix + "billing_subscription where id in (select id from " + schemaPrefix + "mview_gdpr_subscriptions)");
	                    statement.execute("delete from " + schemaPrefix + "audit_field_changes_history where entity_class='org.meveo.model.billing.Subscription' and entity_id in (select id from " + schemaPrefix
	                            + "mview_gdpr_subscriptions)");	
	                }
	                statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_subscriptions");
					if (gdprConfiguration.isDeactivateDBTriggers()) {
						statement.execute("SET session_replication_role = DEFAULT");
					}
	
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
					if (gdprConfiguration.isDeactivateDBTriggers()) {
						statement.execute("SET session_replication_role = replica");
					}
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
	    jobExecutionResultService.persistResultJob(jobExecutionResult);
	
	    hibernateSession.doWork(new org.hibernate.jdbc.Work() {
	
	        @Override
	        public void execute(Connection connection) throws SQLException {
	
	            try (Statement statement = connection.createStatement()) {
	
	                if (recordCount[0] > 0) {
	
	                    statement.execute("update " + schemaPrefix + "billing_invoice set order_id=null where order_id in (select id from " + schemaPrefix + "mview_gdpr_orders)");
						statement.execute("DELETE FROM " + schemaPrefix + "billing_invoices_orders AS bio USING " + schemaPrefix + "mview_gdpr_orders AS m WHERE bio.order_id = m.id;");
	                    statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_payment_token");
	                    statement.execute("create materialized view " + schemaPrefix + "mview_gdpr_payment_token (id) as (select payment_method_id from " + schemaPrefix + "ord_order o join " + schemaPrefix
	                            + "mview_gdpr_orders go on o.id=go.id)");
	                    statement.execute("create index mview_gdpr_payment_token_pk on " + schemaPrefix + "mview_gdpr_payment_token (id)");
	
	                    statement.execute("delete from " + schemaPrefix + "ord_order_history where order_item_id in (select oi.id from " + schemaPrefix + "ord_order_item oi join " + schemaPrefix
	                            + "mview_gdpr_orders go on oi.order_id=go.id)");
	                    statement.execute("delete from " + schemaPrefix + "ord_item_offerings where order_item_id in (select oi.id from " + schemaPrefix + "ord_order_item oi join " + schemaPrefix
	                            + "mview_gdpr_orders go on oi.order_id=go.id)");

						statement.execute("delete from " + schemaPrefix + "ord_item_prd_instance as t1 using " + schemaPrefix + "ord_order_item as t2,  " + schemaPrefix + "mview_gdpr_orders as mv where t2.id = t1.order_item_id and t2.order_id = mv.id");
	                    statement.execute("delete from " + schemaPrefix + "ord_order_item where order_id in (select id from " + schemaPrefix + "mview_gdpr_orders)");

	                    statement.execute("delete from " + schemaPrefix + "ord_order where id in (select id from " + schemaPrefix + "mview_gdpr_orders)");
	
	                    statement.execute("delete from " + schemaPrefix + "ar_payment_token where id in (select id from " + schemaPrefix + "mview_gdpr_payment_token)");
	                    statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_payment_token");
	                }
	
	                statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_orders");
					if (gdprConfiguration.isDeactivateDBTriggers()) {
						statement.execute("SET session_replication_role = DEFAULT");
					}
	
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
	        	String queryInvoice = (String) getParamOrCFValue(jobExecutionResult.getJobInstance(), "GDPRcustomQueryListInvoice", null);
	            try (Statement statement = connection.createStatement()) {
					if (gdprConfiguration.isDeactivateDBTriggers()) {
						statement.execute("SET session_replication_role = replica");
					}
	    	    	if(StringUtils.isBlank(queryInvoice)) {
	    	    		queryInvoice = "select id from " + schemaPrefix + "billing_invoice i where i.invoice_date<=now() - interval '"
	                            + gdprConfiguration.getInvoiceLife() + " year'";
	    	    	}					
	                statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_invoices");
	                statement.execute("create materialized view " + schemaPrefix + "mview_gdpr_invoices (id) as ("+queryInvoice+")");
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
	    jobExecutionResultService.persistResultJob(jobExecutionResult);
	
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
	                    statement.execute("delete from " + schemaPrefix + "billing_invoice_agregate where type='F' and invoice_id in (select id from " + schemaPrefix + "mview_gdpr_invoices)");
	                    statement.execute("delete from " + schemaPrefix + "billing_invoice_agregate where invoice_id in (select id from " + schemaPrefix + "mview_gdpr_invoices)");
	                    statement.execute("delete from " + schemaPrefix + "billing_linked_invoices as bli using " + schemaPrefix + "mview_gdpr_orders as m where bli.id = m.id or bli.linked_invoice_id = m.id");
						statement.execute("delete from " + schemaPrefix + "billing_invoice where id in (select id from " + schemaPrefix + "mview_gdpr_invoices)");
	
	                }
	
	                statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_invoices");
					if (gdprConfiguration.isDeactivateDBTriggers()) {
						statement.execute("SET session_replication_role = DEFAULT");
					}
	
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
					if (gdprConfiguration.isDeactivateDBTriggers()) {
						statement.execute("SET session_replication_role = replica");
					}
	                statement.execute("drop materialized view if exists " + schemaPrefix + "mview_gdpr_aos");
	                statement.execute("create materialized view " + schemaPrefix + "mview_gdpr_aos (id) as (select id from " + schemaPrefix + "ar_account_operation ao where " + where + ")");
	                statement.execute("create index mview_gdpr_aos_pk on " + schemaPrefix + "mview_gdpr_aos (id)");
	
	                ResultSet resultset = statement.executeQuery("select count(*) from " + schemaPrefix + "mview_gdpr_aos");
	                resultset.next();
	                recordCount[0] = resultset.getLong(1);
	
	            } catch (Exception e) {
	                log.error("Failed to remove AOs in GDPR", e);
	                throw new BusinessException(e);
	            }
	        }
	    });
	
	    jobExecutionResult.addReport("Account operations");
	    jobExecutionResult.addNbItemsToProcess(recordCount[0]);
	    jobExecutionResultService.persistResultJob(jobExecutionResult);
	
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
					if (gdprConfiguration.isDeactivateDBTriggers()) {
						statement.execute("SET session_replication_role = DEFAULT");
					}
	
	            } catch (Exception e) {
	                log.error("Failed to remove Account operations in GDPR", e);
	                throw new BusinessException(e);
	            }
	        }
	    });
	    return recordCount[0];
	}

}
