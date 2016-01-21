package org.meveo.util;

import org.meveo.model.CustomFieldEntity;
import org.meveo.service.job.Job;

public class EntityCustomizationUtils {

    /**
     * Determine appliesTo prefix for custom field templates, actions, etc..
     * 
     * @param clazz Class customization applies to
     * @return An "appliesTo" value for a given class
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String getAppliesToPrefix(Class clazz) {

        String appliesToPrefix = null;
        if (Job.class.isAssignableFrom(clazz)) {
            appliesToPrefix = Job.CFT_PREFIX + "_" + clazz.getSimpleName();

        } else {
            appliesToPrefix = ((CustomFieldEntity) clazz.getAnnotation(CustomFieldEntity.class)).cftCodePrefix();
        }

        return appliesToPrefix;
    }
}