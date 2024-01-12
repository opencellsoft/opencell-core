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

package org.meveo.admin.job;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.event.qualifier.RejectedCDR;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.mediation.MediationSettingService;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.service.medina.impl.CDRService;
import org.meveo.service.medina.impl.DuplicateException;

/**
 * Job implementation to process CDR from DB converting CDRs to EDR records
 * 
 * @author Abdelmounaim Akadid
 *
 */
@Stateless
public class MediationJobBeanV2 extends IteratorBasedScopedJobBean<Long> {

    private static final long serialVersionUID = -3545282683190559013L;

    @Inject
    private CDRService cdrService;
    
    @Inject
    private EdrService edrService;
    
    @Inject
    @RejectedCDR
    private Event<Serializable> rejectededCdrEventProducer;

    @Inject
    private MediationSettingService mediationsettingService;
    
    @Inject
    private CDRParsingService cdrParsingService;
    
    private boolean hasMore = false;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, null, this::processCDR, this::processCDRBatch, this::hasMore, null, null);
    }

    public static Throwable getRootCause(Throwable e) {
        if (e.getCause() != null) {
            return getRootCause(e.getCause());
        }
        return e;
    }
    
    private void failedCDR(JobExecutionResultImpl jobExecutionResult, CDR cdr, CDRStatusEnum status) {
        log.error("Failed to process a CDR id = {}. Reason: {}", cdr.getId(), cdr.getRejectReason());
        jobExecutionResult.registerError("id=" + cdr.getId() + ": " + cdr.getRejectReason());
        jobExecutionResult.unRegisterSucces();
        cdr.setStatus(status);
        rejectededCdrEventProducer.fire(cdr);
        cdrService.createOrUpdateCdr(cdr);
    }
    
    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of CDR Ids to convert to EDRs
     */
    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
        return getIterator(jobExecutionResult);
    }
    
    /**
     * process CDR
     * 
     * @param edrId EDR id to rate
     * @param jobExecutionResult Job execution result
     */
    private void processCDR(CDR cdr, JobExecutionResultImpl jobExecutionResult) {
        
        try {
            
            if (!StringUtils.isBlank(cdr.getRejectReason())) {
                failedCDR(jobExecutionResult, cdr, CDRStatusEnum.ERROR);
            } else {

                List<Access> accessPoints = cdrParsingService.accessPointLookup(cdr);
                List<EDR> edrs = cdrParsingService.convertCdrToEdr(cdr, accessPoints);

                if (EdrService.isDuplicateCheckOn() && edrService.isDuplicateFound(cdr.getOriginRecord())) {
                     throw new DuplicateException(cdr);
                }
                
                for(EDR edr : edrs) {
                    edrService.create(edr);
                    cdr.setHeaderEDR(edr);
                    cdr.setStatus(CDRStatusEnum.PROCESSED);
                    cdrService.update(cdr);
                }
                
                mediationsettingService.applyEdrVersioningRule(edrs, cdr, false);
                if (!StringUtils.isBlank(cdr.getRejectReason())) {
                    failedCDR(jobExecutionResult, cdr, cdr.getStatus());
                }

                jobExecutionResult.registerSucces();
            }

        } catch (Exception e) {
            String errorReason = e.getMessage();
            final Throwable rootCause = getRootCause(e);
            if (e instanceof EJBTransactionRolledbackException && rootCause instanceof ConstraintViolationException) {
                StringBuilder builder = new StringBuilder();
                builder.append("Invalid values passed: ");
                for (ConstraintViolation<?> violation : ((ConstraintViolationException) rootCause).getConstraintViolations()) {
                    builder
                        .append(String.format(" %s.%s: value '%s' - %s;", violation.getRootBeanClass().getSimpleName(), violation.getPropertyPath().toString(), violation.getInvalidValue(), violation.getMessage()));
                }
                errorReason = builder.toString();
                log.error("Failed to process a CDR id: {}, Reason: {}", cdr.getId(), errorReason);
            } else if (e instanceof CDRParsingException) {
                log.error("Failed to process a CDR id: {}, Reason: {}", cdr.getId(), errorReason);
            } else {
                log.error("Failed to process a CDR id: {}, Reason: {}", cdr.getId(), errorReason, e);
            }


            jobExecutionResult.registerError("id=" + cdr.getId() + ": " + errorReason);
            cdr.setStatus(CDRStatusEnum.ERROR);
            cdr.setRejectReason(e.getMessage());

            rejectededCdrEventProducer.fire(cdr);
            cdrService.createOrUpdateCdr(cdr);
        }
    }

    /**
     * Process CDR
     * 
     * @param cdrId CDR id to process
     * @param jobExecutionResult Job execution result
     */
    private void processCDR(Long cdrId, JobExecutionResultImpl jobExecutionResult) {
        CDR cdr = cdrService.findById(cdrId);
        processCDR(cdr, jobExecutionResult);
    }

    /**
     * Process CDR batch
     * 
     * @param cdrIds A list of CDR ids to process
     * @param jobExecutionResult Job execution result
     */
    private void processCDRBatch(List<Long> cdrIds, JobExecutionResultImpl jobExecutionResult) {
        List<CDR> cdrs = cdrService.findByIds(cdrIds);
        for (CDR cdr : cdrs) {
            processCDR(cdr, jobExecutionResult);
        }
    }

    private boolean hasMore(JobInstance jobInstance) {
        return hasMore;
    }

    private Optional<Iterator<Long>> getSynchronizedIterator(int jobItemsLimit) {
        // Number of CDRs to process in a single job run
        List<Long> ids = cdrService.getCDRsToProcess(jobItemsLimit);
        return Optional.of(new SynchronizedIterator<Long>(ids));
    }

    @Override
    Optional<Iterator<Long>> getSynchronizedIteratorWithLimit(JobExecutionResultImpl jobExecutionResult, int jobItemsLimit) {
        return getSynchronizedIterator(jobItemsLimit);
    }

    @Override
    Optional<Iterator<Long>> getSynchronizedIterator(JobExecutionResultImpl jobExecutionResult) {
        return getSynchronizedIterator(0);
    }
}