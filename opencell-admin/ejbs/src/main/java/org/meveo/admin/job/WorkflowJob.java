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
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.wf.Workflow;
import org.meveo.service.job.Job;

/**
 * The Class WorkflowJob execute the given workflow  on each entity entity return by the given filter.
 */
@Stateless
public class WorkflowJob extends Job {

    /** The workflow job bean. */
    @Inject
    private WorkflowJobBean workflowJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        workflowJobBean.execute(result, jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.UTILS;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate filterCF = new CustomFieldTemplate();
        filterCF.setCode("wfJob_filter");
        filterCF.setAppliesTo("JOB_WorkflowJob");
        filterCF.setActive(true);
        filterCF.setDescription("Filter");
        filterCF.setFieldType(CustomFieldTypeEnum.ENTITY);
        filterCF.setEntityClazz(Filter.class.getName());
        filterCF.setValueRequired(true);
        result.put("wfJob_filter", filterCF);

        CustomFieldTemplate worklowCF = new CustomFieldTemplate();
        worklowCF.setCode("wfJob_workflow");
        worklowCF.setAppliesTo("JOB_WorkflowJob");
        worklowCF.setActive(true);
        worklowCF.setDescription("Workflow");
        worklowCF.setFieldType(CustomFieldTypeEnum.ENTITY);
        worklowCF.setEntityClazz(Workflow.class.getName());
        worklowCF.setValueRequired(true);
        result.put("wfJob_workflow", worklowCF);

        CustomFieldTemplate nbRuns = new CustomFieldTemplate();
        nbRuns.setCode("wfJob_nbRuns");
        nbRuns.setAppliesTo("JOB_WorkflowJob");
        nbRuns.setActive(true);
        nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        nbRuns.setValueRequired(false);
        nbRuns.setDefaultValue("1");
        result.put("wfJob_nbRuns", nbRuns);

        CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
        waitingMillis.setCode("wfJob_waitingMillis");
        waitingMillis.setAppliesTo("JOB_WorkflowJob");
        waitingMillis.setActive(true);
        waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
        waitingMillis.setValueRequired(false);
        waitingMillis.setDefaultValue("0");
        result.put("wfJob_waitingMillis", waitingMillis);

        return result;
    }
}