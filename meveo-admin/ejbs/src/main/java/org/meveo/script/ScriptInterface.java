package org.meveo.script;

import java.util.Map;

/**
 * 
 * @author anasseh
 * @created 16.06.2015
 */

public interface ScriptInterface {
	public void setup(Map<String,Object> initContext);
	public void execute(Map<String,Object> methodContext);
	public void teardown(Map<String,Object> endContext);
}
