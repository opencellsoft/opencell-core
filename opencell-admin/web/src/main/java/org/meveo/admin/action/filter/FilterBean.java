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

package org.meveo.admin.action.filter;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Entity;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.XmlUtil;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.NotifiableEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.Filter;
import org.meveo.model.rating.CDR;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.filter.FilterService;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class FilterBean extends BaseBean<Filter> {

    private static final long serialVersionUID = 6689238784280187702L;

    @Inject
    private FilterService filterService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    private List<CustomFieldTemplate> parameters;

    private boolean forceUpdateParameters;

    public FilterBean() {
        super(Filter.class);
    }

    @Override
    protected IPersistenceService<Filter> getPersistenceService() {
        return filterService;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        String inputXml = entity.getInputXml();

        if (inputXml != null && !StringUtils.isBlank(inputXml)) {
            if (!XmlUtil.validate(inputXml)) {
                messages.error(new BundleKey("messages", "message.filter.invalidXml"));
                return "";
            }
        }
        forceUpdateParameters = true;
        return super.saveOrUpdate(killConversation);
    }

    public List<CustomFieldTemplate> getParameters() {
        if (parameters == null || forceUpdateParameters) {
            log.trace("Initializing filter parameters.");
            forceUpdateParameters = false;
            parameters = new ArrayList<>();
            if (this.getEntity() != null) {
                Map<String, CustomFieldTemplate> customFieldTemplateMap = customFieldTemplateService.findByAppliesTo(this.getEntity());
                for (Map.Entry<String, CustomFieldTemplate> customFieldTemplateEntry : customFieldTemplateMap.entrySet()) {
                    parameters.add(customFieldTemplateEntry.getValue());
                }
            }
            log.trace("Filter parameters initialized.");
        }
        return parameters;
    }

    /**
     * Autocomplete method for class filter field - search entity type classes
     * 
     * @param query A partial class name (including a package)
     * @return A list of classnames
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<String> autocompleteClassNames(String query) {

        List<Class> classes = null;
        try {
            classes = ReflectionUtils.getClasses("org.meveo.model");
            classes.add(CDR.class);
        } catch (Exception e) {
            log.error("Failed to get a list of classes for a model package", e);
            return null;
        }

        String queryLc = query.toLowerCase();
        List<String> classNames = new ArrayList<String>();
        for (Class clazz : classes) {
            if (Proxy.isProxyClass(clazz) || clazz.getName().contains("$$")) {
                continue;
            }
            if (clazz.isAnnotationPresent(Entity.class) && clazz.getName().toLowerCase().contains(queryLc)) {
                classNames.add(clazz.getName());
            }
        }

        Collections.sort(classNames);
        return classNames;
    }
}