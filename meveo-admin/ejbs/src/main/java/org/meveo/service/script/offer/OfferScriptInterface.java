package org.meveo.service.script.offer;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.service.script.ScriptInterface;

public interface OfferScriptInterface extends ScriptInterface {

    /**
     * Called after OfferTemplate entity creation
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=OfferTemplate
     * @param currentUser Current user
     * @throws BusinessException
     */
    public void createOfferTemplate(Map<String, Object> methodContext, User currentUser) throws BusinessException;

    /**
     * Called after OfferTemplate entity update.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=OfferTemplate
     * @param currentUser Current user
     * @throws BusinessException
     */
    public void updateOfferTemplate(Map<String, Object> methodContext, User currentUser) throws BusinessException;

    /**
     * Called after Subscription entity creation
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=Subscription
     * @param currentUser Current user
     * @throws BusinessException
     */
    public void subscribe(Map<String, Object> methodContext, User currentUser) throws BusinessException;

    /**
     * Called before subscription suspension
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=Subscription, CONTEXT_SUSPENSION_DATE=Suspension date
     * @param currentUser Current user
     * @throws BusinessException
     */
    public void suspendSubscription(Map<String, Object> methodContext, User currentUser) throws BusinessException;

    /**
     * Called after subscription reactivation
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=Subscription, CONTEXT_ACTIVATION_DATE=Reactivation date
     * @param currentUser Current user
     * @throws BusinessException
     */
    public void reactivateSubscription(Map<String, Object> methodContext, User currentUser) throws BusinessException;

    /**
     * Called before subscription termination
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=Subscription, CONTEXT_TERMINATION_DATE=Termination date, CONTEXT_TERMINATION_REASON=Termination
     *        reason
     * @param currentUser Current user
     * @throws BusinessException
     */
    public void terminateSubscription(Map<String, Object> methodContext, User currentUser) throws BusinessException;

}