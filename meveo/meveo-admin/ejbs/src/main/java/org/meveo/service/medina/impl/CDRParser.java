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

import org.meveo.model.billing.Subscription;
import org.meveo.model.rating.EDR;

/**
 * This Interface must be implemented to parse CDR and create EDR from it
 * The implementation must be a Named class, i.e. a class annotated
 * with the javax.ejb.Nammed annotation.
 * 
 */
public interface CDRParser {
	
	/**
	 * Verify that the format of the CDR is correct and
	 *  modify it if needed.
	 * @param line : the input CDR
	 * @return the modified CDR
	 * @throws InvalidFormatException 
	 */
	String verifyFormat(String line) throws InvalidFormatException;
	
	/**
	 * Build and return a unique identifier from the CDR in order
	 * to avoid importing twice the same CDR in MEVEO
	 * @param line : CDR
	 * @return CDR's unique key
	 */
	String getUniqueKey(String line);

	/**
	 * Return in the form of an AccessDAO object the user id, sercice id and
	 * and parameters that will allow to lookup the ACCESS.
	 * @param line : CDR
	 * @return 
	 * @throws InvalidAccessException
	 */
	AccessDAO getAccessInfo(String line) throws InvalidAccessException;

	/**
	 * Construct EDR from the CDR and its associated Subscription
	 * @param line : CDR
	 * @param s : Subsctiprion associated to the CDR
	 * @return
	 * @throws CDRParsingException
	 */
	EDR getEDR(String line,Subscription s);
}
