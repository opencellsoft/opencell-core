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
import org.meveo.service.job.Job;

/**
 * The Class GenericWorkflowJob execute the transition script on each workflowed entity instance.
 */
@Stateless
public class GenericWorkflowJob extends Job {

    /**
     * Transition script params
     */
    public final static String GENERIC_WF = "GENERIC_WF";
    public final static String WF_INS = "WF_INS";
    public final static String IWF_ENTITY = "IWF_ENTITY";

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
        return JobCategoryEnum.UTILS;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        CustomFieldTemplate worklowCF = new CustomFieldTemplate();
        worklowCF.setCode("gwfJob_generic_wf");
        worklowCF.setAppliesTo("JOB_GenericWorkflowJob");
        worklowCF.setActive(true);
        worklowCF.setDescription("Generic workflow");
        worklowCF.setFieldType(CustomFieldTypeEnum.ENTITY);
        worklowCF.setEntityClazz(GenericWorkflow.class.getName());
        worklowCF.setValueRequired(true);
        result.put("gwfJob_generic_wf", worklowCF);

        CustomFieldTemplate nbRuns = new CustomFieldTemplate();
        nbRuns.setCode("gwfJob_nbRuns");
        nbRuns.setAppliesTo("JOB_GenericWorkflowJob");
        nbRuns.setActive(true);
        nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        nbRuns.setValueRequired(false);
        nbRuns.setDefaultValue("1");
        result.put("gwfJob_nbRuns", nbRuns);

        CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
        waitingMillis.setCode("gwfJob_waitingMillis");
        waitingMillis.setAppliesTo("JOB_GenericWorkflowJob");
        waitingMillis.setActive(true);
        waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
        waitingMillis.setValueRequired(false);
        waitingMillis.setDefaultValue("0");
        result.put("gwfJob_waitingMillis", waitingMillis);

        return result;
    }
}