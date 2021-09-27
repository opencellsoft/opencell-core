package org.meveo.service.medina.impl;

import java.io.IOException;
import java.util.List;

import org.meveo.model.rating.CDR;

/**
 * The Interface CdrReader.
 * 
 * @author h.znibar
 */
public interface ICdrReader {

    /**
     * Initialize CDR Reader to read from originBatch.
     *
     * @param originBatch the origin batch
     */
    void init(String originBatch);

    /**
     * Return a total number of records if a reader supports such functionality
     * 
     * @return A total number of records or NULL if reader does not support such functionality
     */
    default Integer getNumberOfRecords() {
        return null;
    }

    /**
     * Get next record. A synchronized method to read from CDR source. Any failure to parse a line is reflected in CDR.rejectReason and CDR.rejectReasonException.
     *
     * @param cdrParser The cdr parser to apply
     * @return CDR record
     * @throws IOException Failure to read the CDR source
     */
    CDR getNextRecord(ICdrParser cdrParser) throws IOException;

    /**
     * Close CDR record reader
     * 
     * @throws IOException IO exception
     */
    void close() throws IOException;
}