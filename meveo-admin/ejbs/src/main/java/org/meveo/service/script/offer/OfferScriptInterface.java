package org.meveo.service.script.offer;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.service.script.ScriptInterface;

public interface OfferScriptInterface extends ScriptInterface {
	
	/**
     * Called at the beginning of BusinessOfferModelService.createOfferFromBOM method.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_PARAMETERS=List<CustomFieldDto>
     * @param user Current user
     * @throws BusinessException
     */
	void beforeCreateOfferFromBOM(Map<String, Object> methodContext, User user) throws BusinessException;
	
	/**
     * Called at the end of BusinessOfferModelService.createOfferFromBOM method.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=OfferTemplate, CONTEXT_PARAMETERS=List<CustomFieldDto>
     * @param user Current user
     * @throws BusinessException
     */
	void afterCreateOfferFromBOM(Map<String, Object> methodContext, User user) throws BusinessException;

    /**
     * Called after Subscription entity creation
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=Subscription
     * @param user Current user
     * @throws BusinessException
     */
    public void subscribe(Map<String, Object> methodContext, User user) throws BusinessException;

    /**
     * Called before subscription suspension
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=Subscription, CONTEXT_SUSPENSION_DATE=Suspension date
     * @param user Current user
     * @throws BusinessException
     */
    public void suspendSubscription(Map<String, Object> methodContext, User user) throws BusinessException;

    /**
     * Called after subscription reactivation
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=Subscription, CONTEXT_ACTIVATION_DATE=Reactivation date
     * @param user Current user
     * @throws BusinessException
     */
    public void reactivateSubscription(Map<String, Object> methodContext, User user) throws BusinessException;

    /**
     * Called before subscription termination
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=Subscription, CONTEXT_TERMINATION_DATE=Termination date, CONTEXT_TERMINATION_REASON=Termination
     *        reason
     * @param user Current user
     * @throws BusinessException
     */
    public void terminateSubscription(Map<String, Object> methodContext, User user) throws BusinessException;

}