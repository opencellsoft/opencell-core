package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Query;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class UpdateStepExecutor extends IteratorBasedJobBean<Long[]> {

    private static final Logger log = LoggerFactory.getLogger(UpdateStepExecutor.class);

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    public static final String PARAM_MIN_ID = "minId";
    public static final String PARAM_MAX_ID = "maxId";
    public static final String PARAM_CHUNK_SIZE = "chunkSize";
    public static final String PARAM_UPDATE_QUERY = "updateQuery";
    public static final String PARAM_NAMED_QUERY = "namedQuery";
    public static final String PARAM_TABLE_ALIAS = "tableAlias";

    /**
     * Initializes intervals for updating based on the specified chunk size.
     *
     * @return An iterator of Long[] representing intervals.
     */
    public Optional<Iterator<Long[]>> initFunction(JobExecutionResultImpl jobExecutionResult) {
        List<Long[]> intervals = new ArrayList<>();

        Long minId = (Long) jobExecutionResult.getJobParam(PARAM_MIN_ID);
        Long maxId = (Long) jobExecutionResult.getJobParam(PARAM_MAX_ID);
        Long chunkSize = (Long) jobExecutionResult.getJobParam(PARAM_CHUNK_SIZE);

        if (minId == null || maxId == null || chunkSize == null) {
            log.error("params should not be null - minId: {}, maxId: {}, chunkSize: {}", minId, maxId, chunkSize);
            return Optional.empty();
        }

        long start = minId;

        while (start <= maxId) {
            long end = Math.min(start + chunkSize - 1, maxId);
            intervals.add(new Long[] { start, end });
            start = end + 1;
        }
        return Optional.of(new SynchronizedIterator(intervals));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initFunction, this::processUpdateByInterval, null, null, null, null, true);
    }

    /**
     * Process an update operation for a specific interval.
     *
     * @param interval           The interval to update.
     * @param jobExecutionResult The job execution result.
     */
	private void processUpdateByInterval(Long[] interval, JobExecutionResultImpl jobExecutionResult) {
		String namedQuery = (String) jobExecutionResult.getJobParam(PARAM_NAMED_QUERY);
		String updateQuery = (String) jobExecutionResult.getJobParam(PARAM_UPDATE_QUERY);
		String tableAlias = (String) jobExecutionResult.getJobParam(PARAM_TABLE_ALIAS);

		if (namedQuery == null && (tableAlias == null || updateQuery == null)) {
			log.error("params should not be null - updateQuery: {}, tableAlias: {}, namedQuery: {}", updateQuery, tableAlias, namedQuery);
			return;
		}

		Query query = namedQuery != null ? emWrapper.getEntityManager().createNamedQuery(namedQuery)
				: emWrapper.getEntityManager().createQuery((updateQuery.toUpperCase().contains("WHERE") ? " AND " : " WHERE ") + tableAlias + ".id BETWEEN :minId AND :maxId");

		query.setParameter("minId", interval[0]).setParameter("maxId", interval[1]).executeUpdate();
	}
}
