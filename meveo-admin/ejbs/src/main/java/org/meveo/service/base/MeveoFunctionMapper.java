package org.meveo.service.base;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.el.FunctionMapper;

import org.meveo.model.shared.DateUtils;
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
    public MeveoFunctionMapper() {

        super();

        try {
                addFunction("mv", "formatDate", MeveoFunctionMapper.class.getMethod("formatDate", Date.class,String.class));

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

  
    
    /**
     * Format date
     * 
     * @param dateFormatPattern  standard java  date and time patterns
     * @return A formated date
     */
    public static String formatDate(Date date,String dateFormatPattern) { 
    	if(date == null){
    		return DateUtils.formatDateWithPattern(new Date(), dateFormatPattern);
    	}
    	return DateUtils.formatDateWithPattern(date, dateFormatPattern);
    }    
    
}