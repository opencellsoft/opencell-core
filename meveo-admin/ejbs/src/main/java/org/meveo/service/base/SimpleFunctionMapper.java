package org.meveo.service.base;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.el.FunctionMapper;

public class SimpleFunctionMapper extends FunctionMapper {
	 private Map<String, Method> functionMap = new HashMap<String, Method>();
	  
	  @Override
	  public Method resolveFunction(String prefix, String localName) {
	    String key = prefix + ":" + localName;
	    return functionMap.get(key);
	  }

	  public void addFunction(String prefix, String localName, Method method) {
	    if(prefix==null || localName==null || method==null) {
	      throw new NullPointerException();
	    }
	    int modifiers = method.getModifiers();
	    if(!Modifier.isPublic(modifiers)) {
	      throw new IllegalArgumentException("method not public");
	    }
	    if(!Modifier.isStatic(modifiers)) {
	      throw new IllegalArgumentException("method not static");
	    }
	    Class<?> retType = method.getReturnType();
	    if(retType == Void.TYPE) {
	      throw new IllegalArgumentException("method returns void");
	    }
	    
	    String key = prefix + "." + localName;
	    functionMap.put(key, method);
	  }
	  
}
