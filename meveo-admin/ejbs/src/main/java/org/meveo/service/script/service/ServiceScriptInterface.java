package org.meveo.service.script.service;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.ScriptInterface;

public interface ServiceScriptInterface extends ScriptInterface {
	
	/**
     * Called at the beginning of BusinessOfferModelService.createOfferFromBOM method at the beginning of service template creation for each service to duplicate.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_PARAMETERS=List<CustomFieldDto>
     * @throws BusinessException
     */
	void beforeCreateServiceFromBSM(Map<String, Object> methodContext) throws BusinessException;
	
	/**
     * Called at the end of BusinessOfferModelService.createOfferFromBOM method at the end of service template creation for each service to duplicate.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceTemplate, CONTEXT_PARAMETERS=List<CustomFieldDto>
     * @throws BusinessException
     */
	void afterCreateServiceFromBSM(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called after ServiceInstance instantiation 
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceInstance
     * @throws BusinessException
     */
    public void instantiateServiceInstance(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called after ServiceInstance activation
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceInstance
     * @throws BusinessException
     */
    public void activateServiceInstance(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called before ServiceInstance suspension
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceInstance, CONTEXT_SUSPENSION_DATE=Suspension date
     * @throws BusinessException
     */
    public void suspendServiceInstance(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called after ServiceInstance reactivation
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceInstance, CONTEXT_ACTIVATION_DATE=Reactivation date
     * @throws BusinessException
     */
    public void reactivateServiceInstance(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called before ServiceInstance termination
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceInstance, CONTEXT_TERMINATION_DATE=Termination date,
     *        CONTEXT_TERMINATION_REASON=Termination reason
     * @throws BusinessException
     */
    public void terminateServiceInstance(Map<String, Object> methodContext) throws BusinessException;
}