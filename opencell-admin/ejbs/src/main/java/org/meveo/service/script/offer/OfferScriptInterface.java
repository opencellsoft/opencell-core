package org.meveo.service.script.offer;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.ScriptInterface;

/**
 * @author phung
 * @author Edward P. Legaspi
 *
 */
public interface OfferScriptInterface extends ScriptInterface {
	
	/**
     * Called at the beginning of BusinessOfferModelService.createOfferFromBOM method.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_PARAMETERS=List&lt;CustomFieldDto&gt;
     * @throws BusinessException business exception
     */
	void beforeCreateOfferFromBOM(Map<String, Object> methodContext) throws BusinessException;
	
	/**
     * Called at the end of BusinessOfferModelService.createOfferFromBOM method.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=OfferTemplate, CONTEXT_PARAMETERS=List&lt;CustomFieldDto&gt;
     * @throws BusinessException business exception
     */
	void afterCreateOfferFromBOM(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called after Subscription entity creation.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=Subscription
     * @throws BusinessException business exception
     */
    void subscribe(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called before subscription suspension.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=Subscription, CONTEXT_SUSPENSION_DATE=Suspension date
     * @throws BusinessException business exception
     */
    void suspendSubscription(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called after subscription reactivation.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=Subscription, CONTEXT_ACTIVATION_DATE=Reactivation date
     * @throws BusinessException business exception
     */
    void reactivateSubscription(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called before subscription termination.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=Subscription, CONTEXT_TERMINATION_DATE=Termination date, CONTEXT_TERMINATION_REASON=Termination
     *        reason
     * @throws BusinessException business exception
     */
    void terminateSubscription(Map<String, Object> methodContext) throws BusinessException;

}