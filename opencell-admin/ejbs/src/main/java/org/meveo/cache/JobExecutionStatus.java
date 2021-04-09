package org.meveo.cache;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.meveo.model.shared.DateUtils;

/**
 * Tracks job execution status in cache
 * 
 * @author Andrius Karpavicius
 *
 */
public class JobExecutionStatus implements Serializable {

    private static final long serialVersionUID = 88415736291469158L;

    /**
     * Job instance id
     */
    private Long jobId;

    /**
     * Job instance code
     */
    private String jobCode;

    /**
     * Timestamp when job started on
     */
    private Date startedOn;

    /**
     * A request was made to stop
     */
    private boolean requestedToStop;

    /**
     * A request was made to run on a specific node - applies when job can be executed on a single node only.
     */
    private String lockForNode;

    /**
     * Cluster nodes (map key) and job instance runs threads (map value) the job is running on.
     */
    private Map<String, JobExecutionInfo> nodesAndThreads = new HashMap<>();

    /**
     * Constructor
     */
    public JobExecutionStatus() {

    }

    /**
     * Constructor
     * 
     * @param jobId Job identifier
     * @param jobCode Job code
     */
    public JobExecutionStatus(Long jobId, String jobCode) {
        this.jobId = jobId;
        this.jobCode = jobCode;
    }

    /**
     * @return Job instance id
     */
    public Long getJobId() {
        return jobId;
    }

    /**
     * @param jobId Job instance id
     */
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    /**
     * @return Job instance code
     */
    public String getJobCode() {
        return jobCode;
    }

    /**
     * @param jobCode Job instance code
     */
    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    /**
     * @return Timestamp when job started on
     */
    public Date getStartedOn() {
        return startedOn;
    }

    /**
     * @param startedOn Timestamp when job started on
     */
    public void setStartedOn(Date startedOn) {
        this.startedOn = startedOn;
    }

    /**
     * @return Cluster nodes (map key) and job instance runs threads (map value) the job is running on.
     */
    public Map<String, JobExecutionInfo> getNodesAndThreads() {
        return nodesAndThreads;
    }

    /**
     * @param nodesAndThreads Cluster nodes (map key) and job instance runs threads (map value) the job is running on.
     */
    public void setNodesAndThreads(Map<String, JobExecutionInfo> nodesAndThreads) {
        this.nodesAndThreads = nodesAndThreads;
    }

    /**
     * Mark as running on a cluster node
     * 
     * @param node Cluster node name
     */
    public void markAsRunningOn(String node) {
        this.nodesAndThreads.put(node, new JobExecutionInfo());
    }

    /**
     * Mark as running on a cluster node. Remove lock.
     * 
     * @param node Cluster node name
     * @param numberOfThreads A number of threads/futures that are running a job
     */
    public void markAsRunningOn(String node, Long jobExecutionResultId, Integer numberOfThreads) {

        this.lockForNode = null;
        if (jobExecutionResultId == null && numberOfThreads == null) {
            this.nodesAndThreads.put(node, new JobExecutionInfo());
        } else {
            this.nodesAndThreads.put(node, new JobExecutionInfo(jobExecutionResultId, numberOfThreads == null ? 0 : numberOfThreads));
        }
    }

    /**
     * Mark as completed running on a cluster node. In multi cluster environment remove "requested to stop" flag only when ALL clusters are finished running a job
     * 
     * @param node Cluster node name
     */
    public void markAsFinished(String node) {
        this.lockForNode = null;
        this.nodesAndThreads.remove(node);

        // In multi cluster environment remove "requested to stop" flag only when ALL clusters are finished running a job
        if (this.nodesAndThreads.isEmpty()) {
            setRequestedToStop(false);
        }
    }

    /**
     * Mark job as locked to run on a cluster node
     * 
     * @param node Cluster node to run on
     */
    public void markAsLockedOn(String currentNode) {
        lockForNode = currentNode;
    }

    /**
     * Is job running on any nodes
     * 
     * @return True if job is running on any cluster node
     */
    public boolean isRunning() {
        return !this.nodesAndThreads.isEmpty();
    }

    /**
     * Is job running on this cluster node
     * 
     * @param node Cluster node name
     * @return True if job is running on this cluster node
     */
    public boolean isRunning(String node) {
        return this.nodesAndThreads.containsKey(node);
    }

    public JobExecutionStatus clone() {

        JobExecutionStatus clone = new JobExecutionStatus(jobId, jobCode);
        clone.setLockForNode(lockForNode);
        clone.setRequestedToStop(requestedToStop);
        clone.setStartedOn(startedOn);
        clone.getNodesAndThreads().putAll(this.nodesAndThreads);
        return clone;
    }

    @Override
    public String toString() {

        String nodeInfo = "";
        for (Entry<String, JobExecutionInfo> nodeThreadInfo : nodesAndThreads.entrySet()) {
            nodeInfo = nodeInfo + (nodeInfo.length() > 0 ? ", " : "") + nodeThreadInfo.getKey() + "(" + nodeThreadInfo.getValue().toString() + ")";
        }

        return jobId + "/" + jobCode + ", started on " + DateUtils.formatAsTime(startedOn) + (requestedToStop ? ", stopping" : "") + (lockForNode != null ? ", locked for " + lockForNode : "")
                + (!nodesAndThreads.isEmpty() ? ", running on " + nodeInfo : "");
    }

    /**
     * Get the threads/futures that job is running on on a given cluster node
     * 
     * @param node Cluster node name
     * @return A list of threads/futures or an empty list if not found
     */
    public int getNumberThreads(String node) {

        JobExecutionInfo jobExecutionInfo = this.nodesAndThreads.get(node);
        if (jobExecutionInfo != null) {
            return jobExecutionInfo.getNumberOfThreads();
        }
        return 0;
    }

    /**
     * Get nodes that job is being executed on
     * 
     * @return A list of cluster node names
     */
    public Set<String> getNodes() {
        return this.nodesAndThreads.keySet();
    }

    /**
     * Was request made to stop the job
     * 
     * @return True if request was made to stop the job execution
     */
    public boolean isRequestedToStop() {
        return requestedToStop;
    }

    /**
     * @param requestedToStop True if request was made to stop the job execution
     */
    public void setRequestedToStop(boolean requestedToStop) {
        this.requestedToStop = requestedToStop;
    }

    /**
     * @return A request was made to run on a specific node - applies when job can be executed on a single node only.
     */
    public String getLockForNode() {
        return lockForNode;
    }

    /**
     * @param lockForNode A request was made to run on a specific node - applies when job can be executed on a single node only.
     */
    public void setLockForNode(String lockForNode) {
        this.lockForNode = lockForNode;
    }

    /**
     * Tracks job execution per node run (JobExecutionResult entity equivalent)
     */
    public class JobExecutionInfo implements Serializable {

        private static final long serialVersionUID = -7414918696509398557L;

        /**
         * Job execution identifier (JobExecutionResult entity id)
         */
        private Long jobExecutionId;

        /**
         * A request was made to stop
         */
        private boolean requestedToStop;

        /**
         * Threads/futures that job is running on
         */
        private int numberOfThreads;

        /**
         * Constructor
         */
        public JobExecutionInfo() {
        }

        /**
         * Constructor
         * 
         * @param jobExecutionId Job execution result identifier
         * @param numberOfThreads Number of threads/futures that job is run on
         */
        public JobExecutionInfo(Long jobExecutionId, int numberOfThreads) {
            super();
            this.jobExecutionId = jobExecutionId;
            this.numberOfThreads = numberOfThreads;
        }

        /**
         * Get a number of threads
         * 
         * @return Number of threads
         */
        public int getNumberOfThreads() {
            return numberOfThreads;
        }

        @Override
        public String toString() {
            return (jobExecutionId != null ? "id:" + jobExecutionId : "") + "|threads:" + numberOfThreads + "|to stop:" + requestedToStop;
        }
    }
}