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
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

/**
 * The Class GenericWorkflowJob execute the transition script on each workflowed entity instance.
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class GenericWorkflowJob extends Job {

    /**
     * Transition script params
     */
    public final static String GENERIC_WF = "GENERIC_WF";
    public final static String WF_INS = "WF_INS";
    public final static String IWF_ENTITY = "IWF_ENTITY";
    public final static String WF_ACTUAL_TRANSITION = "WF_ACTUAL_TRANSITION";

    /** The generic workflow job bean. */
    @Inject
    private GenericWorkflowJobBean genericWorkflowJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        genericWorkflowJobBean.execute(result, jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.UTILS;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();


        CustomFieldTemplate nbRuns = new CustomFieldTemplate();
        nbRuns.setCode("gwfJob_nbRuns");
        nbRuns.setAppliesTo("JobInstance_GenericWorkflowJob");
        nbRuns.setActive(true);
        nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        nbRuns.setValueRequired(false);
        nbRuns.setDefaultValue("1");
        nbRuns.setGuiPosition("tab:Configuration:0;field:0");
        result.put("gwfJob_nbRuns", nbRuns);

        CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
        waitingMillis.setCode("gwfJob_waitingMillis");
        waitingMillis.setAppliesTo("JobInstance_GenericWorkflowJob");
        waitingMillis.setActive(true);
        waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
        waitingMillis.setValueRequired(false);
        waitingMillis.setDefaultValue("0");
        waitingMillis.setGuiPosition("tab:Configuration:0;field:1");
        result.put("gwfJob_waitingMillis", waitingMillis);

        CustomFieldTemplate worklowCF = new CustomFieldTemplate();
        worklowCF.setCode("gwfJob_generic_wf");
        worklowCF.setAppliesTo("JobInstance_GenericWorkflowJob");
        worklowCF.setActive(true);
        worklowCF.setDescription("Generic workflow");
        worklowCF.setFieldType(CustomFieldTypeEnum.ENTITY);
        worklowCF.setEntityClazz(GenericWorkflow.class.getName());
        worklowCF.setValueRequired(true);
        worklowCF.setGuiPosition("tab:Configuration:0;field:2");
        result.put("gwfJob_generic_wf", worklowCF);
        
        return result;
    }
}