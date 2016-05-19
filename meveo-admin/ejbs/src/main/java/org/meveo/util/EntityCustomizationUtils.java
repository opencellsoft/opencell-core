package org.meveo.util;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.job.Job;

public class EntityCustomizationUtils {

    /**
     * Determine appliesTo value for custom field templates, actions, etc..
     * 
     * @param clazz Class customization applies to
     * @param code Entity code (applies to CustomEntityTemplate only)
     * @return An "appliesTo" value for a given class
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String getAppliesTo(Class clazz, String code) {

        String appliesToPrefix = null;
        if (Job.class.isAssignableFrom(clazz)) {
            appliesToPrefix = Job.CFT_PREFIX + "_" + ReflectionUtils.getCleanClassName(clazz.getSimpleName());
            
        } else if (CustomEntityTemplate.class.isAssignableFrom(clazz)) {
            appliesToPrefix = CustomEntityTemplate.getAppliesTo(code);
            
        } else {
            appliesToPrefix = ((CustomFieldEntity) clazz.getAnnotation(CustomFieldEntity.class)).cftCodePrefix();
        }

        return appliesToPrefix;
    }
}