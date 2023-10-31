package org.meveo.admin.job;

import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.job.ScopedJob;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Optional;

/**
 * Implements job logic to iterate over data and process one item at a time
 *
 * @author Abdellatif BARI
 * @since 16.0.0
 */
public abstract class IteratorBasedScopedJobBean<T> extends IteratorBasedJobBean<T> {

    private static final long serialVersionUID = 1L;

    @Inject
    protected JobInstanceService jobInstanceService;

    abstract Optional<Iterator<T>> getSynchronizedIteratorWithLimit(JobExecutionResultImpl jobExecutionResult, int jobItemsLimit);

    abstract Optional<Iterator<T>> getSynchronizedIterator(JobExecutionResultImpl jobExecutionResult);

    protected Optional<Iterator<T>> getIterator(JobExecutionResultImpl jobExecutionResult) {
        JobInstance jobInstance = jobExecutionResult.getJobInstance();
        Job job = jobInstanceService.getJobByName(jobInstance.getJobTemplate());
        if (ScopedJob.class.isAssignableFrom(job.getClass())) {
            Integer jobItemsLimit = ((ScopedJob) job).getJobItemsLimit(jobInstance);
            if (jobItemsLimit != null && jobItemsLimit > 0) {
                return getSynchronizedIteratorWithLimit(jobExecutionResult, jobItemsLimit);
            }
        }
        return getSynchronizedIterator(jobExecutionResult);
    }
}