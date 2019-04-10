package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;

/**
 * The Class WorkflowJob execute the given workflow on each entity entity return by the given filter.
 * 
 * @see GenericWorkflowJob
 */
//@Stateless
@Deprecated
public class WorkflowJob /* extends Job */ {

    /** The workflow job bean. */
    @Inject
    private WorkflowJobBean workflowJobBean;

    // @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        workflowJobBean.execute(result, jobInstance);
    }

    // @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.UTILS;
    }

    // @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();
        return result;
    }
}