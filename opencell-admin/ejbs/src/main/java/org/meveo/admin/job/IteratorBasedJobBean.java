package org.meveo.admin.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
import javax.jms.Destination;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.async.QueueBasedIterator;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.async.SynchronizedIteratorGrouped;
import org.meveo.admin.async.SynchronizedMultiItemIterator;
import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.MethodCallingUtils;
import org.meveo.event.monitoring.ClusterEventDto.CrudActionEnum;
import org.meveo.event.monitoring.ClusterEventPublisher;
import org.meveo.model.IEntity;
import org.meveo.model.audit.ChangeOriginEnum;
import org.meveo.model.jobs.JobClusterBehaviorEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobLauncherEnum;
import org.meveo.security.MeveoUser;
import org.meveo.service.audit.AuditOrigin;
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

    @Inject
    @JMSConnectionFactory("java:/jms/remoteCF")
    private JMSContext jmsContext;

    @Inject
    private ClusterEventPublisher clusterEventPublisher;

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
     * <p>
     * When run in mode where same data set is processed on multiple nodes, initFunction and finalizeInitFunction are called only on a master node that initiated job execution. It will NOT be called on worked nodes.
     * 
     * @param jobExecutionResult Job execution result
     * @param jobInstance Job instance
     * @param initFunction A function to initialize the data to process.
     * @param processSingleItemFunction A function to process a single item. Will be executed in its own transaction
     * @param hasMoreFunction A function to determine if the are more data to process even though this job run has completed. Optional.
     * @param finalizeInitFunction A function to close any resources opened during initFunction call. Optional. Run once job is finished, independently if it was canceled, stopped, or completed successfully
     * @param finalizeFunction A function to finalize data to process. Optional. Run once job is finished - only when job completed successfully.
     */
    protected void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance, Function<JobExecutionResultImpl, Optional<Iterator<T>>> initFunction,
            BiConsumer<T, JobExecutionResultImpl> processSingleItemFunction, Predicate<JobInstance> hasMoreFunction, Consumer<JobExecutionResultImpl> finalizeInitFunction,
            Consumer<JobExecutionResultImpl> finalizeFunction) {

        execute(jobExecutionResult, jobInstance, initFunction, processSingleItemFunction, null, hasMoreFunction, finalizeInitFunction, finalizeFunction);
    }

    /**
     * Execute a job - retrieve a list of data to process, iterate over the data and process one or multiple items at a time, checking if job is still running and update job progress in DB periodically. <br/>
     * <p>
     * 
     * If processMultipleItemFunction is provided at first an attempt to process multiple items in one transaction will be attempted. If any items fail, each item will be processed one by one in a separate transaction.
     * <p>
     * When run in mode where same data set is processed on multiple nodes, initFunction and finalizeInitFunction are called only on a master node that initiated job execution. It will NOT be called on worked nodes.
     * 
     * @param jobExecutionResult Job execution result
     * @param jobInstance Job instance
     * @param initFunction A function to initialize the data to process
     * @param processSingleItemFunction A function to process a single item. Will be executed in its own transaction
     * @param processMultipleItemFunction A function to process multiple items. Will be executed in its own transaction.
     * @param hasMoreFunction A function to determine if the are more data to process even though this job run has completed. Optional.
     * @param finalizeInitFunction A function to close any resources opened during initFunction call. Optional. Run once job is finished, independently if it no data was found to process, if it was canceled or completed.
     * @param finalizeFunction A function to finalize data to process. Optional. Run once job is finished, independently if it was canceled or completed. Not run if no data were foudn to process. Consult job execution
     *        result status if needed.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance, Function<JobExecutionResultImpl, Optional<Iterator<T>>> initFunction,
            BiConsumer<T, JobExecutionResultImpl> processSingleItemFunction, BiConsumer<List<T>, JobExecutionResultImpl> processMultipleItemFunction, Predicate<JobInstance> hasMoreFunction,
            Consumer<JobExecutionResultImpl> finalizeInitFunction, Consumer<JobExecutionResultImpl> finalizeFunction) {

        boolean isRunningAsJobManager = jobExecutionResult.getJobLauncherEnum() != JobLauncherEnum.WORKER;

        boolean spreadOverCluster = jobInstance.getClusterBehavior() == JobClusterBehaviorEnum.SPREAD_OVER_CLUSTER_NODES && EjbUtils.isRunningInClusterMode();

        final String queueName = "JOB_" + jobInstance.getCode().replace(' ', '_');

        Iterator<T> iterator = null;

        if (EjbUtils.isRunningInClusterMode()) {
            jobExecutionResult.addReport("Node " + EjbUtils.getCurrentClusterNode());

        }

        // When running as a primary job manager, initialize job history tracking
        if (isRunningAsJobManager) {
            jobExecutionErrorService.purgeJobErrors(jobExecutionResult.getJobInstance());

            Optional<Iterator<T>> iteratorOpt = initFunction.apply(jobExecutionResult);

            if (!iteratorOpt.isPresent()) {
                // When running as a primary job manager, Close data initialization
                if (finalizeInitFunction != null) {
                    finalizeInitFunction.accept(jobExecutionResult);
                }
                return;
            }

            iterator = iteratorOpt.get();

            if (iterator instanceof SynchronizedIterator) {
                jobExecutionResult.setNbItemsToProcess(((SynchronizedIterator) iterator).getSize());

            } else if (iterator instanceof SynchronizedIteratorGrouped) {
                jobExecutionResult.setNbItemsToProcess(((SynchronizedIteratorGrouped) iterator).getSize());

            } else if (iterator instanceof SynchronizedMultiItemIterator) {
                jobExecutionResult.setNbItemsToProcess(((SynchronizedMultiItemIterator) iterator).getSize());
            }

            if (jobExecutionResult.getNbItemsToProcess() == 0) {
                log.info("{}/{} will skip as nothing to process", jobInstance.getJobTemplate(), jobInstance.getCode());

                // When running as a primary job manager, Close data initialization
                if (finalizeInitFunction != null) {
                    finalizeInitFunction.accept(jobExecutionResult);
                }
                if (finalizeFunction != null) {
                    finalizeFunction.accept(jobExecutionResult);
                }
                return;
            }
            if (!jobExecutionService.isShouldJobContinue(jobInstance.getId())) {
                log.info("{}/{} will skip as should not continue", jobInstance.getJobTemplate(), jobInstance.getCode());
                return;
            }

            log.info("{}/{} - {} records to process", jobInstance.getJobTemplate(), jobInstance.getCode(), jobExecutionResult.getNbItemsToProcess());

        } else {

            log.info("{}/{} running as a worker node", jobInstance.getJobTemplate(), jobInstance.getCode());
        }

        jobExecutionResultService.persistResult(jobExecutionResult);

        Long nbThreads = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbThreads <= -1) {
            nbThreads = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        List<Future> futures = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();

        int checkJobStatusEveryNr = jobInstance.getJobSpeed().getCheckNb();
        int updateJobStatusEveryNr = nbThreads.longValue() > 3 ? jobInstance.getJobSpeed().getUpdateNb() * nbThreads.intValue() / 2 : jobInstance.getJobSpeed().getUpdateNb();

        boolean isNewTx = isProcessItemInNewTx();

        // Multiple item processing will happen only if batch size is greater than one,
        Long batchSize = (Long) getParamOrCFValue(jobInstance, Job.CF_BATCH_SIZE, 0L);
        boolean useMultipleItemProcessing = (processMultipleItemFunction != null && batchSize != null && batchSize > 1) || processSingleItemFunction == null;

        List<Runnable> tasks = new ArrayList<Runnable>(nbThreads.intValue());

        boolean wasCanceled = false;
        boolean wasKilled = false;

        final Iterator<T> finalIterator = iterator;
        try {
            Queue jobQueue = spreadOverCluster ? jmsContext.createQueue(queueName) : null;

            // Publish data to the job processing queue if data processing is spread over a cluster
            if (isRunningAsJobManager && spreadOverCluster) {

                log.info("{}/{} Will submit task to publish data for cluster-wide data processing", jobInstance.getJobTemplate(), jobInstance.getCode());

                JMSProducer jmsProducer = jmsContext.createProducer();
                Runnable publishingTask = getDataPublishingToQueueTask(jobInstance.getCode(), lastCurrentUser, finalIterator, checkJobStatusEveryNr, jmsProducer, jobQueue, finalizeInitFunction, jobExecutionResult);
                tasks.add(publishingTask);
            }

            final JMSConsumer jmsConsumer = spreadOverCluster ? jmsContext.createConsumer(jobQueue) : null;

            for (int k = 0; k < nbThreads; k++) {
                tasks.add(getDataProcessingTask(jobInstance.getCode(), finalIterator, k, lastCurrentUser, isRunningAsJobManager, spreadOverCluster, jmsConsumer, batchSize.intValue(), checkJobStatusEveryNr,
                    updateJobStatusEveryNr, jobExecutionResult, isNewTx, useMultipleItemProcessing, processSingleItemFunction, processMultipleItemFunction));
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

            // Mark number of threads it will be running on
            JobRunningStatusEnum jobStatus = jobExecutionService.markJobAsRunning(jobInstance, false, jobExecutionResult.getId(), futures);

            // Job manager launches worker jobs in other cluster nodes
            if (isRunningAsJobManager && spreadOverCluster) {
                clusterEventPublisher.publishEventAsync(jobInstance, CrudActionEnum.executeWorker,
                    MapUtils.putAll(new HashMap<String, Object>(), new Object[] { Job.JOB_PARAM_HISTORY_PARENT_ID, jobExecutionResult.getId(), Job.JOB_PARAM_LAUNCHER, JobLauncherEnum.WORKER }),
                    currentUser.getProviderCode(), currentUser.getUserName());
            }

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

            // When running as a primary job manager, Close data initialization
            if (isRunningAsJobManager && finalizeInitFunction != null) {
                finalizeInitFunction.accept(jobExecutionResult);
            }

            if (jmsConsumer != null) {
                jmsConsumer.close();
            }

            // Mark job as stopped if task was killed
            if (wasKilled) {
                jobExecutionService.markJobToStop(jobInstance);

                // Mark that all threads are finished
            } else {
                jobStatus = jobExecutionService.markJobAsRunning(jobInstance, false, jobExecutionResult.getId(), null);
            }

            wasCanceled = wasKilled || jobStatus == JobRunningStatusEnum.REQUEST_TO_STOP;

            if (wasCanceled && isRunningAsJobManager && spreadOverCluster) {
                clearPendingWorkLoad(jobInstance);
            }

            // Check if there are any more data to process and mark job as completed if there are none
            if (!wasCanceled && hasMoreFunction != null) {
                jobExecutionResult.setMoreToProcess(hasMoreFunction.test(jobInstance));
            }

        } catch (Exception e) {
            log.error("Failed to run a job {}", jobInstance, e);
            jobExecutionResult.registerError(e.getMessage());
        }

        if (finalizeFunction != null) {
            finalizeFunction.accept(jobExecutionResult);
        }
    }

    /**
     * Process a single item
     * 
     * @param itemToProcess Item to process
     * @param isNewTx Shall a new transaction be initiated. If false, its expected that transaction handling will be provided by the function itself
     * @param processSingleItemFunction A function to process a single item
     * @param jobExecutionResult Job execution results
     * @return A total number of processed items, successful or failed
     */
    private long processItem(T itemToProcess, boolean isNewTx, BiConsumer<T, JobExecutionResultImpl> processSingleItemFunction, JobExecutionResultImpl jobExecutionResult) {

        int itemCount = 1;
        if (itemToProcess instanceof List && isCountIndividualListItemForProgress()) {
            itemCount = ((List) itemToProcess).size();
        }
        try {
            if (isNewTx) {
                methodCallingUtils.callMethodInNewTx(() -> processSingleItemFunction.accept(itemToProcess, jobExecutionResult));
            } else {
                processSingleItemFunction.accept(itemToProcess, jobExecutionResult);
            }

            return jobExecutionResult.registerSucces(itemCount);

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
                return jobExecutionResult.registerError(e.getMessage(), itemCount);
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

    /**
     * When a single item being processed is a list of items e.g. SynchronizedIteratorGrouped or SynchronizedMultiItemIterator, shall each item in a list be counted individually for success and error progress. <br/>
     * Note, that in this is not related to batch processing, as items in batch are still processed as individual items
     * 
     * @return True if A list of 10 items will count as 10 success/failure. False if a list of 10 items should count as 1 sucess/failure
     */
    protected boolean isCountIndividualListItemForProgress() {
        return true;
    }

    private Runnable getDataPublishingToQueueTask(String jobInstanceCode, MeveoUser lastCurrentUser, Iterator<T> iterator, int checkJobStatusEveryNr, JMSProducer jmsProducer, Destination jobQueue,
            Consumer<JobExecutionResultImpl> finalizeInitFunction, JobExecutionResultImpl jobExecutionResult) {

        Runnable task = () -> {
            Thread.currentThread().setName(jobInstanceCode + "-PublishToCluster");

            currentUserProvider.reestablishAuthentication(lastCurrentUser);

            int i = 0;
            T itemToProcess = iterator.next();

            log.trace("Thread {} will publish data for cluster-wide data processing");
            while (itemToProcess != null) {

                if (i % checkJobStatusEveryNr == 0 && !jobExecutionService.isShouldJobContinue(jobExecutionResult.getJobInstance().getId())) {
                    break;
                }

                jmsProducer.send(jobQueue, (Serializable) itemToProcess);
                i++;

                itemToProcess = iterator.next();
            }

            log.debug("Thread {} published {} data items for cluster-wide data processing", Thread.currentThread().getName(), i);
        };

        return task;
    }

    private Runnable getDataProcessingTask(String jobInstanceCode, Iterator<T> dataIterator, int threadNr, MeveoUser lastCurrentUser, boolean isRunningAsJobManager, boolean spreadOverCluster, JMSConsumer jmsConsumer,
            int batchSize, int checkJobStatusEveryNr, int updateJobStatusEveryNr, JobExecutionResultImpl jobExecutionResult, boolean isNewTx, boolean useMultipleItemProcessing,
            BiConsumer<T, JobExecutionResultImpl> processSingleItemFunction, BiConsumer<List<T>, JobExecutionResultImpl> processMultipleItemFunction) {

        String auditOriginName = jobExecutionResult.getJobInstance().getJobTemplate() + "/" + jobInstanceCode;

        Runnable task = () -> {

            Thread.currentThread().setName(jobInstanceCode + "-" + threadNr);

            currentUserProvider.reestablishAuthentication(lastCurrentUser);

            AuditOrigin.setAuditOriginAndName(ChangeOriginEnum.JOB, auditOriginName);

            int i = 0;
            long globalI = 0;
            Iterator<T> threadIterator = dataIterator;
            QueueBasedIterator<T> queueBasedIterator = new QueueBasedIterator<T>(jmsConsumer);

            boolean isQueueBasedIterator = false;
            // In worker job/thread, dataIterator is null. A new iterator based on JMS Queue consumer must be created
            if (spreadOverCluster && !isRunningAsJobManager) {
                threadIterator = queueBasedIterator;
                isQueueBasedIterator = true;
            }

            // Job running as job manager publish data to a queue from a DB based iterator in parallel as other threads process data.
            // Job running as job manager can obtain data from two places - DB based iterator and queue based iterator once no more data is retrieved from a DB based iterator
            // First try to obtain a value from a current iterator. If no item is retrieved, give a try in Queue based iterator if applicable.
            T itemToProcess = threadIterator.next();
            if (itemToProcess == null && spreadOverCluster && isRunningAsJobManager && !isQueueBasedIterator) {
                threadIterator = queueBasedIterator;
                isQueueBasedIterator = true;
                log.trace("Switching to queue based iterator. Processed none from db iterator");
                itemToProcess = threadIterator.next();
            }
            if (itemToProcess != null) {
                i++;
            }

            // In multiItemProcessing try to read batch size in 1/5th intervals as to spread better load among threads when there are little of data to process
            int minMultiReadSize = batchSize / 5 + 1;
            mainLoop: while (itemToProcess != null) {

                if (useMultipleItemProcessing) {

                    final List<T> itemsToProcess = new ArrayList<T>();
                    itemsToProcess.add(itemToProcess);
                    int nrOfItemsInBatch = 1;

                    while (nrOfItemsInBatch < batchSize) {
                        // Job running as job manager publish data to a queue from a DB based iterator in parallel as other threads process data.
                        // Job running as job manager can obtain data from two places - DB based iterator and queue based iterator once no more data is retrieved from a DB based iterator
                        // First try to obtain a value from a current iterator. If no item is retrieved, give a try in Queue based iterator if applicable.
                        if (threadIterator instanceof SynchronizedIterator) {
                            int nextJobStatusCheck = i / checkJobStatusEveryNr + checkJobStatusEveryNr;

                            // Read items in batch as to minimize blocking time on synchronized method SynchronizedIterator.next
                            int nrItemsToRead = (batchSize - nrOfItemsInBatch) < minMultiReadSize ? (batchSize - nrOfItemsInBatch) : minMultiReadSize;
                            List<T> itemsToProcessBatchRead = ((SynchronizedIterator<T>) threadIterator).next(nrItemsToRead);
                            if (itemsToProcessBatchRead != null) {

                                itemsToProcess.addAll(itemsToProcessBatchRead);
                                nrOfItemsInBatch = nrOfItemsInBatch + itemsToProcessBatchRead.size();
                                i = i + itemsToProcessBatchRead.size();

                                // Check if job is not stopped yet
                                if (i >= nextJobStatusCheck && !jobExecutionService.isShouldJobContinue(jobExecutionResult.getJobInstance().getId())) {
                                    break mainLoop;
                                }

                                continue;

                                // Nothing found in batch read
                            } else {
                                itemToProcess = null;
                            }
                        } else {
                            itemToProcess = threadIterator.next();
                        }
                        if (itemToProcess == null && spreadOverCluster && isRunningAsJobManager && !isQueueBasedIterator) {
                            threadIterator = queueBasedIterator;
                            isQueueBasedIterator = true;
                            log.trace("Switching to queue based iterator. Processed {} from db iterator.", i);
                            itemToProcess = threadIterator.next();
                        }
                        // Still nothing retrieved neither from current nor queue based iterator, so no more data to retrieve - continue with processing
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
                            i = i - nrOfItemsInBatch;

                            for (T itemToProcessFromFailedBatch : itemsToProcess) {
                                globalI = processItem(itemToProcessFromFailedBatch, isNewTx, processSingleItemFunction, jobExecutionResult);
                                i++;
                            }

                            // Record a message if there is no single item processing function available
                        } else {
                            globalI = jobExecutionResult.registerError(e.getMessage(), nrOfItemsInBatch);
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

                // There was nothing retrieved the last time, so dont try it again
                if (itemToProcess != null) {
                    // Job running as job manager publish data to a queue from a DB based iterator in parallel as other threads process data.
                    // Job running as job manager can obtain data from two places - DB based iterator and queue based iterator once no more data is retrieved from a DB based iterator
                    // First try to obtain a value from a current iterator. If no item is retrieved, give a try in Queue based iterator if applicable.
                    itemToProcess = threadIterator.next();
                    if (itemToProcess == null && spreadOverCluster && isRunningAsJobManager && !isQueueBasedIterator) {
                        threadIterator = queueBasedIterator;
                        isQueueBasedIterator = true;
                        log.trace("Switching to queue based iterator. Processed {} from db based iterator.", i);
                        itemToProcess = threadIterator.next();
                    }
                    if (itemToProcess != null) {
                        i++;
                    }
                }
            }
            log.debug("Thread {} processed {} items", Thread.currentThread().getName(), i);

        };

        return task;
    }

    /**
     * Clear pending workload if data set was distributed over a JMS queue
     * 
     * @param jobInstance Job instance to clear
     */
    private void clearPendingWorkLoad(JobInstance jobInstance) {

        try {

            String mqHost = System.getenv("opencell.mq.host");
            if (StringUtils.isBlank(mqHost)) {
                mqHost = "localhost";
            }

            JMXServiceURL target = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + mqHost + ":1099/jmxrmi");
            JMXConnector connector = JMXConnectorFactory.connect(target);
            MBeanServerConnection remote = connector.getMBeanServerConnection();

            String queueName = "JOB_" + jobInstance.getCode().replace(' ', '_');
            ObjectName bean = new ObjectName("org.apache.activemq.artemis:broker=\"0.0.0.0\",component=addresses,address=\"" + queueName + "\"");

            remote.invoke(bean, "purge", null, null);
            connector.close();

        } catch (Exception e) {
            log.error("Failed to purge pending workload messages for job {}", jobInstance.getCode(), e);
        }
    }
}