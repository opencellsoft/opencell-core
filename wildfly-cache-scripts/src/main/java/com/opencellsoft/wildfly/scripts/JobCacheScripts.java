package com.opencellsoft.wildfly.scripts;

import org.infinispan.util.function.SerializableBiFunction;
import org.meveo.cache.CacheKeyLong;
import org.meveo.cache.JobExecutionStatus;
import org.meveo.cache.JobRunningStatusEnum;

/**
 * @author Andrius Karpavicius
 */
public class JobCacheScripts {

    public static SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> getMarkJobAsRunningFunction(String currentNode, boolean limitToSingleNode, Long jobInstanceId,
            String jobInstanceCode, Long jobExecutionResultId, Integer nrOfThreads) {

        SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> remappingFunction = (jobInstIdFullKey, jobExecutionStatusOld) -> {

            JobRunningStatusEnum isRunning = null;

            if (jobExecutionStatusOld == null) {
                isRunning = JobRunningStatusEnum.NOT_RUNNING;

                // No change is status when job was requested to stop
            } else if (jobExecutionStatusOld.isRequestedToStop()) {
                isRunning = JobRunningStatusEnum.REQUEST_TO_STOP;
                return jobExecutionStatusOld;

            } else if (!jobExecutionStatusOld.isRunning()) {

                if (jobExecutionStatusOld.getLockForNode() == null) {
                    isRunning = JobRunningStatusEnum.NOT_RUNNING;
                } else if (jobExecutionStatusOld.getLockForNode().equals(currentNode)) {
                    isRunning = JobRunningStatusEnum.LOCKED_THIS;
                } else {
                    isRunning = JobRunningStatusEnum.LOCKED_OTHER;
                }

                // If already running, don't modify nodes
            } else if (jobExecutionStatusOld.isRunning(currentNode)) {
                isRunning = JobRunningStatusEnum.RUNNING_THIS;

            } else {
                isRunning = JobRunningStatusEnum.RUNNING_OTHER;
            }

            // If limited to run on a single node but is running or locked on another server, don't change
            if (limitToSingleNode && (isRunning == JobRunningStatusEnum.RUNNING_OTHER || isRunning == JobRunningStatusEnum.LOCKED_OTHER)) {
                return jobExecutionStatusOld;
            }

            JobExecutionStatus jobExecutionStatus = null;
            if (jobExecutionStatusOld == null) {
                jobExecutionStatus = new JobExecutionStatus(jobInstanceId, jobInstanceCode);
            } else {
                jobExecutionStatus = jobExecutionStatusOld.clone();
            }

            jobExecutionStatus.markAsRunningOn(currentNode, jobExecutionResultId, nrOfThreads);

            return jobExecutionStatus;
        };

        return remappingFunction;
    }

    public static SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> getMarkAsFinishedFunction(String currentNode, Long jobInstanceId, String jobInstanceCode, boolean isClusterMode) {
        SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> remappingFunction = (jobInstIdFullKey, jobExecutionStatusOld) -> {

            if (jobExecutionStatusOld == null || !jobExecutionStatusOld.isRunning()) {
                return new JobExecutionStatus(jobInstanceId, jobInstanceCode);

            } else if (!isClusterMode) {
                return new JobExecutionStatus(jobInstanceId, jobInstanceCode);

                // In multi cluster environment remove "requested to stop" flag only when ALL clusters are finished running a job
            } else {
                JobExecutionStatus jobExecutionStatus = jobExecutionStatusOld.clone();
                jobExecutionStatus.markAsFinished(currentNode);

                return jobExecutionStatus;
            }
        };

        return remappingFunction;
    }

    public static SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> getResetJobRunningStatusFunction(Long jobInstanceId, String jobInstanceCode) {

        SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> remappingFunction = (jobInstIdFullKey, jobExecutionStatusOld) -> {
            return new JobExecutionStatus(jobInstanceId, jobInstanceCode);
        };

        return remappingFunction;
    }

    public static SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> getAddUpdateJobInstanceFunction(Long jobInstanceId, String jobInstanceCode, boolean preserveCurrentStatus,
            String currentNode) {

        SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> remappingFunction = (jobInstIdFullKey, jobExecutionStatusOld) -> {

            if (jobExecutionStatusOld != null) {

                if (preserveCurrentStatus) {
                    return jobExecutionStatusOld;

                } else {
                    JobExecutionStatus jobExecutionStatus = jobExecutionStatusOld.clone();
                    jobExecutionStatus.markAsFinished(currentNode);

                    return jobExecutionStatus;
                }
            } else {
                return new JobExecutionStatus(jobInstanceId, jobInstanceCode);
            }
        };

        return remappingFunction;
    }

    public static SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> getLockForRunningFunction(Long jobInstanceId, String jobInstanceCode, String currentNode,
            boolean limitToSingleNode) {

        SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> remappingFunction = (jobInstIdFullKey, jobExecutionStatusOld) -> {

            JobRunningStatusEnum isRunning = null;

            if (jobExecutionStatusOld == null) {
                isRunning = JobRunningStatusEnum.NOT_RUNNING;

                // No change is status when job was requested to stop
            } else if (jobExecutionStatusOld.isRequestedToStop()) {
                isRunning = JobRunningStatusEnum.REQUEST_TO_STOP;
                return jobExecutionStatusOld;

            } else if (!jobExecutionStatusOld.isRunning()) {

                if (jobExecutionStatusOld.getLockForNode() == null) {
                    isRunning = JobRunningStatusEnum.NOT_RUNNING;
                } else if (jobExecutionStatusOld.getLockForNode().equals(currentNode)) {
                    isRunning = JobRunningStatusEnum.LOCKED_THIS;
                } else {
                    isRunning = JobRunningStatusEnum.LOCKED_OTHER;
                }

                // If already running, don't modify nodes
            } else if (jobExecutionStatusOld.isRunning(currentNode)) {
                isRunning = JobRunningStatusEnum.RUNNING_THIS;

            } else {
                isRunning = JobRunningStatusEnum.RUNNING_OTHER;
            }

            if (limitToSingleNode && isRunning == JobRunningStatusEnum.NOT_RUNNING) {

                JobExecutionStatus jobExecutionStatus = null;
                if (jobExecutionStatusOld == null) {
                    jobExecutionStatus = new JobExecutionStatus(jobInstanceId, jobInstanceCode);
                } else {
                    jobExecutionStatus = jobExecutionStatusOld.clone();
                }

                jobExecutionStatus.markAsLockedOn(currentNode);

                return jobExecutionStatus;
            } else {
                return jobExecutionStatusOld;
            }
        };

        return remappingFunction;
    }

    public static SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> getMarkJobToStopFunction(Long jobInstanceId, String jobInstanceCode) {

        SerializableBiFunction<? super CacheKeyLong, JobExecutionStatus, JobExecutionStatus> remappingFunction = (jobInstIdFullKey, jobExecutionStatusOld) -> {
            JobExecutionStatus jobExecutionStatus = null;
            if (jobExecutionStatusOld != null) {
                jobExecutionStatus = jobExecutionStatusOld.clone();
            } else {
                jobExecutionStatus = new JobExecutionStatus(jobInstanceId, jobInstanceCode);
            }
            jobExecutionStatus.setRequestedToStop(true);

            return jobExecutionStatus;
        };

        return remappingFunction;
    }
}