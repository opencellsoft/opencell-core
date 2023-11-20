package org.meveo.admin.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Resource;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;

import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.commons.utils.MethodCallingUtils;
import org.meveo.model.IEntity;
import org.meveo.model.audit.ChangeOriginEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.audit.AuditOrigin;
import org.meveo.service.job.JobExecutionResultService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 */
@Stateless
public class IteratorBasedJobProcessing implements Serializable {

    private static final long serialVersionUID = -9121240812039114457L;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    protected JobExecutionService jobExecutionService;

    @Resource(lookup = "java:jboss/ee/concurrency/executor/job_executor")
    protected ManagedExecutorService executor;

    @Inject
    protected CurrentUserProvider currentUserProvider;

    /** Logger. */
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    private MethodCallingUtils methodCallingUtils;

    @Inject
    private JobExecutionResultService jobExecutionResultService;

    /**
     * Execute a given function on a list of items (accessible with iterator) and return execution results
     * 
     * @param <T> Iterator's item class
     * @param <R> Result class
     * @param jobExecutionResult Job execution result
     * @param iterator Iterator containing a list of items to apply a function on
     * @param processSingleItemFunction A function to process a single item. Will be executed in its own transaction when parameter processItemInOwnTx=true
     * @param nbThreads Number of threads to launch
     * @param waitingMillis Number of milliseconds to wait between launching new thread
     * @param processItemInNewTx Shall function be processed in a new transaction
     * @param updateJobExecutionStatistics Shall job execution statistics be updated on each item processed (successfully or not)
     * @return Function execution results
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public <T, R> List<R> processItemsAndAgregateResults(JobExecutionResultImpl jobExecutionResult, Iterator<T> iterator, Function<T, R> processSingleItemFunction, Long nbThreads, Long waitingMillis,
            boolean processItemInNewTx, boolean updateJobExecutionStatistics) {

        List<Future> futures = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();

        JobInstance jobInstance = jobExecutionResult.getJobInstance();
        Long jobInstanceId = jobInstance.getId();

        List<Callable<List<R>>> tasks = new ArrayList<Callable<List<R>>>(nbThreads.intValue());
        String auditOriginName = jobInstance.getJobTemplate() + "/" + jobInstance.getCode();

        for (int k = 0; k < nbThreads; k++) {

            int finalK = k;
            tasks.add(() -> {

                List<R> taskResult = new ArrayList<R>();

                Thread.currentThread().setName(jobInstance.getCode() + "-" + finalK);

                currentUserProvider.reestablishAuthentication(lastCurrentUser);

                AuditOrigin.setAuditOriginAndName(ChangeOriginEnum.JOB, auditOriginName);

                int i = 0;
                long globalI = 0;

                T itemToProcess = iterator.next();
                while (itemToProcess != null) {

                    if (BaseJobBean.isJobRequestedToStop(jobInstanceId)) {
                        break;
                    }

                    try {
                        T itemToProcessFinal = itemToProcess;
                        R itemResult = null;
                        if (processItemInNewTx) {

                            Callable<R> function = () -> processSingleItemFunction.apply(itemToProcessFinal);
                            itemResult = methodCallingUtils.callCallableInNewTx(function);

                        } else {
                            itemResult = processSingleItemFunction.apply(itemToProcessFinal);
                        }

                        if (itemResult != null) {
                            taskResult.add(itemResult);
                            if (updateJobExecutionStatistics) {
                                globalI = jobExecutionResult.registerSucces();
                            }
                        }

                    } catch (Exception e) {

                        String rejectReason = org.meveo.commons.utils.StringUtils.truncate(e.getMessage(), 255, true);
                        if (updateJobExecutionStatistics) {
                            globalI = jobExecutionResult.registerError(itemToProcess + ": " + rejectReason);
                        }
                    }

                    itemToProcess = iterator.next();
                }

                return taskResult;
            });
        }

        // Tracks if job's main thread is still running. Used only to stop job status reporting thread.
        boolean[] isProcessing = { !jobExecutionService.isJobCancelled(jobInstanceId) };

        // Start job status report task. Not run in future, so it will die when main thread dies
        Runnable jobStatusReportTask = IteratorBasedJobBean.getJobStatusReportingTask(jobInstance.getCode(), lastCurrentUser, jobInstance.getJobStatusReportFrequency(), jobExecutionResult, isProcessing,
            currentUserProvider, log, jobExecutionResultService);
        Thread jobStatusReportThread = new Thread(jobStatusReportTask);
        jobStatusReportThread.start();

        // Launch main processing tasks
        int i = 0;
        for (Callable task : tasks) {
            log.info("{}/{} Will submit task #{} to run", jobInstance.getJobTemplate(), jobInstance.getCode(), i++);
            futures.add(executor.submit(task));
            try {
                Thread.sleep(waitingMillis.longValue());
            } catch (InterruptedException e) {
                log.error("", e);
            }
        }

        @SuppressWarnings("unused")
        JobRunningStatusEnum jobStatus = jobExecutionService.markJobAsRunning(jobInstance, false, jobExecutionResult.getId(), futures);

        boolean wasKilled = false;

        List<R> processingResult = new ArrayList<R>();

        // Wait for all async methods to finish
        for (Future future : futures) {
            try {
                List<R> futureResult = (List<R>) future.get();
                processingResult.addAll(futureResult);

            } catch (InterruptedException | CancellationException e) {
                wasKilled = true;
                log.error("Thread/future for job {} was canceled", jobInstance);

            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                jobExecutionResult.registerError(cause.getMessage());
                log.error("Failed to execute async method", cause);
            }
        }

        // This will exit the status report task
        isProcessing[0] = false;
        jobStatusReportThread.interrupt();

        // Mark job as stopped if task was killed
        if (wasKilled) {
            jobExecutionService.markJobToStop(jobInstance);

            // Mark that all threads are finished
        } else {
            jobStatus = jobExecutionService.markJobAsRunning(jobInstance, false, jobExecutionResult.getId(), null);
        }

        return processingResult;
    }

    /**
     * Execute a given consumer function on a list of items (accessible with iterator)
     * 
     * @param <T> Iterator's item class
     * @param jobExecutionResult Job execution result
     * @param iterator Iterator containing a list of items to apply a function on
     * @param processSingleItemFunction A function to process a single item. Will be executed in its own transaction when parameter processItemInOwnTx=true.
     * @param processMultipleItemFunction A function to process multiple items. Will be executed in its own transaction when parameter processItemInOwnTx=true.
     * @param batchSize Batch size when processing multiple items together
     * @param nbThreads Number of threads to launch
     * @param waitingMillis Number of milliseconds to wait between launching new thread
     * @param processItemInNewTx Shall function be processed in a new transaction
     * @param updateJobExecutionStatistics Shall job execution statistics be updated on each item processed (successfully or not)
     * @param True if job was canceled (killed or requested to stop)
     */
    @SuppressWarnings({ "rawtypes" })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public <T> boolean processItems(JobExecutionResultImpl jobExecutionResult, Iterator<T> iterator, BiConsumer<T, JobExecutionResultImpl> processSingleItemFunction,
            BiConsumer<List<T>, JobExecutionResultImpl> processMultipleItemFunction, Long batchSize, Long nbThreads, Long waitingMillis, boolean processItemInNewTx, boolean updateJobExecutionStatistics) {

        List<Future> futures = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        Long jobInstanceId = jobInstance.getId();

        boolean useMultipleItemProcessing = (processMultipleItemFunction != null && batchSize != null && batchSize > 1) || processSingleItemFunction == null;

        List<Runnable> tasks = new ArrayList<Runnable>(nbThreads.intValue() > 0 ? nbThreads.intValue() : 0);
        String auditOriginName = jobInstance.getJobTemplate() + "/" + jobInstance.getCode();

        for (int k = 0; k < nbThreads; k++) {

            int finalK = k;
            tasks.add(() -> {

                Thread.currentThread().setName(jobInstance.getCode() + "-" + finalK);

                currentUserProvider.reestablishAuthentication(lastCurrentUser);
                AuditOrigin.setAuditOriginAndName(ChangeOriginEnum.JOB, auditOriginName);

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

                            if (BaseJobBean.isJobRequestedToStop(jobInstanceId)) {
                                break mainLoop;
                            }
                            i++;
                            nrOfItemsInBatch++;
                        }

                        // Process items in batch
                        try {
                            if (processItemInNewTx) {
                                methodCallingUtils.callMethodInNewTx(() -> processMultipleItemFunction.accept(itemsToProcess, jobExecutionResult));
                            } else {
                                processMultipleItemFunction.accept(itemsToProcess, jobExecutionResult);
                            }

                            if (updateJobExecutionStatistics) {
                                globalI = jobExecutionResult.registerSucces(nrOfItemsInBatch);
                            }

                            // Batch processing has failed, so process item one by one
                        } catch (Exception e) {

                            if (processSingleItemFunction != null) {
                                // reset counter to previous value, so job continuity check would still be valid
                                i = i - itemsToProcess.size();

                                for (T itemToProcessFromFailedBatch : itemsToProcess) {
                                    globalI = processItem(itemToProcessFromFailedBatch, processItemInNewTx, processSingleItemFunction, jobExecutionResult, updateJobExecutionStatistics);
                                    i++;
                                }
                            }
                        }

                    } else {

                        if (BaseJobBean.isJobRequestedToStop(jobInstanceId)) {
                            break;
                        }
                        // Process each item
                        globalI = processItem(itemToProcess, processItemInNewTx, processSingleItemFunction, jobExecutionResult, updateJobExecutionStatistics);
                    }

                    itemToProcess = iterator.next();
                    i++;
                }
            });
        }

        // Tracks if job's main thread is still running. Used only to stop job status reporting thread.
        boolean[] isProcessing = { !jobExecutionService.isJobCancelled(jobInstanceId) };

        // Start job status report task. Not run in future, so it will die when main thread dies
        Runnable jobStatusReportTask = IteratorBasedJobBean.getJobStatusReportingTask(jobInstance.getCode(), lastCurrentUser, jobInstance.getJobStatusReportFrequency(), jobExecutionResult, isProcessing,
            currentUserProvider, log, jobExecutionResultService);
        Thread jobStatusReportThread = new Thread(jobStatusReportTask);
        jobStatusReportThread.start();

        // Launch main processing tasks
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

        @SuppressWarnings("unused")
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

        // This will exit the status report task
        isProcessing[0] = false;
        jobStatusReportThread.interrupt();

        // Mark job as stopped if task was killed
        if (wasKilled) {
            jobExecutionService.markJobToStop(jobInstance);

            // Mark that all threads are finished
        } else {
            jobStatus = jobExecutionService.markJobAsRunning(jobInstance, false, jobExecutionResult.getId(), null);
        }

        boolean wasCanceled = wasKilled || jobStatus == JobRunningStatusEnum.REQUEST_TO_STOP;

        return wasCanceled;
    }

    /**
     * Process a single item
     * 
     * @param itemToProcess Item to process
     * @param isNewTx Shall a new trasaction be initiated. If false, its expected that transaction handling will be provided by the function itself
     * @param processSingleItemFunction A function to process a single item
     * @param jobExecutionResult Job execution results
     * @param updateJobExecutionStatistics True to update job execution statistics
     * @return If updateJobExecutionStatistics=true, a total number of processed items, successful or failed. Otherwise a -1
     */
    private <T> long processItem(T itemToProcess, boolean isNewTx, BiConsumer<T, JobExecutionResultImpl> processSingleItemFunction, JobExecutionResultImpl jobExecutionResult, boolean updateJobExecutionStatistics) {

        try {
            if (isNewTx) {
                methodCallingUtils.callMethodInNewTx(() -> processSingleItemFunction.accept(itemToProcess, jobExecutionResult));
            } else {
                processSingleItemFunction.accept(itemToProcess, jobExecutionResult);
            }
            if (updateJobExecutionStatistics) {
                return jobExecutionResult.registerSucces();
            }

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
                if (updateJobExecutionStatistics) {
                    return jobExecutionResult.registerError(itemId, org.meveo.commons.utils.StringUtils.truncate(e.getMessage(), 255, true));
                }

            } else {
                log.error("Failed to process item", e);
                if (updateJobExecutionStatistics) {
                    return jobExecutionResult.registerError(org.meveo.commons.utils.StringUtils.truncate(e.getMessage(), 255, true));
                }
            }
        }

        return -1L;
    }
}