package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.commons.utils.MethodCallingUtils;
import org.meveo.model.IEntity;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.MeveoUser;
import org.meveo.service.job.JobExecutionService.JobSpeedEnum;

/**
 * Implements job logic to iterate over data and process one item at a time, checking if job is still running and update job progress in DB periodically
 * 
 * @author Andrius Karpavicius
 *
 * @param <T> Data type to process
 */
public abstract class IteratorBasedJobBean<T> extends BaseJobBean {

    private static final long serialVersionUID = 649152055662228506L;

    @Inject
    private MethodCallingUtils methodCallingUtils;

    /**
     * Execute job implementation
     * 
     * @param jobExecutionResult Job execution result
     * @param jobInstance Job instance
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public abstract void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance);

    /**
     * 
     * @param jobExecutionResult Job execution result
     * @param jobInstance Job instance
     * @param initFunction A function to initialize the data to process
     * @param processSingleItemFunction A function to process a single item. Will be executed in its own transaction
     * @param hasMoreFunction A function to determine if the are more data to process even though this job run has completed. Optional.
     * @param finalizeFunction A function to finalize data to process. Optional.
     * @param jobSpeed Job execution speed to check if job is still running and update job progress in DB
     */
    @SuppressWarnings("rawtypes")
    protected void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance, Function<JobExecutionResultImpl, Optional<Iterator<T>>> initFunction,
            BiConsumer<T, JobExecutionResultImpl> processSingleItemFunction, Predicate<JobInstance> hasMoreFunction, Consumer<JobExecutionResultImpl> finalizeFunction, JobSpeedEnum jobSpeed) {

        jobExecutionErrorService.purgeJobErrors(jobExecutionResult.getJobInstance());

        Optional<Iterator<T>> iteratorOpt = initFunction.apply(jobExecutionResult);

        if (!iteratorOpt.isPresent()) {
            return;
        }

        Iterator<T> iterator = iteratorOpt.get();

        if (iterator instanceof SynchronizedIterator) {
            jobExecutionResult.setNbItemsToProcess(((SynchronizedIterator) iterator).getSize());
        }

        if (jobExecutionResult.getNbItemsToProcess() == 0 || !jobExecutionService.isShouldJobContinue(jobInstance.getId())) {
            log.info("{}/{} will skip as nothing to process or should not continue", jobInstance.getJobTemplate(), jobInstance.getCode());
            return;
        }

        log.info("{}/{} - {} records to process", jobInstance.getJobTemplate(), jobInstance.getCode(), jobExecutionResult.getNbItemsToProcess());

        jobExecutionResultService.persistResult(jobExecutionResult);

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        List<Future> futures = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();

        int checkJobStatusEveryNr = jobSpeed.getCheckNb();
        int updateJobStatusEveryNr = jobSpeed.getUpdateNb();

        boolean isNewTx = isProcessItemInNewTx();

        Runnable task = () -> {

            currentUserProvider.reestablishAuthentication(lastCurrentUser);
            int i = 0;

            T itemToProcess = iterator.next();
            while (itemToProcess != null) {

                if (i % checkJobStatusEveryNr == 0 && !jobExecutionService.isShouldJobContinue(jobExecutionResult.getJobInstance().getId())) {
                    break;
                }
                try {
                    // Record progress
                    if (i > 0 && i % updateJobStatusEveryNr == 0) {
                        jobExecutionResultService.persistResult(jobExecutionResult);
                    }
                } catch (EJBTransactionRolledbackException e) {
                    // Will ignore the error here, as its most likely to happen - updating jobExecutionResultImpl entity from multiple threads
                } catch (Exception e) {
                    log.error("Failed to update job progress", e);
                }

                // Process each item
                try {
                    T itemToProcessFinal = itemToProcess;
                    if (isNewTx) {
                        methodCallingUtils.callMethodInNewTx(() -> processSingleItemFunction.accept(itemToProcessFinal, jobExecutionResult));
                    } else {
                        processSingleItemFunction.accept(itemToProcessFinal, jobExecutionResult);
                    }

                    jobExecutionResult.registerSucces();

                    // Register errors
                } catch (Exception e) {

                    Long itemId = null;
                    if (itemToProcess instanceof Long) {
                        itemId = (Long) itemToProcess;
                    } else if (itemToProcess instanceof IEntity) {
                        itemId = (Long) ((IEntity) itemToProcess).getId();
                    }

                    if (itemId != null) {
                        jobExecutionErrorService.registerJobError(jobExecutionResult.getJobInstance(), itemId, e);
                        jobExecutionResult.registerError(itemId, e.getMessage());
                    } else {
                        jobExecutionResult.registerError(e.getMessage());
                    }
                }

                itemToProcess = iterator.next();
                i++;
            }
        };

        try {
            for (int i = 0; i < nbRuns; i++) {
                log.info("{}/{} Will submit task to run", jobInstance.getJobTemplate(), jobInstance.getCode());
                futures.add(executor.submit(task));
                try {
                    Thread.sleep(waitingMillis.longValue());
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }

            // Mark number of threads it will be running on
            JobRunningStatusEnum jobStatus = jobExecutionService.markJobAsRunning(jobInstance, false, jobExecutionResult.getId(), futures);

            boolean wasKilled = false;
            if (jobStatus != JobRunningStatusEnum.REQUEST_TO_STOP) {

                // Wait for all async methods to finish
                for (Future future : futures) {
                    try {
                        future.get();

                    } catch (InterruptedException e) {
                        wasKilled = true;
                        log.error("Thread/future for job {} was canceled", jobInstance);

                    } catch (ExecutionException e) {
                        Throwable cause = e.getCause();
                        jobExecutionResult.registerError(cause.getMessage());
                        log.error("Failed to execute async method", cause);
                    }
                }

                // Mark job as stopped if task was killed
                if (wasKilled) {
                    jobExecutionService.markJobToStop(jobInstance);

                    // Mark that all threads are finished
                } else {
                    jobStatus = jobExecutionService.markJobAsRunning(jobInstance, false, jobExecutionResult.getId(), null);
                }
            }

            boolean wasCanceled = wasKilled || jobStatus == JobRunningStatusEnum.REQUEST_TO_STOP;

            // Check if there are any more data to process and mark job as completed if there are none
            if (!wasCanceled && hasMoreFunction != null) {
                jobExecutionResult.setMoreToProcess(hasMoreFunction.test(jobInstance));
            }

        } catch (Exception e) {
            log.error("Failed to run a job", e);
            jobExecutionResult.registerError(e.getMessage());
        }

        if (finalizeFunction != null) {
            finalizeFunction.accept(jobExecutionResult);
        }
    }

    /**
     * Shall each item be processed in its own transaction
     * 
     * @return True if each item shall be processed in its own transaction
     */
    protected boolean isProcessItemInNewTx() {
        return true;
    }
}