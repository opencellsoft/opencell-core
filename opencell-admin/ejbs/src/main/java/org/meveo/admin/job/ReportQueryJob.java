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
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.service.job.Job;

/**
 * Job definition to execute the query stored in reportQuery.generatedQuery
 * 
 * @author BEN AICHA Amine
 * @lastModifiedVersion 11.0
 */
@Stateless
public class ReportQueryJob extends Job {

    /** The ReportQuery job bean. */
    @Inject
    private ReportQueryJobBean reportQueryJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        return reportQueryJobBean.execute(result, jobInstance);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.REPORTING_QUERY;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        CustomFieldTemplate reportQueryCF = new CustomFieldTemplate();
        reportQueryCF.setCode("reportQuery");
        reportQueryCF.setAppliesTo("JobInstance_ReportQueryJob");
        reportQueryCF.setActive(true);
        reportQueryCF.setDescription(resourceMessages.getString("jobExecution.reportQuery"));
        reportQueryCF.setFieldType(CustomFieldTypeEnum.ENTITY);
        reportQueryCF.setStorageType(CustomFieldStorageTypeEnum.SINGLE);
        reportQueryCF.setEntityClazz(ReportQuery.class.getName());
        reportQueryCF.setValueRequired(true);
        reportQueryCF.setGuiPosition("tab:Configuration:0;field:0");
        result.put("reportQuery", reportQueryCF);

        return result;
    }
}