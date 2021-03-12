package org.meveo.admin.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
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
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobSpeedEnum;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionResultService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements job logic to iterate over data and process one item at a time, checking if job is still running and update job progress in DB periodically
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
     * @param processItemInOwnTx Shall function be processed in a new transaction
     * @param jobSpeed Job execution speed to check if job is still running and update job progress in DB
     * @param updateJobExecutionStatistics Shall job execution statistics be updated on each item processed (successfully or not)
     * @return Function execution results
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public <T, R> List<R> processItemsAndAgregateResults(JobExecutionResultImpl jobExecutionResult, Iterator<T> iterator, Function<T, R> processSingleItemFunction, Long nbThreads, Long waitingMillis,
            boolean processItemInOwnTx, JobSpeedEnum jobSpeed, boolean updateJobExecutionStatistics) {

        List<Future> futures = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        int checkJobStatusEveryNr = jobSpeed.getCheckNb();
        int updateJobStatusEveryNr = nbThreads.longValue() > 3 ? jobSpeed.getUpdateNb() * nbThreads.intValue() / 2 : jobSpeed.getUpdateNb();

        List<Callable<List<R>>> tasks = new ArrayList<Callable<List<R>>>(nbThreads.intValue());

        for (int k = 0; k < nbThreads; k++) {

            int finalK = k;
            tasks.add(() -> {

                List<R> taskResult = new ArrayList<R>();

                Thread.currentThread().setName(jobInstance.getCode() + "-" + finalK);

                currentUserProvider.reestablishAuthentication(lastCurrentUser);

                int i = 0;
                long globalI = 0;

                T itemToProcess = iterator.next();
                while (itemToProcess != null) {

                    if (i % checkJobStatusEveryNr == 0 && !jobExecutionService.isShouldJobContinue(jobExecutionResult.getJobInstance().getId())) {
                        break;
                    }

                    try {
                        T itemToProcessFinal = itemToProcess;
                        R itemResult = null;
                        if (processItemInOwnTx) {

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

                    try {
                        // Record progress
                        if (updateJobExecutionStatistics && globalI > 0 && globalI % updateJobStatusEveryNr == 0) {
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

                return taskResult;
            });
        }

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

        return processingResult;
    }

    /**
     * Execute a given consumer function on a list of items (accessible with iterator)
     * 
     * @param <T> Iterator's item class
     * @param jobExecutionResult Job execution result
     * @param iterator Iterator containing a list of items to apply a function on
     * @param processSingleItemFunction A function to process a single item. Will be executed in its own transaction when parameter processItemInOwnTx=true
     * @param nbThreads Number of threads to launch
     * @param waitingMillis Number of milliseconds to wait between launching new thread
     * @param processItemInOwnTx Shall function be processed in a new transaction
     * @param jobSpeed Job execution speed to check if job is still running and update job progress in DB
     * @param updateJobExecutionStatistics Shall job execution statistics be updated on each item processed (successfully or not)
     */
    @SuppressWarnings({ "rawtypes" })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public <T> void processItems(JobExecutionResultImpl jobExecutionResult, Iterator<T> iterator, Consumer<T> processSingleItemFunction, Long nbThreads, Long waitingMillis, boolean processItemInOwnTx,
            JobSpeedEnum jobSpeed, boolean updateJobExecutionStatistics) {

        List<Future> futures = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        int checkJobStatusEveryNr = jobSpeed.getCheckNb();
        int updateJobStatusEveryNr = nbThreads.longValue() > 3 ? jobSpeed.getUpdateNb() * nbThreads.intValue() / 2 : jobSpeed.getUpdateNb();

        List<Runnable> tasks = new ArrayList<Runnable>(nbThreads.intValue());

        for (int k = 0; k < nbThreads; k++) {

            int finalK = k;
            tasks.add(() -> {

                Thread.currentThread().setName(jobInstance.getCode() + "-" + finalK);

                currentUserProvider.reestablishAuthentication(lastCurrentUser);
                int i = 0;
                long globalI = 0;

                T itemToProcess = iterator.next();
                while (itemToProcess != null) {

                    if (i % checkJobStatusEveryNr == 0 && !jobExecutionService.isShouldJobContinue(jobExecutionResult.getJobInstance().getId())) {
                        break;
                    }

                    try {
                        T itemToProcessFinal = itemToProcess;
                        if (processItemInOwnTx) {
                            methodCallingUtils.callMethodInNewTx(() -> processSingleItemFunction.accept(itemToProcessFinal));

                        } else {
                            processSingleItemFunction.accept(itemToProcessFinal);
                        }

                        if (updateJobExecutionStatistics) {
                            globalI = jobExecutionResult.registerSucces();
                        }

                    } catch (Exception e) {

                        String rejectReason = org.meveo.commons.utils.StringUtils.truncate(e.getMessage(), 255, true);
                        if (updateJobExecutionStatistics) {
                            globalI = jobExecutionResult.registerError(itemToProcess + ": " + rejectReason);
                        }
                    }

                    // Thread.currentThread().interrupt();

                    try {
                        // Record progress
                        if (updateJobExecutionStatistics && globalI > 0 && globalI % updateJobStatusEveryNr == 0) {
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
}