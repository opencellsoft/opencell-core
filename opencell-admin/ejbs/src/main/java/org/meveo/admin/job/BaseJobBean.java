package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.crm.impl.CustomFieldInstanceService;

/**
 * The Class BaseJobBean : Holding a common behaviors for all JoBbeans instances
 */
public abstract class BaseJobBean {

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    /**
     * Gets the parameter CF value if found, otherwise return CF value from job definition
     *
     * @param jobInstance  the job instance
     * @param cfCode       Custom field code
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
     * @param cfCode      Custom field code
     * @return Parameter or custom field value
     */
    protected Object getParamOrCFValue(JobInstance jobInstance, String cfCode) {
        Object value = jobInstance.getParamValue(cfCode);
        if (value == null) {
            return customFieldInstanceService.getCFValue(jobInstance, cfCode);
        }
        return value;
    }

    /**
     * Gets the Enum value from text.
     *
     * @param <T>         an Enum status
     * @param jobInstance a job instance
     * @param clazz       an enum class
     * @param cfCode      a name of the enum
     * @return a list of an enum status
     */
    protected <T extends Enum<T>> List<T> getTargetStatusList(JobInstance jobInstance, Class<T> clazz, String cfCode) {
        List<T> formattedStatus = new ArrayList<>();
        List<String> statusList = (List<String>) this.getParamOrCFValue(jobInstance, cfCode, new ArrayList<>());
        for (String status : statusList) {
            T statusEnum = Enum.valueOf(clazz, status.toUpperCase());
            formattedStatus.add(statusEnum);
        }
        return formattedStatus;
    }
}
