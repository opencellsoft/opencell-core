package org.meveo.api.security.config.annotation;

import org.meveo.api.security.config.*;
import org.slf4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.interceptor.InvocationContext;


@Singleton
@Default
public class SecuredBusinessEntityAnnotationConfigFactory implements SecuredBusinessEntityConfigFactory {

    @Inject
    private Logger log;

    @Override
    public SecuredBusinessEntityConfig get(InvocationContext context) {
        SecuredBusinessEntityConfig config = new SecuredBusinessEntityConfig();

        SecuredBusinessEntityMethod securedMethodAnnotation = context.getMethod()
                .getAnnotation(SecuredBusinessEntityMethod.class);
        if (securedMethodAnnotation != null) {
            SecuredMethodConfig securedMethodConfig = mapSecuredMethodConfig(securedMethodAnnotation);
            config.setSecuredMethodConfig(securedMethodConfig);
        }

        FilterResults filterResultsAnnotation = context.getMethod().getAnnotation(FilterResults.class);
        if (filterResultsAnnotation != null) {
            FilterResultsConfig filterResultsConfig = mapFilterResultsConfig(filterResultsAnnotation);
            config.setFilterResultsConfig(filterResultsConfig);
        }

        return config;
    }

    private SecuredMethodConfig mapSecuredMethodConfig(SecuredBusinessEntityMethod annotation) {
        SecuredMethodConfig methodConfig = new SecuredMethodConfig();
        // map validate
        SecureMethodParameter[] validate = annotation.validate();
        SecureMethodParameterConfig[] paramConfigs = new SecureMethodParameterConfig[validate.length];
        for (int i = 0; i < validate.length; i++) {
            SecureMethodParameter secureMethodParameter = validate[i];
            SecureMethodParameterConfig paramConfig = new SecureMethodParameterConfig();
            paramConfig.setIndex(secureMethodParameter.index());
            paramConfig.setProperty(secureMethodParameter.property());
            paramConfig.setEntityClass(secureMethodParameter.entityClass());
            paramConfig.setParser(secureMethodParameter.parser());

            paramConfigs[i] = paramConfig;
        }
        methodConfig.setValidate(paramConfigs);

        // map FilterResult
        methodConfig.setResultFilter(annotation.resultFilter());

        return methodConfig;
    }


    private FilterResultsConfig mapFilterResultsConfig(FilterResults filterResultsAnnotation) {
        FilterResultsConfig filterResultsConfig = new FilterResultsConfig();

        filterResultsConfig.setPropertyToFilter(filterResultsAnnotation.propertyToFilter());
        filterResultsConfig.setTotalRecords(filterResultsAnnotation.totalRecords());
        //map filterProperties
        FilterProperty[] filterProperties = filterResultsAnnotation.itemPropertiesToFilter();
        FilterPropertyConfig[] propertyConfigs = new FilterPropertyConfig[filterProperties.length];
        for (int i = 0; i < filterProperties.length; i++) {
            FilterProperty filterPropAnno = filterProperties[i];
            FilterPropertyConfig propertyConfig = new FilterPropertyConfig();
            propertyConfig.setProperty(filterPropAnno.property());
            propertyConfig.setEntityClass(filterPropAnno.entityClass());
            propertyConfig.setAllowAccessIfNull(filterPropAnno.allowAccessIfNull());

            propertyConfigs[i] = propertyConfig;
        }
        filterResultsConfig.setItemPropertiesToFilter(propertyConfigs);

        return filterResultsConfig;
    }
}
