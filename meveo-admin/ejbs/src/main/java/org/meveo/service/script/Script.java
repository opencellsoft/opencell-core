package org.meveo.service.script;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;



public abstract class Script  implements ScriptInterface{

	@Override
    public void init(Map<String,Object> methodContext,Provider provider,User user){
    }
    
	@Override
    public void init(Map<String,Object> methodContext,Provider provider){
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public void execute(Map<String,Object> methodContext,Provider provider,User user) throws BusinessException{
    		execute(methodContext,provider);
    }
    
    @Override
    public void finalize(Map<String,Object> methodContext,Provider provider,User user){
    }

    @Override
    public void finalize(Map<String,Object> methodContext,Provider provider){
    }

	public  Object getServiceInterface(String serviceInterfaceName){		
		return EjbUtils.getServiceInterface(serviceInterfaceName);
	}
}
