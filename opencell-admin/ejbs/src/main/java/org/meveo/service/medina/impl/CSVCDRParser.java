/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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