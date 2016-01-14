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

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.script.EntityActionScriptService;
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

    private static EntityActionScriptService entityActionScriptService;

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
            addFunction("mv", "getCFValueByMatrix", MeveoFunctionMapper.class.getMethod("getCFValueByMatrix", ICustomFieldEntity.class, String.class, String.class));
            addFunction("mv", "getCFValueByMatrix2Keys",
                MeveoFunctionMapper.class.getMethod("getCFValueByMatrix2Keys", ICustomFieldEntity.class, String.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByMatrix3Keys",
                MeveoFunctionMapper.class.getMethod("getCFValueByMatrix3Keys", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByMatrix4Keys",
                MeveoFunctionMapper.class.getMethod("getCFValueByMatrix4Keys", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByMatrix5Keys", MeveoFunctionMapper.class.getMethod("getCFValueByMatrix5Keys", ICustomFieldEntity.class, String.class, Object.class,
                Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByMatrixForDate",
                MeveoFunctionMapper.class.getMethod("getCFValueByMatrixForDate", ICustomFieldEntity.class, String.class, Date.class, String.class));
            addFunction("mv", "getCFValueByMatrixForDate2Keys",
                MeveoFunctionMapper.class.getMethod("getCFValueByMatrixForDate2Keys", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByMatrixForDate3Keys",
                MeveoFunctionMapper.class.getMethod("getCFValueByMatrixForDate3Keys", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByMatrixForDate4Keys", MeveoFunctionMapper.class.getMethod("getCFValueByMatrixForDate4Keys", ICustomFieldEntity.class, String.class,
                Date.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getCFValueByMatrixForDate5Keys", MeveoFunctionMapper.class.getMethod("getCFValueByMatrixForDate5Keys", ICustomFieldEntity.class, String.class,
                Date.class, Object.class, Object.class, Object.class, Object.class, Object.class));

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
    private static EntityActionScriptService getEntityActionScriptService() {

        if (entityActionScriptService == null) {
            try {
                InitialContext initialContext = new InitialContext();
                BeanManager beanManager = (BeanManager) initialContext.lookup("java:comp/BeanManager");

                Bean<EntityActionScriptService> bean = (Bean<EntityActionScriptService>) beanManager.resolve(beanManager.getBeans(EntityActionScriptService.class));
                entityActionScriptService = (EntityActionScriptService) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));

            } catch (NamingException e) {
                Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
                log.error("Unable to access EntityActionScriptService", e);
                throw new RuntimeException(e);
            }
        }
        return entityActionScriptService;
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
     * 
     * @param entity Entity to find CF value for
     * @param code Custom field code
     * @param concatenatedKeys Keys concatenated by "|" sign
     */
    public static Object getCFValueByMatrix(ICustomFieldEntity entity, String code, String concatenatedKeys) {

        if (StringUtils.isBlank(concatenatedKeys)) {
            return null;
        }

        String[] keys = concatenatedKeys.split("|");

        Object cfValue = getCustomFieldInstanceService().getCFValueByMatrix(entity, code, (Object[]) keys);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by matrix for keys {} for {}/{}", cfValue, keys, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getCFValueByMatrix() function for documentation
     */
    public static Object getCFValueByMatrix2Keys(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByMatrix(entity, code, keyOne, keyTwo);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by matrix for keys {}/{} for {}/{}", cfValue, keyOne, keyTwo, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getCFValueByMatrix() function for documentation
     */
    public static Object getCFValueByMatrix3Keys(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByMatrix(entity, code, keyOne, keyTwo, keyThree);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by matrix for keys {}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getCFValueByMatrix() function for documentation
     */
    public static Object getCFValueByMatrix4Keys(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByMatrix(entity, code, keyOne, keyTwo, keyThree, keyFour);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by matrix for keys {}/{}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, keyFour, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getCFValueByMatrix() function for documentation
     */
    public static Object getCFValueByMatrix5Keys(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour, Object keyFive) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByMatrix(entity, code, keyOne, keyTwo, keyThree, keyFour, keyFive);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by matrix for keys {}/{}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getCFValueByMatrix() function for documentation
     * 
     * @param entity Entity to find CF value for
     * @param code Custom field code
     * @param date Date Value date
     * @param concatenatedKeys Keys concatenated by "|" sign
     */
    public static Object getCFValueByMatrixForDate(ICustomFieldEntity entity, String code, Date date, String concatenatedKeys) {

        if (StringUtils.isBlank(concatenatedKeys)) {
            return null;
        }

        String[] keys = concatenatedKeys.split("|");

        Object cfValue = getCustomFieldInstanceService().getCFValueByMatrix(entity, code, (Object[]) keys);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by matrix for keys {} for {}/{}", cfValue, keys, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getCFValueByMatrix() function for documentation
     */
    public static Object getCFValueByMatrixForDate2Keys(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByMatrix(entity, code, date, keyOne, keyTwo);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by matrix for keys {}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getCFValueByMatrix() function for documentation
     */
    public static Object getCFValueByMatrixForDate3Keys(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByMatrix(entity, code, date, keyOne, keyTwo, keyThree);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by matrix for keys {}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getCFValueByMatrix() function for documentation
     */
    public static Object getCFValueByMatrixForDate4Keys(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByMatrix(entity, code, date, keyOne, keyTwo, keyThree, keyFour);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by matrix for keys {}/{}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, keyFour, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getCFValueByMatrix() function for documentation
     */
    public static Object getCFValueByMatrixForDate5Keys(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour,
            Object keyFive) {

        Object cfValue = getCustomFieldInstanceService().getCFValueByMatrix(entity, code, date, keyOne, keyTwo, keyThree, keyFour, keyFive);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} by matrix for keys {}/{}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code, date);

        return cfValue;
    }

    /**
     * Execute action on an entity
     * 
     * @param entity Entity to execute action on
     * @param scriptCode Script to execute, identified by a code
     * @param encodedParameters Additional parameters encoded in URL like style param=value&param=value
     * @param currentUser Current user
     * @param currentProvider Current provider
     * @return A script execution result value
     */
    public static Object executeEntityAction(IEntity entity, String scriptCode, String encodedParameters, User currentUser, Provider currentProvider) {

        Object result = null;

        try {
            result = getEntityActionScriptService().execute(entity, scriptCode, encodedParameters, currentUser, currentProvider);
        } catch (InstantiationException | IllegalAccessException | BusinessException e) {
            Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
            log.error("Failed to execute a script {} on entity {}", scriptCode, entity);
        }

        return result;
    }
}