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

/**
 * 
 */
package org.meveo.admin.async;

import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.EDR;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.medina.impl.*;
import org.slf4j.Logger;

/**
 * @author Mohammed Amine Tazi
 * @lastModifiedVersion 10.0
 * 
 */

@Stateless
public class MediationReprocessing {

	@Inject
	protected Logger log;

	@Inject
	private CurrentUserProvider currentUserProvider;

	/** The cdr parser. */
	@Inject
	private CDRParsingService cdrParserService;

	@Inject 
	private CDRService cdrService;

	@Inject
	private EdrService edrService;
	
	/**
	 * Read/parse mediation file and process one line at a time. NOTE: Executes in
	 * NO transaction - each line will be processed in a separate transaction, one
	 * line failure will not affect processing of other lines.
	 *
	 * @param cdrReader        CDR file reader
	 * @param cdrParser        The cdr parser
	 * @param result           Job execution result
	 * @param lastCurrentUser  Current user. In case of multitenancy, when user
	 *                         authentication is forced as result of a fired trigger
	 *                         (scheduled jobs, other timed event expirations),
	 *                         current user might be lost, thus there is a need to
	 *                         reestablish.
	 * @return Future
	 * @throws BusinessException General exception
	 */
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Future<String> processAsync(ICdrReader cdrReader, ICdrParser cdrParser, JobExecutionResultImpl result, 
            MeveoUser lastCurrentUser) throws BusinessException {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);
        List<CDR> cdrs = cdrReader.getRecords(cdrParser, null);
        for (CDR cdr : cdrs) {
            try {
                if (StringUtils.isBlank(cdr.getRejectReason())) {
					this.convertAndCreateCdrToEdr(cdrParser, cdr);
                    result.registerSucces();
                } else {
                    result.registerError("cdr =" + (cdr != null ? cdr.getLine() : "") + ": " + cdr.getRejectReason());
                    cdrService.updateReprocessedCdr(cdr);
                }
            } catch (Exception e) {

                String errorReason = e.getMessage();
                if (e instanceof CDRParsingException) {
                    log.error("Failed to process CDR id: {} error {}", cdr != null ? cdr.getId() : null, errorReason);
                } else {
                    log.error("Failed to process CDR id: {}  error {}", cdr != null ? cdr.getId() : null, errorReason, e);
                }
                result.registerError("cdr id=" + (cdr != null ? cdr.getId() : "") + ": " + errorReason);
                cdrService.updateReprocessedCdr(cdr);
            }
        }
        return new AsyncResult<String>("OK");
    }

	public synchronized void convertAndCreateCdrToEdr(ICdrParser cdrParser, CDR cdr) throws CDRParsingException {
		List<Access> accessPoints = cdrParser.accessPointLookup(cdr);
		List<EDR> edrs = cdrParser.convertCdrToEdr(cdr, accessPoints);
		log.debug("Processing cdr id:{}", cdr.getId());

		if (edrService.isDBDuplicateFound(cdr.getOriginBatch(), cdr.getOriginRecord())) {
			throw new DuplicateException(cdr);
		} else {
			cdrParserService.createEdrs(edrs, cdr);
		}
	}

}