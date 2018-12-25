package org.meveo.service.script.offer;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.module.ModuleScript;

/**
 * @author Edward P. Legaspi
 **/
public class OfferScript extends ModuleScript implements OfferScriptInterface {

    public static String CONTEXT_ACTIVATION_DATE = "CONTEXT_ACTIVATION_DATE";
    public static String CONTEXT_SUSPENSION_DATE = "CONTEXT_SUSPENSION_DATE";
    public static String CONTEXT_TERMINATION_DATE = "CONTEXT_TERMINATION_DATE";
    public static String CONTEXT_TERMINATION_REASON = "CONTEXT_TERMINATION_REASON";
    public static String CONTEXT_PARAMETERS = "CONTEXT_PARAMETERS";

    @Override
    public void subscribe(Map<String, Object> methodContext) throws BusinessException {

    }

    @Override
    public void suspendSubscription(Map<String, Object> methodContext) throws BusinessException {

    }

    @Override
    public void reactivateSubscription(Map<String, Object> methodContext) throws BusinessException {

    }

    @Override
    public void terminateSubscription(Map<String, Object> methodContext) throws BusinessException {

    }

	@Override
	public void beforeCreateOfferFromBOM(Map<String, Object> methodContext) throws BusinessException {
		
	}

	@Override
	public void afterCreateOfferFromBOM(Map<String, Object> methodContext) throws BusinessException {
		
	}
}