package org.meveo.service.medina.impl;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 
 * @author h.znibar
 *
 */
public interface CdrCsvReader extends CdrReader {
    
    /**
     * Initialize CDR Reader to read from a file
     * 
     * @param CDRFile CDR file.
     * @throws FileNotFoundException
     */
    void init(File file) throws FileNotFoundException;
    
    /**
     * Initialize CDR Reader
     *
     * @param userName the user name
     * @param ip the ip address
     */
    void init(String userName, String ip);

}
