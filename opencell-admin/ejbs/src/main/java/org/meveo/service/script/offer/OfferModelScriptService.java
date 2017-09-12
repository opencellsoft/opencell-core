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

    public void beforeCreateOfferFromBOM(List<CustomFieldDto> customFields, String scriptCode) throws BusinessException {
        OfferScriptInterface scriptInterface = (OfferScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(OfferScript.CONTEXT_PARAMETERS, customFields);
        scriptInterface.beforeCreateOfferFromBOM(scriptContext);
    }

    public void afterCreateOfferFromBOM(OfferTemplate entity, List<CustomFieldDto> customFields, String scriptCode)
            throws ElementNotFoundException, InvalidScriptException, BusinessException {
        OfferScriptInterface scriptInterface = (OfferScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptContext.put(OfferScript.CONTEXT_PARAMETERS, customFields);
        scriptInterface.afterCreateOfferFromBOM(scriptContext);
    }

}