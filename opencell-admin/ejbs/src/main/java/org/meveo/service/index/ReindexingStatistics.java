package org.meveo.service.index;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

    public Map<String, ReindexRecordsProcessed> getRecordsProcessed() {
        return recordsProcessed;
    }

    public void setRecordsProcessed(Map<String, ReindexRecordsProcessed> recordsProcessed) {
        this.recordsProcessed = recordsProcessed;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public void updateStatstics(String classname, int addTotal, int addFailed) {
        if (!recordsProcessed.containsKey(classname)) {
            recordsProcessed.put(classname, new ReindexRecordsProcessed(addTotal, addFailed));

        } else {
            recordsProcessed.get(classname).updateStatistics(addTotal, addFailed);
        }
    }
}