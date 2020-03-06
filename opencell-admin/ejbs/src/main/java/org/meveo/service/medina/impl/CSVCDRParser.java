/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.service.medina.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.meveo.model.rating.CDR;



/**
 * This Interface must be implemented to parse CDR and create EDR from it The implementation must be a Named class, i.e. a class annotated with the javax.ejb.Nammed annotation.
 * 
 * @lastModifiedVersion willBeSetLater
 * 
 */
public interface CSVCDRParser {

    /**
     * Initialize CDR parser to read from a file
     * 
     * @param CDRFile cdr file.
     * @throws FileNotFoundException
     */
    void init(File CDRFile) throws FileNotFoundException;

    /**
     * Get next record. A synchronized method to read from a file
     * 
     * @return CDR record
     * @throws IOException Failure to read a file
     */
    CDR getNextRecord() throws IOException;

    /**
     * Close CDR record reader when it was initialized from a file
     * 
     * @throws IOException IO exception
     */
    void close() throws IOException;

    /**
     * Initialize CDR parser from API
     * 
     * @param username user name
     * @param ip Ip address
     */
    void initByApi(String username, String ip);

    /**
     * Convert text line/record into a CDR object. Parsing exceptions are available in CDR.rejectReason
     * 
     * @param line Text line/record
     * @return CDR object
     */
    CDR parseCDR(String line);

}