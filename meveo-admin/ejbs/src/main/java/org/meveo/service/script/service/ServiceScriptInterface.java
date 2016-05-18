package org.meveo.service.script.service;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.service.script.ScriptInterface;

public interface ServiceScriptInterface extends ScriptInterface {

    /**
     * Called after ServiceTemplate entity creation
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceTemplate
     * @param currentUser Current user
     * @throws BusinessException
     */
    public void createServiceTemplate(Map<String, Object> methodContext, User currentUser) throws BusinessException;

    /**
     * Called after ServiceTemplate entity update
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceTemplate
     * @param currentUser Current user
     * @throws BusinessException
     */
    public void updateServiceTemplate(Map<String, Object> methodContext, User currentUser) throws BusinessException;

    /**
     * Called after ServiceInstance instantiation 
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceInstance
     * @param currentUser Current user
     * @throws BusinessException
     */
    public void instantiateServiceInstance(Map<String, Object> methodContext, User currentUser) throws BusinessException;

    /**
     * Called after ServiceInstance activation
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceInstance
     * @param currentUser Current user
     * @throws BusinessException
     */
    public void activateServiceInstance(Map<String, Object> methodContext, User currentUser) throws BusinessException;

    /**
     * Called before ServiceInstance suspension
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceInstance, CONTEXT_SUSPENSION_DATE=Suspension date
     * @param currentUser Current user
     * @throws BusinessException
     */
    public void suspendServiceInstance(Map<String, Object> methodContext, User currentUser) throws BusinessException;

    /**
     * Called after ServiceInstance reactivation
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceInstance, CONTEXT_ACTIVATION_DATE=Reactivation date
     * @param currentUser Current user
     * @throws BusinessException
     */
    public void reactivateServiceInstance(Map<String, Object> methodContext, User currentUser) throws BusinessException;

    /**
     * Called before ServiceInstance termination
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceInstance, CONTEXT_TERMINATION_DATE=Termination date,
     *        CONTEXT_TERMINATION_REASON=Termination reason
     * @param currentUser Current user
     * @throws BusinessException
     */
    public void terminateServiceInstance(Map<String, Object> methodContext, User currentUser) throws BusinessException;
}