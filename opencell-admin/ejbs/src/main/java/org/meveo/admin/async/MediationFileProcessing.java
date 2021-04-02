/**
 * 
 */
package org.meveo.admin.async;

import java.io.IOException;
import java.io.PrintWriter;
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

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.parse.csv.CDR;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.service.medina.impl.CSVCDRParser;
import org.slf4j.Logger;

/**
 * @author anasseh
 * @author HORRI Khalid
 * @lastModifiedVersion 5.4
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

    /**
     * Read/parse mediation file and process one line at a time. NOTE: Executes in NO transaction - each line will be processed in a separate transaction, one line failure will not
     * affect processing of other lines.
     * 
     * @param cdrParser CDR file parser
     * @param result Job execution result
     * @param fileName File name being processed
     * @param outputFileWriter File writer to output processed data
     * @param rejectFileWriter File writer to output failed data
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return Future
     * @throws BusinessException General exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> processFileAsync(CSVCDRParser cdrParser, JobExecutionResultImpl result, String fileName, PrintWriter rejectFileWriter, PrintWriter outputFileWriter,
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
                cdr = cdrParser.getNextRecord();
                if (cdr == null) {
                    break;
                }

                log.debug("Processing record line content:{} from file {}", cdr.getLine(), fileName);

                if (cdr.getRejectReason() != null) {
                    throw cdr.getRejectReason();
                }

                cdrParserService.createEdrs(cdr);

                synchronized (outputFileWriter) {
                    outputFileWriter.println(cdr.getLine());
                }
                result.registerSucces();

            } catch (IOException e) {
                log.error("Failed to read a CDR line from file {}", fileName, e);
                result.addReport("Failed to read a CDR line from file " + fileName + " " + e.getMessage());
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
				
				if (e instanceof CDRParsingException) {
                    log.error("Failed to process a CDR line: {} from file {} error {}", cdr != null ? cdr.getLine() : null, fileName, errorReason);
                } else {
                    log.error("Failed to process a CDR line: {} from file {} error {}", cdr != null ? cdr.getLine() : null, fileName, errorReason, e);
                }

                synchronized (rejectFileWriter) {
                    rejectFileWriter.println((cdr != null ? cdr.getLine() : "") + "\t" + errorReason);
                }
                result.registerError("file=" + fileName + ", line=" + (cdr != null ? cdr.getLine() : "") + ": " + errorReason);
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
}