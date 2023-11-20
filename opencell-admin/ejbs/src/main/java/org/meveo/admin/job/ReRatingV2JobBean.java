package org.meveo.admin.job;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import org.meveo.model.rating.EDR;
import org.meveo.service.billing.impl.ReratingService;
import org.meveo.service.job.Job;

@Stateless
public class ReRatingV2JobBean extends IteratorBasedJobBean<List<Object[]>> {

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
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
		super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::applyReRating,
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
		statelessSession = entityManager.unwrap(Session.class).getSessionFactory().openStatelessSession();
		getProcessingSummary();
		if (nrOfInitialWOs.intValue() == 0) {
			dropView();
			return Optional.empty();
		}
		jobExecutionResult.addReport(" Start rerate step for " + nrOfInitialWOs + " WOs");
		
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

	private void applyReRating(List<Object[]> reratingTree, JobExecutionResultImpl jobExecutionResult) {

		if (reratingTree != null) {
			long min = ((Number) reratingTree.get(0)[0]).longValue();
			long max = ((Number) reratingTree.get(reratingTree.size() - 1)[0]).longValue();
			Long count = reratingTree.stream().mapToLong(x -> ((Number) x[1]).longValue()).sum();
			log.info("start processing " + count + " items, view lines from: " + min + " to: " + max);
			rerateMainWO(min, max);
			
		}
	}

	
	private void rerateMainWO(long min, long max) {
		
		String readWOsQuery = "SELECT wo.* FROM billing_wallet_operation wo WHERE wo.status='TO_RERATE' and wo.id IN "
				+ "(SELECT CAST(unnest(string_to_array(wo_id, ',')) AS bigint) as to_rerate FROM rerate_tree rr WHERE rr.billed_il is null and rr.id between :min and :max)";

		List<WalletOperation> walletOperations = entityManager.createNativeQuery(readWOsQuery, WalletOperation.class).setParameter("min", min).setParameter("max", max).getResultList();
		
		String readEDRsQuery = "SELECT edr.* FROM rating_edr edr WHERE edr.id IN "
			    + "(SELECT wo.edr_id FROM billing_wallet_operation wo WHERE wo.status='TO_RERATE' and wo.id IN "
			    + "(SELECT CAST(unnest(string_to_array(wo_id, ',')) AS bigint) as to_rerate FROM rerate_tree rr WHERE rr.billed_il is null and rr.id between :min and :max))";

		List<EDR> edrs = entityManager.createNativeQuery(readEDRsQuery, EDR.class).setParameter("min", min).setParameter("max", max).getResultList();

		// Map EDRs to their corresponding WalletOperations
		Map<Long, EDR> edrMap = edrs.stream().collect(Collectors.toMap(EDR::getId, Function.identity()));
		Long edrId=null;
		// Associate each EDR with its WalletOperation
		for (WalletOperation walletOperation : walletOperations) {
		     edrId = walletOperation.getEdr().getId(); 
		    if (edrId != null) {
		        EDR edr = edrMap.get(edrId);
		        walletOperation.setEdr(edr);
		    }
		}
		
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
					//statement.execute("drop materialized view if exists " + viewName);
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

}