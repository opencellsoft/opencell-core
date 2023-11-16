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
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.ReratingService;
import org.meveo.service.job.Job;

@Stateless
public class RatingCancellationJobBean extends IteratorBasedJobBean<List<Object[]>> {

	private static final long serialVersionUID = -4097694568061727769L;
	private static final String viewName = "rerate_tree";

	@Inject
	@MeveoJpa
	private EntityManagerWrapper emWrapper;
	
	private EntityManager entityManager;

	private StatelessSession statelessSession;
	
	private ScrollableResults scrollableResults;

	private Long nrOfInitialWOs = null;
	
	private boolean useSamePricePlan;
	
    @Inject
    private ReratingService reratingService;

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
		super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::applyRatingCancellation,
				null, null, this::closeResultset, null);
	}

	@SuppressWarnings({ "unchecked" })
	private Optional<Iterator<List<Object[]>>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

		useSamePricePlan = "justPrice".equalsIgnoreCase(jobExecutionResult.getJobInstance().getParametres());
		
		JobInstance jobInstance = jobExecutionResult.getJobInstance();

		int processNrInJobRun = ParamBean.getInstance().getPropertyAsInteger("RatingCancellationJob.processNrInJobRun", 10000000);

		Long nbThreads = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
		if (nbThreads == -1) {
			nbThreads = (long) Runtime.getRuntime().availableProcessors();
		}

		final long configuredNrPerTx = (Long) this.getParamOrCFValue(jobInstance, RatingCancellationJob.CF_INVOICE_LINES_NR_RTS_PER_TX, 100000L);
		
		entityManager = emWrapper.getEntityManager();
		createView(configuredNrPerTx);
		statelessSession = entityManager.unwrap(Session.class).getSessionFactory().openStatelessSession();
		getProcessingSummary();
		if (nrOfInitialWOs.intValue() == 0) {
			dropView();
			return Optional.empty();
		}
		jobExecutionResult.addReport(" Start rerate cancelllation process for " + nrOfInitialWOs + " WOs");
		
		final long nrPerTx = (nrOfInitialWOs / nbThreads) < configuredNrPerTx ? nrOfInitialWOs / nbThreads : configuredNrPerTx;
		int fetchSize = ((Long) nrPerTx).intValue() * nbThreads.intValue();
		org.hibernate.query.Query nativeQuery = statelessSession.createNativeQuery("select id, count_wo from " + viewName + " order by id");
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
			rerateMainWO(min, max);
			
		}
	}

	private void cancelAllObjects(long min, long max) {
		markFailedToRerate("", min, max);
		markFailedToRerate("t", min, max);
		markFailedToRerate("d", min, max);

		recalculateInvoiceLinesAndCancelRTs("", min, max);
		recalculateInvoiceLinesAndCancelRTs("t", min, max);
		recalculateInvoiceLinesAndCancelRTs("d", min, max);


		markCanceledEDRs(min, max);

		markCanceledWOs("t", min, max);
		markCanceledWOs("d", min, max);
	}
	
	private void rerateMainWO(long min, long max) {
		String readWOsQuery = "SELECT wo.* FROM billing_wallet_operation wo WHERE wo.status='TO_RERATE' and wo.id IN "
				+ "(SELECT CAST(unnest(string_to_array(wo_id, ',')) AS bigint) as to_rerate FROM rerate_tree rr WHERE rr.billed_il is null and rr.id between :min and :max)";

		List<WalletOperation> walletOperations = entityManager.createNativeQuery(readWOsQuery, WalletOperation.class).setParameter("min", min).setParameter("max", max).getResultList();
		walletOperations.stream().forEach(operationToRerate -> reratingService.rerateWalletOperationAndInstantiateTriggeredEDRs(operationToRerate, useSamePricePlan));
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
		dropView();
	}

	private void dropView() {
		Session hibernateSession = entityManager.unwrap(Session.class);

		hibernateSession.doWork(new org.hibernate.jdbc.Work() {
			@Override
			public void execute(Connection connection) throws SQLException {

				try (Statement statement = connection.createStatement()) {
					log.info("Dropping materialized view {}", viewName);
					statement.execute("drop materialized view if exists " + viewName);
				} catch (Exception e) {
					log.error("Failed to drop/create the materialized view " + viewName, e.getMessage());
					throw new BusinessException(e);
				}
			}
		});
	}

	private void getProcessingSummary() {

		Object[] count = (Object[]) entityManager.createNativeQuery("select sum(count_wo), count(id) from " + viewName)
				.getSingleResult();

		nrOfInitialWOs = count[0] != null ? ((Number) count[0]).longValue() : null;

	}

	private void createView(long configuredNrPerTx) {
		Session hibernateSession = entityManager.unwrap(Session.class);
		String sql = "CREATE MATERIALIZED VIEW " + viewName + " AS\n"
				+ "SELECT string_agg(edr.id::text,',') as edr_id, string_agg(wo.id::text,',') AS wo_id, string_agg(rt.id::text,',') as rt_id, il.id as il_id, sum(rt.amount_without_tax) as rt_amount_without_tax, sum(rt.amount_with_tax) as rt_amount_with_tax, sum(rt.amount_tax) as rt_amount_tax, sum(rt.quantity) as rt_quantity,\n"
				+ "			string_agg(dwo.id::text,',') AS dwo_id, string_agg(drt.id::text,',') as drt_id, dil.id as dil_id, sum(drt.amount_without_tax) as drt_amount_without_tax, sum(drt.amount_with_tax) as drt_amount_with_tax, sum(drt.amount_tax) as drt_amount_tax, sum(drt.quantity) as drt_quantity, \n"
				+ "			string_agg(two.id::text,',') AS two_id, string_agg(trt.id::text,',') as trt_id, til.id as til_id, sum(trt.amount_without_tax) as trt_amount_without_tax, sum(trt.amount_with_tax) as trt_amount_with_tax, sum(trt.amount_tax) as trt_amount_tax, sum(trt.quantity) as trt_quantity, \n"
				+ "			wo.subscription_id as s_id, CASE  WHEN il.status = 'BILLED' THEN il.id  WHEN dil.status = 'BILLED' THEN dil.id WHEN til.status = 'BILLED' THEN til.id ELSE null END AS billed_il, count(1) as count_WO, ROW_NUMBER() OVER (ORDER BY count(1)/"+configuredNrPerTx+" desc, wo.subscription_id) AS id\n"
				+ "	FROM billing_wallet_operation wo\n"
				+ "		LEFT JOIN billing_rated_transaction rt ON rt.id = wo.rated_transaction_id and rt.status<>'CANCELED'\n"
				+ "		LEFT JOIN billing_invoice_line il ON il.id = rt.invoice_line_id\n"
				+ "		LEFT JOIN billing_wallet_operation dwo ON wo.id = dwo.discounted_wallet_operation_id\n"
				+ "		LEFT JOIN billing_rated_transaction drt ON drt.id = dwo.rated_transaction_id and drt.status<>'CANCELED'\n"
				+ "		LEFT JOIN billing_invoice_line dil ON dil.id = drt.invoice_line_id\n"
				+ "		LEFT JOIN rating_edr edr ON edr.id = wo.edr_id and edr.status <> 'CANCELLED'\n"
				+ "		LEFT JOIN billing_wallet_operation two ON two.id = edr.wallet_operation_id\n"
				+ "		LEFT JOIN billing_rated_transaction trt ON trt.id = two.rated_transaction_id and trt.status<>'CANCELED'\n"
				+ "		LEFT JOIN billing_invoice_line til ON til.id = trt.invoice_line_id\n"
				+ "WHERE wo.status = 'TO_RERATE'\n" + "group by s_id, il_id, dil_id, til_id";

		hibernateSession.doWork(new org.hibernate.jdbc.Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				try (Statement statement = connection.createStatement()) {
					log.info("Dropping and recreating materialized view {} : ", viewName);
					statement.execute("drop materialized view if EXISTS " + viewName);
					statement.execute(sql);
					statement.execute("create index idx__" + viewName + "__subscription_id ON " + viewName + " USING btree (s_id) ");
				} catch (Exception e) {
					log.error("Failed to drop/create the materialized view " + viewName, e.getMessage());
					throw new BusinessException(e);
				}
			}
		});
	}

	private void markCanceledRTs(String prefix, long min, long max) {
		String updateILQuery = "UPDATE billing_Rated_transaction rt SET status='CANCELED', updated = CURRENT_TIMESTAMP, reject_Reason='Origin wallet operation has been rerated' "
				+ " FROM " + viewName 
				+ " rr, unnest(string_to_array(" + prefix + "rt_id, ',')) AS to_update WHERE rr.billed_il is null and rr.id between :min and :max and " + prefix + "rt_id is not null and rt.id = CAST(to_update AS bigint)";
		statelessSession.createNativeQuery(updateILQuery).setParameter("min", min).setParameter("max", max)
				.executeUpdate();
	}

	private void markCanceledWOs(String prefix, long min, long max) {
		String updateILQuery = "UPDATE billing_wallet_operation wo SET status='CANCELED', updated = CURRENT_TIMESTAMP, reject_Reason='Origin wallet operation has been rerated' "
				+ " FROM " + viewName 
				+ " rr, unnest(string_to_array(" + prefix + "wo_id, ',')) AS to_update WHERE rr.billed_il is null and rr.id between :min and :max and " + prefix + "wo_id is not null and wo.id = CAST(to_update AS bigint)";
		statelessSession.createNativeQuery(updateILQuery).setParameter("min", min).setParameter("max", max)
				.executeUpdate();
	}

	private void markCanceledEDRs(long min, long max) {
		String updateILQuery = "UPDATE rating_EDR edr SET status='CANCELLED', last_updated = CURRENT_TIMESTAMP, reject_Reason='Origin wallet operation has been rerated' "
				+ " FROM " + viewName
				+ " rr, unnest(string_to_array(edr_id, ',')) AS to_update WHERE rr.billed_il is null and rr.id between :min and :max and edr_id is not null and edr.id = CAST(to_update AS bigint)";
		statelessSession.createNativeQuery(updateILQuery).setParameter("min", min).setParameter("max", max)
				.executeUpdate();
	}

	private void markFailedToRerate(String prefix, long min, long max) {
		String updateILQuery = "UPDATE billing_wallet_operation wo SET status='F_TO_RERATE', updated = CURRENT_TIMESTAMP, reject_reason='failed to rerate operation because invoiceLine '||rr.billed_il||' already billed' "
				+ " FROM " + viewName 
				+ " rr, unnest(string_to_array(" + prefix + "wo_id, ',')) AS to_update WHERE rr.billed_il is not null and rr.id between :min and :max and wo.id = CAST(to_update AS bigint)";
		statelessSession.createNativeQuery(updateILQuery).setParameter("min", min).setParameter("max", max)
				.executeUpdate();
	}

	private void recalculateInvoiceLinesAndCancelRTs(String prefix, long min, long max) {
		String updateILQuery = "UPDATE billing_invoice_line il SET"
				+ "    amount_without_tax = il.amount_without_tax + rr." + prefix
				+ "rt_amount_without_tax, amount_with_tax = il.amount_with_tax + rr." + prefix + "rt_amount_with_tax,"
				+ "    quantity = il.quantity + rr." + prefix + "rt_quantity, amount_tax = il.amount_tax + rr." + prefix
				+ "rt_amount_tax, updated = CURRENT_TIMESTAMP" + " FROM " + viewName
				+ " rr WHERE rr.billed_il is null and rr." + prefix + "il_id is not null and il.id = rr." + prefix + "il_id and rr.id between :min and :max";
		statelessSession.createNativeQuery(updateILQuery).setParameter("min", min).setParameter("max", max)
				.executeUpdate();
		markCanceledRTs(prefix, min, max);
	}

}