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

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.finance.ReportExtract;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

/**
 * Job definition to run ReportExtracts and generate the file with matching records
 * 
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 7.0
 */
@Stateless
public class ReportExtractJob extends Job {

    /**
     * A custom field for Report extracts to execute
     */
    public static final String CF_REPORTS = "reports";

    @Inject
    private ReportExtractJobBean reportingJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        reportingJobBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.DWH;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
        customFieldNbRuns.setCode(CF_NB_RUNS);
        customFieldNbRuns.setAppliesTo("JobInstance_ReportExtractJob");
        customFieldNbRuns.setActive(true);
        customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbRuns.setValueRequired(false);
        customFieldNbRuns.setDefaultValue("-1");
        customFieldNbRuns.setGuiPosition("tab:Configuration:0;field:0");
        result.put(CF_NB_RUNS, customFieldNbRuns);

        CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
        customFieldNbWaiting.setCode(Job.CF_WAITING_MILLIS);
        customFieldNbWaiting.setAppliesTo("JobInstance_ReportExtractJob");
        customFieldNbWaiting.setActive(true);
        customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbWaiting.setDefaultValue("0");
        customFieldNbWaiting.setValueRequired(false);
        customFieldNbWaiting.setGuiPosition("tab:Configuration:0;field:1");
        result.put(Job.CF_WAITING_MILLIS, customFieldNbWaiting);

        CustomFieldTemplate customFieldStartDate = new CustomFieldTemplate();
        customFieldStartDate.setCode("startDate");
        customFieldStartDate.setAppliesTo("JobInstance_ReportExtractJob");
        customFieldStartDate.setActive(true);
        customFieldStartDate.setDescription(resourceMessages.getString("jobExecution.startDate"));
        customFieldStartDate.setFieldType(CustomFieldTypeEnum.DATE);
        customFieldStartDate.setValueRequired(false);
        customFieldStartDate.setGuiPosition("tab:Configuration:0;field:2");
        result.put("startDate", customFieldStartDate);

        CustomFieldTemplate customFieldEndDate = new CustomFieldTemplate();
        customFieldEndDate.setCode("endDate");
        customFieldEndDate.setAppliesTo("JobInstance_ReportExtractJob");
        customFieldEndDate.setActive(true);
        customFieldEndDate.setDescription(resourceMessages.getString("jobExecution.endDate"));
        customFieldEndDate.setFieldType(CustomFieldTypeEnum.DATE);
        customFieldEndDate.setValueRequired(false);
        customFieldEndDate.setGuiPosition("tab:Configuration:0;field:3");
        result.put("endDate", customFieldEndDate);

        CustomFieldTemplate customFieldReport = new CustomFieldTemplate();
        customFieldReport.setCode(CF_REPORTS);
        customFieldReport.setAppliesTo("JobInstance_ReportExtractJob");
        customFieldReport.setActive(true);
        customFieldReport.setDescription(resourceMessages.getString("jobExecution.reportExtractJob.reports"));
        customFieldReport.setFieldType(CustomFieldTypeEnum.ENTITY);
        customFieldReport.setEntityClazz(ReportExtract.class.getName());
        customFieldReport.setStorageType(CustomFieldStorageTypeEnum.LIST);
        customFieldReport.setValueRequired(false);
        customFieldReport.setGuiPosition("tab:Configuration:0;field:4");
        result.put(CF_REPORTS, customFieldReport);

        return result;
    }

}
