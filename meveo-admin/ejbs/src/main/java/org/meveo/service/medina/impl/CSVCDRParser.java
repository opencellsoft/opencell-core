/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.service.medina.impl;

import java.io.File;
import java.io.Serializable;


/**
 * This Interface must be implemented to parse CDR and create EDR from it
 * The implementation must be a Named class, i.e. a class annotated
 * with the javax.ejb.Nammed annotation.
 * 
 */
public interface CSVCDRParser {

	
	void init(File CDRFile);
	
	/**
	 * 
	 * @return a unique identifier from the batch
	 */	
	String getOriginBatch();
	
	/**
	 * Verify that the format of the CDR is correct and
	 *  modify it if needed.
	 *  The implementation should save locally the CDR as call to other methods do not contain reference to the CDR
	 * @param line : the input CDR
	 * @return the modified CDR
	 * @throws InvalidFormatException 
	 */
	Serializable getCDR(String line) throws InvalidFormatException;
	
	/**
	 * Build and return a unique identifier from the CDR in order
	 * to avoid importing twice the same CDR in MEVEO
	 * @param cdr : CDR returned by the getCDR method
	 * @return CDR's unique key
	 */
	String getOriginRecord(Serializable cdr);

	/**
	 * Return in the form of an AccessDAO object the user id and sercice id 
	 *  that will allow to lookup the ACCESS.
	 * @param cdr : CDR returned by the getCDR method
	 * @return the Access userId
	 * @throws InvalidAccessException
	 */
	String getAccessUserId(Serializable cdr) throws InvalidAccessException;

	/**
	 * Construct EDRDAO from the CDR
	 * @param cdr : CDR returned by the getCDR method
	 * @return  EDR Data Access Object
	 * @throws CDRParsingException
	 */
	EDRDAO getEDR(Serializable cdr);

	/**
	 * Construct a csv record for the rejected CDR with given rejection reason
	 * @param cdr
	 * @param reason
	 * @return
	 */
	String getCDRLine(Serializable cdr, String reason);

}
