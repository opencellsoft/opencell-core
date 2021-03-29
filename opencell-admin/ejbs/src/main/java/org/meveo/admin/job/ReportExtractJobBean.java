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

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ReportExtractExecutionException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.finance.ReportExtract;
import org.meveo.model.finance.ReportExtractExecutionOrigin;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.finance.ReportExtractService;
import org.meveo.service.script.finance.ReportExtractScript;

/**
 * Job implementation to run ReportExtracts and generate the file with matching records
 * 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.1
 **/
@Stateless
public class ReportExtractJobBean extends IteratorBasedJobBean<Long> {

    private static final long serialVersionUID = 9159856207913605563L;

    @Inject
    private ReportExtractService reportExtractService;

    private String startDate;
    private String endDate;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::runReport, null, null);
        startDate = null;
        endDate = null;
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Report Ids to produce reports
     */
    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        try {

            ParamBean paramBean = ParamBean.getInstance();

            Date date = (Date) this.getParamOrCFValue(jobInstance, "startDate");
            if (date != null) {
                startDate = DateUtils.formatDateWithPattern(date, paramBean.getDateFormat());
            }

            date = (Date) this.getParamOrCFValue(jobInstance, "endDate");
            if (date != null) {
                endDate = DateUtils.formatDateWithPattern(date, paramBean.getDateFormat());
            }

        } catch (Exception e) {
            log.warn("Cant get customFields for {}", jobInstance, e.getMessage());
        }

        // Resolve report extracts from CF value
        List<Long> ids = null;
        List<EntityReferenceWrapper> reportExtractReferences = (List<EntityReferenceWrapper>) this.getParamOrCFValue(jobInstance, ReportExtractJob.CF_REPORTS);
        if (reportExtractReferences != null && !reportExtractReferences.isEmpty()) {
            ids = reportExtractService.findByCodes(reportExtractReferences.stream().map(er -> er.getCode()).collect(Collectors.toList())).stream().map(re -> re.getId()).collect(Collectors.toList());

            // Or use all reports
        } else {
            ids = reportExtractService.listIds();
        }

        return Optional.of(new SynchronizedIterator<Long>(ids));
    }

    /**
     * Run report
     * 
     * @param reportExtractId Report extract ID
     * @param jobExecutionResult Job execution result
     */
    private void runReport(Long reportExtractId, JobExecutionResultImpl jobExecutionResult) {

        ReportExtract reportExtract = reportExtractService.findById(reportExtractId);

        if (startDate != null) {
            reportExtract.getParams().put(ReportExtractScript.START_DATE, startDate);
        }
        if (endDate != null) {
            reportExtract.getParams().put(ReportExtractScript.END_DATE, endDate);
        }

        try {
            reportExtractService.runReport(reportExtract, null, ReportExtractExecutionOrigin.JOB);

        } catch (ReportExtractExecutionException e) {
            throw new BusinessException(e);
        }
    }
}