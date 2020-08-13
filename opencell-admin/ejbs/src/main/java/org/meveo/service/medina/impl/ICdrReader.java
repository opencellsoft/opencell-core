package org.meveo.service.medina.impl;

import java.io.IOException;
import java.util.List;

import org.meveo.model.rating.CDR;

/**
 * The Interface CdrReader.
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
     * Get next record. A synchronized method to read from CDR source
     *
     * @param cdrParser the cdr parser
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

    /**
     * Get all record from list of cdr lines
     *
     * @param cdrParser the cdr parser
     * @param cdrLines list of cdr lines
     * @return List<CDR> records
     */
    List<CDR> getRecords(ICdrParser cdrParser, List<String> cdrLines);
}
