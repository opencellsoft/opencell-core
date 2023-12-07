package org.meveo.admin.job;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.meveo.admin.async.SynchronizedMultiItemIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

@Stateless
public class RatingCancellationJobBean extends IteratorBasedJobBean<List<Object[]>> {

	private static final long serialVersionUID = -4097694568061727769L;
	private static final String mainViewName = "main_rerate_tree";
	private static final String billedViewName = "rerate_billed_IL";
	private static final String triggeredViewName = "triggered_rerate_tree";

	@Inject
	@MeveoJpa
	private EntityManagerWrapper emWrapper;
	
	private EntityManager entityManager;

	private StatelessSession statelessSession;
	
	private ScrollableResults scrollableResults;

	private Long nrOfInitialWOs = null;
	

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
		super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::applyRatingCancellation, null, null, this::closeResultset, null);
	}

	@SuppressWarnings({ "unchecked" })
	private Optional<Iterator<List<Object[]>>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

		JobInstance jobInstance = jobExecutionResult.getJobInstance();

		int processNrInJobRun = ParamBean.getInstance().getPropertyAsInteger("RatingCancellationJob.processNrInJobRun", 10000000);

		Long nbThreads = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
		if (nbThreads == -1) {
			nbThreads = (long) Runtime.getRuntime().availableProcessors();
		}

		final long configuredNrPerTx = (Long) this.getParamOrCFValue(jobInstance, RatingCancellationJob.CF_INVOICE_LINES_NR_RTS_PER_TX, 100000L);
		
		entityManager = emWrapper.getEntityManager();
		createMainView(configuredNrPerTx);
		createTriggeredOperationsView();
		createBilledILsView();
		statelessSession = entityManager.unwrap(Session.class).getSessionFactory().openStatelessSession();
		getProcessingSummary();
		if (nrOfInitialWOs.intValue() == 0) {
			return Optional.empty();
		}
		jobExecutionResult.addReport(" Start rerate cancelllation process for " + nrOfInitialWOs + " WOs");
		
		final long nrPerTx = (nrOfInitialWOs / nbThreads) < configuredNrPerTx ? nrOfInitialWOs / nbThreads : configuredNrPerTx;
		int fetchSize = ((Long) nrPerTx).intValue() * nbThreads.intValue();
		org.hibernate.query.Query nativeQuery = statelessSession.createNativeQuery("select id, count_wo from " + mainViewName + " order by id");
		scrollableResults = nativeQuery.setReadOnly(true).setCacheable(false).setMaxResults(processNrInJobRun).setFetchSize(fetchSize).scroll(ScrollMode.FORWARD_ONLY);

		return Optional.of(
				new SynchronizedMultiItemIterator<Object[]>(scrollableResults, nrOfInitialWOs.intValue(), true, null) {

					long count = 0L;

					@Override
					public void initializeDecisionMaking(Object[] item) {
						count = ((Number) item[1]).longValue();
					}

					@Override
					public boolean isIncludeItem(Object[] item) {
						Long woCount = ((Number) item[1]).longValue();
						if (count + woCount > nrPerTx) {
							return false;
						}
						count = count + woCount;
						return true;
					}
				});
	}

	private void applyRatingCancellation(List<Object[]> reratingTree, JobExecutionResultImpl jobExecutionResult) {

		if (reratingTree != null) {
			long min = ((Number) reratingTree.get(0)[0]).longValue();
			long max = ((Number) reratingTree.get(reratingTree.size() - 1)[0]).longValue();
			Long count = reratingTree.stream().mapToLong(x -> ((Number) x[1]).longValue()).sum();
			log.info("start processing " + count + " items, view lines from: " + min + " to: " + max);
			
			cancelAllObjects(min, max);
		}
	}

	private void cancelAllObjects(long min, long max) {
		
		markFailedToRerate(min, max);
		
		markCanceledEDRs(min, max);
		
		markCanceledWOs("d",mainViewName, min, max);
		markCanceledWOs("t",triggeredViewName, min, max);
		
		recalculateInvoiceLinesAndCancelRTs("",mainViewName, min, max);
		recalculateInvoiceLinesAndCancelRTs("d",mainViewName, min, max);
		recalculateInvoiceLinesAndCancelRTs("t",triggeredViewName, min, max);
		
	}
	
	/**
	 * Close data resultset
	 * 
	 * @param jobExecutionResult Job execution result
	 */
	private void closeResultset(JobExecutionResultImpl jobExecutionResult) {
		if (scrollableResults != null) {
			scrollableResults.close();
		}
		if (statelessSession != null) {
			statelessSession.close();
		}
	}

	private void getProcessingSummary() {

		Object[] count = (Object[]) entityManager.createNativeQuery("select sum(count_wo), count(id) from " + mainViewName)
				.getSingleResult();

		nrOfInitialWOs = count[0] != null ? ((Number) count[0]).longValue() : 0;

	}

	private void createMainView(long configuredNrPerTx) {
		Session hibernateSession = entityManager.unwrap(Session.class);
		String mainViewQuery = "CREATE MATERIALIZED VIEW " + mainViewName + " AS\n"
				+ "SELECT string_agg(wo.id::text, ',') AS wo_id, string_agg(rt.id::text, ',') AS rt_id, il.id AS il_id, SUM(CASE WHEN rt.status = 'BILLED' THEN rt.amount_without_tax ELSE 0 END) AS rt_amount_without_tax,\n"
				+ "	    SUM(CASE WHEN rt.status = 'BILLED' THEN rt.amount_with_tax ELSE 0 END) AS rt_amount_with_tax, SUM(CASE WHEN rt.status = 'BILLED' THEN rt.amount_tax ELSE 0 END) AS rt_amount_tax, SUM(CASE WHEN rt.status = 'BILLED' THEN rt.quantity ELSE 0 END) AS rt_quantity,\n"
				+ "	    string_agg(dwo.id::text, ',') AS dwo_id, string_agg(drt.id::text, ',') AS drt_id, dil.id AS dil_id, SUM(CASE WHEN drt.status = 'BILLED' THEN drt.amount_without_tax ELSE 0 END) AS drt_amount_without_tax,\n"
				+ "	    SUM(CASE WHEN drt.status = 'BILLED' THEN drt.amount_with_tax ELSE 0 END) AS drt_amount_with_tax, SUM(CASE WHEN drt.status = 'BILLED' THEN drt.amount_tax ELSE 0 END) AS drt_amount_tax, SUM(CASE WHEN drt.status = 'BILLED' THEN drt.quantity ELSE 0 END) AS drt_quantity,\n"
				+ "	    wo.subscription_id AS s_id, COUNT(1) AS count_WO, ROW_NUMBER() OVER (ORDER BY COUNT(1) / "+configuredNrPerTx+" DESC, wo.subscription_id) AS id, CASE WHEN il.status = 'BILLED' THEN il.id WHEN dil.status = 'BILLED' THEN dil.id ELSE NULL END AS billed_il\n"
				+ "	FROM billing_wallet_operation wo\n"
				+ "		LEFT JOIN billing_rated_transaction rt ON rt.id = wo.rated_transaction_id\n"
				+ "		LEFT JOIN billing_invoice_line il ON il.id = rt.invoice_line_id\n"
				+ "		LEFT JOIN billing_wallet_operation dwo ON wo.id = dwo.discounted_wallet_operation_id\n"
				+ "		LEFT JOIN billing_rated_transaction drt ON drt.id = dwo.rated_transaction_id\n"
				+ "		LEFT JOIN billing_invoice_line dil ON dil.id = drt.invoice_line_id\n"
				+ "	WHERE wo.status = 'TO_RERATE'\n"
				+ "GROUP BY s_id, il_id, dil_id";

		hibernateSession.doWork(new org.hibernate.jdbc.Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				try (Statement statement = connection.createStatement()) {
					log.info("Dropping and recreating materialized view {} : ", mainViewName);
					statement.execute("drop materialized view if EXISTS " + mainViewName +" cascade");
					log.info(mainViewQuery);
					statement.execute(mainViewQuery);
					statement.execute("create index idx__" + mainViewName + "__billed_il ON " + mainViewName + " USING btree (billed_il) ");
					statement.execute("create index idx__" + mainViewName + "__subscription_id ON " + mainViewName + " USING btree (s_id) ");
					statement.execute("create index idx__" + mainViewName + "__id ON " + mainViewName + " USING btree (id) ");
				} catch (Exception e) {
					log.error("Failed to drop/create the materialized view " + mainViewName, e.getMessage());
					throw new BusinessException(e);
				}
			}
		});
	}
	
	private void createTriggeredOperationsView() {
		Session hibernateSession = entityManager.unwrap(Session.class);
		String mainViewQuery = "CREATE MATERIALIZED VIEW " + triggeredViewName + " AS\n"
				+ "select mrt.id AS id, til.id AS til_id, string_agg(edr.id::text, ',') AS edr_id, string_agg(two.id::text, ',') AS two_id, string_agg(trt.id::text, ',') AS trt_id,\n"
				+ "	    SUM(CASE WHEN trt.status = 'BILLED' THEN trt.amount_without_tax ELSE 0 END) AS trt_amount_without_tax,\n"
				+ "	    SUM(CASE WHEN trt.status = 'BILLED' THEN trt.amount_with_tax ELSE 0 END) AS trt_amount_with_tax,\n"
				+ "	    SUM(CASE WHEN trt.status = 'BILLED' THEN trt.amount_tax ELSE 0 END) AS trt_amount_tax,\n"
				+ "	    SUM(CASE WHEN trt.status = 'BILLED' THEN trt.quantity ELSE 0 END) AS trt_quantity,\n"
				+ "	    CASE  WHEN til.status = 'BILLED' THEN til.id ELSE null END AS billed_il\n"
				+ "	FROM main_rerate_tree mrt\n"
				+ "		JOIN rating_edr edr on mrt.billed_il is null and edr.wallet_operation_id = ANY(string_to_array(CASE WHEN mrt.dwo_id IS NULL THEN mrt.wo_id ELSE mrt.wo_id || ',' || mrt.dwo_id END, ',')::bigint[])\n"
				+ "		LEFT JOIN billing_wallet_operation two ON two.edr_id = edr.id\n"
				+ "		LEFT JOIN billing_rated_transaction trt ON trt.id = two.rated_transaction_id\n"
				+ "		LEFT JOIN billing_invoice_line til ON til.id = trt.invoice_line_id\n"
				+ "GROUP BY mrt.id, til.id";

		hibernateSession.doWork(new org.hibernate.jdbc.Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				try (Statement statement = connection.createStatement()) {
					log.info("Dropping and recreating materialized view {} : ", triggeredViewName);
					statement.execute("drop materialized view if EXISTS " + triggeredViewName);
					log.info(mainViewQuery);
					statement.execute(mainViewQuery);
					statement.execute("create index idx__" + triggeredViewName + "__billed_il ON " + triggeredViewName + " USING btree (billed_il) ");
					statement.execute("create index idx__" + triggeredViewName + "__main_id ON " + triggeredViewName + " USING btree (id) ");
				} catch (Exception e) {
					log.error("Failed to drop/create the materialized view " + triggeredViewName, e.getMessage());
					throw new BusinessException(e);
				}
			}
		});
	}
	
	private void createBilledILsView() {
		Session hibernateSession = entityManager.unwrap(Session.class);
		String mainViewQuery = "CREATE MATERIALIZED VIEW " + billedViewName + " AS\n"
				+ "	(SELECT id, MIN(BILLED_IL) AS BILLED_IL FROM " + triggeredViewName + " WHERE BILLED_IL IS NOT NULL GROUP BY id\n"
				+ "		UNION SELECT ID as id, BILLED_IL FROM " + mainViewName + " where BILLED_IL is not null)";

		hibernateSession.doWork(new org.hibernate.jdbc.Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				try (Statement statement = connection.createStatement()) {
					log.info("Dropping and recreating materialized view {} : ", billedViewName);
					statement.execute("drop materialized view if EXISTS " + billedViewName);
					log.info(mainViewQuery);
					statement.execute(mainViewQuery);
					statement.execute("create index idx__" + billedViewName + "__billed_il ON " + billedViewName + " USING btree (billed_il) ");
					statement.execute("create index idx__" + billedViewName + "__main_id ON " + billedViewName + " USING btree (id) ");
				} catch (Exception e) {
					log.error("Failed to drop/create the materialized view " + billedViewName, e.getMessage());
					throw new BusinessException(e);
				}
			}
		});
	}
	
	private void markFailedToRerate(long min, long max) {
		String updateILQuery = "UPDATE billing_wallet_operation wo\n"
				+ "	SET status='F_TO_RERATE', updated = CURRENT_TIMESTAMP, reject_reason = 'failed to rerate operation because invoiceLine ' || bil.billed_il || ' already billed' "
				+ "		FROM " + mainViewName + " rr CROSS JOIN unnest(string_to_array(wo_id, ',')) AS to_update JOIN " + billedViewName + " bil ON rr.id = bil.BILLED_IL "
				+ "			WHERE rr.id between :min and :max and wo.id = CAST(to_update AS bigint)";
		statelessSession.createNativeQuery(updateILQuery).setParameter("min", min).setParameter("max", max).executeUpdate();
	}
	
	private void markCanceledEDRs(long min, long max) {
	    String updateILQuery = "UPDATE rating_EDR edr " +
	            "SET status='CANCELLED', last_updated = CURRENT_TIMESTAMP, reject_Reason='Origin wallet operation has been rerated' " +
	            "	FROM " + triggeredViewName + " rr CROSS JOIN unnest(string_to_array(edr_id, ',')) AS to_update " +
	            "		WHERE rr.id BETWEEN :min AND :max " +
	            "			AND rr.id NOT IN (SELECT id FROM " + billedViewName + ") " +
	            "			AND edr.id = CAST(to_update AS bigint)";
	    
	    statelessSession.createNativeQuery(updateILQuery).setParameter("min", min).setParameter("max", max).executeUpdate();
	}
	
	private void markCanceledWOs(String prefix, String viewName, long min, long max) {
	    String updateILQuery = "UPDATE billing_wallet_operation wo " +
	            "SET status='CANCELED', updated = CURRENT_TIMESTAMP, reject_Reason='Origin wallet operation has been rerated' " +
	            "	FROM " + viewName + " rr CROSS JOIN unnest(string_to_array(" + prefix + "wo_id, ',')) AS to_update " +
	            "		WHERE rr.id BETWEEN :min AND :max " +
	            "			AND rr.id NOT IN (SELECT id FROM " + billedViewName + ") " +
	            "			AND wo.id = CAST(to_update AS bigint)";
	    
	    statelessSession.createNativeQuery(updateILQuery).setParameter("min", min).setParameter("max", max).executeUpdate();
	}

	private void markCanceledRTs(String prefix, String viewName, long min, long max) {
	    String updateILQuery = "UPDATE billing_Rated_transaction rt " +
	            "SET status='CANCELED', updated = CURRENT_TIMESTAMP, reject_Reason='Origin wallet operation has been rerated' " +
	            "	FROM " + viewName + " rr CROSS JOIN unnest(string_to_array(" + prefix + "rt_id, ',')) AS to_update" +
	            "		WHERE rr.id BETWEEN :min AND :max " +
	            "			AND rr.id NOT IN (SELECT id FROM " + billedViewName + ") " +
	            "			AND rt.id = CAST(to_update AS bigint)";
	    
	    statelessSession.createNativeQuery(updateILQuery).setParameter("min", min).setParameter("max", max).executeUpdate();
	}

	private void recalculateInvoiceLinesAndCancelRTs(String prefix, String viewName, long min, long max) {
	    String updateILQuery = "UPDATE billing_invoice_line il " +
	    		"	SET amount_without_tax = il.amount_without_tax + rr." + prefix + "rt_amount_without_tax, amount_with_tax = il.amount_with_tax + rr." + prefix + "rt_amount_with_tax," +
	    		"		quantity = il.quantity + rr." + prefix + "rt_quantity, amount_tax = il.amount_tax + rr." + prefix + "rt_amount_tax, updated = CURRENT_TIMESTAMP " +
	            "	FROM " + viewName + " rr " +
	            "		WHERE rr.id NOT IN (SELECT id FROM rerate_billed_IL) " +
	            "		AND rr." + prefix + "il_id IS NOT NULL " +
	            "		AND il.id = rr." + prefix + "il_id " +
	            "		AND rr.id BETWEEN :min AND :max";

	    statelessSession.createNativeQuery(updateILQuery).setParameter("min", min).setParameter("max", max).executeUpdate();
	    markCanceledRTs(prefix, viewName, min, max);
	}

}