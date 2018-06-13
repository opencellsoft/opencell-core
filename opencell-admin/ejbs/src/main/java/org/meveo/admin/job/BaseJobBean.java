package org.meveo.admin.job;

import javax.inject.Inject;

import org.meveo.model.jobs.JobInstance;
import org.meveo.service.crm.impl.CustomFieldInstanceService;

/**
 * The Class BaseJobBean : Holding a common behaviors for all JoBbeans instances 
 */
public abstract class BaseJobBean {

    @Inject
    private CustomFieldInstanceService customFieldInstanceService;
    
    /**
     * Gets the parameter CF value if found , otherwise return CF value from customFieldInstanceService.
     *
     * @param jobInstance the job instance
     * @param cfCode the cf code
     * @return the param or CF value
     */
    protected Object getParamOrCFValue(JobInstance jobInstance, String cfCode) {
        Object value = jobInstance.getParamValue(cfCode);
        if (value == null) {
            return customFieldInstanceService.getCFValue(jobInstance, cfCode);
        }
        return value;
    }
}
