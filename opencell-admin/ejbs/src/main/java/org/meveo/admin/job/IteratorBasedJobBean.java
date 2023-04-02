package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
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
import org.meveo.service.job.Job;

/**
 * Implements job logic to iterate over data and process one item at a time, checking if job is still running and update job progress in DB periodically
 * 
 * <pre>
 * If queue-size is specified it will queue tasks untill queue is filled before a new thread is created. It acts as a buffer to not create new tasks immediately.
 * 
 * E.g. core-threads=10, queue-size=5, max-thread-pool=20
 * 
 * tasks received = threads running
 * 1   = 10
 * 10 = 10
 * 11 = 10
 * 14 = 10
 * 15 = 10
 * 16 = 16
 * 19 = 16
 * 20 = 16
 * 21 = 20
 * 25 = 20
 * 26 = should be 26, but task is rejected as max-pool-size is reached
 * 
 * If we just specify core-threads=20, queue-size=300 we will never see more than 20 threads if run less than 300 users in paralel.
 * 
 * So have to keep queue-size very small - so new tasks will keep using up new threads if  none are available.
 * </pre>
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
     * Execute a job - retrieve a list of data to process, iterate over the data and process one item at a time, checking if job is still running and update job progress in DB periodically
     * 
     * @param jobExecutionResult Job execution result
     * @param jobInstance Job instance
     * @param initFunction A function to initialize the data to process
     * @param processSingleItemFunction A function to process a single item. Will be executed in its own transaction
     * @param hasMoreFunction A function to determine if the are more data to process even though this job run has completed. Optional.
     * @param finalizeFunction A function to finalize data to process. Optional.
     */
    protected void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance, Function<JobExecutionResultImpl, Optional<Iterator<T>>> initFunction,
            BiConsumer<T, JobExecutionResultImpl> processSingleItemFunction, Predicate<JobInstance> hasMoreFunction, Consumer<JobExecutionResultImpl> finalizeFunction) {

        execute(jobExecutionResult, jobInstance, initFunction, processSingleItemFunction, null, hasMoreFunction, finalizeFunction);
    }

    /**
     * Execute a job - retrieve a list of data to process, iterate over the data and process one or multiple items at a time, checking if job is still running and update job progress in DB periodically. <br/>
     * <br/>
     * 
     * If processMultipleItemFunction is provided at first an atempt to process multiple items in one transaction will be atempted. If any items fail, each item will be processed one by one in a separate transaction.
     * 
     * @param jobExecutionResult Job execution result
     * @param jobInstance Job instance
     * @param initFunction A function to initialize the data to process
     * @param processSingleItemFunction A function to process a single item. Will be executed in its own transaction
     * @param processMultipleItemFunction A function to process multiple items. Will be executed in its own transaction.
     * @param hasMoreFunction A function to determine if the are more data to process even though this job run has completed. Optional.
     * @param finalizeFunction A function to finalize data to process. Optional.
     */
    @SuppressWarnings("rawtypes")
    protected void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance, Function<JobExecutionResultImpl, Optional<Iterator<T>>> initFunction,
            BiConsumer<T, JobExecutionResultImpl> processSingleItemFunction, BiConsumer<List<T>, JobExecutionResultImpl> processMultipleItemFunction, Predicate<JobInstance> hasMoreFunction,
            Consumer<JobExecutionResultImpl> finalizeFunction) {

        jobExecutionErrorService.purgeJobErrors(jobExecutionResult.getJobInstance());

        Optional<Iterator<T>> iteratorOpt = initFunction.apply(jobExecutionResult);

        if (!iteratorOpt.isPresent()) {
            return;
        }

        Iterator<T> iterator = iteratorOpt.get();

        if (iterator instanceof SynchronizedIterator) {
            jobExecutionResult.setNbItemsToProcess(((SynchronizedIterator) iterator).getSize());
        }

        if (jobExecutionResult.getNbItemsToProcess() == 0) {
            log.info("{}/{} will skip as nothing to process", jobInstance.getJobTemplate(), jobInstance.getCode());
            return;
        }
        if (!jobExecutionService.isShouldJobContinue(jobInstance.getId())) {
            log.info("{}/{} will skip as should not continue", jobInstance.getJobTemplate(), jobInstance.getCode());
            return;
        }

        log.info("{}/{} - {} records to process", jobInstance.getJobTemplate(), jobInstance.getCode(), jobExecutionResult.getNbItemsToProcess());

        jobExecutionResultService.persistResult(jobExecutionResult);

        Long nbThreads = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbThreads == -1) {
            nbThreads = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        List<Future> futures = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();

        int checkJobStatusEveryNr = jobInstance.getJobSpeed().getCheckNb();
        int updateJobStatusEveryNr = nbThreads.longValue() > 3 ? jobInstance.getJobSpeed().getUpdateNb() * nbThreads.intValue() / 2 : jobInstance.getJobSpeed().getUpdateNb();

        boolean isNewTx = isProcessItemInNewTx();

        // Multiple item processing will happen only of batch size is greater than one,
        Long batchSize = (Long) getParamOrCFValue(jobInstance, Job.CF_BATCH_SIZE, 0L);
        boolean useMultipleItemProcessing = (processMultipleItemFunction != null && batchSize != null && batchSize > 1) || processSingleItemFunction == null;

        List<Runnable> tasks = new ArrayList<Runnable>(nbThreads.intValue());

        for (int k = 0; k < nbThreads; k++) {

            int finalK = k;
            tasks.add(() -> {

                Thread.currentThread().setName(jobInstance.getCode() + "-" + finalK);

                currentUserProvider.reestablishAuthentication(lastCurrentUser);

                int i = 0;
                long globalI = 0;

                T itemToProcess = iterator.next();
                mainLoop: while (itemToProcess != null) {

                    if (useMultipleItemProcessing) {

                        final List<T> itemsToProcess = new ArrayList<T>();
                        itemsToProcess.add(itemToProcess);
                        int nrOfItemsInBatch = 1;

                        while (nrOfItemsInBatch < batchSize) {
                            itemToProcess = iterator.next();
                            if (itemToProcess == null) {
                                break;
                            }

                            itemsToProcess.add(itemToProcess);

                            if (i % checkJobStatusEveryNr == 0 && !jobExecutionService.isShouldJobContinue(jobExecutionResult.getJobInstance().getId())) {
                                break mainLoop;
                            }
                            i++;
                            nrOfItemsInBatch++;
                        }

                        // Process items in batch
                        try {
                            if (isNewTx) {
                                methodCallingUtils.callMethodInNewTx(() -> processMultipleItemFunction.accept(itemsToProcess, jobExecutionResult));
                            } else {
                                processMultipleItemFunction.accept(itemsToProcess, jobExecutionResult);
                            }

                            if (!isProcessMultipleItemFunctionUpdateProgress()) {
                                globalI = jobExecutionResult.registerSucces(nrOfItemsInBatch);
                            } else {
                                globalI = globalI + nrOfItemsInBatch;
                            }

                            // Batch processing has failed, so process item one by one
                        } catch (Exception e) {

                            if (processSingleItemFunction != null) {
                                // reset counter to previous value, so job continuity check would still be valid
                                i = i - itemsToProcess.size();

                                for (T itemToProcessFromFailedBatch : itemsToProcess) {
                                    globalI = processItem(itemToProcessFromFailedBatch, isNewTx, processSingleItemFunction, jobExecutionResult);
                                    i++;
                                }
                            }
                        }

                    } else {

                        if (i % checkJobStatusEveryNr == 0 && !jobExecutionService.isShouldJobContinue(jobExecutionResult.getJobInstance().getId())) {
                            break;
                        }
                        // Process each item
                        globalI = processItem(itemToProcess, isNewTx, processSingleItemFunction, jobExecutionResult);
                    }

                    try {
                        // Record progress
                        if (globalI > 0 && globalI % updateJobStatusEveryNr == 0) {
                            jobExecutionResultService.persistResult(jobExecutionResult);
                        }
                    } catch (EJBTransactionRolledbackException e) {
                        // Will ignore the error here, as its most likely to happen - updating jobExecutionResultImpl entity from multiple threads
                    } catch (Exception e) {
                        log.error("Failed to update job progress", e);
                    }
                    itemToProcess = iterator.next();
                    i++;
                }
            });
        }

        boolean wasCanceled = false;

        try {
            int i = 0;
            for (Runnable task : tasks) {
                log.info("{}/{} Will submit task #{} to run", jobInstance.getJobTemplate(), jobInstance.getCode(), i++);
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

            // Wait for all async methods to finish
            for (Future future : futures) {
                try {
                    future.get();

                } catch (InterruptedException | CancellationException e) {
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

            wasCanceled = wasKilled || jobStatus == JobRunningStatusEnum.REQUEST_TO_STOP;

            // Check if there are any more data to process and mark job as completed if there are none
            if (!wasCanceled && hasMoreFunction != null) {
                jobExecutionResult.setMoreToProcess(hasMoreFunction.test(jobInstance));
            }

        } catch (Exception e) {
            log.error("Failed to run a job {}", jobInstance, e);
            jobExecutionResult.registerError(e.getMessage());
        }

        if (!wasCanceled && finalizeFunction != null) {
            finalizeFunction.accept(jobExecutionResult);
        }
    }

    /**
     * Process a single item
     * 
     * @param itemToProcess Item to process
     * @param isNewTx Shall a new trasaction be initiated. If false, its expected that transaction handling will be provided by the function itself
     * @param processSingleItemFunction A function to process a single item
     * @param jobExecutionResult Job execution results
     * @return A total number of processed items, successful or failed
     */
    private long processItem(T itemToProcess, boolean isNewTx, BiConsumer<T, JobExecutionResultImpl> processSingleItemFunction, JobExecutionResultImpl jobExecutionResult) {

        try {
            if (isNewTx) {
                methodCallingUtils.callMethodInNewTx(() -> processSingleItemFunction.accept(itemToProcess, jobExecutionResult));
            } else {
                processSingleItemFunction.accept(itemToProcess, jobExecutionResult);
            }

            return jobExecutionResult.registerSucces();

            // Register errors
        } catch (Exception e) {

            Long itemId = null;
            if (itemToProcess instanceof Long) {
                itemId = (Long) itemToProcess;
            } else if (itemToProcess instanceof IEntity) {
                itemId = (Long) ((IEntity) itemToProcess).getId();
            }

            if (itemId != null) {
                log.error("Failed to process item {}", itemId, e);
                jobExecutionErrorService.registerJobError(jobExecutionResult.getJobInstance(), itemId, e);
                return jobExecutionResult.registerError(itemId, e.getMessage());

            } else {
                log.error("Failed to process item", e);
                return jobExecutionResult.registerError(e.getMessage());
            }
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

    /**
     * Is "Process multiple items" function updates job progress itself
     * 
     * @return True if "Process multiple items" function updates job progress itself
     */
    protected boolean isProcessMultipleItemFunctionUpdateProgress() {
        return false;
    }
}