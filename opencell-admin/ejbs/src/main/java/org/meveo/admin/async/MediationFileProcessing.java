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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.service.medina.impl.CDRService;
import org.meveo.service.medina.impl.ICdrParser;
import org.meveo.service.medina.impl.ICdrReader;
import org.slf4j.Logger;

/**
 * @author anasseh
 * @author HORRI Khalid
 * @author H.ZNIBAR
 * @lastModifiedVersion 10.0
 * 
 */

@Stateless
public class MediationFileProcessing {

	@Inject
	protected Logger log;

	/** The job execution service. */
	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private CurrentUserProvider currentUserProvider;

	/** The cdr parser. */
	@Inject
	private CDRParsingService cdrParserService;

	@Inject
	private CDRService cdrService;
	
	/**
	 * Read/parse mediation file and process one line at a time. NOTE: Executes in
	 * NO transaction - each line will be processed in a separate transaction, one
	 * line failure will not affect processing of other lines.
	 *
	 * @param cdrReader        CDR file reader
	 * @param cdrParser        The cdr parser
	 * @param result           Job execution result
	 * @param fileName         File name being processed
	 * @param rejectFileWriter File writer to output failed data
	 * @param outputFileWriter File writer to output processed data
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
	public Future<String> processFileAsync(ICdrReader cdrReader, ICdrParser cdrParser, JobExecutionResultImpl result, String fileName, PrintWriter rejectFileWriter, PrintWriter outputFileWriter,
			MeveoUser lastCurrentUser) throws BusinessException {

		currentUserProvider.reestablishAuthentication(lastCurrentUser);

		int i = 0;
		CDR cdr = null;		
		
		while (true) {

			i++;
			if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
				break;
			}

			try {
				cdr = cdrReader.getNextRecord(cdrParser);
				if (cdr == null) {
                    break;
                }
				if (StringUtils.isBlank(cdr.getRejectReason())) {
				    List<Access> accessPoints = cdrParser.accessPointLookup(cdr);
	                List<EDR> edrs = cdrParser.convertCdrToEdr(cdr,accessPoints);               
	                log.debug("Processing record line content:{} from file {}", cdr.getLine(), fileName);

	                cdrParserService.createEdrs(edrs,cdr);

	                synchronized (outputFileWriter) {
	                    outputFileWriter.println(cdr.getLine());
	                }
	                result.registerSucces();
				} else {
				    result.registerError("file=" + fileName + ", line=" + (cdr != null ? cdr.getLine() : "") + ": " + cdr.getRejectReason());
				    cdr.setStatus(CDRStatusEnum.ERROR);
				    createOrUpdateCdr(cdr);
				    
				}
	            

			} catch (IOException e) {
				log.error("Failed to read a CDR line from file {}", fileName, e);
				result.addReport("Failed to read a CDR line from file " + fileName + " " + e.getMessage());
	            cdr.setStatus(CDRStatusEnum.ERROR);
	            cdr.setRejectReason(e.getMessage());
	            createOrUpdateCdr(cdr);
				break;

			} catch (Exception e) {
				String errorReason = e.getMessage();
				final Throwable rootCause = getRootCause(e);
				if (e instanceof EJBTransactionRolledbackException && rootCause instanceof ConstraintViolationException) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Invalid values passed: ");
                    for (ConstraintViolation<?> violation : ((ConstraintViolationException) rootCause).getConstraintViolations()) {
                        builder.append(String.format(" %s.%s: value '%s' - %s;", violation.getRootBeanClass().getSimpleName(), violation.getPropertyPath().toString(), violation.getInvalidValue(), violation.getMessage()));
                    }
                    errorReason = builder.toString();
					log.error("Failed to process a CDR line: {} from file {} error {}", cdr != null ? cdr.getLine() : null, fileName, errorReason);
				}
				else if (e instanceof CDRParsingException) {
					log.error("Failed to process a CDR line: {} from file {} error {}", cdr != null ? cdr.getLine() : null, fileName, errorReason);
				} 
				else {
					log.error("Failed to process a CDR line: {} from file {} error {}", cdr != null ? cdr.getLine() : null, fileName, errorReason, e);
				}

				synchronized (rejectFileWriter) {
					rejectFileWriter.println((cdr != null ? cdr.getLine() : "") + "\t" + errorReason);
				}
				result.registerError("file=" + fileName + ", line=" + (cdr != null ? cdr.getLine() : "") + ": " + errorReason);
                cdr.setStatus(CDRStatusEnum.ERROR);
                cdr.setRejectReason(e.getMessage());
                createOrUpdateCdr(cdr);
			}
		}
		return new AsyncResult<String>("OK");
	}
	
	private Throwable getRootCause(Throwable e) {
		if(e.getCause()!=null) {
			return getRootCause(e.getCause());
		}
		return e;
	}

	/**
     * Save the cdr if the configuration property mediation.persistCDR is true.
     *
     * @param cdr the cdr
     */
    private void createOrUpdateCdr(CDR cdr) {
        boolean persistCDR = "true".equals(ParamBeanFactory.getAppScopeInstance().getProperty("mediation.persistCDR", "false"));
        if(cdr != null && persistCDR) {
            if(cdr.getId() == null) {
                cdrService.create(cdr);
            } else {
                cdrService.update(cdr);
            }                       
        }
    }
}