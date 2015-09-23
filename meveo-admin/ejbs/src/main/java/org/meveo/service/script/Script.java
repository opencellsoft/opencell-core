package org.meveo.service.script;

import java.util.Map;

import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;



public abstract class Script  implements ScriptInterface{

    public void init(Map<String,Object> methodContext,Provider provider,User user){
    }
    
    public void init(Map<String,Object> methodContext,Provider provider){
    }
    
    public void execute(Map<String,Object> methodContext,Provider provider,User user){
    		execute(methodContext,provider,user);
    }
    
    public void finalize(Map<String,Object> methodContext,Provider provider,User user){
    }

    public void finalize(Map<String,Object> methodContext,Provider provider){
    }

	public  Object getServiceInterface(String serviceInterfaceName){		
		return EjbUtils.getServiceInterface(serviceInterfaceName);
	}
}
