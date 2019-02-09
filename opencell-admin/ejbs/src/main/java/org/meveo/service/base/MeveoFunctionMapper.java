package org.meveo.service.base;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.el.FunctionMapper;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.custom.CustomTableService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides custom functions for Meveo application. The following functions are provided:
 * <ul>
 * <li>mv:getCFValue(&lt;entity&gt;,&lt;cf field code&gt;) - retrieve a custom field value by code for a given entity</li>
 * </ul>
 * 
 * @author Andrius Karpavicius
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
public class MeveoFunctionMapper extends FunctionMapper {
    private Map<String, Method> functionMap = new HashMap<String, Method>();

    private static CustomFieldInstanceService customFieldInstanceService;

    private static ScriptInstanceService scriptInstanceService;

    private static CustomTableService customTableService;

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
            addFunction("mv", "getCFValueByMatrix5Keys", MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class,
                Object.class, Object.class, Object.class));

            addFunction("mv", "getCFValueByMatrixForDate",
                MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class));
            addFunction("mv", "getCFValueByMatrixForDate2Keys",
                MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByMatrixForDate3Keys",
                MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByMatrixForDate4Keys",
                MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByMatrixForDate5Keys", MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class,
                Object.class, Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "getCFValueByKey", MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Object.class));
            addFunction("mv", "getCFValueByKey2Keys", MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByKey3Keys",
                MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByKey4Keys",
                MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByKey5Keys", MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class,
                Object.class, Object.class, Object.class));

            addFunction("mv", "getCFValueByKeyForDate", MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class));
            addFunction("mv", "getCFValueByKeyForDate2Keys",
                MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByKeyForDate3Keys",
                MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByKeyForDate4Keys",
                MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByKeyForDate5Keys", MeveoFunctionMapper.class.getMethod("getCFValueByKey", ICustomFieldEntity.class, String.class, Date.class,
                Object.class, Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "isCFValueHasKey", MeveoFunctionMapper.class.getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class));
            addFunction("mv", "isCFValueHasKey2Keys", MeveoFunctionMapper.class.getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class, Object.class));
            addFunction("mv", "isCFValueHasKey3Keys",
                MeveoFunctionMapper.class.getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class));
            addFunction("mv", "isCFValueHasKey4Keys",
                MeveoFunctionMapper.class.getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "isCFValueHasKey5Keys", MeveoFunctionMapper.class.getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class, Object.class,
                Object.class, Object.class, Object.class));

            addFunction("mv", "isCFValueHasKeyForDate", MeveoFunctionMapper.class.getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class, Object.class));
            addFunction("mv", "isCFValueHasKeyForDate2Keys",
                MeveoFunctionMapper.class.getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class));
            addFunction("mv", "isCFValueHasKeyForDate3Keys",
                MeveoFunctionMapper.class.getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class));
            addFunction("mv", "isCFValueHasKeyForDate4Keys",
                MeveoFunctionMapper.class.getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "isCFValueHasKeyForDate5Keys", MeveoFunctionMapper.class.getMethod("isCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class,
                Object.class, Object.class, Object.class, Object.class, Object.class));

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
            addFunction("mv", "getInheritedCFValueByMatrix4Keys",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrix5Keys", MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class,
                Object.class, Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "getInheritedCFValueByMatrixForDate",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrixForDate2Keys",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrixForDate3Keys",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrixForDate4Keys", MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class,
                Date.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrixForDate5Keys", MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class,
                Date.class, Object.class, Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "getInheritedCFValueByKey", MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Object.class));
            addFunction("mv", "getInheritedCFValueByKey2Keys",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByKey3Keys",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByKey4Keys",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByKey5Keys", MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Object.class,
                Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "getInheritedCFValueByKeyForDate",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class));
            addFunction("mv", "getInheritedCFValueByKeyForDate2Keys",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByKeyForDate3Keys",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByKeyForDate4Keys", MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class,
                Date.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByKeyForDate5Keys", MeveoFunctionMapper.class.getMethod("getInheritedCFValueByKey", ICustomFieldEntity.class, String.class,
                Date.class, Object.class, Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "isInheritedCFValueHasKey", MeveoFunctionMapper.class.getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class));
            addFunction("mv", "isInheritedCFValueHasKey2Keys",
                MeveoFunctionMapper.class.getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class, Object.class));
            addFunction("mv", "isInheritedCFValueHasKey3Keys",
                MeveoFunctionMapper.class.getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class));
            addFunction("mv", "isInheritedCFValueHasKey4Keys",
                MeveoFunctionMapper.class.getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "isInheritedCFValueHasKey5Keys", MeveoFunctionMapper.class.getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Object.class,
                Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "isInheritedCFValueHasKeyForDate",
                MeveoFunctionMapper.class.getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class, Object.class));
            addFunction("mv", "isInheritedCFValueHasKeyForDate2Keys",
                MeveoFunctionMapper.class.getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class));
            addFunction("mv", "isInheritedCFValueHasKeyForDate3Keys",
                MeveoFunctionMapper.class.getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class));
            addFunction("mv", "isInheritedCFValueHasKeyForDate4Keys", MeveoFunctionMapper.class.getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class,
                Date.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "isInheritedCFValueHasKeyForDate5Keys", MeveoFunctionMapper.class.getMethod("isInheritedCFValueHasKey", ICustomFieldEntity.class, String.class,
                Date.class, Object.class, Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "executeScript", MeveoFunctionMapper.class.getMethod("executeScript", IEntity.class, String.class, String.class));

            addFunction("mv", "now", MeveoFunctionMapper.class.getMethod("getNowTimestamp"));

            addFunction("mv", "formatDate", MeveoFunctionMapper.class.getMethod("formatDate", Date.class, String.class));

            addFunction("mv", "parseDate", MeveoFunctionMapper.class.getMethod("parseDate", String.class, String.class));

            addFunction("mv", "getDate", MeveoFunctionMapper.class.getMethod("getDate", Long.class));

            addFunction("mv", "getBean", EjbUtils.class.getMethod("getServiceInterface", String.class));

            addFunction("mv", "addToDate", MeveoFunctionMapper.class.getMethod("addToDate", Date.class, Long.class, Long.class));

            addFunction("mv", "getEndOfMonth", MeveoFunctionMapper.class.getMethod("getEndOfMonth", Date.class));

            addFunction("mv", "getStartOfNextMonth", MeveoFunctionMapper.class.getMethod("getStartOfNextMonth", Date.class));

            addFunction("mv", "getCTValue", MeveoFunctionMapper.class.getMethod("getCTValue", String.class, String.class, String.class, Object.class, String.class, Object.class,
                String.class, Object.class, String.class, Object.class, String.class, Object.class));

            addFunction("mv", "getCTValues", MeveoFunctionMapper.class.getMethod("getCTValues", String.class, String.class, Object.class, String.class, Object.class, String.class,
                Object.class, String.class, Object.class, String.class, Object.class));

            addFunction("mv", "getCTValueForDate", MeveoFunctionMapper.class.getMethod("getCTValue", String.class, String.class, Date.class, String.class, Object.class,
                String.class, Object.class, String.class, Object.class, String.class, Object.class, String.class, Object.class));

            addFunction("mv", "getCTValuesForDate", MeveoFunctionMapper.class.getMethod("getCTValues", String.class, Date.class, String.class, Object.class, String.class,
                Object.class, String.class, Object.class, String.class, Object.class, String.class, Object.class));

            // addFunction("mv", "call", MeveoFunctionMapper.class.getMethod("call", String.class, String.class,String.class, Object[].class));
        } catch (NoSuchMethodException | SecurityException e) {
            Logger log = LoggerFactory.getLogger(this.getClass());
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

    @SuppressWarnings("unchecked")
    private static CustomFieldInstanceService getCustomFieldInstanceService() {

        if (customFieldInstanceService == null) {
            try {
                InitialContext initialContext = new InitialContext();
                BeanManager beanManager = (BeanManager) initialContext.lookup("java:comp/BeanManager");

                Bean<CustomFieldInstanceService> bean = (Bean<CustomFieldInstanceService>) beanManager.resolve(beanManager.getBeans(CustomFieldInstanceService.class));
                customFieldInstanceService = (CustomFieldInstanceService) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));

            } catch (NamingException e) {
                Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
                log.error("Unable to access CustomFieldInstanceService", e);
                throw new RuntimeException(e);
            }
        }
        return customFieldInstanceService;
    }

    @SuppressWarnings("unchecked")
    private static ScriptInstanceService getScriptInstanceService() {

        if (scriptInstanceService == null) {
            try {
                InitialContext initialContext = new InitialContext();
                BeanManager beanManager = (BeanManager) initialContext.lookup("java:comp/BeanManager");

                Bean<ScriptInstanceService> bean = (Bean<ScriptInstanceService>) beanManager.resolve(beanManager.getBeans(ScriptInstanceService.class));
                scriptInstanceService = (ScriptInstanceService) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));

            } catch (NamingException e) {
                Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
                log.error("Unable to access ScriptInstanceService", e);
                throw new RuntimeException(e);
            }
        }
        return scriptInstanceService;
    }

    @SuppressWarnings("unchecked")
    private static CustomTableService getCustomTableService() {

        if (customTableService == null) {
            try {
                InitialContext initialContext = new InitialContext();
                BeanManager beanManager = (BeanManager) initialContext.lookup("java:comp/BeanManager");

                Bean<CustomTableService> bean = (Bean<CustomTableService>) beanManager.resolve(beanManager.getBeans(CustomTableService.class));
                customTableService = (CustomTableService) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));

            } catch (NamingException e) {
                Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
                log.error("Unable to access CustomTableService", e);
                throw new RuntimeException(e);
            }
        }
        return customTableService;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValue() function as EL function. See CustomFieldInstanceService.getCFValue() function for documentation
     * 
     * @param entity entity to get infos
     * @param code code of entity
     * @return cf value.
     */
    public static Object getCFValue(ICustomFieldEntity entity, String code) {

        Object cfValue = getCustomFieldInstanceService().getCFValue(entity, code);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} for {}/{}", cfValue, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValue() function as EL function. See CustomFieldInstanceService.getCFValue() function for documentation
     * 
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check
     * @return cf value.
     */
    public static Object getCFValue(ICustomFieldEntity entity, String code, Date date) {

        Object cfValue = getCustomFieldInstanceService().getCFValue(entity, code, date);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} for {}/{} for {}", cfValue, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByClosestMatch() function as EL function. See CustomFieldInstanceService.getCFValueByClosestMatch() function for documentation
     * 
     * @param entity entity to get infos
     * @param code code of entity
     * @param keyToMatch jey to match.
     * @return cf value.
     */
    public static Object getCFValueByClosestMatch(ICustomFieldEntity entity, String code, String keyToMatch) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByClosestMatch(entity, code, keyToMatch);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by closest match for key {} for {}/{}", cfValue, keyToMatch, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByClosestMatch() function as EL function. See CustomFieldInstanceService.getCFValueByClosestMatch() function for documentation
     * 
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check.
     * @param keyToMatch jey to match.
     * @return cf value
     */
    public static Object getCFValueByClosestMatch(ICustomFieldEntity entity, String code, Date date, String keyToMatch) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByClosestMatch(entity, code, date, keyToMatch);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by closest match for key {} for {}/{} for {}", cfValue, keyToMatch, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByRangeOfNumbers() function as EL function. See CustomFieldInstanceService.getCFValueByRangeOfNumbers() function for
     * documentation
     * 
     * @param entity entity to get infos
     * @param code code of entity
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
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by range of numbers for number {} for {}/{}", cfValue, numberToMatch, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByRangeOfNumbers() function as EL function. See CustomFieldInstanceService.getCFValueByRangeOfNumbers() function for
     * documentation
     * 
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check.
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
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by range of numbers for number {} for {}/{} for {}", cfValue, numberToMatch, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     * 
     * @param entity Entity to find CF value for
     * @param code Custom field code
     * @param concatenatedKeysOrSingleKey Keys concatenated by "|" sign or a single key
     * @return cfValue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Object concatenatedKeysOrSingleKey) {

        if (concatenatedKeysOrSingleKey == null || (concatenatedKeysOrSingleKey instanceof String && StringUtils.isBlank((String) concatenatedKeysOrSingleKey))) {
            return null;
        }

        Object cfValue = null;

        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);

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
     * @param code code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @return cfValue
     *
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, keyOne, keyTwo);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by key for keys {}/{} for {}/{}", cfValue, keyOne, keyTwo, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @return cfvalue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, keyOne, keyTwo, keyThree);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by key for keys {}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @param keyFour key four.
     * @return cfvalue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, keyOne, keyTwo, keyThree, keyFour);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by key for keys {}/{}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, keyFour, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @param keyFour key four.
     * @param keyFive key five.
     * @return cfvalue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour, Object keyFive) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, keyOne, keyTwo, keyThree, keyFour, keyFive);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by key for keys {}/{}/{}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     * 
     * @param entity Entity to find CF value for
     * @param code Custom field code
     * @param date Date Value date
     * @param concatenatedKeysOrSingleKey Keys concatenated by "|" sign or a single key
     * @return cfValue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object concatenatedKeysOrSingleKey) {

        if (concatenatedKeysOrSingleKey == null || (concatenatedKeysOrSingleKey instanceof String && StringUtils.isBlank((String) concatenatedKeysOrSingleKey))) {
            return null;
        }

        Object cfValue = null;

        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);

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
     * @param code code of entity
     * @param date date to check.
     * @param keyOne key one
     * @param keyTwo key two
     * @return cfvalue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, date, keyOne, keyTwo);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by key for keys {}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check.
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @return cfvalue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, date, keyOne, keyTwo, keyThree);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by key for keys {}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check.
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @param keyFour key four.
     * @return cfvalue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, date, keyOne, keyTwo, keyThree, keyFour);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by key for keys {}/{}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, keyFour, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByKey() function as EL function. See CustomFieldInstanceService.getCFValueByKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check.
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @param keyFour key four.
     * @param keyFive key five.
     * @return cfvalue
     */
    public static Object getCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour, Object keyFive) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByKey(entity, code, date, keyOne, keyTwo, keyThree, keyFour, keyFive);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by key for keys {}/{}/{}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValue() function as EL function. See CustomFieldInstanceService.getInheritedCFValue() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @return cfvalue
     */
    public static Object getInheritedCFValue(ICustomFieldEntity entity, String code) {

        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        // log.debug("AKK start getInheritedCFvalue for entity {}", entity.getClass());
        Object cfValue = getCustomFieldInstanceService().getInheritedCFValue(entity, code);
        log.trace("Obtained inherited CF value {} for {}/{}", cfValue, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValue() function as EL function. See CustomFieldInstanceService.getInheritedCFValue() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check.
     * @return cfvalue
     */
    public static Object getInheritedCFValue(ICustomFieldEntity entity, String code, Date date) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValue(entity, code, date);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} for {}/{} for {}", cfValue, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByClosestMatch() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByClosestMatch() function
     * for documentation
     * 
     * @param entity entity to get infos
     * @param code code of entity
     * @param keyToMatch key to match.
     * @return cfvalue
     */
    public static Object getInheritedCFValueByClosestMatch(ICustomFieldEntity entity, String code, String keyToMatch) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByClosestMatch(entity, code, keyToMatch);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by closest match for key {} for {}/{}", cfValue, keyToMatch, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByClosestMatch() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByClosestMatch() function
     * for documentation.
     * 
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check.
     * @param keyToMatch key to match.
     * @return cfvalue
     */
    public static Object getInheritedCFValueByClosestMatch(ICustomFieldEntity entity, String code, Date date, String keyToMatch) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByClosestMatch(entity, code, date, keyToMatch);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by closest match for key {} for {}/{} for {}", cfValue, keyToMatch, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByRangeOfNumbers() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByRangeOfNumbers()
     * function for documentation
     * 
     * @param entity entity to get infos
     * @param code code of entity
     * @param numberToMatch number to match.
     * @return cfvalue
     * 
     */
    public static Object getInheritedCFValueByRangeOfNumbers(ICustomFieldEntity entity, String code, Object numberToMatch) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByRangeOfNumbers(entity, code, numberToMatch);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by range of numbers for number {} for {}/{}", cfValue, numberToMatch, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByRangeOfNumbers() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByRangeOfNumbers()
     * function for documentation.
     * 
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check.
     * @param numberToMatch number to match.
     * @return cfvalue
     * 
     */
    public static Object getInheritedCFValueByRangeOfNumbers(ICustomFieldEntity entity, String code, Date date, Object numberToMatch) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByRangeOfNumbers(entity, code, date, numberToMatch);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by range of numbers for number {} for {}/{} for {}", cfValue, numberToMatch, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByMatrix() function for
     * documentation
     * 
     * @param entity Entity to find CF value for
     * @param code Custom field code
     * @param concatenatedKeysOrSingleKey Keys concatenated by "|" sign or a single key
     * @return cfValue.
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Object concatenatedKeysOrSingleKey) {

        if (concatenatedKeysOrSingleKey == null || (concatenatedKeysOrSingleKey instanceof String && StringUtils.isBlank((String) concatenatedKeysOrSingleKey))) {
            return null;
        }

        Object cfValue = null;

        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);

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
     * @param code code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @return cfvalue
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, keyOne, keyTwo);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by key for keys {}/{} for {}/{}", cfValue, keyOne, keyTwo, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByKey() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByKey() function for documentation
     * 
     * @param entity entity to get infos
     * @param code code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @return cfvalue
     *
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, keyOne, keyTwo, keyThree);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by key for keys {}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByKey() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @param keyFour key four.
     * @return cfvalue
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, keyOne, keyTwo, keyThree, keyFour);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by key for keys {}/{}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, keyFour, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByKey() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @param keyFour key four.
     * @param keyFive key five.
     * @return cfvalue
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour, Object keyFive) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, keyOne, keyTwo, keyThree, keyFour, keyFive);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by key for keys {}/{}/{}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByKey() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByKey() function for documentation
     * 
     * @param entity Entity to find CF value for
     * @param code Custom field code
     * @param date Date Value date
     * @param concatenatedKeysOrSingleKey Keys concatenated by "|" sign
     * @return cfValue.
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object concatenatedKeysOrSingleKey) {

        if (concatenatedKeysOrSingleKey == null || (concatenatedKeysOrSingleKey instanceof String && StringUtils.isBlank((String) concatenatedKeysOrSingleKey))) {
            return null;
        }

        Object cfValue = null;

        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);

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
     * @param code code of entity
     * @param date date to check.
     * @param keyOne key one
     * @param keyTwo key two
     * @return cfvalue
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, date, keyOne, keyTwo);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by key for keys {}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByKey() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check.
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @return cfvalue
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, date, keyOne, keyTwo, keyThree);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by key for keys {}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByKey() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByKey() function for documentation
     * 
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check.
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @param keyFour key four.
     * @return cfvalue.
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, date, keyOne, keyTwo, keyThree, keyFour);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by key for keys {}/{}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, keyFour, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByKey() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByKey() function for documentation
     * 
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check
     * @param keyOne key of CF.
     * @param keyTwo key of CF.
     * @param keyThree key three
     * @param keyFour key four.
     * @param keyFive key five.
     * @return cf value
     */
    public static Object getInheritedCFValueByKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour,
            Object keyFive) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByKey(entity, code, date, keyOne, keyTwo, keyThree, keyFour, keyFive);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by key for keys {}/{}/{}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code, date);

        return cfValue;
    }

    /**
     * Execute script on an entity.
     * 
     * @param entity Entity to execute action on
     * @param scriptCode Script to execute, identified by a code
     * @param encodedParameters Additional parameters encoded in URL like style param=value&amp;param=value
     * @return A script execution result value
     */
    public static Object executeScript(IEntity entity, String scriptCode, String encodedParameters) {

        Map<String, Object> result = null;

        try {
            try {
                result = getScriptInstanceService().execute(entity, scriptCode, encodedParameters);
            } catch (ElementNotFoundException enf) {
                result = null;
            }

        } catch (BusinessException e) {
            Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
            log.error("Failed to execute a script {} on entity {}", scriptCode, entity, e);
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
     * @param date date to be formatted.
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
     * @param dateString Date string
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
     * @param entity Entity to check CF value for
     * @param code Custom field code
     * @param concatenatedKeysOrSingleKey Keys concatenated by "|" sign or a single key
     * @return true if cfValue has key.
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Object concatenatedKeysOrSingleKey) {

        if (concatenatedKeysOrSingleKey == null || (concatenatedKeysOrSingleKey instanceof String && StringUtils.isBlank((String) concatenatedKeysOrSingleKey))) {
            return false;
        }

        boolean hasKey = false;

        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);

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
     * @param code code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @return true if cfvalue has key
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo) {

        boolean hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, keyOne, keyTwo);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("CF value has {} key for keys {}/{} for {}/{}", hasKey, keyOne, keyTwo, entity, code);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isCFValueHasKey() function as EL function. See CustomFieldInstanceService.isCFValueHasKey() function for documentation
     * 
     * @param entity entity to get infos
     * @param code code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @return true if cfvalue has key.
     * 
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree) {

        boolean hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, keyOne, keyTwo, keyThree);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("CF value has {} key for keys {}/{}/{} for {}/{}", hasKey, keyOne, keyTwo, keyThree, entity, code);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isCFValueHasKey() function as EL function. See CustomFieldInstanceService.isCFValueHasKey() function for documentation
     * 
     * @param entity entity to get infos
     * @param code code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @param keyFour key four
     * @return true if cfvalue has key.
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        boolean hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, keyOne, keyTwo, keyThree, keyFour);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("CF value has {} key for keys {}/{}/{}/{} for {}/{}", hasKey, keyOne, keyTwo, keyThree, keyFour, entity, code);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isCFValueHasKey() function as EL function. See CustomFieldInstanceService.isCFValueHasKey() function for documentation
     * 
     * @param entity entity to get infos
     * @param code code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @param keyFour key four
     * @param keyFive key five.
     * @return true if cfValue has key.
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour, Object keyFive) {

        boolean hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, keyOne, keyTwo, keyThree, keyFour, keyFive);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("CF value has {} key for keys {}/{}/{}/{}/{} for {}/{}", hasKey, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isCFValueHasKey() function as EL function. See CustomFieldInstanceService.isCFValueHasKey() function for documentation
     * 
     * @param entity Entity to check CF value for
     * @param code Custom field code
     * @param date Date
     * @param concatenatedKeysOrSingleKey Keys concatenated by "|" sign or a single key
     * @return true if cfValue has key.
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object concatenatedKeysOrSingleKey) {

        if (concatenatedKeysOrSingleKey == null || (concatenatedKeysOrSingleKey instanceof String && StringUtils.isBlank((String) concatenatedKeysOrSingleKey))) {
            return false;
        }

        boolean hasKey = false;

        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);

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
     * @param code code of entity
     * @param date date to check.
     * @param keyOne key one
     * @param keyTwo key two
     * @return true if cfvalue has key.
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo) {

        boolean hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, date, keyOne, keyTwo);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("CF value has {} key for keys {}/{} for {}/{} for {}", hasKey, keyOne, keyTwo, entity, code, date);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isCFValueHasKey() function as EL function. See CustomFieldInstanceService.isCFValueHasKey() function for documentation
     * 
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @return true if cfvalue has key.
     *
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree) {

        boolean hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, date, keyOne, keyTwo, keyThree);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("CF value has {} key for keys {}/{}/{} for {}/{} for {}", hasKey, keyOne, keyTwo, keyThree, entity, code, date);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isCFValueHasKey() function as EL function. See CustomFieldInstanceService.isCFValueHasKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @param keyFour key four
     * @return true if cfvalue has key
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        boolean hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, date, keyOne, keyTwo, keyThree, keyFour);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("CF value has {} key for keys {}/{}/{}/{} for {}/{} for {}", hasKey, keyOne, keyTwo, keyThree, keyFour, entity, code, date);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isCFValueHasKey() function as EL function. See CustomFieldInstanceService.isCFValueHasKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @param keyFour key four
     * @param keyFive key five
     * @return true if cfvalue has key
     */
    public static boolean isCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour, Object keyFive) {

        boolean hasKey = getCustomFieldInstanceService().isCFValueHasKey(entity, code, date, keyOne, keyTwo, keyThree, keyFour, keyFive);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("CF value has {} key for keys {}/{}/{}/{}/{} for {}/{} for {}", hasKey, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code, date);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     * 
     * @param entity Entity to check CF value for
     * @param code Custom field code
     * @param concatenatedKeysOrSingleKey Keys concatenated by "|" sign or a single key
     * @return true if cfVaue has key.
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Object concatenatedKeysOrSingleKey) {

        if (concatenatedKeysOrSingleKey == null || (concatenatedKeysOrSingleKey instanceof String && StringUtils.isBlank((String) concatenatedKeysOrSingleKey))) {
            return false;
        }

        boolean hasKey = false;

        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);

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
     * @param code code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @return true if cfvalue has key
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo) {

        boolean hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, keyOne, keyTwo);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Inherited CF value has {} key for keys {}/{} for {}/{}", hasKey, keyOne, keyTwo, entity, code);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @return true if cfvalue has key
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree) {

        boolean hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, keyOne, keyTwo, keyThree);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Inherited CF value has {} key for keys {}/{}/{} for {}/{}", hasKey, keyOne, keyTwo, keyThree, entity, code);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     * 
     * @param entity entity to get infos.
     * @param code code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @param keyFour key four.
     * @return true if cfvalue has key
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        boolean hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, keyOne, keyTwo, keyThree, keyFour);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Inherited CF value has {} key for keys {}/{}/{}/{} for {}/{}", hasKey, keyOne, keyTwo, keyThree, keyFour, entity, code);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @param keyFour key four
     * @param keyFive key five.
     * @return true if cfvalue has key
     *
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour, Object keyFive) {

        boolean hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, keyOne, keyTwo, keyThree, keyFour, keyFive);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Inherited CF value has {} key for keys {}/{}/{}/{}/{} for {}/{}", hasKey, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     * 
     * @param entity Entity to check CF value for
     * @param code Custom field code
     * @param date Date
     * @param concatenatedKeysOrSingleKey Keys concatenated by "|" sign or a single key
     * @return true if cfValue has key.
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object concatenatedKeysOrSingleKey) {

        if (concatenatedKeysOrSingleKey == null || (concatenatedKeysOrSingleKey instanceof String && StringUtils.isBlank((String) concatenatedKeysOrSingleKey))) {
            return false;
        }

        boolean hasKey = false;

        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);

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
     * @param code code of entity
     * @param date date to check
     * @param keyOne key one
     * @param keyTwo key two
     * @return true if cfvalue has key
     *
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo) {

        boolean hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, date, keyOne, keyTwo);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Inherited CF value has {} key for keys {}/{} for {}/{} for {}", hasKey, keyOne, keyTwo, entity, code, date);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @return true if cfvalue has key
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree) {

        boolean hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, date, keyOne, keyTwo, keyThree);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Inherited CF value has {} key for keys {}/{}/{} for {}/{} for {}", hasKey, keyOne, keyTwo, keyThree, entity, code, date);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @param keyFour key four.
     * @return true if cfvalue has key
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        boolean hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, date, keyOne, keyTwo, keyThree, keyFour);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Inherited CF value has {} key for keys {}/{}/{}/{} for {}/{} for {}", hasKey, keyOne, keyTwo, keyThree, keyFour, entity, code, date);

        return hasKey;
    }

    /**
     * Exposes CustomFieldInstanceService.isInheritedCFValueHasKey() function as EL function. See CustomFieldInstanceService.isInheritedCFValueHasKey() function for documentation
     *
     * @param entity entity to get infos
     * @param code code of entity
     * @param date date to check
     * @param keyOne key one
     * @param keyTwo key two
     * @param keyThree key three.
     * @param keyFour key four.
     * @param keyFive key five.
     * @return true if cfvalue has key
     */
    public static boolean isInheritedCFValueHasKey(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour,
            Object keyFive) {

        boolean hasKey = getCustomFieldInstanceService().isInheritedCFValueHasKey(entity, code, date, keyOne, keyTwo, keyThree, keyFour, keyFive);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Inherited CF value has {} key for keys {}/{}/{}/{}/{} for {}/{} for {}", hasKey, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code, date);

        return hasKey;
    }

    /**
     * Adds or subtracts duration to the given date.
     * 
     * @param date date to be added.
     * @param durationType The same value as java.util.Calendar constants : 5 for day, 2 for month,...
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

    public static Date getStartOfNextMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, 1);

        return c.getTime();
    }

    /**
     * Exposes CustomTableService.getValue() function as EL function. See CustomTableService.getValue() function for documentation.
     * 
     * Accepts up to 5 field name/field value combinations
     * 
     * @param customTableCode Custom table code
     * @param fieldToReturn Field value to return
     * @param fieldName1 Field (or condition) to query
     * @param fieldValue1 Field search value
     * @param fieldName2 Field (or condition) to query (optional)
     * @param fieldValue2 Field search value (optional)
     * @param fieldName3 Field (or condition) to query (optional)
     * @param fieldValue3 Field search value (optional)
     * @param fieldName4 Field (or condition) to query (optional)
     * @param fieldValue4 Field search value (optional)
     * @param fieldName5 Field (or condition) to query (optional)
     * @param fieldValue5 Field search value (optional)
     * @return A field value
     * @throws BusinessException General exception
     */
    public static Object getCTValue(String customTableCode, String fieldToReturn, String fieldName1, Object fieldValue1, String fieldName2, Object fieldValue2, String fieldName3,
            Object fieldValue3, String fieldName4, Object fieldValue4, String fieldName5, Object fieldValue5) throws BusinessException {

        if (fieldName2 == null) {
            return getCustomTableService().getValue(customTableCode, fieldToReturn, fieldName1, fieldValue1);
        } else if (fieldName3 == null) {
            return getCustomTableService().getValue(customTableCode, fieldToReturn, fieldName1, fieldValue1, fieldName2, fieldValue2);
        } else if (fieldName4 == null) {
            return getCustomTableService().getValue(customTableCode, fieldToReturn, fieldName1, fieldValue1, fieldName2, fieldValue2, fieldName3, fieldValue3);
        } else if (fieldName5 == null) {
            return getCustomTableService().getValue(customTableCode, fieldToReturn, fieldName1, fieldValue1, fieldName2, fieldValue2, fieldName3, fieldValue3, fieldName4,
                fieldValue4);
        } else {
            return getCustomTableService().getValue(customTableCode, fieldToReturn, fieldName1, fieldValue1, fieldName2, fieldValue2, fieldName3, fieldValue3, fieldName4,
                fieldValue4, fieldName5, fieldValue5);
        }
    }

    /**
     * Exposes CustomTableService.getValue() function as EL function. See CustomTableService.getValue() function for documentation.
     * 
     * Accepts up to 5 field name/field value combinations
     * 
     * @param customTableCode Custom table code
     * @param fieldToReturn Field value to return
     * @param date Record validity date, as expressed by 'valid_from' and 'valid_to' fields, to match
     * @param fieldName1 Field (or condition) to query
     * @param fieldValue1 Field search value
     * @param fieldName2 Field (or condition) to query (optional)
     * @param fieldValue2 Field search value (optional)
     * @param fieldName3 Field (or condition) to query (optional)
     * @param fieldValue3 Field search value (optional)
     * @param fieldName4 Field (or condition) to query (optional)
     * @param fieldValue4 Field search value (optional)
     * @param fieldName5 Field (or condition) to query (optional)
     * @param fieldValue5 Field search value (optional)
     * @return A field value
     * @throws BusinessException General exception
     */
    public static Object getCTValue(String customTableCode, String fieldToReturn, Date date, String fieldName1, Object fieldValue1, String fieldName2, Object fieldValue2,
            String fieldName3, Object fieldValue3, String fieldName4, Object fieldValue4, String fieldName5, Object fieldValue5) throws BusinessException {

        if (fieldName2 == null) {
            return getCustomTableService().getValue(customTableCode, fieldToReturn, date, fieldName1, fieldValue1);
        } else if (fieldName3 == null) {
            return getCustomTableService().getValue(customTableCode, fieldToReturn, date, fieldName1, fieldValue1, fieldName2, fieldValue2);
        } else if (fieldName4 == null) {
            return getCustomTableService().getValue(customTableCode, fieldToReturn, date, fieldName1, fieldValue1, fieldName2, fieldValue2, fieldName3, fieldValue3);
        } else if (fieldName5 == null) {
            return getCustomTableService().getValue(customTableCode, fieldToReturn, date, fieldName1, fieldValue1, fieldName2, fieldValue2, fieldName3, fieldValue3, fieldName4,
                fieldValue4);
        } else {
            return getCustomTableService().getValue(customTableCode, fieldToReturn, date, fieldName1, fieldValue1, fieldName2, fieldValue2, fieldName3, fieldValue3, fieldName4,
                fieldValue4, fieldName5, fieldValue5);
        }
    }

    /**
     * Exposes CustomTableService.getValue() function as EL function. See CustomTableService.getValue() function for documentation.
     * 
     * Accepts up to 5 field name/field value combinations
     * 
     * @param customTableCode Custom table code
     * @param fieldName1 Field (or condition) to query
     * @param fieldValue1 Field search value
     * @param fieldName2 Field (or condition) to query (optional)
     * @param fieldValue2 Field search value (optional)
     * @param fieldName3 Field (or condition) to query (optional)
     * @param fieldValue3 Field search value (optional)
     * @param fieldName4 Field (or condition) to query (optional)
     * @param fieldValue4 Field search value (optional)
     * @param fieldName5 Field (or condition) to query (optional)
     * @param fieldValue5 Field search value (optional)
     * @return A map of field values with field name as a key and field value as a value
     * @throws BusinessException General exception
     */
    public static Map<String, Object> getCTValues(String customTableCode, String fieldName1, Object fieldValue1, String fieldName2, Object fieldValue2, String fieldName3,
            Object fieldValue3, String fieldName4, Object fieldValue4, String fieldName5, Object fieldValue5) throws BusinessException {

        if (fieldName2 == null) {
            return getCustomTableService().getValues(customTableCode, fieldName1, fieldValue1);
        } else if (fieldName3 == null) {
            return getCustomTableService().getValues(customTableCode, fieldName1, fieldValue1, fieldName2, fieldValue2);
        } else if (fieldName4 == null) {
            return getCustomTableService().getValues(customTableCode, fieldName1, fieldValue1, fieldName2, fieldValue2, fieldName3, fieldValue3);
        } else if (fieldName5 == null) {
            return getCustomTableService().getValues(customTableCode, fieldName1, fieldValue1, fieldName2, fieldValue2, fieldName3, fieldValue3, fieldName4, fieldValue4);
        } else {
            return getCustomTableService().getValues(customTableCode, fieldName1, fieldValue1, fieldName2, fieldValue2, fieldName3, fieldValue3, fieldName4, fieldValue4,
                fieldName5, fieldValue5);
        }
    }

    /**
     * Exposes CustomTableService.getValue() function as EL function. See CustomTableService.getValue() function for documentation.
     * 
     * Accepts up to 5 field name/field value combinations
     * 
     * @param customTableCode Custom table code
     * @param date Record validity date, as expressed by 'valid_from' and 'valid_to' fields, to match
     * @param fieldName1 Field (or condition) to query
     * @param fieldValue1 Field search value
     * @param fieldName2 Field (or condition) to query (optional)
     * @param fieldValue2 Field search value (optional)
     * @param fieldName3 Field (or condition) to query (optional)
     * @param fieldValue3 Field search value (optional)
     * @param fieldName4 Field (or condition) to query (optional)
     * @param fieldValue4 Field search value (optional)
     * @param fieldName5 Field (or condition) to query (optional)
     * @param fieldValue5 Field search value (optional)
     * @return A map of field values with field name as a key and field value as a value
     * @throws BusinessException General exception
     */
    public static Map<String, Object> getCTValues(String customTableCode, Date date, String fieldName1, Object fieldValue1, String fieldName2, Object fieldValue2,
            String fieldName3, Object fieldValue3, String fieldName4, Object fieldValue4, String fieldName5, Object fieldValue5) throws BusinessException {

        if (fieldName2 == null) {
            return getCustomTableService().getValues(customTableCode, date, fieldName1, fieldValue1);
        } else if (fieldName3 == null) {
            return getCustomTableService().getValues(customTableCode, date, fieldName1, fieldValue1, fieldName2, fieldValue2);
        } else if (fieldName4 == null) {
            return getCustomTableService().getValues(customTableCode, date, fieldName1, fieldValue1, fieldName2, fieldValue2, fieldName3, fieldValue3);
        } else if (fieldName5 == null) {
            return getCustomTableService().getValues(customTableCode, date, fieldName1, fieldValue1, fieldName2, fieldValue2, fieldName3, fieldValue3, fieldName4, fieldValue4);
        } else {
            return getCustomTableService().getValues(customTableCode, date, fieldName1, fieldValue1, fieldName2, fieldValue2, fieldName3, fieldValue3, fieldName4, fieldValue4,
                fieldName5, fieldValue5);
        }
    }
}