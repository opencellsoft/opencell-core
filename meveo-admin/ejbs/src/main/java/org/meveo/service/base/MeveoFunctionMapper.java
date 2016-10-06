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
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
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

    private static ScriptInstanceService scriptInstanceService;

    public MeveoFunctionMapper() {

        super();

        try {
            addFunction("mv", "getCFValue", MeveoFunctionMapper.class.getMethod("getCFValue", ICustomFieldEntity.class, String.class));
            addFunction("mv", "getCFValueForDate", MeveoFunctionMapper.class.getMethod("getCFValueForDate", ICustomFieldEntity.class, String.class, Date.class));
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

            addFunction("mv", "getInheritedCFValue", MeveoFunctionMapper.class.getMethod("getInheritedCFValue", ICustomFieldEntity.class, String.class));
            addFunction("mv", "getInheritedCFValueForDate", MeveoFunctionMapper.class.getMethod("getInheritedCFValueForDate", ICustomFieldEntity.class, String.class, Date.class));

            addFunction("mv", "getInheritedCFValueByClosestMatch",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByClosestMatch", ICustomFieldEntity.class, String.class, String.class));
            addFunction("mv", "getInheritedCFValueByClosestMatchForDate",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByClosestMatchForDate", ICustomFieldEntity.class, String.class, Date.class, String.class));
            addFunction("mv", "getInheritedCFValueByRangeOfNumbers",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByRangeOfNumbers", ICustomFieldEntity.class, String.class, Object.class));
            addFunction("mv", "getInheritedCFValueByRangeOfNumbersForDate",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByRangeOfNumbersForDate", ICustomFieldEntity.class, String.class, Date.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrix",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByMatrix", ICustomFieldEntity.class, String.class, String.class));
            addFunction("mv", "getInheritedCFValueByMatrix2Keys",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByMatrix2Keys", ICustomFieldEntity.class, String.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrix3Keys",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByMatrix3Keys", ICustomFieldEntity.class, String.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrix4Keys", MeveoFunctionMapper.class.getMethod("getInheritedCFValueByMatrix4Keys", ICustomFieldEntity.class, String.class,
                Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrix5Keys", MeveoFunctionMapper.class.getMethod("getInheritedCFValueByMatrix5Keys", ICustomFieldEntity.class, String.class,
                Object.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrixForDate",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByMatrixForDate", ICustomFieldEntity.class, String.class, Date.class, String.class));
            addFunction("mv", "getInheritedCFValueByMatrixForDate2Keys",
                MeveoFunctionMapper.class.getMethod("getInheritedCFValueByMatrixForDate2Keys", ICustomFieldEntity.class, String.class, Date.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrixForDate3Keys", MeveoFunctionMapper.class.getMethod("getInheritedCFValueByMatrixForDate3Keys", ICustomFieldEntity.class,
                String.class, Date.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrixForDate4Keys", MeveoFunctionMapper.class.getMethod("getInheritedCFValueByMatrixForDate4Keys", ICustomFieldEntity.class,
                String.class, Date.class, Object.class, Object.class, Object.class, Object.class));
            addFunction("mv", "getInheritedCFValueByMatrixForDate5Keys", MeveoFunctionMapper.class.getMethod("getInheritedCFValueByMatrixForDate5Keys", ICustomFieldEntity.class,
                String.class, Date.class, Object.class, Object.class, Object.class, Object.class, Object.class));

            addFunction("mv", "executeScript", MeveoFunctionMapper.class.getMethod("executeScript", IEntity.class, String.class, String.class, User.class));

            addFunction("mv", "now", MeveoFunctionMapper.class.getMethod("getNowTimestamp"));

            addFunction("mv", "formatDate", MeveoFunctionMapper.class.getMethod("formatDate", Date.class, String.class));

            addFunction("mv", "getBean", EjbUtils.class.getMethod("getServiceInterface", String.class));

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

    /**
     * Exposes CustomFieldInstanceService.getCFValue() function as EL function. See CustomFieldInstanceService.getCFValue() function for documentation
     */
    public static Object getCFValue(ICustomFieldEntity entity, String code) {

        Object cfValue = getCustomFieldInstanceService().getCFValue(entity, code, getCustomFieldInstanceService().getCurrentUser());
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} for {}/{}", cfValue, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getCFValue() function as EL function. See CustomFieldInstanceService.getCFValue() function for documentation
     */
    public static Object getCFValueForDate(ICustomFieldEntity entity, String code, Date date) {

        Object cfValue = getCustomFieldInstanceService().getCFValue(entity, code, date, getCustomFieldInstanceService().getCurrentUser());
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} for {}/{} for {}", cfValue, entity, code, date);

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
     * Exposes CustomFieldInstanceService.getInheritedCFValue() function as EL function. See CustomFieldInstanceService.getInheritedCFValue() function for documentation
     */
    public static Object getInheritedCFValue(ICustomFieldEntity entity, String code) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValue(entity, code, getCustomFieldInstanceService().getCurrentUser());
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} for {}/{}", cfValue, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValue() function as EL function. See CustomFieldInstanceService.getInheritedCFValue() function for documentation
     */
    public static Object getInheritedCFValueForDate(ICustomFieldEntity entity, String code, Date date) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValue(entity, code, date, getCustomFieldInstanceService().getCurrentUser());
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} for {}/{} for {}", cfValue, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByClosestMatch() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByClosestMatch() function
     * for documentation
     */
    public static Object getInheritedCFValueByClosestMatch(ICustomFieldEntity entity, String code, String keyToMatch) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByClosestMatch(entity, code, keyToMatch);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by closest match for key {} for {}/{}", cfValue, keyToMatch, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByClosestMatch() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByClosestMatch() function
     * for documentation
     */
    public static Object getInheritedCFValueByClosestMatchForDate(ICustomFieldEntity entity, String code, Date date, String keyToMatch) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByClosestMatch(entity, code, date, keyToMatch);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by closest match for key {} for {}/{} for {}", cfValue, keyToMatch, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByRangeOfNumbers() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByRangeOfNumbers()
     * function for documentation
     */
    public static Object getInheritedCFValueByRangeOfNumbers(ICustomFieldEntity entity, String code, Object numberToMatch) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByRangeOfNumbers(entity, code, numberToMatch);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by range of numbers for number {} for {}/{}", cfValue, numberToMatch, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByRangeOfNumbers() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByRangeOfNumbers()
     * function for documentation
     */
    public static Object getInheritedCFValueByRangeOfNumbersForDate(ICustomFieldEntity entity, String code, Date date, Object numberToMatch) {

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
     * @param concatenatedKeys Keys concatenated by "|" sign
     */
    public static Object getInheritedCFValueByMatrix(ICustomFieldEntity entity, String code, String concatenatedKeys) {

        if (StringUtils.isBlank(concatenatedKeys)) {
            return null;
        }

        String[] keys = concatenatedKeys.split("|");

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByMatrix(entity, code, (Object[]) keys);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by matrix for keys {} for {}/{}", cfValue, keys, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByMatrix() function for
     * documentation
     */
    public static Object getInheritedCFValueByMatrix2Keys(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByMatrix(entity, code, keyOne, keyTwo);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by matrix for keys {}/{} for {}/{}", cfValue, keyOne, keyTwo, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByMatrix() function for
     * documentation
     */
    public static Object getInheritedCFValueByMatrix3Keys(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByMatrix(entity, code, keyOne, keyTwo, keyThree);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by matrix for keys {}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByMatrix() function for
     * documentation
     */
    public static Object getInheritedCFValueByMatrix4Keys(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByMatrix(entity, code, keyOne, keyTwo, keyThree, keyFour);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by matrix for keys {}/{}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, keyFour, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByMatrix() function for
     * documentation
     */
    public static Object getInheritedCFValueByMatrix5Keys(ICustomFieldEntity entity, String code, Object keyOne, Object keyTwo, Object keyThree, Object keyFour, Object keyFive) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByMatrix(entity, code, keyOne, keyTwo, keyThree, keyFour, keyFive);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by matrix for keys {}/{}/{}/{} for {}/{}", cfValue, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByMatrix() function for
     * documentation
     * 
     * @param entity Entity to find CF value for
     * @param code Custom field code
     * @param date Date Value date
     * @param concatenatedKeys Keys concatenated by "|" sign
     */
    public static Object getInheritedCFValueByMatrixForDate(ICustomFieldEntity entity, String code, Date date, String concatenatedKeys) {

        if (StringUtils.isBlank(concatenatedKeys)) {
            return null;
        }

        String[] keys = concatenatedKeys.split("|");

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByMatrix(entity, code, (Object[]) keys);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by matrix for keys {} for {}/{}", cfValue, keys, entity, code);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByMatrix() function for
     * documentation
     */
    public static Object getInheritedCFValueByMatrixForDate2Keys(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByMatrix(entity, code, date, keyOne, keyTwo);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by matrix for keys {}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByMatrix() function for
     * documentation
     */
    public static Object getInheritedCFValueByMatrixForDate3Keys(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByMatrix(entity, code, date, keyOne, keyTwo, keyThree);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by matrix for keys {}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByMatrix() function for
     * documentation
     */
    public static Object getInheritedCFValueByMatrixForDate4Keys(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByMatrix(entity, code, date, keyOne, keyTwo, keyThree, keyFour);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by matrix for keys {}/{}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, keyFour, entity, code, date);

        return cfValue;
    }

    /**
     * Exposes CustomFieldInstanceService.getInheritedCFValueByMatrix() function as EL function. See CustomFieldInstanceService.getInheritedCFValueByMatrix() function for
     * documentation
     */
    public static Object getInheritedCFValueByMatrixForDate5Keys(ICustomFieldEntity entity, String code, Date date, Object keyOne, Object keyTwo, Object keyThree, Object keyFour,
            Object keyFive) {

        Object cfValue = getCustomFieldInstanceService().getInheritedCFValueByMatrix(entity, code, date, keyOne, keyTwo, keyThree, keyFour, keyFive);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained inherited CF value {} by matrix for keys {}/{}/{}/{} for {}/{} for {}", cfValue, keyOne, keyTwo, keyThree, keyFour, keyFive, entity, code, date);

        return cfValue;
    }

    /**
     * Execute script on an entity
     * 
     * @param entity Entity to execute action on
     * @param scriptCode Script to execute, identified by a code
     * @param encodedParameters Additional parameters encoded in URL like style param=value&param=value
     * @param currentUser Current user
     * @return A script execution result value
     */
    public static Object executeScript(IEntity entity, String scriptCode, String encodedParameters, User currentUser) {

        Map<String, Object> result = null;

        try {
            try {
                result = getScriptInstanceService().execute(entity, scriptCode, encodedParameters, currentUser);
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
     * Get a timestamp
     * 
     * @return
     */
    public static Date getNowTimestamp() {
        return new Date();
    }

    /**
     * Format date
     * 
     * @param dateFormatPattern standard java date and time patterns
     * @return A formated date
     */
    public static String formatDate(Date date, String dateFormatPattern) {
        if (date == null) {
            return DateUtils.formatDateWithPattern(new Date(), dateFormatPattern);
        }
        return DateUtils.formatDateWithPattern(date, dateFormatPattern);
    }
}