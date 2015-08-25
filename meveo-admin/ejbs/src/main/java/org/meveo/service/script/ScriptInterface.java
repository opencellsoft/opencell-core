package org.meveo.service.script;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;

public interface ScriptInterface {
	public void init(Map<String,Object> methodContext);
	public void execute(Map<String,Object> methodContext) throws BusinessException;
	public void finilaze(Map<String,Object> methodContext);
	
}
