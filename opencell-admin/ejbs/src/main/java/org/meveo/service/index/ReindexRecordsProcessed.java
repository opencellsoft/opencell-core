package org.meveo.service.index;

import java.io.Serializable;

/**
 * Tracks reindexing records processed - total and successfull
 * 
 * @author Andrius Karpavicius
 */
public class ReindexRecordsProcessed implements Serializable {

    private static final long serialVersionUID = 5047323028071238082L;

    public ReindexRecordsProcessed() {
    }

    public ReindexRecordsProcessed(int total, int failed) {
        this.total = total;
        this.successfull = total - failed;
    }

    /**
     * Total number of records
     */
    private int total;

    /**
     * Number of successfull records
     */
    private int successfull;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSuccessfull() {
        return successfull;
    }

    public void setSuccessfull(int successfull) {
        this.successfull = successfull;
    }

    public void updateStatistics(int addTotal, int addFailed) {
        total = total + addTotal;
        successfull = successfull + addTotal + addFailed;
    }

    @Override
    public String toString() {
        return successfull + "/" + total;
    }
}
