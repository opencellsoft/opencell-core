package org.meveo.model.jobs;

/**
 * How job execution has completed
 * 
 * @author Andrius Karpavicius
 */
public enum JobExecutionResultStatusEnum {

    /**
     * Job is currently running
     */
    RUNNING(3),

    /**
     * Finished completely, no more data to process
     */
    COMPLETED(1),

    /**
     * Job finished, but there is more data to process
     */
    COMPLETED_MORE(2),

    /**
     * Job execution was cancelled
     */
    CANCELLED(5),

    /**
     * Job execution had failed because of some errors
     */
    FAILED(4);

    /**
     * Priority of status. The higher the number, the more "unsuccessful" was the job execution.
     */
    private int priority;

    JobExecutionResultStatusEnum(int priority) {
        this.priority = priority;
    }

    /**
     * @return Label text for GUI
     */
    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }

    /**
     * Get status priority. The higher the number, the more "unsuccessful" was the job execution.
     * 
     * @return Status priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Compare two statuses and return the one with a higher priority number
     * 
     * @param statusOther Another status to compare to
     * @return A status with a higher priority number
     */
    public JobExecutionResultStatusEnum getStatusWithHigherPriority(JobExecutionResultStatusEnum statusOther) {

        if (priority > statusOther.getPriority()) {
            return this;
        }
        return statusOther;
    }

}