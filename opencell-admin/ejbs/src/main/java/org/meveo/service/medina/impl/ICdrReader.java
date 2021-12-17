package org.meveo.service.medina.impl;

import java.io.IOException;

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
     * Get next CDR record. A synchronized method to read from CDR source. Any failure to parse a line is reflected in CDR.rejectReason and CDR.rejectReasonException.
     *
     * @param cdrParser The cdr parser to apply
     * @return CDR record
     * @throws IOException Failure to read the CDR source
     */
    CDR getNextRecord(ICdrParser cdrParser) throws IOException;

    /**
     * Get CDR record from CDR data. Any failure to parse a line is reflected in CDR.rejectReason and CDR.rejectReasonException.
     *
     * @param cdrParser The cdr parser to apply
     * @param cdrData CDR data to parse
     * @return CDR record
     */
    CDR getRecord(ICdrParser cdrParser, Object cdrData);

    /**
     * Close CDR record reader
     * 
     * @throws IOException IO exception
     */
    void close() throws IOException;
}