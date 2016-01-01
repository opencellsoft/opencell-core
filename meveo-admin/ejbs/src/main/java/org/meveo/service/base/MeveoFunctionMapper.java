package org.meveo.service.base;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.el.FunctionMapper;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.meveo.model.ICustomFieldEntity;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides custom functions for Meveo application. The following functions are provided:
 * <ul>
 * <li>mv:getCFValue(<entity>,<cf field code>) - retrieve a custom field value by code for a given entity</li>
 * </ul>
 * 
 * @author Andrius Karpavicius
 * 
 */
public class MeveoFunctionMapper extends FunctionMapper {
    private Map<String, Method> functionMap = new HashMap<String, Method>();

    private static CustomFieldInstanceService customFieldInstanceService;
    Logger log = LoggerFactory.getLogger(this.getClass());

    public MeveoFunctionMapper() {

        super();

        try {
            addFunction("mv", "getCFValue", MeveoFunctionMapper.class.getMethod("getCFValue", ICustomFieldEntity.class, String.class));
            addFunction("mv", "getCFValueForDate", MeveoFunctionMapper.class.getMethod("getCFValueForDate", ICustomFieldEntity.class, String.class, Date.class));
            addFunction("mv", "getInheritedCFValue", MeveoFunctionMapper.class.getMethod("getInheritedCFValue", ICustomFieldEntity.class, String.class));
            addFunction("mv", "getInheritedCFValueForDate", MeveoFunctionMapper.class.getMethod("getInheritedCFValueForDate", ICustomFieldEntity.class, String.class, Date.class));
            addFunction("mv", "getCFValueByClosestMatch", MeveoFunctionMapper.class.getMethod("getCFValueByClosestMatch", ICustomFieldEntity.class, String.class, String.class));
            addFunction("mv", "getCFValueByClosestMatchForDate",
                MeveoFunctionMapper.class.getMethod("getCFValueByClosestMatchForDate", ICustomFieldEntity.class, String.class, Date.class, String.class));
            addFunction("mv", "getCFValueByRangeOfNumbers", MeveoFunctionMapper.class.getMethod("getCFValueByRangeOfNumbers", ICustomFieldEntity.class, String.class, Object.class));
            addFunction("mv", "getCFValueByRangeOfNumbersForDate",
                MeveoFunctionMapper.class.getMethod("getCFValueByRangeOfNumbersForDate", ICustomFieldEntity.class, String.class, Date.class, Object.class));
            addFunction("mv", "getCFValueByMatrix", MeveoFunctionMapper.class.getMethod("getCFValueByMatrix", ICustomFieldEntity.class, String.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByMatrixForDate",
                MeveoFunctionMapper.class.getMethod("getCFValueByMatrixForDate", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class));

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

    /**
     * Exposes CustomFieldInstanceService.getCFValue() function as EL function. See CustomFieldInstanceService.getCFValue() function for documentation
     */
    public static Object getCFValue(ICustomFieldEntity entity, String code) {

        Object cfValue = getCustomFieldInstanceService().getCFValue(entity, code, null);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} for {}/{}", cfValue, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValue() function as EL function. See CustomFieldInstanceService.getCFValue() function for documentation
     */
    public static Object getCFValueForDate(ICustomFieldEntity entity, String code, Date date) {

        Object cfValue = getCustomFieldInstanceService().getCFValue(entity, code, date, null);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} for {}/{} for {}", cfValue, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValue() function as EL function. See CustomFieldInstanceService.getInheritedCFValue() function for documentation
     */
    public static Object getInheritedCFValue(ICustomFieldEntity entity, String code) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValue(entity, code, null);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} for {}/{}", cfValue, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValue() function as EL function. See CustomFieldInstanceService.getInheritedCFValue() function for documentation
     */
    public static Object getInheritedCFValueForDate(ICustomFieldEntity entity, String code, Date date) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValue(entity, code, date, null);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} for {}/{} for {}", cfValue, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByClosestMatch() function as EL function. See CustomFieldInstanceService.getCFValueByClosestMatch() function for documentation
     */
    public static Object getCFValueByClosestMatch(ICustomFieldEntity entity, String code, String keyToMatch) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByClosestMatch(entity, code, keyToMatch);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by closest match for key {} for {}/{}", cfValue, keyToMatch, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByClosestMatch() function as EL function. See CustomFieldInstanceService.getCFValueByClosestMatch() function for documentation
     */
    public static Object getCFValueByClosestMatchForDate(ICustomFieldEntity entity, String code, Date date, String keyToMatch) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByClosestMatch(entity, code, date, keyToMatch);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by closest match for key {} for {}/{} for {}", cfValue, keyToMatch, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByRangeOfNumbers() function as EL function. See CustomFieldInstanceService.getCFValueByRangeOfNumbers() function for
     * documentation
     */
    public static Object getCFValueByRangeOfNumbers(ICustomFieldEntity entity, String code, Object numberToMatch) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByRangeOfNumbers(entity, code, numberToMatch);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by range of numbers for number {} for {}/{}", cfValue, numberToMatch, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByRangeOfNumbers() function as EL function. See CustomFieldInstanceService.getCFValueByRangeOfNumbers() function for
     * documentation
     */
    public static Object getCFValueByRangeOfNumbersForDate(ICustomFieldEntity entity, String code, Date date, Object numberToMatch) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByRangeOfNumbers(entity, code, date, numberToMatch);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by range of numbers for number {} for {}/{} for {}", cfValue, numberToMatch, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getCFValueByMatrix() function for documentation
     */
    public static Object getCFValueByMatrix(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByMatrix(entity, code, keyOne, keyTwo);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by matrix for keys {}/{} for {}/{}", cfValue, keyOne, keyTwo, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getCFValueByMatrix() function for documentation
     */
    public static Object getCFValueByMatrixForDate(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByMatrix(entity, code, date, keyOne, keyTwo);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by matrix for keys {}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, entity, code, date);

        return cfValue;
    }
}