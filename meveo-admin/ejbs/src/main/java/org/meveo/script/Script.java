package org.meveo.script;

import java.util.Map;
import javax.naming.InitialContext;
import org.meveo.commons.utils.ParamBean;

/**
 * 
 * @author anasseh
 * @created 16.06.2015
 */

public abstract class Script  implements ScriptInterface{

	@Override
	public abstract void setup(Map<String, Object> initContext);
	
	@Override
	public abstract void execute(Map<String, Object> methodContext);

	@Override
	public abstract void teardown(Map<String, Object> endContext);

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
