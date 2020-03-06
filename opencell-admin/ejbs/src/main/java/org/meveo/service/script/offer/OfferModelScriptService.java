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

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class OfferModelScriptService implements Serializable {

    private static final long serialVersionUID = -2580475102375024245L;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public void subscribe(Subscription entity, String scriptCode) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        OfferScriptInterface scriptInterface = (OfferScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.subscribe(scriptContext);
    }

    public void terminateSubscription(Subscription entity, String scriptCode, Date terminationDate, SubscriptionTerminationReason terminationReason)
            throws ElementNotFoundException, InvalidScriptException, BusinessException {
        OfferScriptInterface scriptInterface = (OfferScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(OfferScript.CONTEXT_TERMINATION_DATE, terminationDate);
        scriptContext.put(OfferScript.CONTEXT_TERMINATION_REASON, terminationReason);
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.terminateSubscription(scriptContext);
    }

    public void suspendSubscription(Subscription entity, String scriptCode, Date suspensionDate) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        OfferScriptInterface scriptInterface = (OfferScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(OfferScript.CONTEXT_SUSPENSION_DATE, suspensionDate);
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.suspendSubscription(scriptContext);
    }

    public void reactivateSubscription(Subscription entity, String scriptCode, Date activationDate) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        OfferScriptInterface scriptInterface = (OfferScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(OfferScript.CONTEXT_ACTIVATION_DATE, activationDate);
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.reactivateSubscription(scriptContext);
    }

    public OfferScriptInterface beforeCreateOfferFromBOM(List<CustomFieldDto> customFields, String scriptCode) throws BusinessException {
        OfferScriptInterface scriptInterface = (OfferScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(OfferScript.CONTEXT_PARAMETERS, customFields);
        scriptInterface.beforeCreateOfferFromBOM(scriptContext);
        return scriptInterface;
    }

    public void afterCreateOfferFromBOM(OfferTemplate entity, List<CustomFieldDto> customFields, OfferScriptInterface scriptInterface)
            throws ElementNotFoundException, InvalidScriptException, BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptContext.put(OfferScript.CONTEXT_PARAMETERS, customFields);
        scriptInterface.afterCreateOfferFromBOM(scriptContext);
    }
}