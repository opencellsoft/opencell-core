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

package org.meveo.api.security.config.annotation;

import org.meveo.api.security.config.*;
import org.slf4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.interceptor.InvocationContext;


/**
 * the default secured entities configuration factory that is based on annotations
 *
 * @author Mounir Boukayoua
 * @since 10.0
 */
@Singleton
@Default
public class SecuredBusinessEntityAnnotationConfigFactory implements SecuredBusinessEntityConfigFactory {

    @Inject
    private Logger log;

    /**
     * build and get secured entity config instance based on method defined annotations
     * @param context method's invocation context
     * @return SecuredBusinessEntityConfig
     */
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

        //log.debug("Secured business entity config is built: {}", config);

        return config;
    }

    /**
     *  build Secured method config from annotations
     * @param annotation SecuredBusinessEntityMethod annotation
     * @return SecuredMethodConfig object
     */
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

    /**
     * build filter results config form annotation
     * @param filterResultsAnnotation FilterResults annotation
     * @return FilterResultsConfig object
     */
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
