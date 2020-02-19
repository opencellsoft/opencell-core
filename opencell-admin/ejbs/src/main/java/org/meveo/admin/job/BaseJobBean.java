package org.meveo.admin.job;

import org.meveo.model.jobs.JobInstance;
import org.meveo.service.crm.impl.CustomFieldInstanceService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * The Class BaseJobBean : Holding a common behaviors for all JoBbeans instances
 */
public abstract class BaseJobBean {

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    /**
     * Gets the parameter CF value if found, otherwise return CF value from job definition
     *
     * @param jobInstance the job instance
     * @param cfCode Custom field code
     * @param defaultValue Default value if no value found
     * @return Parameter or custom field value
     */
    protected Object getParamOrCFValue(JobInstance jobInstance, String cfCode, Object defaultValue) {
        Object value = getParamOrCFValue(jobInstance, cfCode);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Gets the parameter CF value if found, otherwise return CF value from job definition
     *
     * @param jobInstance the job instance
     * @param cfCode Custom field code
     * @return Parameter or custom field value
     */
    protected Object getParamOrCFValue(JobInstance jobInstance, String cfCode) {
        Object value = jobInstance.getParamValue(cfCode);
        if (value == null) {
            return customFieldInstanceService.getCFValue(jobInstance, cfCode);
        }
        return value;
    }
    
    public  <T extends Enum<T>> List<T> getTargetStatusList(JobInstance jobInstance, Class<T> clazz, String cfCode) {
        List<T> formattedStatus = new ArrayList<T>();
        String statusListStr = (String) this.getParamOrCFValue(jobInstance, cfCode);
        if (statusListStr != null && !statusListStr.isEmpty()) {
            List<String> statusList = Arrays.asList(statusListStr.split(";"));
            for (String status : statusList) {
                T statusEnum = T.valueOf(clazz, status.toUpperCase());
                if (statusEnum != null) {
                    formattedStatus.add(statusEnum);
                }
            }
        }
        return formattedStatus;
    }
    
}
