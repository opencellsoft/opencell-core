package org.meveo.model.jobs;

/**
 * Projected job execution speed. Used to determine the frequency of job status check and progress update
 * 
 * @author Andrius Karpavicius
 *
 */
public enum JobSpeedEnum {

    /**
     * Slow. Job status check and update every 25 records
     */
    SLOW(25, 25),
    /**
     * Normal. Job status check and update every 50 records
     */
    NORMAL(50, 50),

    /**
     * Fast. Job status check and update every 100 records
     */
    FAST(100, 100),

    /**
     * Very fast. Job status check and update every 500 records
     */
    VERY_FAST(500, 500);

    int checkNb;

    int updateNb;

    private JobSpeedEnum(int checkNb, int updateNb) {

        this.checkNb = checkNb;
        this.updateNb = updateNb;
    }

    /**
     * @return Check job status every X number of records
     */
    public int getCheckNb() {
        return checkNb;
    }

    /**
     * @return Update job progress every X number of records
     */
    public int getUpdateNb() {
        return updateNb;
    }

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}