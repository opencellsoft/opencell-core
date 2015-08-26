package org.meveo.service.script;

import java.util.Map;
import javax.naming.InitialContext;

import org.meveo.commons.utils.ParamBean;



public abstract class Script  implements ScriptInterface{

    public void init(Map<String,Object> methodContext){
    };
    
    public void finalize(Map<String,Object> methodContext){
    };

	public  Object getServiceInterface(String serviceInterfaceName){
		try {
			InitialContext ic = new InitialContext();
			return ic.lookup("java:global/"+ParamBean.getInstance().getProperty("meveo.moduleName", "meveo")+"/"+serviceInterfaceName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
