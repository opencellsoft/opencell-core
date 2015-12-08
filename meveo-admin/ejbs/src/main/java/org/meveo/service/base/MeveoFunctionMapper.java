package org.meveo.service.base;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

        } catch (NoSuchMethodException | SecurityException e) {
            log.error("Failed to instantiate EL custom function mv:getCFValue", e);
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
    public static Object getCFValue(ICustomFieldEntity entity, String code) {

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
        Object cfValue = customFieldInstanceService.getCFValue(entity, code, null);
        Logger log = LoggerFactory.getLogger(MeveoFunctionMapper.class);
        log.trace("Obtained CF value {} for {}/{}", cfValue, entity, code);

        return cfValue;
    }
}