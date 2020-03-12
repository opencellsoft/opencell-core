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