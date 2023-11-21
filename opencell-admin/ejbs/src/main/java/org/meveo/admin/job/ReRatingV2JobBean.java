package org.meveo.admin.job;

import static org.apache.commons.collections4.ListUtils.partition;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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
import org.hibernate.query.NativeQuery;
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
public class ReRatingV2JobBean extends IteratorBasedJobBean<List<Object[]>> {

	private static final long serialVersionUID = 8799763764569695857L;

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
		NativeQuery nativeQuery = statelessSession.createNativeQuery("SELECT CAST(unnest(string_to_array(wo_id, ',')) AS bigint) as id FROM " + viewName + " WHERE billed_il is null order by s_id");
		scrollableResults = nativeQuery.setReadOnly(true).setCacheable(false).setMaxResults(processNrInJobRun).setFetchSize(fetchSize).scroll(ScrollMode.FORWARD_ONLY);

		return Optional.of(
				new SynchronizedMultiItemIterator<Object[]>(scrollableResults, nrOfInitialWOs.intValue(), true, null) {

					long count = 0L;

					@Override
					public void initializeDecisionMaking(Object[] item) {
						count = 1;
					}

					@Override
					public boolean isIncludeItem(Object[] item) {
						if (count ++ > nrPerTx) {
							return false;
						}
						return true;
					}
				});
	}

	private void applyReRating(List<Object[]> reratingTree, JobExecutionResultImpl jobExecutionResult) {
		if (reratingTree != null) {
			rerateByGroup(reratingTree.stream().map(x->((Number)x[0]).longValue()).collect(Collectors.toList()));
		}
	}

	
	private void rerateByGroup(List<Long> reratingTree) {
    	final int maxValue = ParamBean.getInstance().getPropertyAsInteger("database.number.of.inlist.limit", reratingService.SHORT_MAX_VALUE);
    	List<List<Long>> subList = partition(reratingTree, maxValue);
    	subList.forEach(ids -> rerate(ids));
    	
		
	}

	private void rerate(List<Long> ids) {
		String readWOsQuery = "FROM WalletOperation wo left join fetch wo.chargeInstance ci left join fetch wo.wallet w left join fetch w.userAccount ua left JOIN FETCH wo.edr edr WHERE wo.status='TO_RERATE' AND wo.id IN (:ids)";
		List<WalletOperation> walletOperations = entityManager.createQuery(readWOsQuery, WalletOperation.class).setParameter("ids", ids).getResultList();
		List<Long> failedIds = new ArrayList<>();
		walletOperations.stream().forEach(operationToRerate -> {
		    try {
		        reratingService.rerateWalletOperationAndInstantiateTriggeredEDRs(operationToRerate, useSamePricePlan, false);
		    } catch (Exception e) {
		        failedIds.add(operationToRerate.getId());
		        throw e;
		    }
		});
		ids.removeAll(failedIds);
		String updateILQuery = "UPDATE billing_wallet_operation wo SET status='RERATED', updated = CURRENT_TIMESTAMP where id in (:ids) ";
		statelessSession.createNativeQuery(updateILQuery).setParameter("ids", ids).executeUpdate();
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
		Object[] count = (Object[]) entityManager.createNativeQuery("select sum(count_wo), count(id) from " + viewName).getSingleResult();
		nrOfInitialWOs = count[0] != null ? ((Number) count[0]).longValue() : null;
	}

}