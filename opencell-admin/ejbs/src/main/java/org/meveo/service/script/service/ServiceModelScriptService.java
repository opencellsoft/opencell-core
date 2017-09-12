package org.meveo.service.script.service;

import java.io.Serializable;
import java.util.ArrayList;
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
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class ServiceModelScriptService implements Serializable {

    private static final long serialVersionUID = -236471508767180502L;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public void instantiateServiceInstance(ServiceInstance entity, String scriptCode) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ServiceScriptInterface scriptInterface = (ServiceScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.instantiateServiceInstance(scriptContext);
    }

    public void activateServiceInstance(ServiceInstance entity, String scriptCode) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ServiceScriptInterface scriptInterface = (ServiceScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.activateServiceInstance(scriptContext);
    }

    public void suspendServiceInstance(ServiceInstance entity, String scriptCode, Date suspensionDate) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ServiceScriptInterface scriptInterface = (ServiceScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(ServiceScript.CONTEXT_SUSPENSION_DATE, suspensionDate);
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.suspendServiceInstance(scriptContext);
    }

    public void reactivateServiceInstance(ServiceInstance entity, String scriptCode, Date reactivationDate)
            throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ServiceScriptInterface scriptInterface = (ServiceScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(ServiceScript.CONTEXT_ACTIVATION_DATE, reactivationDate);
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.reactivateServiceInstance(scriptContext);
    }

    public void terminateServiceInstance(ServiceInstance entity, String scriptCode, Date terminationDate, SubscriptionTerminationReason terminationReason)
            throws ElementNotFoundException, InvalidScriptException, BusinessException {

        ServiceScriptInterface scriptInterface = (ServiceScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(ServiceScript.CONTEXT_TERMINATION_DATE, terminationDate);
        scriptContext.put(ServiceScript.CONTEXT_TERMINATION_REASON, terminationReason);
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.terminateServiceInstance(scriptContext);
    }

    public void beforeCreateServiceFromBSM(List<CustomFieldDto> customFields, String scriptCode) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ServiceScriptInterface scriptInterface = (ServiceScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        if (customFields != null) {
            scriptContext.put(ServiceScript.CONTEXT_PARAMETERS, customFields);
        } else {
            scriptContext.put(ServiceScript.CONTEXT_PARAMETERS, new ArrayList<CustomFieldDto>());
        }
        scriptInterface.beforeCreateServiceFromBSM(scriptContext);
    }

    public void afterCreateServiceFromBSM(ServiceTemplate entity, List<CustomFieldDto> customFields, String scriptCode)
            throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ServiceScriptInterface scriptInterface = (ServiceScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        if (customFields != null) {
            scriptContext.put(ServiceScript.CONTEXT_PARAMETERS, customFields);
        } else {
            scriptContext.put(ServiceScript.CONTEXT_PARAMETERS, new ArrayList<CustomFieldDto>());
        }
        scriptInterface.afterCreateServiceFromBSM(scriptContext);
    }
}