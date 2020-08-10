package org.meveo.service.medina.impl;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 
 * @author h.znibar
 *
 */
public interface ICdrCsvReader extends ICdrReader {
    
    /**
     * Initialize CDR Reader to read from a file
     * 
     * @param CDRFile CDR file.
     * @throws FileNotFoundException
     */
    void init(File file) throws FileNotFoundException;
    
    /**
     * Initialize CDR parser from API
     * 
     * @param username user name
     * @param ip Ip address
     */
    void init(String userName, String ip);        

}
