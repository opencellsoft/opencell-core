package org.meveo.service.index;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Reindexing statistics
 * 
 * @author Andrius Karpavicius
 */
public class ReindexingStatistics implements Serializable {

    private static final long serialVersionUID = 5769400825175515203L;

    /**
     * Records processed
     */
    private Map<String, ReindexRecordsProcessed> recordsProcessed = new HashMap<String, ReindexRecordsProcessed>();

    /**
     * Occurred exception
     */
    private Throwable exception;

    /**
     * @return Records processed
     */
    public Map<String, ReindexRecordsProcessed> getRecordsProcessed() {
        return recordsProcessed;
    }

    /**
     * @param recordsProcessed Records processed
     */
    public void setRecordsProcessed(Map<String, ReindexRecordsProcessed> recordsProcessed) {
        this.recordsProcessed = recordsProcessed;
    }

    /**
     * @return Occurred exception
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * @param exception Occurred exception
     */
    public void setException(Throwable exception) {
        this.exception = exception;
    }

    /**
     * Update statistics
     * 
     * @param classname Classname that statistics are tracked for
     * @param addTotal Additional total number of records processed
     * @param addFailed Additional number of records failed
     */
    public void updateStatistics(String classname, int addTotal, int addFailed) {
        if (!recordsProcessed.containsKey(classname)) {
            recordsProcessed.put(classname, new ReindexRecordsProcessed(addTotal, addFailed));

        } else {
            recordsProcessed.get(classname).updateStatistics(addTotal, addFailed);
        }
    }

    /**
     * Update statistics
     * 
     * @param statistics Statistics to aggregate
     */
    public void updateStatistics(ReindexingStatistics statistics) {

        if (statistics.getException() != null) {
            this.setException(statistics.getException());
        }

        for (Entry<String, ReindexRecordsProcessed> recProcessedInfo : statistics.getRecordsProcessed().entrySet()) {
            if (!this.recordsProcessed.containsKey(recProcessedInfo.getKey())) {
                this.recordsProcessed.put(recProcessedInfo.getKey(), recProcessedInfo.getValue());
            } else {
                this.recordsProcessed.get(recProcessedInfo.getKey()).updateStatistics(recProcessedInfo.getValue());
            }
        }
    }
}