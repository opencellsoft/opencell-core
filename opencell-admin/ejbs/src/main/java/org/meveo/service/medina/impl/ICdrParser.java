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

import java.util.List;

import org.meveo.model.mediation.Access;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.EDR;

/**
 * This Interface must be implemented to parse CDR and create EDR from it.The implementation must be a Named class, i.e. a class annotated with the javax.ejb.Nammed annotation.
 * 
 * @author h.znibar
 */
public interface ICdrParser {

    /**
     * Convert record into a CDR object. Parsing exceptions are available in CDR.rejectReason
     *
     * @param source the source
     * @return the cdr
     */
    CDR parse(Object source);

    /**
     * Get a list of Access points CDR corresponds to
     * 
     * @param cdr CDR
     * @return A list of Access points
     * @throws InvalidAccessException No Access point was matched
     */
    List<Access> accessPointLookup(CDR cdr) throws InvalidAccessException;

    /**
     * Convert cdr to edr.
     *
     * @param cdr the cdr
     * @param accessPoints 
     * @return the list
     * @throws CDRParsingException 
     */
    List<EDR> convertCdrToEdr(CDR cdr, List<Access> accessPoints) throws CDRParsingException;

    /**
     * >Identifies a specific data type. Null by default. Would be needed only in case where multiple CDR formats have to be supported at once.
     * 
     * @return
     */
    String getType();

    /**
     * determine if cdrToEdrConverter is applicable for a given source/data type
     * 
     * @param type Identifies a specific data type. Would be needed only in case where multiple CDR formats have to be supported at once.
     * @return
     */
    boolean isApplicable(String type);

}