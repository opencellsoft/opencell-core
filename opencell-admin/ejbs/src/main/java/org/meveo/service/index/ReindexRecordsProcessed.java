package org.meveo.service.index;

import java.io.Serializable;

/**
 * Tracks reindexing records processed - total and successful
 * 
 * @author Andrius Karpavicius
 */
public class ReindexRecordsProcessed implements Serializable {

    private static final long serialVersionUID = 5047323028071238082L;

    public ReindexRecordsProcessed() {
    }

    /**
     * Constructor
     * 
     * @param total Total records indexed
     * @param failed Number of records failed
     */
    public ReindexRecordsProcessed(int total, int failed) {
        this.total = total;
        this.successfull = total - failed;
    }

    /**
     * Total number of records
     */
    private int total;

    /**
     * Number of successful records
     */
    private int successfull;

    /**
     * @return Total number of records
     */
    public int getTotal() {
        return total;
    }

    /**
     * @param total Total number of records
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * @return Number of successful records
     */
    public int getSuccessfull() {
        return successfull;
    }

    /**
     * @param successfull Number of successful records
     */
    public void setSuccessfull(int successfull) {
        this.successfull = successfull;
    }

    /**
     * Update statistics with additional information
     * 
     * @param addTotal Additional total number of records processed
     * @param addFailed Additional number of records failed
     */
    public void updateStatistics(int addTotal, int addFailed) {
        total = total + addTotal;
        successfull = successfull + addTotal - addFailed;
    }

    /**
     * Update statistics
     * 
     * @param recordInfo Reindexing records processed info - total and successful
     */
    public void updateStatistics(ReindexRecordsProcessed recordInfo) {
        total = total + recordInfo.getTotal();
        successfull = successfull + recordInfo.getSuccessfull();
    }

    @Override
    public String toString() {
        return successfull + "/" + total;
    }
}