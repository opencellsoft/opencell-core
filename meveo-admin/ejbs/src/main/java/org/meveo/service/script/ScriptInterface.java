package org.meveo.service.script;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.Provider;

public interface ScriptInterface {
	public void init(Map<String,Object> methodContext,Provider provider);
	public void execute(Map<String,Object> methodContext,Provider provider) throws BusinessException;
	public void finilaze(Map<String,Object> methodContext,Provider provider);
	
}
