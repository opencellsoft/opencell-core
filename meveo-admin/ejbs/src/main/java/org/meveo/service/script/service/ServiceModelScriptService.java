package org.meveo.service.script.service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 **/
@Singleton
@Startup
public class ServiceModelScriptService implements Serializable {

    private static final long serialVersionUID = -236471508767180502L;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public void instantiateServiceInstance(ServiceInstance entity, String scriptCode, User currentUser) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ServiceScriptInterface scriptInterface = (ServiceScriptInterface) scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.instantiateServiceInstance(scriptContext, currentUser);
    }

    public void activateServiceInstance(ServiceInstance entity, String scriptCode, User currentUser) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ServiceScriptInterface scriptInterface = (ServiceScriptInterface) scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.activateServiceInstance(scriptContext, currentUser);
    }

    public void suspendServiceInstance(ServiceInstance entity, String scriptCode, Date suspensionDate, User currentUser) throws ElementNotFoundException, InvalidScriptException,
            BusinessException {
        ServiceScriptInterface scriptInterface = (ServiceScriptInterface) scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(ServiceScript.CONTEXT_SUSPENSION_DATE, suspensionDate);
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.suspendServiceInstance(scriptContext, currentUser);
    }

    public void reactivateServiceInstance(ServiceInstance entity, String scriptCode, Date reactivationDate, User currentUser) throws ElementNotFoundException,
            InvalidScriptException, BusinessException {
        ServiceScriptInterface scriptInterface = (ServiceScriptInterface) scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(ServiceScript.CONTEXT_ACTIVATION_DATE, reactivationDate);
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.reactivateServiceInstance(scriptContext, currentUser);
    }

    public void terminateServiceInstance(ServiceInstance entity, String scriptCode, Date terminationDate, SubscriptionTerminationReason terminationReason, User currentUser)
            throws ElementNotFoundException, InvalidScriptException, BusinessException {

        ServiceScriptInterface scriptInterface = (ServiceScriptInterface) scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(ServiceScript.CONTEXT_TERMINATION_DATE, terminationDate);
        scriptContext.put(ServiceScript.CONTEXT_TERMINATION_REASON, terminationReason);
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.terminateServiceInstance(scriptContext, currentUser);
    }
    
    public void beforeCreateServiceFromBSM(List<CustomFieldDto> customFields, String scriptCode, User currentUser) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ServiceScriptInterface scriptInterface = (ServiceScriptInterface) scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(ServiceScript.CONTEXT_PARAMETERS, customFields);
        scriptInterface.beforeCreateServiceFromBSM(scriptContext, currentUser);
    }

    public void afterCreateServiceFromBSM(ServiceTemplate entity, List<CustomFieldDto> customFields, String scriptCode, User currentUser) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ServiceScriptInterface scriptInterface = (ServiceScriptInterface) scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptContext.put(ServiceScript.CONTEXT_PARAMETERS, customFields);
        scriptInterface.afterCreateServiceFromBSM(scriptContext, currentUser);
    }
    
}