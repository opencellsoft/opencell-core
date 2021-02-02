package org.meveo.api.billing;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.service.script.module.ModuleScript;

@SuppressWarnings("serial")
public class OrderScriptTemp extends ModuleScript {

    
	
	@Override
	public void execute(Map<String, Object> methodContext) throws BusinessException {
		final CommercialOrder order = (CommercialOrder) methodContext.get("commercialOrder");
		if(order == null)
			throw new BusinessException("No Commercial order is found");
		
		throw new BusinessException("Found Commercial order : " + order.getId());
	}
		
}
