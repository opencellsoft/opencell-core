package org.meveo.admin.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Resource;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.RuntimeMBeanException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang3.StringUtils;
import org.jboss.as.ee.component.ComponentIsStoppedException;
import org.jgroups.JChannel;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.async.SynchronizedIteratorGrouped;
import org.meveo.admin.async.SynchronizedMultiItemIterator;
import org.meveo.admin.exception.JobExecutionException;
import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.MethodCallingUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.event.monitoring.ClusterEventDto.CrudActionEnum;
import org.meveo.event.monitoring.ClusterEventPublisher;
import org.meveo.event.qualifier.LastJobDataMessageReceived;
import org.meveo.model.IEntity;
import org.meveo.model.audit.ChangeOriginEnum;
import org.meveo.model.jobs.JobClusterBehaviorEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobLauncherEnum;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.audit.AuditOrigin;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionResultService;
import org.meveo.service.job.JobExecutionService;
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
 *
 * @param <T> Data type to process
 */
public abstract class IteratorBasedJobBean<T> extends BaseJobBean {

    private static final long serialVersionUID = 649152055662228506L;

    /**
     * Remote MQ connection url
     */
    private static final String REMOTE_MQ_HOST = "REMOTE_MQ_HOST";

    /**
     * Remote MQ JMX connection port
     */
    private static final String REMOTE_MQ_JMX_PORT = "REMOTE_MQ_JMX_PORT";

    /**
     * Remote MQ connection username
     */
    private static final String REMOTE_MQ_ADMIN_USER = "REMOTE_MQ_ADMIN_USER";

    /**
     * Remote MQ connection password
     */
    private static final String REMOTE_MQ_ADMIN_PASSWORD = "REMOTE_MQ_ADMIN_PASSWORD";

    @Inject
    private MethodCallingUtils methodCallingUtils;

    @Inject
    @JMSConnectionFactory("java:/jms/remoteCF")
    private JMSContext jmsContextForPublishing;

    @Resource(lookup = "java:/jms/remoteCF-consumer")
    private ConnectionFactory jmsConnectionFactory;

    @Inject
    private ClusterEventPublisher clusterEventPublisher;

    @Inject
    @LastJobDataMessageReceived
    protected Event<Long> lastJobDataMsgEventProducer;

    @Resource(lookup = "java:jboss/jgroups/channel/default")
    private JChannel channel;

    /**
     * Tracks countDowns used to wait for last message to receive when job load is spread over multiple nodes. Job identifier is a map key.<br/>
     * When EOF message is received in a queue, a EJB event is fired and countDown is reduced.
     */
    private static final Map<Long, CountDownLatch> countDowns = new HashMap<Long, CountDownLatch>();

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
     * When run in mode where same data set is processed on multiple nodes, initMainNodeAndRetrieveDataFunction and finalizeInitMainNodeAndCloseDataFunction are called only on a master node that initiated job execution.
     * It will NOT be called on worked nodes.
     * 
     * @param jobExecutionResult Job execution result
     * @param jobInstance Job instance
     * @param initMainNodeAndRetrieveDataFunction A function to initialize the data to process.
     * @param processSingleItemFunction A function to process a single item. Will be executed in its own transaction
     * @param hasMoreFunction A function to determine if the are more data to process even though this job run has completed. Optional.
     * @param finalizeInitMainNodeAndCloseDataFunction A function to close any resources opened during initMainNodeAndRetrieveDataFunction call. Optional. Run once job is finished, independently if it was canceled,
     *        stopped, or completed successfully
     * @param finalizeFunction A function to finalize data to process. Optional. Run once job is finished - only when job completed successfully.
     */
    protected void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance, Function<JobExecutionResultImpl, Optional<Iterator<T>>> initMainNodeAndRetrieveDataFunction,
            BiConsumer<T, JobExecutionResultImpl> processSingleItemFunction, Predicate<JobInstance> hasMoreFunction, Consumer<JobExecutionResultImpl> finalizeInitMainNodeAndCloseDataFunction,
            Consumer<JobExecutionResultImpl> finalizeFunction) {

        execute(jobExecutionResult, jobInstance, initMainNodeAndRetrieveDataFunction, null, processSingleItemFunction, null, hasMoreFunction, finalizeInitMainNodeAndCloseDataFunction, finalizeFunction);
    }

    /**
     * Execute a job - retrieve a list of data to process, iterate over the data and process one or multiple items at a time, checking if job is still running and update job progress in DB periodically. <br/>
     * <p>
     * 
     * If processMultipleItemFunction is provided at first an attempt to process multiple items in one transaction will be attempted. If any items fail, each item will be processed one by one in a separate transaction.
     * <p>
     * When run in mode where same data set is processed on multiple nodes, initMainNodeAndRetrieveDataFunction and finalizeInitMainNodeAndCloseDataFunction are called only on a master node that initiated job execution.
     * It will NOT be called on worked nodes.
     * 
     * @param jobExecutionResult Job execution result
     * @param jobInstance Job instance
     * @param initMainNodeAndRetrieveDataFunction A function to initialize the data to process
     * @param processSingleItemFunction A function to process a single item. Will be executed in its own transaction
     * @param processMultipleItemFunction A function to process multiple items. Will be executed in its own transaction.
     * @param hasMoreFunction A function to determine if the are more data to process even though this job run has completed. Optional.
     * @param finalizeInitMainNodeAndCloseDataFunction A function to close any resources opened during initMainNodeAndRetrieveDataFunction call. Optional. Run once job is finished, independently if it no data was found
     *        to process, if it was canceled or completed.
     * @param finalizeFunction A function to finalize data to process. Optional. Run once job is finished, independently if it was canceled or completed. Not run if no data were found to process. Consult job execution
     *        result status if needed.
     */
    @SuppressWarnings({ "rawtypes" })
    protected void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance, Function<JobExecutionResultImpl, Optional<Iterator<T>>> initMainNodeAndRetrieveDataFunction,
            Consumer<JobExecutionResultImpl> initWorkerNodeFunction, BiConsumer<T, JobExecutionResultImpl> processSingleItemFunction, BiConsumer<List<T>, JobExecutionResultImpl> processMultipleItemFunction,
            Predicate<JobInstance> hasMoreFunction, Consumer<JobExecutionResultImpl> finalizeInitMainNodeAndCloseDataFunction, Consumer<JobExecutionResultImpl> finalizeFunction) {

        boolean isRunningAsJobManager = jobExecutionResult.getJobLauncherEnum() != JobLauncherEnum.WORKER;

        Iterator<T> iterator = null;

        // Clean up canceled jobs map
        requestToStopJobs.put(jobInstance.getId(), Boolean.FALSE);

        // Clean up counter for job thread scheduling purpose
        CountDownLatch countDown = countDowns.get(jobInstance.getId());
        if (countDown != null) {
            countDown.countDown();
            countDowns.remove(jobInstance.getId());
        }

        // When running as a primary job manager, initialize job history tracking
        if (isRunningAsJobManager) {
            jobExecutionErrorService.purgeJobErrors(jobExecutionResult.getJobInstance());

            Optional<Iterator<T>> iteratorOpt = initMainNodeAndRetrieveDataFunction.apply(jobExecutionResult);

            // Close data initialization if no data was found to process
            if (!iteratorOpt.isPresent()) {
                if (finalizeInitMainNodeAndCloseDataFunction != null) {
                    finalizeInitMainNodeAndCloseDataFunction.accept(jobExecutionResult);
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
                if (finalizeInitMainNodeAndCloseDataFunction != null) {
                    finalizeInitMainNodeAndCloseDataFunction.accept(jobExecutionResult);
                }
                if (finalizeFunction != null) {
                    finalizeFunction.accept(jobExecutionResult);
                }
                return;
            }
            if (isJobRequestedToStop(jobInstance.getId())) {
                log.info("{}/{} will skip as should not continue", jobInstance.getJobTemplate(), jobInstance.getCode());
                return;
            }

            log.info("{}/{} - {} records to process by main node", jobInstance.getJobTemplate(), jobInstance.getCode(), jobExecutionResult.getNbItemsToProcess());

        } else {

            if (initWorkerNodeFunction != null) {
                initWorkerNodeFunction.accept(jobExecutionResult);
            }

            log.info("{}/{} running as a worker node", jobInstance.getJobTemplate(), jobInstance.getCode());
        }

        jobExecutionResultService.persistResult(jobExecutionResult);

        Long nbThreads = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbThreads <= -1) {
            nbThreads = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        Long nbPublishers = null;

        List<Future> futures = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();

        boolean isNewTx = isProcessItemInNewTx();

        // Multiple item processing will happen only if batch size is greater than one,
        Long batchSize = (Long) getParamOrCFValue(jobInstance, Job.CF_BATCH_SIZE, 1L);
        boolean useMultipleItemProcessing = (processMultipleItemFunction != null && batchSize != null && batchSize > 1) || processSingleItemFunction == null;

        List<Runnable> tasks = new ArrayList<Runnable>(nbThreads.intValue());

        boolean wasCanceled = false;
        boolean wasKilled = false;

        boolean spreadOverCluster = jobInstance.getClusterBehavior() == JobClusterBehaviorEnum.SPREAD_OVER_CLUSTER_NODES && EjbUtils.isRunningInClusterMode();

        String queueName = JobExecutionService.getJobQueueName(jobInstance.getCode());

        final Iterator<T> finalIterator = iterator;
        try {
            Queue jobQueue = null;
            if (spreadOverCluster) {
                // Determine and create or connect to a queue
                if (isRunningAsJobManager) {

                    // If queue is not durable - use a JMX API to create it
                    boolean isDurableQueue = ParamBean.getInstance().getPropertyAsBoolean("jobs.mq.durable", false);
                    if (!isDurableQueue) {
                        if (!createAQueueIfDoesNotExist(jobInstance.getCode(), queueName)) {
                            throw new JobExecutionException("Failed to create a queue " + queueName + ". Job will stop.");
                        }
                    }
                }

                // Connect to a job queue
                jobQueue = jmsContextForPublishing.createQueue(queueName);

                // Create publishing data to the job processing queue tasks if data processing is spread over a cluster
                if (isRunningAsJobManager) {

                    // Clear any leftover data in a queue if it is a durable queue
                    clearPendingWorkLoad(jobInstance, queueName);

                    // A value of data publishers might come from a Custom Field or calculated dynamically based on number of nodes in a cluster
                    nbPublishers = (Long) getParamOrCFValue(jobInstance, Job.CF_NB_PUBLISHERS, 0L);
                    if (nbPublishers == null || nbPublishers < 1) {
                        // Number of data publishing tasks is half of the cluster members or the number of nodes that job can run on
                        int nrOfNodes = jobInstance.getRunOnNodes() != null ? jobInstance.getRunOnNodes().split(",").length : channel.getView().getMembers().size();
                        nbPublishers = ((Integer) (nrOfNodes < 1 ? 1 : (3 * nrOfNodes) / 4)).longValue();
                    }

                    log.info("{}/{} Will submit {} task(s) to publish data for cluster-wide data processing", jobInstance.getJobTemplate(), jobInstance.getCode(), nbPublishers);

                    final Queue jobQueueFinal = jobQueue;
                    for (int k = 0; k < nbPublishers; k++) {
                        int kFinal = k;
                        Runnable publishingTask = methodCallingUtils
                            .callCallableInNoTx(() -> getDataPublishingToQueueTask(jobInstance.getCode(), lastCurrentUser, finalIterator, batchSize, jobQueueFinal, jobExecutionResult, kFinal));
                        tasks.add(publishingTask);
                    }
                }
            }

            // Initialize counter to 1. When EOF message is received in a queue, a EJB event is fired and countDown is reduced.
            countDown = new CountDownLatch(1);
            countDowns.put(jobInstance.getId(), countDown);

            // Create data processing tasks
            for (int k = 0; k < nbThreads; k++) {

                tasks.add(getDataProcessingTask(jobInstance.getCode(), finalIterator, k, lastCurrentUser, isRunningAsJobManager, spreadOverCluster, jobQueue, batchSize.intValue(), jobExecutionResult, isNewTx,
                    useMultipleItemProcessing, processSingleItemFunction, processMultipleItemFunction, countDown));
            }

            // Tracks if job's main thread is still running. Used only to stop job status reporting thread.
            boolean[] isProcessing = { !jobExecutionService.isJobCancelled(jobInstance.getId()) };

            // Start job status report task. Not run in future, so it will die when main thread dies
            Runnable jobStatusReportTask = IteratorBasedJobBean.getJobStatusReportingTask(jobInstance, lastCurrentUser, jobInstance.getJobStatusReportFrequency(), jobExecutionResult, isProcessing, currentUserProvider,
                log, jobExecutionResultService, jobExecutionService);
            Thread jobStatusReportThread = new Thread(jobStatusReportTask);
            jobStatusReportThread.start();

            // Launch main publishing and processing tasks
            int i = 0;
            for (Runnable task : tasks) {
                log.info("{}/{} Will submit data {} task #{} to run", jobInstance.getJobTemplate(), jobInstance.getCode(), nbPublishers != null && i < nbPublishers ? "publishing" : "processing", i++);
                futures.add(executor.submit(task));
                try {
                    Thread.sleep(waitingMillis.longValue());
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }

            // Mark number of threads it will be running on
            JobRunningStatusEnum jobStatus = jobExecutionService.markJobAsRunning(jobInstance, jobExecutionResult.getId(), futures);

            // Job manager launches worker jobs in other cluster nodes
            if (isRunningAsJobManager && spreadOverCluster) {

                Map<String, Object> additionalInformation = new HashMap<String, Object>();
                if (jobInstance.getRunTimeValues() != null) {
                    additionalInformation.putAll(jobInstance.getRunTimeValues());
                }
                additionalInformation.put(Job.JOB_PARAM_HISTORY_PARENT_ID, jobExecutionResult.getId());
                additionalInformation.put(Job.JOB_PARAM_LAUNCHER, JobLauncherEnum.WORKER);

                clusterEventPublisher.publishEventAsync(jobInstance, CrudActionEnum.executeWorker, additionalInformation, currentUser.getProviderCode(), currentUser.getUserName());
            }

            // Wait for all async methods to finish
            i = 0;
            for (Future future : futures) {
                try {
                    future.get();

                    // Send EOF message to a queue once all data publishing tasks are finished
                    if (!wasKilled && nbPublishers != null && i == nbPublishers - 1) {
                        final Queue jobQueueFinal = jobQueue;
                        methodCallingUtils.callMethodInNoTx(() -> sendEOFMessageToAQueue(jobQueueFinal));
                    }

                } catch (InterruptedException | CancellationException e) {
                    wasKilled = true;
                    log.error("Thread/future for job {} was canceled", jobInstance);

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    jobExecutionResult.registerError(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
                i++;
            }

            // This will exit the status report task
            isProcessing[0] = false;
            jobStatusReportThread.interrupt();

            // Clean up countDown counter for job thread scheduling purpose
            countDowns.remove(jobInstance.getId());

            // When running as a primary job manager, Close data initialization
            if (isRunningAsJobManager && finalizeInitMainNodeAndCloseDataFunction != null) {
                finalizeInitMainNodeAndCloseDataFunction.accept(jobExecutionResult);
            }

            // Mark job as stopped if task was killed but not shut down
            if (wasKilled && !JobExecutionService.isServerIsInShutdownMode()) {
                jobExecutionService.markJobToStop(jobInstance);

                // Mark that all threads are finished
            } else {
                jobStatus = jobExecutionService.markJobAsRunning(jobInstance, jobExecutionResult.getId(), null);
            }

            wasCanceled = wasKilled || jobStatus == JobRunningStatusEnum.REQUEST_TO_STOP;

            // Clear pending workload in MQ queue if multinode job was canceled but not shut down
            if (wasCanceled && isRunningAsJobManager && spreadOverCluster && !JobExecutionService.isServerIsInShutdownMode()) {
                clearPendingWorkLoad(jobInstance, queueName);
            }

            // Check if there are any more data to process and mark job as completed if there are none
            if (!wasCanceled && hasMoreFunction != null) {
                jobExecutionResult.setMoreToProcess(hasMoreFunction.test(jobInstance));
            }

        } catch (Exception e) {
            log.error("Failed to run a job {}", jobInstance, e);
            jobExecutionResult.registerError(e.getMessage());
        }

        if (finalizeFunction != null && !JobExecutionService.isServerIsInShutdownMode()) {
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
    @SuppressWarnings("rawtypes")
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

            // Server is shutting down
        } catch (ComponentIsStoppedException e) {

            throw e;

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
    protected boolean isProcessMultipleItemFunctionUpdateProgressItself() {
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

    /**
     * Create a task to save to DB job processing progress
     * 
     * @param jobInstance Job instance
     * @param lastCurrentUser Current user
     * @param reportFrequency How often (number of seconds) job progress should be saved to DB
     * @param jobExecutionResult Job execution result
     * @param jobExecutionService Job execution service
     * @return A task definition
     */
    public static Runnable getJobStatusReportingTask(JobInstance jobInstance, MeveoUser lastCurrentUser, int reportFrequency, JobExecutionResultImpl jobExecutionResult, boolean[] isProcessing,
            CurrentUserProvider currentUserProvider, Logger log, JobExecutionResultService jobExecutionResultService, JobExecutionService jobExecutionService) {

        String jobInstanceCode = jobInstance.getCode();
        Long jobInstanceId = jobExecutionResult.getJobInstance().getId();
        Long jobDurationLimit = jobExecutionResultService.getJobDurationLimit(jobExecutionResult, jobInstance);
        Long jobTimeLimit = jobExecutionResultService.getJobTimeLimit(jobExecutionResult, jobInstance);

        final AtomicLong durationLimit = jobDurationLimit != null ? new AtomicLong(jobDurationLimit) : null;
        final AtomicLong timeLimit = jobTimeLimit != null ? new AtomicLong(jobTimeLimit) : null;

        Runnable task = () -> {
            Thread.currentThread().setName(jobInstanceCode + "-ReportJobStatus");

            currentUserProvider.reestablishAuthentication(lastCurrentUser);

            log.debug("Thread {} will store job progress", Thread.currentThread().getName());

            while (isProcessing[0] && !BaseJobBean.isJobRequestedToStop(jobInstanceId)) {
                try {
                    // Record progress
                    jobExecutionResultService.persistResult(jobExecutionResult);

                } catch (EJBTransactionRolledbackException e) {
                    // Will ignore the error here, as its most likely to happen - updating jobExecutionResultImpl entity from multiple threads
                } catch (Exception e) {
                    log.error("Failed to update job progress", e);
                }

                try {
                    if ((durationLimit != null && durationLimit.get() <= 0) || (timeLimit != null && timeLimit.get() <= 0)) {
                        jobExecutionResult.setLimitExceeded(true);
                        jobExecutionService.stopJob(jobInstance);
                    } else {
                        if ((durationLimit != null && durationLimit.get() < reportFrequency) || (timeLimit != null && timeLimit.get() < reportFrequency)) {
                            if (durationLimit != null && timeLimit != null) {
                                if (durationLimit.get() < timeLimit.get()) {
                                    Thread.sleep(durationLimit.get() * 1000);
                                    durationLimit.set(0L);
                                } else {
                                    Thread.sleep(timeLimit.get() * 1000);
                                    timeLimit.set(0L);
                                }
                            } else {
                                if (durationLimit != null) {
                                    Thread.sleep(durationLimit.get() * 1000);
                                    durationLimit.set(0L);
                                } else {
                                    Thread.sleep(timeLimit.get() * 1000);
                                    timeLimit.set(0L);
                                }
                            }
                        } else {
                            if (durationLimit != null) {
                                durationLimit.set(durationLimit.get() - reportFrequency);
                            }
                            if (timeLimit != null) {
                                timeLimit.set(timeLimit.get() - reportFrequency);
                            }
                            Thread.sleep(reportFrequency * 1000);
                        }
                    }
                } catch (InterruptedException e1) {
                }
            }
            log.info("Thread {} will stop storing job progress", Thread.currentThread().getName());
        };
        return task;
    }

    /**
     * Create a task to publish data to a queue
     * 
     * @param jobInstanceCode Job instance code
     * @param lastCurrentUser Current user
     * @param iterator Iterator from a DB based data source
     * @param batchSize Batch processing size - number of items to send as a single message
     * @param jmsProducer JMS message producer
     * @param jobQueue JMS queue where messages should be posted
     * @param jobExecutionResult Job execution result
     * @param threadNr Thread identifier (index)
     * @return A task definition
     * @throws JMSException An exception publishing to JMS
     */
    private Runnable getDataPublishingToQueueTask(String jobInstanceCode, MeveoUser lastCurrentUser, Iterator<T> iterator, Long batchSize, Queue jobQueue, JobExecutionResultImpl jobExecutionResult, int threadNr)
            throws JMSException {

        Long jobInstanceId = jobExecutionResult.getJobInstance().getId();
        JMSContext jmsContext = jmsConnectionFactory.createContext(System.getenv(REMOTE_MQ_ADMIN_USER), System.getenv(REMOTE_MQ_ADMIN_PASSWORD), JMSContext.CLIENT_ACKNOWLEDGE);
        JMSProducer jmsProducer = jmsContext.createProducer();
        jmsProducer.setDisableMessageID(true);
        jmsProducer.setDisableMessageTimestamp(true);

//        Message eofMessage = jmsContextForPublishing.createMessage();
//        eofMessage.setStringProperty(ItertatorJobMessageListener.EOF_MESSAGE, ItertatorJobMessageListener.EOF_MESSAGE);

        Runnable task = () -> {
            Thread.currentThread().setName(jobInstanceCode + "-PublishToCluster-" + threadNr);

            currentUserProvider.reestablishAuthentication(lastCurrentUser);

            log.debug("Thread {} will publish data for cluster-wide data processing", Thread.currentThread().getName());

            int nrOfItemsProcessedByThread = 0;
            int nrMessages = 0;

            jmsContext.start();

            while (true) {
                // Retrieve next batchSize of items
                List<T> itemsToProcess = iterator instanceof SynchronizedIterator ? ((SynchronizedIterator<T>) iterator).next(batchSize.intValue()) : new ArrayList<T>();
                if (!(iterator instanceof SynchronizedIterator)) {
                    for (int k = 0; k < batchSize; k++) {
                        T item = iterator.next();
                        if (item == null) {
                            break;
                        }
                        itemsToProcess.add(item);
                    }
                }

                if (itemsToProcess == null || itemsToProcess.isEmpty()) {
                    break;
                }

                int nrOfItemsInBatch = itemsToProcess.size();

                // Check if job is not stopped yet
                if (isJobRequestedToStop(jobInstanceId)) {
                    log.info("Thread {} published {} data items in {} messages for cluster-wide data processing before was canceled", Thread.currentThread().getName(), nrOfItemsProcessedByThread, nrMessages);
                    return;
                }

                jmsProducer.send(jobQueue, (Serializable) itemsToProcess);

                nrOfItemsProcessedByThread = nrOfItemsProcessedByThread + nrOfItemsInBatch;
                nrMessages++;

            }

//            jmsProducer.send(jobQueue, eofMessage);

            log.info("Thread {} published {} data items in {} messages for cluster-wide data processing", Thread.currentThread().getName(), nrOfItemsProcessedByThread, nrMessages);

            jmsContext.close();

        };

        return task;
    }

    /**
     * Send a message to a queue indicating that all messages have been send
     * 
     * @param jobQueue JMS queue where messages should be posted
     */
    private void sendEOFMessageToAQueue(Destination jobQueue) {

        try {
            JMSProducer jmsProducer = jmsContextForPublishing.createProducer();
            Message eofMessage = jmsContextForPublishing.createMessage();
            eofMessage.setStringProperty(ItertatorJobMessageListener.EOF_MESSAGE, ItertatorJobMessageListener.EOF_MESSAGE);

            jmsProducer.send(jobQueue, eofMessage);

            log.info("Published EOF message to queue {} for cluster-wide data processing", jobQueue.toString());

        } catch (Exception e) {
            log.error("Failed to publish EOF message to queue {} for cluster-wide data processing", jobQueue.toString());
            throw new JMSRuntimeException(e.getMessage());
        }
    }

    /**
     * Create a task to process data. <br/>
     * Job running as job manager publish data to a queue from a DB based iterator in parallel as other threads process data.<br/>
     * So, job running as job manager can obtain data from two places - DB based iterator and message queue once no more data is retrieved from a DB based iterator<br/>
     * Job running as a job worked can obtain data only from a message queue
     * 
     * @param jobInstanceCode Job instance code
     * @param dataIterator Iterator from a DB based data source
     * @param threadNr Thread identifier (index)
     * @param lastCurrentUser Current user
     * @param isRunningAsJobManager Is task running as a job manager
     * @param spreadOverCluster Should job processing be spread over multiple cluster nodes
     * @param jmsConsumer JMS message consumer
     * @param batchSize Batch processing size
     * @param jobExecutionResult Job execution tracking result
     * @param isNewTx Should data processing run in a new TX
     * @param useMultipleItemProcessing Process items in batch
     * @param processSingleItemFunction A function to process single item
     * @param processMultipleItemFunction A function to process multiple items
     * @param countDown A synchronization counter to wait for the last message to process
     * @return A task definition
     */
    private Runnable getDataProcessingTask(String jobInstanceCode, Iterator<T> dataIterator, int threadNr, MeveoUser lastCurrentUser, boolean isRunningAsJobManager, boolean spreadOverCluster, Queue jobQueue,
            int batchSize, JobExecutionResultImpl jobExecutionResult, boolean isNewTx, boolean useMultipleItemProcessing, BiConsumer<T, JobExecutionResultImpl> processSingleItemFunction,
            BiConsumer<List<T>, JobExecutionResultImpl> processMultipleItemFunction, CountDownLatch countDown) {

        String auditOriginName = jobExecutionResult.getJobInstance().getJobTemplate() + "/" + jobInstanceCode;

        JMSContext jmsContext = null;
        ItertatorJobMessageListener messageListener = null;
        if (spreadOverCluster) {
            jmsContext = jmsConnectionFactory.createContext(System.getenv(REMOTE_MQ_ADMIN_USER), System.getenv(REMOTE_MQ_ADMIN_PASSWORD), JMSContext.CLIENT_ACKNOWLEDGE);
            jmsContext.setExceptionListener(new ExceptionListener() {

                @Override
                public void onException(JMSException e) {
                    log.error("Exception while consuming Job processing data messages", e);

                }
            });

            JMSConsumer jmsConsumer = jmsContext.createConsumer(jobQueue);
            jmsContext.stop(); // Context is autostarted when consumer is created
            messageListener = new ItertatorJobMessageListener(countDown, isNewTx, useMultipleItemProcessing, processSingleItemFunction, processMultipleItemFunction, jobExecutionResult);
            jmsConsumer.setMessageListener(messageListener);
        }
        final JMSContext jmsContextFinal = jmsContext;
        final ItertatorJobMessageListener messageListenerFinal = messageListener;

        Long jobInstanceId = jobExecutionResult.getJobInstance().getId();

        Runnable task = () -> {

            Thread.currentThread().setName(jobInstanceCode + "-" + threadNr);

            currentUserProvider.reestablishAuthentication(lastCurrentUser);

            AuditOrigin.setAuditOriginAndName(ChangeOriginEnum.JOB, auditOriginName);
            int nrOfItemsProcessedByThread = 0;

            // First process data from a DB based iterator
            if (isRunningAsJobManager) {

                mainLoop: while (true) {

                    List<T> itemsToProcess = getNextItemsToProcess(batchSize, dataIterator, jobExecutionResult.getJobInstance().getId());
                    if (itemsToProcess.isEmpty()) {
                        break mainLoop;
                    }

//                for (T item : itemsToProcess) {
//                    log.error("Will process #" + ((IEntity) item).getId());
//                }
                    processItems(itemsToProcess, isNewTx, useMultipleItemProcessing, processSingleItemFunction, processMultipleItemFunction, jobExecutionResult);

                    int nrOfItemsInBatch = itemsToProcess.size();
                    nrOfItemsProcessedByThread = nrOfItemsProcessedByThread + nrOfItemsInBatch;
                }
            }

            int nrOfItemsDb = nrOfItemsProcessedByThread;
            int nrOfItemsQueue = 0;
            int nrofMessages = 0;

            // Continue processing messages from a message queue if applicable
            if (spreadOverCluster && !isJobRequestedToStop(jobInstanceId)) {

                jmsContextFinal.start();
                try {
                    countDown.await();

                    // Now need to wait until all messages were processed, as countDown is released once last message is received, not when all messages are processed
                    // Polling is done
                    String queueName = jobQueue.getQueueName();
                    do {
                        Thread.sleep(2000);
                    } while (!isJobRequestedToStop(jobInstanceId) && !areAllMessagesConsumed(jobInstanceCode, queueName));

                } catch (InterruptedException e) {
                    log.error("Job message listener was interrupted");
                } catch (JMSException e) {
                    log.error("Failed to obtain queue name", e);
                }

                nrOfItemsQueue = messageListenerFinal.getItemCount();
                nrofMessages = messageListenerFinal.getMsgCount();
                nrOfItemsProcessedByThread = nrOfItemsProcessedByThread + nrOfItemsQueue;
                jmsContextFinal.close();
            }

            log.info("Thread {} processed {} items: {} from db and {} from {} messages", Thread.currentThread().getName(), nrOfItemsProcessedByThread, nrOfItemsDb, nrOfItemsQueue, nrofMessages);

        };

        return task;

    }

    /**
     * Process items
     * 
     * @param itemsToProcess Items to process
     * @param isNewTx Should functions be called in a new TX
     * @param useMultipleItemProcessing Process items in batch
     * @param processSingleItemFunction A function to process single item
     * @param processMultipleItemFunction A function to process multiple items
     * @param jobExecutionResult Job execution result
     */
    private void processItems(List<T> itemsToProcess, boolean isNewTx, boolean useMultipleItemProcessing, BiConsumer<T, JobExecutionResultImpl> processSingleItemFunction,
            BiConsumer<List<T>, JobExecutionResultImpl> processMultipleItemFunction, JobExecutionResultImpl jobExecutionResult) {

        int nrOfItemsInBatch = itemsToProcess.size();

        // Process items in batch.
        if (useMultipleItemProcessing) {
            try {
                // When batch processing happens in its own TX, data is read from a queue in QueueBasedIterator and if rollback occurs, data will remain in a queue
                if (isNewTx) {
                    methodCallingUtils.callMethodInNewTx(() -> processMultipleItemFunction.accept(itemsToProcess, jobExecutionResult));

                } else {
                    processMultipleItemFunction.accept(itemsToProcess, jobExecutionResult);
                }

                if (!isProcessMultipleItemFunctionUpdateProgressItself()) {
                    jobExecutionResult.registerSucces(nrOfItemsInBatch);
                }

                // Server is shutting down
            } catch (ComponentIsStoppedException e) {

                throw e;

                // Batch processing has failed, so process item one by one if possible
            } catch (Exception e) {

                if (processSingleItemFunction != null) {

                    log.error("Failed to process items in batch. Items will be processed one by one", e);
                    for (T itemToProcessFromFailedBatch : itemsToProcess) {
                        processItem(itemToProcessFromFailedBatch, isNewTx, processSingleItemFunction, jobExecutionResult);
                    }

                    // Record a message if there is no single item processing function available
                } else {
                    log.error("Failed to process items in batch", e);
                    jobExecutionResult.registerError(e.getMessage(), nrOfItemsInBatch);
                }
            }

            // Process each item
        } else {

            processItem(itemsToProcess.get(0), isNewTx, processSingleItemFunction, jobExecutionResult);
        }
    }

    /**
     * Get a set of data to process from a DB data source
     * 
     * @param nrItems Number of items to return
     * @param threadIterator Iterator from DB based data source
     * @param jobInstanceId Job instance identifier
     * @return A list of data items
     */
    private List<T> getNextItemsToProcess(int nrItems, Iterator<T> threadIterator, Long jobInstanceId) {

        final List<T> itemsToProcess = new ArrayList<T>();

        // In multiItemProcessing try to read batch size in 1/5th intervals as to spread better load among threads when there are little of data to process
        int maxMultiReadSize = nrItems / 5 + 1;
        int nrOfItemsInBatch = 0;

        while (nrOfItemsInBatch < nrItems) {

            if (threadIterator instanceof SynchronizedIterator) {

                // Read items in batch as to minimize blocking time on synchronized method SynchronizedIterator.next
                int nrItemsToRead = (nrItems - nrOfItemsInBatch) < maxMultiReadSize ? (nrItems - nrOfItemsInBatch) : maxMultiReadSize;
                List<T> itemsToProcessBatchRead = ((SynchronizedIterator<T>) threadIterator).next(nrItemsToRead);
                if (itemsToProcessBatchRead != null) {

                    itemsToProcess.addAll(itemsToProcessBatchRead);
                    int nrItemsRead = itemsToProcessBatchRead.size();
                    nrOfItemsInBatch = nrOfItemsInBatch + nrItemsRead;

                    // Nothing found and no other iterator to switch to
                } else {
                    break;
                }

            } else {
                T itemToProcess = threadIterator.next();
                if (itemToProcess != null) {
                    itemsToProcess.add(itemToProcess);
                    nrOfItemsInBatch++;

                    // No more data found in queue
                } else {
                    break;
                }
            }
        }

        // Check if job is not stopped yet
        if (isJobRequestedToStop(jobInstanceId)) {
            return new ArrayList<T>();
        }

        return itemsToProcess;
    }

    /**
     * Clear pending workload if data set was distributed over a JMS queue
     * 
     * @param jobInstance Job instance to clear
     * @param queueName Name of the MQ queue to clear
     */
    private void clearPendingWorkLoad(JobInstance jobInstance, String queueName) {

        String mqHost = System.getenv(REMOTE_MQ_HOST);
        if (StringUtils.isBlank(mqHost)) {
            mqHost = "localhost";
        }

        String jmxPort = System.getenv(REMOTE_MQ_JMX_PORT);
        if (StringUtils.isBlank(jmxPort)) {
            jmxPort = "1099";
        }

        HashMap<String, Object> environment = new HashMap<String, Object>();
        String[] credentials = new String[] { System.getenv(REMOTE_MQ_ADMIN_USER), System.getenv(REMOTE_MQ_ADMIN_PASSWORD) };
        environment.put(JMXConnector.CREDENTIALS, credentials);

        try (JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + mqHost + ":" + jmxPort + "/jmxrmi"), environment)) {
            MBeanServerConnection remote = connector.getMBeanServerConnection();

            ObjectName bean = new ObjectName("org.apache.activemq.artemis:broker=\"0.0.0.0\",component=addresses,address=\"" + queueName + "\"");

            remote.invoke(bean, "purge", null, null);
            connector.close();

        } catch (InstanceNotFoundException e) {
            // Do nothing, the queue does not exist yet

        } catch (Exception e) {
            log.error("Failed to purge pending job data  messages for job {}", jobInstance.getCode(), e);
        }
    }

    /**
     * Check if there are any messages that were either not delivered or not completely processed yet in a JMS queue
     * 
     * @param queueName Name of the MQ queue where to check if all messages were delivered and processed
     * 
     * @param jobInstance Job instance to clear
     */
    private boolean areAllMessagesConsumed(String jobInstanceCode, String queueName) {

        String mqHost = System.getenv(REMOTE_MQ_HOST);
        if (StringUtils.isBlank(mqHost)) {
            mqHost = "localhost";
        }

        String jmxPort = System.getenv(REMOTE_MQ_JMX_PORT);
        if (StringUtils.isBlank(jmxPort)) {
            jmxPort = "1099";
        }

        HashMap<String, Object> environment = new HashMap<String, Object>();
        String[] credentials = new String[] { System.getenv(REMOTE_MQ_ADMIN_USER), System.getenv(REMOTE_MQ_ADMIN_PASSWORD) };
        environment.put(JMXConnector.CREDENTIALS, credentials);

        try (JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + mqHost + ":" + jmxPort + "/jmxrmi"), environment)) {
            MBeanServerConnection remote = connector.getMBeanServerConnection();

            ObjectName bean = new ObjectName("org.apache.activemq.artemis:broker=\"0.0.0.0\",component=addresses,address=\"" + queueName + "\",subcomponent=queues,routing-type=\"anycast\",queue=\"" + queueName + "\"");

            long nrMessagesNotDelivered = (long) remote.invoke(bean, "countMessages", null, null);
            log.debug("Found {} pending messages in queue {}", nrMessagesNotDelivered, queueName);
            if (nrMessagesNotDelivered == 0) {

                nrMessagesNotDelivered = (long) remote.invoke(bean, "countDeliveringMessages", new String[] { null }, new String[] { String.class.getName() });
                log.debug("Found {} still processing messages in queue {}", nrMessagesNotDelivered, queueName);

                if (nrMessagesNotDelivered == 0) {
                    return true;
                }
            }

        } catch (Exception e) {
            log.error("Failed to check for pending job data messages for job {}", jobInstanceCode, e);
        }
        return false;
    }

    /**
     * Check if there are any messages that were either not delivered yet in a JMS queue
     * 
     * @param jobInstance Job instance to clear
     * @param queueName Name of the MQ queue where to check if all messages were delivered
     */
    public static boolean areAllMessagesDelivered(String jobInstanceCode, String queueName) {

        String mqHost = System.getenv(REMOTE_MQ_HOST);
        if (StringUtils.isBlank(mqHost)) {
            mqHost = "localhost";
        }

        String jmxPort = System.getenv(REMOTE_MQ_JMX_PORT);
        if (StringUtils.isBlank(jmxPort)) {
            jmxPort = "1099";
        }

        HashMap<String, Object> environment = new HashMap<String, Object>();
        String[] credentials = new String[] { System.getenv(REMOTE_MQ_ADMIN_USER), System.getenv(REMOTE_MQ_ADMIN_PASSWORD) };
        environment.put(JMXConnector.CREDENTIALS, credentials);

        try (JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + mqHost + ":" + jmxPort + "/jmxrmi"), environment)) {
            MBeanServerConnection remote = connector.getMBeanServerConnection();

            ObjectName bean = new ObjectName("org.apache.activemq.artemis:broker=\"0.0.0.0\",component=addresses,address=\"" + queueName + "\",subcomponent=queues,routing-type=\"anycast\",queue=\"" + queueName + "\"");

            long nrMessagesNotDelivered = (long) remote.invoke(bean, "countMessages", null, null);
            Logger log = LoggerFactory.getLogger(IteratorBasedJobBean.class);
            log.debug("Found {} pending messages in queue {}", nrMessagesNotDelivered, queueName);
            return nrMessagesNotDelivered == 0;

        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(IteratorBasedJobBean.class);
            log.error("Failed to check for pending job data messages for job {}", jobInstanceCode, e);
        }
        return false;
    }

    /**
     * Create a non durable queue via JMX API
     * 
     * @param jobInstance Job instance to clear
     * @param queueName Name of the MQ queue to create
     */
    private boolean createAQueueIfDoesNotExist(String jobInstanceCode, String queueName) {

        String mqHost = System.getenv(REMOTE_MQ_HOST);
        if (StringUtils.isBlank(mqHost)) {
            mqHost = "localhost";
        }

        String jmxPort = System.getenv(REMOTE_MQ_JMX_PORT);
        if (StringUtils.isBlank(jmxPort)) {
            jmxPort = "1099";
        }

        HashMap<String, Object> environment = new HashMap<String, Object>();
        String[] credentials = new String[] { System.getenv(REMOTE_MQ_ADMIN_USER), System.getenv(REMOTE_MQ_ADMIN_PASSWORD) };
        environment.put(JMXConnector.CREDENTIALS, credentials);

        try (JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + mqHost + ":" + jmxPort + "/jmxrmi"), environment)) {
            MBeanServerConnection remote = connector.getMBeanServerConnection();

            ObjectName bean = new ObjectName("org.apache.activemq.artemis:broker=\"0.0.0.0\"");

            Object[] params = new Object[] { queueName, queueName, false, "ANYCAST" };
            String[] signature = new String[] { "java.lang.String", "java.lang.String", "boolean", "java.lang.String" };
            remote.invoke(bean, "createQueue", params, signature);
            log.debug("Created a non-durable queue {}", queueName);

        } catch (Exception e) {

            if (e instanceof RuntimeMBeanException && e.getMessage().contains("AMQ229019")) {

                log.debug("A queue {} already exists", queueName);

            } else {
                log.error("Failed to create a non-durable queue for a job {}", jobInstanceCode, e);
                return false;
            }
        }
        return true;
    }

    private class ItertatorJobMessageListener implements MessageListener {

        /**
         * A content of a message indicating that its a last message to be processed
         */
        public static final String EOF_MESSAGE = "eof";

        private int msgCount = 0;
        private int itemCount = 0;
//        private CountDownLatch countDown;
        private boolean isNewTx;
        private boolean useMultipleItemProcessing;
        private BiConsumer<T, JobExecutionResultImpl> processSingleItemFunction;
        private BiConsumer<List<T>, JobExecutionResultImpl> processMultipleItemFunction;
        private JobExecutionResultImpl jobExecutionResult;

        public ItertatorJobMessageListener(CountDownLatch countDown, boolean isNewTx, boolean useMultipleItemProcessing, BiConsumer<T, JobExecutionResultImpl> processSingleItemFunction,
                BiConsumer<List<T>, JobExecutionResultImpl> processMultipleItemFunction, JobExecutionResultImpl jobExecutionResult) {

//            this.countDown = countDown;
            this.isNewTx = isNewTx;
            this.useMultipleItemProcessing = useMultipleItemProcessing;
            this.processMultipleItemFunction = processMultipleItemFunction;
            this.processSingleItemFunction = processSingleItemFunction;
            this.jobExecutionResult = jobExecutionResult;
        }

        /**
         * @return A number of messages received
         */
        public int getMsgCount() {
            return msgCount;
        }

        /**
         * @return A number of items received via a messages
         */
        public int getItemCount() {
            return itemCount;
        }

        @Override
        public void onMessage(Message msg) {

            if (isJobRequestedToStop(jobExecutionResult.getJobInstance().getId())) {
                return;
            }

            try {
                // Indicates that a last message in a queue was reached and further processing should stop
                if (EOF_MESSAGE.equals(msg.getStringProperty(EOF_MESSAGE))) {

                    // Fire an event to release threads for data processing
                    lastJobDataMsgEventProducer.fire(jobExecutionResult.getJobInstance().getId());

                    msg.acknowledge();

                } else {

                    @SuppressWarnings("unchecked")
                    List<T> itemsToProcess = (List<T>) msg.getBody(Serializable.class);
                    msgCount++;
                    itemCount = itemCount + itemsToProcess.size();
                    processItems(itemsToProcess, isNewTx, useMultipleItemProcessing, processSingleItemFunction, processMultipleItemFunction, jobExecutionResult);

                    msg.acknowledge();
                }
            } catch (JMSException e) {
                Logger log = LoggerFactory.getLogger(this.getClass());
                log.error("Failed to read JMS JOB processing message body.", e);
            }
        }
    }

    /**
     * Release job data processing threads - count down a countDownLatch that keeps threads in waiting mode. Thread will exit listening for messages in a queue mode.
     * 
     * @param jobInstanceId Job instance identifier
     */
    public static void releaseJobDataProcessingThreads(Long jobInstanceId) {

        // Clean up counter for job thread scheduling purpose
        CountDownLatch countDown = countDowns.get(jobInstanceId);
        if (countDown != null) {
            countDown.countDown();
            countDowns.remove(jobInstanceId);
        } else {
            Logger log = LoggerFactory.getLogger(IteratorBasedJobBean.class);
            log.error("No countDownLatch found for job {}", jobInstanceId);
        }
    }
}