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

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.meveo.model.shared.DateUtils.guessDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.dataCollector.DataCollectorService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

@Stateless
public class DataCollectorJobBean extends BaseJobBean {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String INPUT_DATE_SEPARATOR = "=";
    private static final String SEPARATOR = ";";

    @Inject
    private DataCollectorService dataCollectorService;

    @Inject
    private Logger log;
    
    @Inject
    protected JobExecutionService jobExecutionService;

    @Interceptors({JobLoggingInterceptor.class, PerformanceInterceptor.class})
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, String parameter) {
        try {
            String report;
            if (!StringUtils.isBlank(parameter)) {
                List<String> codes = new ArrayList<>();
                String[] parameters =  parameter.split(SEPARATOR);
                if(parameters.length > 1) {
                    Date from = guessDate(parameters[0].split(INPUT_DATE_SEPARATOR)[1], DATE_PATTERN);
                    Date to = guessDate(parameters[1].split(INPUT_DATE_SEPARATOR)[1], DATE_PATTERN);
                    log.info(format("Run DataCollector request between :%s and %s", from, to));
                    Map<String, Integer> executionResult = dataCollectorService.execute(from, to);
                    codes.addAll(executionResult.keySet());
                    result.setNbItemsToProcess(executionResult.size());
                    report = buildReportFrom(executionResult);
                } else {
                    log.info("Run request for DataCollector : ", parameter);
                    int recordImported = dataCollectorService.executeQuery(parameter);
                    codes.add(parameter);
                    report = format("Number of records imported = [%d] for data collector [%s]",
                            recordImported , parameter);
                }
                dataCollectorService.updateLastRun(codes, new Date());
                result.setReport(report);
                jobExecutionService.registerSucces(result);
            }
        } catch (Exception exception) {
            log.error("Failed to run DataCollector job ", exception);
            jobExecutionService.registerError(result, exception.getMessage());
        }
    }

    private String buildReportFrom(Map<String, Integer> executionResult) {
        return executionResult.entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + " imported records : " + entry.getValue())
                    .collect(joining("\\"));
    }
}