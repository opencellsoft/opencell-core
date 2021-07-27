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

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.report.query.QueryExecutionModeEnum;
import org.meveo.model.report.query.QueryExecutionResult;
import org.meveo.model.report.query.QueryExecutionResultFormatEnum;
import org.meveo.model.report.query.QueryScheduler;
import org.meveo.model.report.query.QueryStatusEnum;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.report.QueryExecutionResultService;
import org.meveo.service.report.QuerySchedulerService;
import org.meveo.service.report.ReportQueryService;

/**
 * @author BEN AICHA Amine
 * @lastModifiedVersion 11.0
 */
@Stateless
public class ReportQueryJobBean extends BaseJobBean {

    private static final long serialVersionUID = 6286711615323804204L;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    @Inject
    private ReportQueryService reportQueryService;

    @Inject
    private QuerySchedulerService querySchedulerService;

    @Inject
    private QueryExecutionResultService queryExecutionResultService;

    private static final String ROOT_FOLDER = "reports" + File.separator + "reportQuery";
    private static final String DATE_PATTERN = "yyyyMMdd-HH_mm_ss";

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public JobExecutionResultImpl execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) throws BusinessException {

        String outputDir = paramBeanFactory.getChrootDir() + File.separator + ROOT_FOLDER + File.separator;

        File f = new File(outputDir);
        if (!f.exists()) {
            f.mkdirs();
        }

        ReportQuery reportQuery = getReportQuery(this.getParamOrCFValue(jobInstance, "reportQuery"));
        if (reportQuery == null) {
            throw new BusinessException("No entiy report query found");
        }

        QueryScheduler queryScheduler = querySchedulerService.findByReportQuery(reportQuery);
        if (queryScheduler == null) {
            throw new BusinessException("The ReportQuery must be linked to a QueryScheduler");
        }

        String fileFormat = queryScheduler.getFileFormat();
        if (StringUtils.isBlank(fileFormat)) {
            throw new BusinessException("The fileFormat must be specified in the Query Scheduler");
        }

        QueryExecutionResultFormatEnum format = null;
        try {
            format = QueryExecutionResultFormatEnum.valueOf(fileFormat.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Failed to get fileFormat " + fileFormat + " from query scheduler", e);
            throw new BusinessException("Failed to get fileFormat", e);
        }

        QueryExecutionResult queryResult = new QueryExecutionResult();
        queryResult.setReportQuery(reportQuery);
        queryResult.setQueryScheduler(queryScheduler);
        queryResult.setQueryExecutionMode(QueryExecutionModeEnum.SCHEDULED);

        String filePath = outputDir + DateUtils.formatDateWithPattern(new Date(), DATE_PATTERN) + format.getExtension();
        try {
            // Execute the query stored in reportQuery.generatedQuery
            reportQueryService.executeQuery(queryResult, reportQuery, new File(filePath), format);
        } catch (IOException e) {
            log.error("Failed to create output file", e);
            throw new BusinessException("Failed to create output file : " + filePath);
        }

        if (queryResult.getQueryStatus() == QueryStatusEnum.ERROR) {
            jobExecutionResult.registerError(queryResult.getErrorMessage());
        }
        // Save execution stats in QueryExecutionResult entity
        queryExecutionResultService.create(queryResult);

        jobExecutionResult.registerSucces();

        return jobExecutionResult;
    }

    /**
     * Get report query to process
     *
     * @param reportQueryCF the ReportQuery setting from the custom field
     * @return ReportQuery
     */
    private ReportQuery getReportQuery(Object reportQueryCF) {
        EntityReferenceWrapper ReportQueryERW = (EntityReferenceWrapper) reportQueryCF;

        if (ReportQueryERW != null) {
            return reportQueryService.findById(ReportQueryERW.getId(), asList("fields"));
        }
        return null;
    }
}