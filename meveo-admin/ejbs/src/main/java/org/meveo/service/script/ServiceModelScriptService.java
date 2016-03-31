package org.meveo.service.script;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.CustomScript;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.model.scripts.ServiceModelScript;
import org.meveo.service.script.service.ServiceScript;
import org.meveo.service.script.service.ServiceScriptInterface;

/**
 * @author Edward P. Legaspi
 **/
@Singleton
@Startup
public class ServiceModelScriptService extends CustomScriptService<ServiceModelScript, ServiceScriptInterface> {

    @Inject
    private ResourceBundle resourceMessages;

    @Override
    public void create(ServiceModelScript serviceModelScript, User creator) throws BusinessException {

        String className = getClassName(serviceModelScript.getScript());
        if (className == null) {
            throw new BusinessException(resourceMessages.getString("message.ServiceModelScript.sourceInvalid"));
        }
        serviceModelScript.setCode(getFullClassname(serviceModelScript.getScript()));

        super.create(serviceModelScript, creator);
    }

    @Override
    public ServiceModelScript update(ServiceModelScript serviceModelScript, User updater) throws BusinessException {

        String className = getClassName(serviceModelScript.getScript());
        if (className == null) {
            throw new BusinessException(resourceMessages.getString("message.ServiceModelScript.sourceInvalid"));
        }
        serviceModelScript.setCode(getFullClassname(serviceModelScript.getScript()));

        serviceModelScript = super.update(serviceModelScript, updater);

        return serviceModelScript;
    }

    /**
     * Get all ServiceModelScripts with error for a provider
     * 
     * @param provider
     * @return
     */
    public List<CustomScript> getServiceModelScriptsWithError(Provider provider) {
        return ((List<CustomScript>) getEntityManager().createNamedQuery("CustomScript.getServiceModelScriptOnError", CustomScript.class).setParameter("isError", Boolean.TRUE)
            .setParameter("provider", provider).getResultList());
    }

    /**
     * Compile all ServiceModelScripts
     */
    @PostConstruct
    void compileAll() {
        List<ServiceModelScript> ServiceModelScripts = findByType(ScriptSourceTypeEnum.JAVA);
        compile(ServiceModelScripts);
    }

    // Interface methods

    public void createServiceTemplate(ServiceTemplate entity, String scriptCode, User currentUser) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ServiceScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.createServiceTemplate(scriptContext, currentUser);
    }

    public void updateServiceTemplate(ServiceTemplate entity, String scriptCode, User currentUser) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ServiceScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.updateServiceTemplate(scriptContext, currentUser);
    }

    public void instantiateServiceInstance(ServiceInstance entity, String scriptCode, User currentUser) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ServiceScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.instantiateServiceInstance(scriptContext, currentUser);
    }

    public void activateServiceInstance(ServiceInstance entity, String scriptCode, User currentUser) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ServiceScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.activateServiceInstance(scriptContext, currentUser);
    }

    public void suspendServiceInstance(ServiceInstance entity, String scriptCode, Date suspensionDate, User currentUser) throws ElementNotFoundException, InvalidScriptException,
            BusinessException {
        ServiceScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(ServiceScript.CONTEXT_SUSPENSION_DATE, suspensionDate);
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.suspendServiceInstance(scriptContext, currentUser);
    }

    public void reactivateServiceInstance(ServiceInstance entity, String scriptCode, Date reactivationDate, User currentUser) throws ElementNotFoundException,
            InvalidScriptException, BusinessException {
        ServiceScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(ServiceScript.CONTEXT_ACTIVATION_DATE, reactivationDate);
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.reactivateServiceInstance(scriptContext, currentUser);
    }

    public void terminateServiceInstance(ServiceInstance entity, String scriptCode, Date terminationDate, SubscriptionTerminationReason terminationReason, User currentUser)
            throws ElementNotFoundException, InvalidScriptException, BusinessException {

        ServiceScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(ServiceScript.CONTEXT_TERMINATION_DATE, terminationDate);
        scriptContext.put(ServiceScript.CONTEXT_TERMINATION_REASON, terminationReason);
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.terminateServiceInstance(scriptContext, currentUser);
    }
}