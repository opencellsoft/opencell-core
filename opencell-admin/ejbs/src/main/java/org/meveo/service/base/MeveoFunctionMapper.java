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

package org.meveo.service.base;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.BaseEntity;
import org.meveo.model.ICounterEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.CounterPeriodService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomTableService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.el.FunctionMapper;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides custom functions for Meveo application. The following functions are provided:
 * <ul>
 * <li>mv:getCFValue(&lt;entity&gt;,&lt;cf field code&gt;) - retrieve a custom field value by code for a given entity</li>
 * </ul>
 *
 * @author Andrius Karpavicius
 * @author Wassim Drira
 * @author Khalid HORRI
 * @lastModifiedVersion 9.0
 */
public class MeveoFunctionMapper extends FunctionMapper {
    private Map<String, Method> functionMap = new HashMap<String, Method>();

    private static CustomFieldInstanceService customFieldInstanceService;

    private static ScriptInstanceService scriptInstanceService;

    private static CustomTableService customTableService;

    private static CounterPeriodService counterPeriodService;

    private static CustomFieldTemplateService customFieldTemplateService;

    private static Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);

    public MeveoFunctionMapper() {

        super();

        try {
            addFunction("mv", "getCFValue", MeveoFunctionMapper.class.getMethod("getCFValue", ICustomFieldEntity.class, String.class));
            addFunction("mv", "getCFValueForDate", MeveoFunctionMapper.class.getMethod("getCFValue", ICustomFieldEntity.class, String.class, Date.class));
            addFunction("mv", "getCFValueByClosestMatch", MeveoFunctionMapper.class.getMethod("getCFValueByClosestMatch", ICustomFieldEntity.class, String.class, String.class));
            addFunction("mv", "getCFValueByClosestMatchForDate",
                    MeveoFunctionMapper.class.getMethod("getCFValueByClosestMatch", ICustomFieldEntity.class, String.class, Date.class, String.class));
            addFunction("mv", "getCFValueByRangeOfNumbers",
                    MeveoFunctionMapper.class.getMethod("getCFValueByRangeOfNumbers", ICustomFieldEntity.class, String.class, Object.class));
            addFunction("mv", "getCFValueByRangeOfNumbersForDate",
                    MeveoFunctionMapper.class.getMethod("getCFValueByRangeOfNumbers", ICustomFieldEntity.class, String.class, Date.class, Object.class));

            addFunction("mv", "getCFValueByMatrix", MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Object.class));
            addFunction("mv", "getCFValueByMatrix2Keys",
                    MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByMatrix3Keys",
                    MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByMatrix4Keys",
                    MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByMatrix5Keys", MeveoFunctionMapper.class
                    .getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "getCFValueByMatrixForDate",
                    MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class));
            addFunction("mv", "getCFValueByMatrixForDate2Keys",
                    MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByMatrixForDate3Keys",
                    MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByMatrixForDate4Keys", MeveoFunctionMapper.class
                    .getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByMatrixForDate5Keys", MeveoFunctionMapper.class
                    .getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "getCFValueByKey", MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Object.class));
            addFunction("mv", "getCFValueByKey2Keys", MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByKey3Keys",
                    MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByKey4Keys",
                    MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByKey5Keys", MeveoFunctionMapper.class
                    .getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "getCFValueByKeyForDate", MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class));
            addFunction("mv", "getCFValueByKeyForDate2Keys",
                    MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByKeyForDate3Keys",
                    MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByKeyForDate4Keys", MeveoFunctionMapper.class
                    .getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByKeyForDate5Keys", MeveoFunctionMapper.class
                    .getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "isCFValueHasKey", MeveoFunctionMapper.class.getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class));
            addFunction("mv", "isCFValueHasKey2Keys", MeveoFunctionMapper.class.getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class, Object.class));
            addFunction("mv", "isCFValueHasKey3Keys",
                    MeveoFunctionMapper.class.getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class));
            addFunction("mv", "isCFValueHasKey4Keys",
                    MeveoFunctionMapper.class.getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "isCFValueHasKey5Keys", MeveoFunctionMapper.class
                    .getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "isCFValueHasKeyForDate", MeveoFunctionMapper.class.getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class, Object.class));
            addFunction("mv", "isCFValueHasKeyForDate2Keys",
                    MeveoFunctionMapper.class.getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class));
            addFunction("mv", "isCFValueHasKeyForDate3Keys",
                    MeveoFunctionMapper.class.getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class));
            addFunction("mv", "isCFValueHasKeyForDate4Keys", MeveoFunctionMapper.class
                    .getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "isCFValueHasKeyForDate5Keys", MeveoFunctionMapper.class
                    .getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "getInheritedCFValue", MeveoFunctionMapper.class.getMethod("getInheritedCFValue", ICustomFieldEntity.class, String.class));
            addFunction("mv", "getInheritedCFValueForDate", MeveoFunctionMapper.class.getMethod("getInheritedCFValue", ICustomFieldEntity.class, String.class, Date.class));

            addFunction("mv", "getInheritedCFValueByClosestMatch",
                    MeveoFunctionMapper.class.getMethod("getInheritedCFValueByClosestMatch", ICustomFieldEntity.class, String.class, String.class));
            addFunction("mv", "getInheritedCFValueByClosestMatchForDate",
                    MeveoFunctionMapper.class.getMethod("getInheritedCFValueByClosestMatch", ICustomFieldEntity.class, String.class, Date.class, String.class));
            addFunction("mv", "getInheritedCFValueByRangeOfNumbers",
                    MeveoFunctionMapper.class.getMethod("getInheritedCFValueByRangeOfNumbers", ICustomFieldEntity.class, String.class, Object.class));
            addFunction("mv", "getInheritedCFValueByRangeOfNumbersForDate",
                    MeveoFunctionMapper.class.getMethod("getInheritedCFValueByRangeOfNumbers", ICustomFieldEntity.class, String.class, Date.class, Object.class));

            addFunction("mv", "getInheritedCFValueByMatrix", MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrix2Keys",
                    MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrix3Keys",
                    MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrix4Keys", MeveoFunctionMapper.class
                    .getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrix5Keys", MeveoFunctionMapper.class
                    .getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "getInheritedCFValueByMatrixForDate",
                    MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrixForDate2Keys",
                    MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrixForDate3Keys",
                    MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrixForDate4Keys", MeveoFunctionMapper.class
                    .getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrixForDate5Keys", MeveoFunctionMapper.class
                    .getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class, Object.class,
                            Object.class));

            addFunction("mv", "getInheritedCFValueByKey", MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Object.class));
            addFunction("mv", "getInheritedCFValueByKey2Keys",
                    MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByKey3Keys",
                    MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByKey4Keys", MeveoFunctionMapper.class
                    .getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByKey5Keys", MeveoFunctionMapper.class
                    .getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "getInheritedCFValueByKeyForDate",
                    MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class));
            addFunction("mv", "getInheritedCFValueByKeyForDate2Keys",
                    MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByKeyForDate3Keys",
                    MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByKeyForDate4Keys", MeveoFunctionMapper.class
                    .getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByKeyForDate5Keys", MeveoFunctionMapper.class
                    .getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class, Object.class,
                            Object.class));

            addFunction("mv", "isInheritedCFValueHasKey", MeveoFunctionMapper.class.getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class));
            addFunction("mv", "isInheritedCFValueHasKey2Keys",
                    MeveoFunctionMapper.class.getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class, Object.class));
            addFunction("mv", "isInheritedCFValueHasKey3Keys",
                    MeveoFunctionMapper.class.getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class));
            addFunction("mv", "isInheritedCFValueHasKey4Keys", MeveoFunctionMapper.class
                    .getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "isInheritedCFValueHasKey5Keys", MeveoFunctionMapper.class
                    .getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "isInheritedCFValueHasKeyForDate",
                    MeveoFunctionMapper.class.getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class, Object.class));
            addFunction("mv", "isInheritedCFValueHasKeyForDate2Keys",
                    MeveoFunctionMapper.class.getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class));
            addFunction("mv", "isInheritedCFValueHasKeyForDate3Keys",
                    MeveoFunctionMapper.class.getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class));
            addFunction("mv", "isInheritedCFValueHasKeyForDate4Keys", MeveoFunctionMapper.class
                    .getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "isInheritedCFValueHasKeyForDate5Keys", MeveoFunctionMapper.class
                    .getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class, Object.class,
                            Object.class));

            addFunction("mv", "executeScript", MeveoFunctionMapper.class.getMethod("executeScript", IEntity.class, String.class, String.class));

            addFunction("mv", "now", MeveoFunctionMapper.class.getMethod("getNowTimestamp"));

            addFunction("mv", "formatDate", MeveoFunctionMapper.class.getMethod("formatDate", Date.class, String.class));

            addFunction("mv", "parseDate", MeveoFunctionMapper.class.getMethod("parseDate", String.class, String.class));

            addFunction("mv", "getDate", MeveoFunctionMapper.class.getMethod("getDate", Long.class));

            addFunction("mv", "getBean", EjbUtils.class.getMethod("getServiceInterface", String.class));

            addFunction("mv", "addToDate", MeveoFunctionMapper.class.getMethod("addToDate", Date.class, Long.class, Long.class));


            addFunction("mv", "getEndOfMonth", MeveoFunctionMapper.class.getMethod("getEndOfMonth", Date.class));
            
            addFunction("mv", "getStartOfMonth", MeveoFunctionMapper.class.getMethod("getStartOfMonth", Date.class));

            addFunction("mv", "getStartOfNextMonth", MeveoFunctionMapper.class.getMethod("getStartOfNextMonth", Date.class));

            addFunction("mv", "getCTValue", MeveoFunctionMapper.class
                    .getMethod("getCTValue", String.class, String.class, String.class, Object.class, String.class, Object.class, String.class, Object.class, String.class,
                            Object.class, String.class, Object.class));

            addFunction("mv", "getCTValues", MeveoFunctionMapper.class
                    .getMethod("getCTValues", String.class, String.class, Object.class, String.class, Object.class, String.class, Object.class, String.class, Object.class,
                            String.class, Object.class));

            addFunction("mv", "getCTValueForDate", MeveoFunctionMapper.class
                    .getMethod("getCTValue", String.class, String.class, Date.class, String.class, Object.class, String.class, Object.class, String.class, Object.class,
                            String.class, Object.class, String.class, Object.class));

            addFunction("mv", "getCTValuesForDate", MeveoFunctionMapper.class
                    .getMethod("getCTValues", String.class, Date.class, String.class, Object.class, String.class, Object.class, String.class, Object.class, String.class,
                            Object.class, String.class, Object.class));

            addFunction("mv", "getCFRefValue", MeveoFunctionMapper.class.getMethod("getCFRefValue", ICustomFieldEntity.class, String.class, String.class));

            addFunction("mv", "getCounterValue", MeveoFunctionMapper.class.getMethod("getCounterValue", ICounterEntity.class, String.class));
            addFunction("mv", "getCounterValueByDate", MeveoFunctionMapper.class.getMethod("getCounterValueByDate", ICounterEntity.class, String.class, Date.class));
            addFunction("mv", "getLocalizedDescription", MeveoFunctionMapper.class.getMethod("getLocalizedDescription", IEntity.class, String.class));

            //adding all Math methods with 'math' as prefix
            for (Method method : Math.class.getMethods()) {
                int modifiers = method.getModifiers();
                Class<?> retType = method.getReturnType();
                if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && !(retType == Void.TYPE)) {
                    String name = method.getName();
                    addFunction("math", name, Math.class.getMethod(name, method.getParameterTypes()));
                }
            }

            // addFunction("mv", "call", MeveoFunctionMapper.class.getMethod("call", String.class, String.class,String.class, Object[].class));
        } catch (NoSuchMethodException | SecurityException e) {
            log.error("Failed to instantiate EL custom function mv:xx", e);
        }
    }

    @Override
    public Method resolveFunction(String prefix, String localName) {
        String key = prefix + ":" + localName;
        return functionMap.get(key);
    }

    public void addFunction(String prefix, String localName, Method method) {
        if (prefix == null || localName == null || method == null) {
            throw new NullPointerException();
        }
        int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalArgumentException("method not public");
        }
        if (!Modifier.isStatic(modifiers)) {
            throw new IllegalArgumentException("method not static");
        }
        Class<?> retType = method.getReturnType();
        if (retType == Void.TYPE) {
            throw new IllegalArgumentException("method returns void");
        }

        String key = prefix + ":" + localName;
        functionMap.put(key, method);
    }

    private static CustomFieldInstanceService getCustomFieldInstanceService() {

        if (customFieldInstanceService == null) {
            try {
                InitialContext initialContext = new InitialContext();
                BeanManager beanManager = (BeanManager) initialContext.lookup("java:comp/BeanManager");

                Bean<CustomFieldInstanceService> bean = (Bean<CustomFieldInstanceService>) beanManager.resolve(beanManager.getBeans(CustomFieldInstanceService.class));
                customFieldInstanceService = (CustomFieldInstanceService) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));

            } catch (NamingException e) {
                log.error("Unable to access CustomFieldInstanceService", e);
                throw new RuntimeException(e);
            }
        }
        return customFieldInstanceService;
    }

    private static ScriptInstanceService getScriptInstanceService() {

        if (scriptInstanceService == null) {
            try {
                InitialContext initialContext = new InitialContext();
                BeanManager beanManager = (BeanManager) initialContext.lookup("java:comp/BeanManager");

                Bean<ScriptInstanceService> bean = (Bean<ScriptInstanceService>) beanManager.resolve(beanManager.getBeans(ScriptInstanceService.class));
                scriptInstanceService = (ScriptInstanceService) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));

            } catch (NamingException e) {
                log.error("Unable to access ScriptInstanceService", e);
                throw new RuntimeException(e);
            }
        }
        return scriptInstanceService;
    }

    private static CustomTableService getCustomTableService() {

        if (customTableService == null) {
            try {
                InitialContext initialContext = new InitialContext();
                BeanManager beanManager = (BeanManager) initialContext.lookup("java:comp/BeanManager");

                Bean<CustomTableService> bean = (Bean<CustomTableService>) beanManager.resolve(beanManager.getBeans(CustomTableService.class));
                customTableService = (CustomTableService) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));

            } catch (NamingException e) {
                log.error("Unable to access CustomTableService", e);
                throw new RuntimeException(e);
            }
        }
        return customTableService;
    }

    private static CounterPeriodService getCounterPeriodService() {

        if (counterPeriodService == null) {
            try {
                InitialContext initialContext = new InitialContext();
                BeanManager beanManager = (BeanManager) initialContext.lookup("java:comp/BeanManager");

                Bean<CounterPeriodService> bean = (Bean<CounterPeriodService>) beanManager.resolve(beanManager.getBeans(CounterPeriodService.class));
                counterPeriodService = (CounterPeriodService) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));

            } catch (NamingException e) {
                Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
                log.error("Unable to access CounterPeriodService", e);
                throw new RuntimeException(e);
            }
        }
        return counterPeriodService;
    }

    private static CustomFieldTemplateService getCustomFieldTemplateService() {
        if (customFieldTemplateService == null) {
            try {
                InitialContext initialContext = new InitialContext();
                BeanManager beanManager = (BeanManager) initialContext.lookup("java:comp/BeanManager");

                Bean<CustomFieldTemplateService> bean = (Bean<CustomFieldTemplateService>) beanManager.resolve(beanManager.getBeans(CustomFieldTemplateService.class));
                customFieldTemplateService = (CustomFieldTemplateService) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));

            } catch (NamingException e) {
                Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
                log.error("Unable to access CustomFieldTemplateService", e);
                throw new RuntimeException(e);
            }
        }
        return customFieldTemplateService;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValue() function as EL function. See CustomFieldInstanceService.getCFValue() function for documentation
     *
     * @param entity entity to get infos
     * @param code   code of entity
     * @return cf value.
     */
    public static Object getCFValue(ICustomFieldEntity entity, String code) {

        Object cfValue = getCustomFieldInstanceService().getCFValue(entity, code);
        log.trace("Obtained CF value {} for {}/{}", cfValue, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValue() function as EL function. See CustomFieldInstanceService.getCFValue() function for documentation
     *
     * @param entity entity to get infos
     * @param code   code of entity
     * @param date   date to check
     * @return cf value.
     */
    public static Object getCFValue(ICustomFieldEntity entity, String code, Date date) {

        Object cfValue = getCustomFieldInstanceService().getCFValue(entity, code, date);
        log.trace("Obtained CF value {} for {}/{} for {}", cfValue, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByClosestMatch() function as EL function. See CustomFieldInstanceService.getCFValueByClosestMatch() function for documentation
     *
     * @param entity     entity to get infos
     * @param code       code of entity
     * @param keyToMatch jey to match.
     * @return cf value.
     */
    public static Object getCFValueByClosestMatch(ICustomFieldEntity entity, String code, String keyToMatch) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByClosestMatch(entity, code, keyToMatch);
        log.trace("Obtained CF value {} by closest match for key {} for {}/{}", cfValue, keyToMatch, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByClosestMatch() function as EL function. See CustomFieldInstanceService.getCFValueByClosestMatch() function for documentation
     *
     * @param entity     entity to get infos
     * @param code       code of entity
     * @param date       date to check.
     * @param keyToMatch jey to match.
     * @return cf value
     */
    public static Object getCFValueByClosestMatch(ICustomFieldEntity entity, String code, Date date, String keyToMatch) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByClosestMatch(entity, code, date, keyToMatch);
        log.trace("Obtained CF value {} by closest match for key {} for {}/{} for {}", cfValue, keyToMatch, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByRangeOfNumbers() function as EL function. See CustomFieldInstanceService.getCFValueByRangeOfNumbers() function for
     * documentation
     *
     * @param entity        entity to get infos
     * @param code          code of entity
     * @param numberToMatch Key to match.
     * @return cf value
     */
    public static Object getCFValueByRangeOfNumbers(ICustomFieldEntity entity, String code, Object numberToMatch) {

        if (numberToMatch != null && numberToMatch instanceof String) {
            try {
                numberToMatch = Double.parseDouble((String) numberToMatch);
            } catch (NumberFormatException e) {
                // Dont care about error nothing will be found later
            }
        }

        Object cfValue = getCustomFieldInstanceService().getCFValueByRangeOfNumbers(entity, code, numberToMatch);
        log.trace("Obtained CF value {} by range of numbers for number {} for {}/{}", cfValue, numberToMatch, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByRangeOfNumbers() function as EL function. See CustomFieldInstanceService.getCFValueByRangeOfNumbers() function for
     * documentation
     *
     * @param entity        entity to get infos
     * @param code          code of entity
     * @param date          date to check.
     * @param numberToMatch number to match.
     * @return cfvalue
     */
    public static Object getCFValueByRangeOfNumbers(ICustomFieldEntity entity, String code, Date date, Object numberToMatch) {

        if (numberToMatch != null && numberToMatch instanceof String) {
            try {
                numberToMatch = Double.parseDouble((String) numberToMatch);
            } catch (NumberFormatException e) {
                // Dont care about error nothing will be found later
            }
        }

        Object cfValue = getCustomFieldInstanceService().getCFValueByRangeOfNumbers(entity, code, date, numberToMatch);
        log.trace("Obtained CF value {} by range of numbers for number {} for {}/{} for {}", cfValue, numberToMatch, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     *
     * @param entity                      Entity to find CF value for
     * @param code                        Custom field code
     * @param concatenatedKeysOrSingleKey Keys concatenated by "|" sign or a single key
     * @return cfValue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Object concatenatedKeysOrSingleKey) {

        if (concatenatedKeysOrSingleKey == null || (concatenatedKeysOrSingleKey instanceof String && StringUtils.isBlank((String) concatenatedKeysOrSingleKey))) {
            return null;
        }

        Object cfValue = null;

        if (concatenatedKeysOrSingleKey instanceof String) {
            String[] keys = ((String) concatenatedKeysOrSingleKey).split("\\|");
            cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, (Object[]) keys);
            log.trace("Obtained CF value {} by key for keys {} for {}/{}", cfValue, keys, entity, code);
        } else {
            cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, concatenatedKeysOrSingleKey);
            log.trace("Obtained CF value {} by key for keys {} for {}/{}", cfValue, concatenatedKeysOrSingleKey, entity, code);
        }

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code   code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @return cfValue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, keyOne, keyTwo);
        log.trace("Obtained CF value {} by key for keys {}/{} for {}/{}", cfValue, keyOne, keyTwo, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @return cfvalue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, keyOne, keyTwo, keyThree);
        log.trace("Obtained CF value {} by key for keys {}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @param keyFour  key four.
     * @return cfvalue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, keyOne, keyTwo, keyThree, keyFour);
        log.trace("Obtained CF value {} by key for keys {}/{}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, keyFour, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @param keyFour  key four.
     * @param keyFive  key five.
     * @return cfvalue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour, Object keyFive) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, keyOne, keyTwo, keyThree, keyFour, keyFive);
        log.trace("Obtained CF value {} by key for keys {}/{}/{}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     *
     * @param entity                      Entity to find CF value for
     * @param code                        Custom field code
     * @param date                        Date Value date
     * @param concatenatedKeysOrSingleKey Keys concatenated by "|" sign or a single key
     * @return cfValue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object concatenatedKeysOrSingleKey) {

        if (concatenatedKeysOrSingleKey == null || (concatenatedKeysOrSingleKey instanceof String && StringUtils.isBlank((String) concatenatedKeysOrSingleKey))) {
            return null;
        }

        Object cfValue = null;

        if (concatenatedKeysOrSingleKey instanceof String) {
            String[] keys = ((String) concatenatedKeysOrSingleKey).split("\\|");
            cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, date, (Object[]) keys);
            log.trace("Obtained CF value {} by key for keys {} for {}/{}", cfValue, keys, entity, code);
        } else {
            cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, date, concatenatedKeysOrSingleKey);
            log.trace("Obtained CF value {} by key for keys {} for {}/{} for {}", cfValue, concatenatedKeysOrSingleKey, entity, code, date);
        }

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code   code of entity
     * @param date   date to check.
     * @param keyOne key one
     * @param keyTwo key two
     * @return cfvalue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, date, keyOne, keyTwo);
        log.trace("Obtained CF value {} by key for keys {}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param date     date to check.
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @return cfvalue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, date, keyOne, keyTwo, keyThree);
        log.trace("Obtained CF value {} by key for keys {}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param date     date to check.
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @param keyFour  key four.
     * @return cfvalue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, date, keyOne, keyTwo, keyThree, keyFour);
        log.trace("Obtained CF value {} by key for keys {}/{}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, keyFour, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param date     date to check.
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @param keyFour  key four.
     * @param keyFive  key five.
     * @return cfvalue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour, Object keyFive) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, date, keyOne, keyTwo, keyThree, keyFour, keyFive);
        log.trace("Obtained CF value {} by key for keys {}/{}/{}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValue() function as EL function. See CustomFieldInstanceService.getInheritedCFValue() function for documentation
     *
     * @param entity entity to get infos
     * @param code   code of entity
     * @return cfvalue
     */
    public static Object getInheritedCFValue(ICustomFieldEntity entity, String code) {

        // log.debug("AKK start getInheritedCFvalue for entity {}", entity.getClass());
        Object cfValue = getCustomFieldInstanceService().getInheritedCFValue(entity, code);
        log.trace("Obtained inherited CF value {} for {}/{}", cfValue, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValue() function as EL function. See CustomFieldInstanceService.getInheritedCFValue() function for documentation
     *
     * @param entity entity to get infos
     * @param code   code of entity
     * @param date   date to check.
     * @return cfvalue
     */
    public static Object getInheritedCFValue(ICustomFieldEntity entity, String code, Date date) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValue(entity, code, date);
        log.trace("Obtained inherited CF value {} for {}/{} for {}", cfValue, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByClosestMatch() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByClosestMatch() function
     * for documentation
     *
     * @param entity     entity to get infos
     * @param code       code of entity
     * @param keyToMatch key to match.
     * @return cfvalue
     */
    public static Object getInheritedCFValueByClosestMatch(ICustomFieldEntity entity, String code, String keyToMatch) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByClosestMatch(entity, code, keyToMatch);
        log.trace("Obtained inherited CF value {} by closest match for key {} for {}/{}", cfValue, keyToMatch, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByClosestMatch() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByClosestMatch() function
     * for documentation.
     *
     * @param entity     entity to get infos
     * @param code       code of entity
     * @param date       date to check.
     * @param keyToMatch key to match.
     * @return cfvalue
     */
    public static Object getInheritedCFValueByClosestMatch(ICustomFieldEntity entity, String code, Date date, String keyToMatch) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByClosestMatch(entity, code, date, keyToMatch);
        log.trace("Obtained inherited CF value {} by closest match for key {} for {}/{} for {}", cfValue, keyToMatch, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByRangeOfNumbers() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByRangeOfNumbers()
     * function for documentation
     *
     * @param entity        entity to get infos
     * @param code          code of entity
     * @param numberToMatch number to match.
     * @return cfvalue
     */
    public static Object getInheritedCFValueByRangeOfNumbers(ICustomFieldEntity entity, String code, Object numberToMatch) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByRangeOfNumbers(entity, code, numberToMatch);
        log.trace("Obtained inherited CF value {} by range of numbers for number {} for {}/{}", cfValue, numberToMatch, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByRangeOfNumbers() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByRangeOfNumbers()
     * function for documentation.
     *
     * @param entity        entity to get infos
     * @param code          code of entity
     * @param date          date to check.
     * @param numberToMatch number to match.
     * @return cfvalue
     */
    public static Object getInheritedCFValueByRangeOfNumbers(ICustomFieldEntity entity, String code, Date date, Object numberToMatch) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByRangeOfNumbers(entity, code, date, numberToMatch);
        log.trace("Obtained inherited CF value {} by range of numbers for number {} for {}/{} for {}", cfValue, numberToMatch, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByMatrix() function for
     * documentation
     *
     * @param entity                      Entity to find CF value for
     * @param code                        Custom field code
     * @param concatenatedKeysOrSingleKey Keys concatenated by "|" sign or a single key
     * @return cfValue.
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Object concatenatedKeysOrSingleKey) {

        if (concatenatedKeysOrSingleKey == null || (concatenatedKeysOrSingleKey instanceof String && StringUtils.isBlank((String) concatenatedKeysOrSingleKey))) {
            return null;
        }

        Object cfValue = null;

        if (concatenatedKeysOrSingleKey instanceof String) {
            String[] keys = ((String) concatenatedKeysOrSingleKey).split("\\|");
            cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, (Object[]) keys);
            log.trace("Obtained inherited CF value {} by key for keys {} for {}/{}", cfValue, keys, entity, code);
        } else {
            cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, concatenatedKeysOrSingleKey);
            log.trace("Obtained inherited CF value {} by key for keys {} for {}/{}", cfValue, concatenatedKeysOrSingleKey, entity, code);
        }

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByKey() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code   code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @return cfvalue
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, keyOne, keyTwo);
        log.trace("Obtained inherited CF value {} by key for keys {}/{} for {}/{}", cfValue, keyOne, keyTwo, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByKey() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @return cfvalue
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, keyOne, keyTwo, keyThree);
        log.trace("Obtained inherited CF value {} by key for keys {}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByKey() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @param keyFour  key four.
     * @return cfvalue
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, keyOne, keyTwo, keyThree, keyFour);
        log.trace("Obtained inherited CF value {} by key for keys {}/{}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, keyFour, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByKey() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @param keyFour  key four.
     * @param keyFive  key five.
     * @return cfvalue
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour, Object keyFive) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, keyOne, keyTwo, keyThree, keyFour, keyFive);
        log.trace("Obtained inherited CF value {} by key for keys {}/{}/{}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByKey() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByKey() function for documentation
     *
     * @param entity                      Entity to find CF value for
     * @param code                        Custom field code
     * @param date                        Date Value date
     * @param concatenatedKeysOrSingleKey Keys concatenated by "|" sign
     * @return cfValue.
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object concatenatedKeysOrSingleKey) {

        if (concatenatedKeysOrSingleKey == null || (concatenatedKeysOrSingleKey instanceof String && StringUtils.isBlank((String) concatenatedKeysOrSingleKey))) {
            return null;
        }

        Object cfValue = null;

        if (concatenatedKeysOrSingleKey instanceof String) {
            String[] keys = ((String) concatenatedKeysOrSingleKey).split("\\|");
            cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, date, (Object[]) keys);
            log.trace("Obtained inherited CF value {} by key for keys {} for {}/{} for {}", cfValue, keys, entity, code, date);
        } else {
            cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, date, concatenatedKeysOrSingleKey);
            log.trace("Obtained inherited CF value {} by key for keys {} for {}/{} for {}", cfValue, concatenatedKeysOrSingleKey, entity, code, date);
        }
        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByKey() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code   code of entity
     * @param date   date to check.
     * @param keyOne key one
     * @param keyTwo key two
     * @return cfvalue
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, date, keyOne, keyTwo);
        log.trace("Obtained inherited CF value {} by key for keys {}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByKey() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param date     date to check.
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @return cfvalue
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, date, keyOne, keyTwo, keyThree);
        log.trace("Obtained inherited CF value {} by key for keys {}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByKey() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param date     date to check.
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @param keyFour  key four.
     * @return cfvalue.
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, date, keyOne, keyTwo, keyThree, keyFour);
        log.trace("Obtained inherited CF value {} by key for keys {}/{}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, keyFour, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByKey() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param date     date to check
     * @param keyOne   key of CF.
     * @param keyTwo   key of CF.
     * @param keyThree key three
     * @param keyFour  key four.
     * @param keyFive  key five.
     * @return cf value
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour,
            Object keyFive) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, date, keyOne, keyTwo, keyThree, keyFour, keyFive);
        
        log.trace("Obtained inherited CF value {} by key for keys {}/{}/{}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code, date);

        return cfValue;
    }

    /**
     * Execute script on an entity.
     *
     * @param entity            Entity to execute action on
     * @param scriptCode        Script to execute, identified by a code
     * @param encodedParameters Additional parameters encoded in URL like style param=value&amp;param=value
     * @return A script execution result value
     */
    public static Object executeScript(IEntity entity, String scriptCode, String encodedParameters) {

        Map<String, Object> result = null;
        try {
            result = getScriptInstanceService().execute(entity, scriptCode, encodedParameters);
        } catch (ElementNotFoundException enf) {
            result = null;
        }
        if (result != null && result.containsKey(Script.RESULT_VALUE)) {
            return result.get(Script.RESULT_VALUE);
        }

        return result;
    }

    /**
     * Get a timestamp.
     *
     * @return current date.
     */
    public static Date getNowTimestamp() {
        return new Date();
    }

    /**
     * Format date.
     *
     * @param date              date to be formatted.
     * @param dateFormatPattern standard java date and time patterns
     * @return A formated date
     */
    public static String formatDate(Date date, String dateFormatPattern) {
        if (date == null) {
            return DateUtils.formatDateWithPattern(new Date(), dateFormatPattern);
        }
        return DateUtils.formatDateWithPattern(date, dateFormatPattern);
    }

    /**
     * Parse date.
     *
     * @param dateString        Date string
     * @param dateFormatPattern standard java date and time patterns
     * @return A parsed date
     */
    public static Date parseDate(String dateString, String dateFormatPattern) {
        ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface(ParamBeanFactory.class.getSimpleName());
        if (dateString == null) {
            return new Date();
        }
        if (dateFormatPattern == null) {
            dateFormatPattern = paramBeanFactory.getInstance().getDateFormat();
        }
        return DateUtils.parseDateWithPattern(dateString, dateFormatPattern);
    }

    /**
     * Get date fro epoch.
     *
     * @param epoch standard java date and time patterns
     * @return a date
     */
    public static Date getDate(Long epoch) {
        if (epoch == null) {
            return new Date();
        }
        return new Date(epoch.longValue());
    }

    /*
     * stupid piece of code : better switch to javaEE7 and EL3
     * 
     * public static Object call(String className, String method,String signature, Object... inputParams) throws Exception{
     * 
     * String[] classNames = signature.split(","); Class<?>[] classes = new Class[classNames.length]; Object[] params = new Object[inputParams.length]; for(int
     * i=0;i<classNames.length;i++){ classes[i]=Class.forName(classNames[i]); } for(int i=0;i<inputParams.length;i++){ try{ ConvertUtils.convert(inputParams[i], classes[i]); }
     * catch(Exception e){ params[i]=inputParams[i]; } } Method met = Class.forName(className).getMethod(method, classes); return met.invoke(null, params); }
     */

    /**
     * Exposes CustomFieldInstanceService.isCFValueHasKey() function as EL function. See CustomFieldInstanceService.isCFValueHasKey() function for documentation
     *
     * @param entity                      Entity to check CF value for
     * @param code                        Custom field code
     * @param concatenatedKeysOrSingleKey Keys concatenated by "|" sign or a single key
     * @return true if cfValue has key.
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Object concatenatedKeysOrSingleKey) {

        if (concatenatedKeysOrSingleKey == null || (concatenatedKeysOrSingleKey instanceof String && StringUtils.isBlank((String) concatenatedKeysOrSingleKey))) {
            return false;
        }

        boolean hasKey = false;

        if (concatenatedKeysOrSingleKey instanceof String) {
            String[] keys = ((String) concatenatedKeysOrSingleKey).split("\\|");
            hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, (Object[]) keys);
            log.trace("CF value has {} key for keys {} for {}/{}", hasKey, keys, entity, code);
        } else {
            hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, concatenatedKeysOrSingleKey);
            log.trace("CF value has {} key for keys {} for {}/{}", hasKey, concatenatedKeysOrSingleKey, entity, code);
        }

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isCFValueHasKey() function as EL function. See CustomFieldInstanceService.isCFValueHasKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code   code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @return true if cfvalue has key
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo) {

        boolean hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, keyOne, keyTwo);
        log.trace("CF value has {} key for keys {}/{} for {}/{}", hasKey, keyOne, keyTwo, entity, code);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isCFValueHasKey() function as EL function. See CustomFieldInstanceService.isCFValueHasKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @return true if cfvalue has key.
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree) {

        boolean hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, keyOne, keyTwo, keyThree);
        log.trace("CF value has {} key for keys {}/{}/{} for {}/{}", hasKey, keyOne, keyTwo, keyThree, entity, code);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isCFValueHasKey() function as EL function. See CustomFieldInstanceService.isCFValueHasKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @param keyFour  key four
     * @return true if cfvalue has key.
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        boolean hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, keyOne, keyTwo, keyThree, keyFour);
        log.trace("CF value has {} key for keys {}/{}/{}/{} for {}/{}", hasKey, keyOne, keyTwo, keyThree, keyFour, entity, code);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isCFValueHasKey() function as EL function. See CustomFieldInstanceService.isCFValueHasKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @param keyFour  key four
     * @param keyFive  key five.
     * @return true if cfValue has key.
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour, Object keyFive) {

        boolean hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, keyOne, keyTwo, keyThree, keyFour, keyFive);
        log.trace("CF value has {} key for keys {}/{}/{}/{}/{} for {}/{}", hasKey, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isCFValueHasKey() function as EL function. See CustomFieldInstanceService.isCFValueHasKey() function for documentation
     *
     * @param entity                      Entity to check CF value for
     * @param code                        Custom field code
     * @param date                        Date
     * @param concatenatedKeysOrSingleKey Keys concatenated by "|" sign or a single key
     * @return true if cfValue has key.
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object concatenatedKeysOrSingleKey) {

        if (concatenatedKeysOrSingleKey == null || (concatenatedKeysOrSingleKey instanceof String && StringUtils.isBlank((String) concatenatedKeysOrSingleKey))) {
            return false;
        }

        boolean hasKey = false;

        if (concatenatedKeysOrSingleKey instanceof String) {
            String[] keys = ((String) concatenatedKeysOrSingleKey).split("\\|");
            hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, date, (Object[]) keys);
            log.trace("CF value has {} key for keys {} for {}/{} for {}", hasKey, keys, entity, code, date);
        } else {
            hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, date, concatenatedKeysOrSingleKey);
            log.trace("CF value has {} key for keys {} for {}/{} for {}", hasKey, concatenatedKeysOrSingleKey, entity, code, date);
        }

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isCFValueHasKey() function as EL function. See CustomFieldInstanceService.isCFValueHasKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code   code of entity
     * @param date   date to check.
     * @param keyOne key one
     * @param keyTwo key two
     * @return true if cfvalue has key.
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo) {

        boolean hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, date, keyOne, keyTwo);
        log.trace("CF value has {} key for keys {}/{} for {}/{} for {}", hasKey, keyOne, keyTwo, entity, code, date);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isCFValueHasKey() function as EL function. See CustomFieldInstanceService.isCFValueHasKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param date     date to check
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @return true if cfvalue has key.
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree) {

        boolean hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, date, keyOne, keyTwo, keyThree);
        log.trace("CF value has {} key for keys {}/{}/{} for {}/{} for {}", hasKey, keyOne, keyTwo, keyThree, entity, code, date);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isCFValueHasKey() function as EL function. See CustomFieldInstanceService.isCFValueHasKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param date     date to check
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @param keyFour  key four
     * @return true if cfvalue has key
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        boolean hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, date, keyOne, keyTwo, keyThree, keyFour);
        log.trace("CF value has {} key for keys {}/{}/{}/{} for {}/{} for {}", hasKey, keyOne, keyTwo, keyThree, keyFour, entity, code, date);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isCFValueHasKey() function as EL function. See CustomFieldInstanceService.isCFValueHasKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param date     date to check
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @param keyFour  key four
     * @param keyFive  key five
     * @return true if cfvalue has key
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour, Object keyFive) {

        boolean hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, date, keyOne, keyTwo, keyThree, keyFour, keyFive);
        log.trace("CF value has {} key for keys {}/{}/{}/{}/{} for {}/{} for {}", hasKey, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code, date);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     *
     * @param entity                      Entity to check CF value for
     * @param code                        Custom field code
     * @param concatenatedKeysOrSingleKey Keys concatenated by "|" sign or a single key
     * @return true if cfVaue has key.
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Object concatenatedKeysOrSingleKey) {

        if (concatenatedKeysOrSingleKey == null || (concatenatedKeysOrSingleKey instanceof String && StringUtils.isBlank((String) concatenatedKeysOrSingleKey))) {
            return false;
        }

        boolean hasKey = false;

        if (concatenatedKeysOrSingleKey instanceof String) {
            String[] keys = ((String) concatenatedKeysOrSingleKey).split("\\|");
            hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, (Object[]) keys);
            log.trace("Inherited CF value has {} key for keys {} for {}/{}", hasKey, keys, entity, code);
        } else {
            hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, concatenatedKeysOrSingleKey);
            log.trace("Inherited CF value has {} key for keys {} for {}/{}", hasKey, concatenatedKeysOrSingleKey, entity, code);
        }

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code   code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @return true if cfvalue has key
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo) {

        boolean hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, keyOne, keyTwo);
        log.trace("Inherited CF value has {} key for keys {}/{} for {}/{}", hasKey, keyOne, keyTwo, entity, code);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @return true if cfvalue has key
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree) {

        boolean hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, keyOne, keyTwo, keyThree);
        log.trace("Inherited CF value has {} key for keys {}/{}/{} for {}/{}", hasKey, keyOne, keyTwo, keyThree, entity, code);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     *
     * @param entity   entity to get infos.
     * @param code     code of entity
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @param keyFour  key four.
     * @return true if cfvalue has key
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        boolean hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, keyOne, keyTwo, keyThree, keyFour);
        log.trace("Inherited CF value has {} key for keys {}/{}/{}/{} for {}/{}", hasKey, keyOne, keyTwo, keyThree, keyFour, entity, code);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @param keyFour  key four
     * @param keyFive  key five.
     * @return true if cfvalue has key
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour, Object keyFive) {

        boolean hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, keyOne, keyTwo, keyThree, keyFour, keyFive);
        log.trace("Inherited CF value has {} key for keys {}/{}/{}/{}/{} for {}/{}", hasKey, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     *
     * @param entity                      Entity to check CF value for
     * @param code                        Custom field code
     * @param date                        Date
     * @param concatenatedKeysOrSingleKey Keys concatenated by "|" sign or a single key
     * @return true if cfValue has key.
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object concatenatedKeysOrSingleKey) {

        if (concatenatedKeysOrSingleKey == null || (concatenatedKeysOrSingleKey instanceof String && StringUtils.isBlank((String) concatenatedKeysOrSingleKey))) {
            return false;
        }

        boolean hasKey = false;

        if (concatenatedKeysOrSingleKey instanceof String) {
            String[] keys = ((String) concatenatedKeysOrSingleKey).split("\\|");
            hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, date, (Object[]) keys);
            log.trace("Inherited CF value has {} key for keys {} for {}/{} for {}", hasKey, keys, entity, code, date);
        } else {
            hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, date, concatenatedKeysOrSingleKey);
            log.trace("Inherited CF value has {} key for keys {} for {}/{} for {}", hasKey, concatenatedKeysOrSingleKey, entity, code, date);
        }

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code   code of entity
     * @param date   date to check
     * @param keyOne key one
     * @param keyTwo key two
     * @return true if cfvalue has key
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo) {

        boolean hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, date, keyOne, keyTwo);
        log.trace("Inherited CF value has {} key for keys {}/{} for {}/{} for {}", hasKey, keyOne, keyTwo, entity, code, date);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param date     date to check
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @return true if cfvalue has key
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree) {

        boolean hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, date, keyOne, keyTwo, keyThree);
        log.trace("Inherited CF value has {} key for keys {}/{}/{} for {}/{} for {}", hasKey, keyOne, keyTwo, keyThree, entity, code, date);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param date     date to check
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @param keyFour  key four.
     * @return true if cfvalue has key
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        boolean hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, date, keyOne, keyTwo, keyThree, keyFour);
        log.trace("Inherited CF value has {} key for keys {}/{}/{}/{} for {}/{} for {}", hasKey, keyOne, keyTwo, keyThree, keyFour, entity, code, date);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     *
     * @param entity   entity to get infos
     * @param code     code of entity
     * @param date     date to check
     * @param keyOne   key one
     * @param keyTwo   key two
     * @param keyThree key three.
     * @param keyFour  key four.
     * @param keyFive  key five.
     * @return true if cfvalue has key
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour,
            Object keyFive) {

        boolean hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, date, keyOne, keyTwo, keyThree, keyFour, keyFive);
        log.trace("Inherited CF value has {} key for keys {}/{}/{}/{}/{} for {}/{} for {}", hasKey, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code, date);

        return hasKey;
    }

    /**
     * Adds or subtracts duration to the given date.
     *
     * @param date          date to be added.
     * @param durationType  The same value as java.util.Calendar constants : 5 for day, 2 for month,...
     * @param durationValue duration to add
     * @return date
     */
    public static Date addToDate(Date date, Long durationType, Long durationValue) {
        Date result = null;
        if (date != null && durationType != null && durationValue != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(durationType.intValue(), durationValue.intValue());
            result = calendar.getTime();
        }
        return result;
    }

    public static Date getEndOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

        return c.getTime();
    }
    
    public static Date getStartOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    public static Date getStartOfNextMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        
        return c.getTime();
    }

    /**
     * Exposes CustomTableService.getValue() function as EL function. See CustomTableService.getValue() function for documentation.
     * <p>
     * Accepts up to 5 field name/field value combinations
     *
     * @param customTableCode Custom table code
     * @param fieldToReturn   Field value to return
     * @param fieldName1      Field (or condition) to query
     * @param fieldValue1     Field search value
     * @param fieldName2      Field (or condition) to query (optional)
     * @param fieldValue2     Field search value (optional)
     * @param fieldName3      Field (or condition) to query (optional)
     * @param fieldValue3     Field search value (optional)
     * @param fieldName4      Field (or condition) to query (optional)
     * @param fieldValue4     Field search value (optional)
     * @param fieldName5      Field (or condition) to query (optional)
     * @param fieldValue5     Field search value (optional)
     * @return A field value
     * @throws BusinessException General exception
     */
    public static Object getCTValue(String customTableCode, String fieldToReturn, String fieldName1, Object fieldValue1, String fieldName2, Object fieldValue2, String fieldName3,
            Object fieldValue3, String fieldName4, Object fieldValue4, String fieldName5, Object fieldValue5) throws BusinessException {

        Map<String, Object> queryValues = new HashMap<>();

        queryValues.put(fieldName1, fieldValue1);

        if (fieldName2 != null) {
            queryValues.put(fieldName2, fieldValue2);
        }
        if (fieldName3 != null) {
            queryValues.put(fieldName3, fieldValue3);
        }
        if (fieldName4 != null) {
            queryValues.put(fieldName4, fieldValue4);
        }
        if (fieldName5 != null) {
            queryValues.put(fieldName5, fieldValue5);
        }
        return getCustomTableService().getValue(customTableCode, fieldToReturn, queryValues);
    }

    /**
     * Exposes CustomTableService.getValue() function as EL function. See CustomTableService.getValue() function for documentation.
     * <p>
     * Accepts up to 5 field name/field value combinations
     *
     * @param customTableCode Custom table code
     * @param fieldToReturn   Field value to return
     * @param date            Record validity date, as expressed by 'valid_from' and 'valid_to' fields, to match
     * @param fieldName1      Field (or condition) to query
     * @param fieldValue1     Field search value
     * @param fieldName2      Field (or condition) to query (optional)
     * @param fieldValue2     Field search value (optional)
     * @param fieldName3      Field (or condition) to query (optional)
     * @param fieldValue3     Field search value (optional)
     * @param fieldName4      Field (or condition) to query (optional)
     * @param fieldValue4     Field search value (optional)
     * @param fieldName5      Field (or condition) to query (optional)
     * @param fieldValue5     Field search value (optional)
     * @return A field value
     * @throws BusinessException General exception
     */
    public static Object getCTValue(String customTableCode, String fieldToReturn, Date date, String fieldName1, Object fieldValue1, String fieldName2, Object fieldValue2,
            String fieldName3, Object fieldValue3, String fieldName4, Object fieldValue4, String fieldName5, Object fieldValue5) throws BusinessException {

        Map<String, Object> queryValues = new HashMap<>();

        queryValues.put(fieldName1, fieldValue1);

        if (fieldName2 != null) {
            queryValues.put(fieldName2, fieldValue2);
        }
        if (fieldName3 != null) {
            queryValues.put(fieldName3, fieldValue3);
        }
        if (fieldName4 != null) {
            queryValues.put(fieldName4, fieldValue4);
        }
        if (fieldName5 != null) {
            queryValues.put(fieldName5, fieldValue5);
        }

        return getCustomTableService().getValue(customTableCode, fieldToReturn, date, queryValues);
    }

    /**
     * Exposes CustomTableService.getValue() function as EL function. See CustomTableService.getValue() function for documentation.
     * <p>
     * Accepts up to 5 field name/field value combinations
     *
     * @param customTableCode Custom table code
     * @param fieldName1      Field (or condition) to query
     * @param fieldValue1     Field search value
     * @param fieldName2      Field (or condition) to query (optional)
     * @param fieldValue2     Field search value (optional)
     * @param fieldName3      Field (or condition) to query (optional)
     * @param fieldValue3     Field search value (optional)
     * @param fieldName4      Field (or condition) to query (optional)
     * @param fieldValue4     Field search value (optional)
     * @param fieldName5      Field (or condition) to query (optional)
     * @param fieldValue5     Field search value (optional)
     * @return A map of field values with field name as a key and field value as a value
     * @throws BusinessException General exception
     */
    public static Map<String, Object> getCTValues(String customTableCode, String fieldName1, Object fieldValue1, String fieldName2, Object fieldValue2, String fieldName3,
            Object fieldValue3, String fieldName4, Object fieldValue4, String fieldName5, Object fieldValue5) throws BusinessException {

        Map<String, Object> queryValues = new HashMap<>();

        queryValues.put(fieldName1, fieldValue1);

        if (fieldName2 != null) {
            queryValues.put(fieldName2, fieldValue2);
        }
        if (fieldName3 != null) {
            queryValues.put(fieldName3, fieldValue3);
        }
        if (fieldName4 != null) {
            queryValues.put(fieldName4, fieldValue4);
        }
        if (fieldName5 != null) {
            queryValues.put(fieldName5, fieldValue5);
        }
        return getCustomTableService().getValues(customTableCode, null, queryValues);
    }

    /**
     * Exposes CustomTableService.getValue() function as EL function. See CustomTableService.getValue() function for documentation.
     * <p>
     * Accepts up to 5 field name/field value combinations
     *
     * @param customTableCode Custom table code
     * @param date            Record validity date, as expressed by 'valid_from' and 'valid_to' fields, to match
     * @param fieldName1      Field (or condition) to query
     * @param fieldValue1     Field search value
     * @param fieldName2      Field (or condition) to query (optional)
     * @param fieldValue2     Field search value (optional)
     * @param fieldName3      Field (or condition) to query (optional)
     * @param fieldValue3     Field search value (optional)
     * @param fieldName4      Field (or condition) to query (optional)
     * @param fieldValue4     Field search value (optional)
     * @param fieldName5      Field (or condition) to query (optional)
     * @param fieldValue5     Field search value (optional)
     * @return A map of field values with field name as a key and field value as a value
     * @throws BusinessException General exception
     */
    public static Map<String, Object> getCTValues(String customTableCode, Date date, String fieldName1, Object fieldValue1, String fieldName2, Object fieldValue2,
            String fieldName3, Object fieldValue3, String fieldName4, Object fieldValue4, String fieldName5, Object fieldValue5) throws BusinessException {

        Map<String, Object> queryValues = new HashMap<>();

        queryValues.put(fieldName1, fieldValue1);

        if (fieldName2 != null) {
            queryValues.put(fieldName2, fieldValue2);
        }
        if (fieldName3 != null) {
            queryValues.put(fieldName3, fieldValue3);
        }
        if (fieldName4 != null) {
            queryValues.put(fieldName4, fieldValue4);
        }
        if (fieldName5 != null) {
            queryValues.put(fieldName5, fieldValue5);
        }
        return getCustomTableService().getValues(customTableCode, null, date, queryValues);
    }

    /**
     * Extract a field value from a referenced entity
     * 
	 * @param entity
	 * @param customTableCode
	 * @param fieldToReturn
	 */
    public static Object getCFRefValue(ICustomFieldEntity entity, String customTableCode, String fieldToReturn) throws BusinessException {
        Object cfValue = getCFValue(entity, customTableCode);
        if (fieldToReturn.contains(".")) {
            String[] fields = fieldToReturn.split("\\.");
            for (String field : fields) {
                cfValue = extractFieldFromRef(cfValue, field);
            }
            return cfValue;
        } else {
            return extractFieldFromRef(cfValue, fieldToReturn);
        }
    }

    private static Object extractFieldFromRef(Object cfValue, String fieldToReturn) {
        if (cfValue instanceof EntityReferenceWrapper) {
            cfValue = getCustomTableService().findEntityFromReference((EntityReferenceWrapper) cfValue);
            if (cfValue instanceof Map) {
                return ((Map) cfValue).get(fieldToReturn.toLowerCase());
            }
        }
        return getFieldOrCFValue(cfValue, fieldToReturn);
    }

    private static Object getFieldOrCFValue(Object obj, String fieldToReturn) {
        Object field = getField(obj, fieldToReturn);
        return field != null ? field : (obj instanceof ICustomFieldEntity) ? getCFValue((ICustomFieldEntity) obj, fieldToReturn) : null;
    }

    private static Object getField(Object iCustomFieldEntity, String fieldToReturn) {
        try {
            if (Arrays.stream(FieldUtils.getAllFields(iCustomFieldEntity.getClass())).anyMatch(f -> f.getName().equals(fieldToReturn))) {
                return FieldUtils.readField(iCustomFieldEntity, fieldToReturn, true);
            }
        } catch (IllegalAccessException e) {
            log.error("Failed to get field " + fieldToReturn + " from Entity " + iCustomFieldEntity, e);
        }
        return null;
    }

    /**
     * Return the counter value of a ICounterEntity UserAccount, BillingAccount, Subscription or ServiceInstance
     *
     * @param entity      the ICounterEntity
     * @param counterCode the counter code
     * @return the counter value.
     */
    public static Object getCounterValue(ICounterEntity entity, String counterCode) {

        return getCounterPeriodService().getCounterValue(entity, counterCode);
    }

    /**
     * Return the counter value of a ICounterEntity UserAccount, BillingAccount, Subscription or ServiceInstance where the startDate<=date<endDate
     *
     * @param entity      the ICounterEntity
     * @param counterCode the counter code
     * @param date the date to be compared to start and end date of a CounterPeriod
     * @return the counter value.
     */
    public static Object getCounterValueByDate(ICounterEntity entity, String counterCode, Date date) {

        return getCounterPeriodService().getCounterValueByDate(entity, counterCode, date);
    }

    public static String getLocalizedDescription(IEntity entity, String lang) {
        String result = "";
        try {
            Method method = entity.getClass().getMethod("getLocalizedDescription", String.class);
            result = (String) method.invoke(entity, lang);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            log.error(exception.getMessage());
        }
        return result;
    }
}