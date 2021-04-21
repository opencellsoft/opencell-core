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
    RUNNING,

    /**
     * Finished completely, no more data to process
     */
    COMPLETED,

    /**
     * Job finished, but there is more data to process
     */
    COMPLETED_MORE,

    /**
     * Job execution was cancelled
     */
    CANCELLED,

    /**
     * Job execution had failed because of some errors
     */
    FAILED;

    /**
     * @return Label text for GUI
     */
    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}