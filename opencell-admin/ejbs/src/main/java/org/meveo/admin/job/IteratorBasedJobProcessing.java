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
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionResultService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobExecutionService.JobSpeedEnum;
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
     * Execute a given function on a list of items (accessible with iterator)
     * 
     * @param <T> Iterator's item class
     * @param <R> Result class
     * @param jobExecutionResult Job execution result
     * @param processSingleItemFunction A function to process a single item. Will be executed in its own transaction when parameter processItemInOwnTx=true
     * @param jobSpeed Job execution speed to check if job is still running and update job progress in DB
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public <T, R> List<R> processItemsAndAgregateResults(JobExecutionResultImpl jobExecutionResult, Iterator<T> iterator, Function<T, R> processSingleItemFunction, Long nbThreads, Long waitingMillis,
            boolean processItemInOwnTx, JobSpeedEnum jobSpeed) {

        List<Future> futures = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        int checkJobStatusEveryNr = jobSpeed.getCheckNb();
        int updateJobStatusEveryNr = jobSpeed.getUpdateNb();

        Callable<List<R>> task = () -> {

            List<R> taskResult = new ArrayList<R>();

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
                        jobExecutionResult.registerSucces();
                    }

                } catch (Exception e) {

                    String rejectReason = org.meveo.commons.utils.StringUtils.truncate(e.getMessage(), 255, true);
                    jobExecutionResult.registerError(itemToProcess + ": " + rejectReason);
                }

                itemToProcess = iterator.next();
                i++;
            }

            return taskResult;
        };

        for (int i = 0; i < nbThreads; i++) {
            log.info("{}/{} Will submit task to run", jobExecutionResult.getJobInstance().getJobTemplate(), jobExecutionResult.getJobInstance().getCode());
            futures.add(executor.submit(task));
            try {
                Thread.sleep(waitingMillis.longValue());
            } catch (InterruptedException e) {
                log.error("", e);
            }
        }

        JobRunningStatusEnum jobStatus = jobExecutionService.markJobAsRunning(jobInstance, false, jobExecutionResult.getId(), futures);

        boolean wasKilled = false;

        List<R> processingResult = new ArrayList<R>();

        if (jobStatus != JobRunningStatusEnum.REQUEST_TO_STOP) {

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
        }

        return processingResult;
    }

    /**
     * Execute a given consumer function on a list of items (accessible with iterator)
     * 
     * @param <T> Iterator's item class
     * @param jobExecutionResult Job execution result
     * @param processSingleItemFunction A function to process a single item. Will be executed in its own transaction when parameter processItemInOwnTx=true
     * @param jobSpeed Job execution speed to check if job is still running and update job progress in DB
     */
    @SuppressWarnings({ "rawtypes" })
    public <T> void processItems(JobExecutionResultImpl jobExecutionResult, Iterator<T> iterator, Consumer<T> processSingleItemFunction, Long nbThreads, Long waitingMillis, boolean processItemInOwnTx,
            JobSpeedEnum jobSpeed) {

        List<Future> futures = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        int checkJobStatusEveryNr = jobSpeed.getCheckNb();
        int updateJobStatusEveryNr = jobSpeed.getUpdateNb();

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

                try {
                    T itemToProcessFinal = itemToProcess;
                    if (processItemInOwnTx) {
                        methodCallingUtils.callMethodInNewTx(() -> processSingleItemFunction.accept(itemToProcessFinal));

                    } else {
                        processSingleItemFunction.accept(itemToProcessFinal);
                    }

                    jobExecutionResult.registerSucces();

                } catch (Exception e) {

                    String rejectReason = org.meveo.commons.utils.StringUtils.truncate(e.getMessage(), 255, true);
                    jobExecutionResult.registerError(itemToProcess + ": " + rejectReason);
                }

                itemToProcess = iterator.next();
                i++;
            }
        };

        for (int i = 0; i < nbThreads; i++) {
            log.info("{}/{} Will submit task to run", jobExecutionResult.getJobInstance().getJobTemplate(), jobExecutionResult.getJobInstance().getCode());
            futures.add(executor.submit(task));
            try {
                Thread.sleep(waitingMillis.longValue());
            } catch (InterruptedException e) {
                log.error("", e);
            }
        }

        JobRunningStatusEnum jobStatus = jobExecutionService.markJobAsRunning(jobInstance, false, jobExecutionResult.getId(), futures);

        boolean wasCanceled = jobStatus == JobRunningStatusEnum.REQUEST_TO_STOP;

        // Wait for all async methods to finish
        for (Future future : futures) {
            try {
                future.get();

            } catch (InterruptedException e) {
                wasCanceled = true;
                log.error("Thread/future for job {} was canceled", jobInstance);

            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                jobExecutionResult.registerError(cause.getMessage());
                log.error("Failed to execute async method", cause);
            }
        }

        jobStatus = jobExecutionService.markJobAsRunning(jobInstance, false, jobExecutionResult.getId(), null);

        wasCanceled = wasCanceled || jobStatus == JobRunningStatusEnum.REQUEST_TO_STOP;
    }
}