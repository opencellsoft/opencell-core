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
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.job.Job;

/**
 * The Class MediationReprocessingJob reprocesses CDR from database
 * 
 * @author Mohammed Amine Tazi
 * 
 */
@Stateless
public class MediationReprocessingJob extends Job {

    private static final String JOB_INSTANCE_MEDIATION_JOB = "JobInstance_MediationReprocessingJob";

    protected static final String MEDIATION_JOB_PARSER = "MediationJob_parser";

    protected static final String MEDIATION_JOB_READER = "MediationJob_reader";

    @Inject
    private MediationReprocessingJobBean mediationReprocessingJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {

        mediationReprocessingJobBean.execute(result, jobInstance);

        return result;
    }

    @Override
    public JobCategoryEnum<?> getJobCategory() {
        return MeveoJobCategoryEnum.MEDIATION;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate nbRuns = new CustomFieldTemplate();
        nbRuns.setCode("nbRuns");
        nbRuns.setAppliesTo(JOB_INSTANCE_MEDIATION_JOB);
        nbRuns.setActive(true);
        nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        nbRuns.setDefaultValue("-1");
        nbRuns.setValueRequired(false);
        nbRuns.setGuiPosition("tab:Configuration:0;field:0");
        result.put("nbRuns", nbRuns);

        CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
        waitingMillis.setCode("waitingMillis");
        waitingMillis.setAppliesTo(JOB_INSTANCE_MEDIATION_JOB);
        waitingMillis.setActive(true);
        waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
        waitingMillis.setDefaultValue("0");
        waitingMillis.setValueRequired(false);
        waitingMillis.setGuiPosition("tab:Configuration:0;field:1");
        result.put("waitingMillis", waitingMillis);

        CustomFieldTemplate parserCF = new CustomFieldTemplate();
        parserCF.setCode(MEDIATION_JOB_PARSER);
        parserCF.setAppliesTo(JOB_INSTANCE_MEDIATION_JOB);
        parserCF.setActive(true);
        parserCF.setDescription(resourceMessages.getString("mediationJob.parser"));
        parserCF.setFieldType(CustomFieldTypeEnum.STRING);
        parserCF.setDefaultValue(null);
        parserCF.setValueRequired(false);
        parserCF.setMaxValue(256L);
        parserCF.setGuiPosition("tab:Configuration:0;field:2");
        result.put(MEDIATION_JOB_PARSER, parserCF);

        CustomFieldTemplate readerCF = new CustomFieldTemplate();
        readerCF.setCode(MEDIATION_JOB_READER);
        readerCF.setAppliesTo(JOB_INSTANCE_MEDIATION_JOB);
        readerCF.setActive(true);
        readerCF.setDescription(resourceMessages.getString("mediationJob.reader"));
        readerCF.setFieldType(CustomFieldTypeEnum.STRING);
        readerCF.setDefaultValue(null);
        readerCF.setValueRequired(false);
        readerCF.setMaxValue(256L);
        readerCF.setGuiPosition("tab:Configuration:0;field:3");
        result.put(MEDIATION_JOB_READER, readerCF);

        return result;
    }
}