package org.meveo.admin.job;

import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.job.ScopedJob;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Optional;


public abstract class IteratorBasedScopedJobBean<T> extends IteratorBasedJobBean {

    private static final long serialVersionUID = 1L;

    @Inject
    protected JobInstanceService jobInstanceService;

    abstract Optional<Iterator<T>> getSynchronizedIteratorWithLimit(JobInstance jobInstance, int jobItemsLimit);

    abstract Optional<Iterator<T>> getSynchronizedIterator(JobInstance jobInstance);

    protected Optional<Iterator<T>> getIterator(JobInstance jobInstance) {
        Job job = jobInstanceService.getJobByName(jobInstance.getJobTemplate());
        if (ScopedJob.class.isAssignableFrom(job.getClass())) {
            Integer jobItemsLimit = ((ScopedJob) job).getJobItemsLimit(jobInstance);
            if (jobItemsLimit != null && jobItemsLimit > 0) {
                return getSynchronizedIteratorWithLimit(jobInstance, jobItemsLimit);
            }
        }
        return getSynchronizedIterator(jobInstance);
    }
}