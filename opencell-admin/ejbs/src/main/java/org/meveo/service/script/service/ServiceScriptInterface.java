/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.script.service;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.ScriptInterface;

public interface ServiceScriptInterface extends ScriptInterface {
	
	/**
     * Called at the beginning of BusinessOfferModelService.createOfferFromBOM method at the beginning of service template creation for each service to duplicate.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_PARAMETERS=List&lt;CustomFieldDto&gt;
     * @throws BusinessException business exception
     */
	void beforeCreateServiceFromBSM(Map<String, Object> methodContext) throws BusinessException;
	
	/**
     * Called at the end of BusinessOfferModelService.createOfferFromBOM method at the end of service template creation for each service to duplicate.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceTemplate, CONTEXT_PARAMETERS=List&lt;CustomFieldDto&gt;
     * @throws BusinessException business exception
     */
	void afterCreateServiceFromBSM(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called after ServiceInstance instantiation 
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceInstance
     * @throws BusinessException business exception
     */
    public void instantiateServiceInstance(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called after ServiceInstance activation
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceInstance
     * @throws BusinessException business exception
     */
    public void activateServiceInstance(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called before ServiceInstance suspension.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceInstance, CONTEXT_SUSPENSION_DATE=Suspension date
     * @throws BusinessException business exception
     */
    public void suspendServiceInstance(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called after ServiceInstance reactivation.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceInstance, CONTEXT_ACTIVATION_DATE=Reactivation date
     * @throws BusinessException business exception
     */
    public void reactivateServiceInstance(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called before ServiceInstance termination.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=ServiceInstance, CONTEXT_TERMINATION_DATE=Termination date,
     *        CONTEXT_TERMINATION_REASON=Termination reason
     * @throws BusinessException business exception
     */
    public void terminateServiceInstance(Map<String, Object> methodContext) throws BusinessException;
}