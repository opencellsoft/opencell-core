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
import java.io.Serializable;
import java.util.Map;


/**
 * This Interface must be implemented to parse CDR and create EDR from it
 * The implementation must be a Named class, i.e. a class annotated
 * with the javax.ejb.Nammed annotation.
 * 
 */
public interface CSVCDRParser {

    /**
     * @param CDRFile cdr file.
     */
    void init(File CDRFile);
	
	/**
	 * 
	 * @return a unique identifier from the batch
	 */	
	Map<String, String> getOriginBatch();
	
	/**
	 * Verify that the format of the CDR is correct and
	 *  modify it if needed.
	 *  The implementation should save locally the CDR as call to other methods do not contain reference to the CDR
	 * @param line : the input CDR
	 * @return the modified CDR
	 * @throws InvalidFormatException  invalid format exception.
	 */
	Serializable getCDR(String line) throws InvalidFormatException;
	
	/**
	 * Build and return a unique identifier from the CDR in order.
	 * to avoid importing twice the same CDR in MEVEO
	 * @param cdr : CDR returned by the getCDR method
	 * @param origin origin.
	 * @return CDR's unique key
	 */
	String getOriginRecord(Serializable cdr, String origin);

	/**
	 * Return in the form of an AccessDAO object the user id and sercice id 
	 *  that will allow to lookup the ACCESS.
	 * @param cdr : CDR returned by the getCDR method
	 * @return the Access userId
	 * @throws InvalidAccessException invalid access exception.
	 */
	String getAccessUserId(Serializable cdr) throws InvalidAccessException;

	/**
	 * Construct EDRDAO from the CDR.
	 * @param cdr : CDR returned by the getCDR method
	 * @param origin origin
	 * @return  EDR Data Access Object
	 * 
	 */
	EDRDAO getEDR(Serializable cdr, String origin);

	/**
	 * Construct a csv record for the rejected CDR with given rejection reason.
	 * @param cdr cdr
	 * @param reason reason
	 * @return cdr line
	 */
	String getCDRLine(Serializable cdr, String reason);

	/**
	 * @param username user name
	 * @param ip Ip address.
	 * 
	 */
	void initByApi(String username, String ip);
	
}
