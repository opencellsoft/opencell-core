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
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.CustomScript;
import org.meveo.model.scripts.OfferModelScript;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.service.script.offer.OfferScript;
import org.meveo.service.script.offer.OfferScriptInterface;

/**
 * @author Edward P. Legaspi
 **/
@Singleton
@Startup
public class OfferModelScriptService extends CustomScriptService<OfferModelScript, OfferScriptInterface> {

    @Inject
    private ResourceBundle resourceMessages;

    @Override
    public void create(OfferModelScript offerModelScript, User creator) throws BusinessException {
        String packageName = getPackageName(offerModelScript.getScript());
        String className = getClassName(offerModelScript.getScript());
        if (packageName == null || className == null) {
            throw new RuntimeException(resourceMessages.getString("message.OfferModelScript.sourceInvalid"));
        }
        offerModelScript.setCode(packageName + "." + className);

        super.create(offerModelScript, creator);
    }

    @Override
    public OfferModelScript update(OfferModelScript offerModelScript, User updater) throws BusinessException {

        String packageName = getPackageName(offerModelScript.getScript());
        String className = getClassName(offerModelScript.getScript());
        if (packageName == null || className == null) {
            throw new RuntimeException(resourceMessages.getString("message.OfferModelScript.sourceInvalid"));
        }
        offerModelScript.setCode(packageName + "." + className);

        offerModelScript = super.update(offerModelScript, updater);

        return offerModelScript;
    }

    /**
     * Get all OfferModelScripts with error for a provider
     * 
     * @param provider
     * @return
     */
    public List<CustomScript> getOfferModelScriptsWithError(Provider provider) {
        return ((List<CustomScript>) getEntityManager().createNamedQuery("CustomScript.getOfferModelScriptOnError", CustomScript.class).setParameter("isError", Boolean.TRUE)
            .setParameter("provider", provider).getResultList());
    }

    /**
     * Compile all OfferModelScripts
     */
    @PostConstruct
    void compileAll() {
        List<OfferModelScript> offerModelScripts = findByType(ScriptSourceTypeEnum.JAVA);
        compile(offerModelScripts);
    }

    public String getDerivedCode(String script) {
        return getPackageName(script) + "." + getClassName(script);
    }

    // Interface methods

    public void subscribe(Subscription entity, String scriptCode, User currentUser) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        OfferScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.subscribe(scriptContext, currentUser);
    }

    public void terminateSubscription(Subscription entity, String scriptCode, Date terminationDate, SubscriptionTerminationReason terminationReason, User currentUser)
            throws ElementNotFoundException, InvalidScriptException, BusinessException {
        OfferScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(OfferScript.CONTEXT_TERMINATION_DATE, terminationDate);
        scriptContext.put(OfferScript.CONTEXT_TERMINATION_REASON, terminationReason);
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.terminateSubscription(scriptContext, currentUser);
    }

    public void suspendSubscription(Subscription entity, String scriptCode, Date suspensionDate, User currentUser) throws ElementNotFoundException, InvalidScriptException,
            BusinessException {
        OfferScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(OfferScript.CONTEXT_SUSPENSION_DATE, suspensionDate);
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.suspendSubscription(scriptContext, currentUser);
    }

    public void reactivateSubscription(Subscription entity, String scriptCode, Date activationDate, User currentUser) throws ElementNotFoundException, InvalidScriptException,
            BusinessException {
        OfferScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(OfferScript.CONTEXT_ACTIVATION_DATE, activationDate);
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.reactivateSubscription(scriptContext, currentUser);
    }

    public void createOfferTemplate(OfferTemplate entity, String scriptCode, User currentUser) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        OfferScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.createOfferTemplate(scriptContext, currentUser);
    }

    public void updateOfferTemplate(OfferTemplate entity, String scriptCode, User currentUser) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        OfferScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        scriptInterface.updateOfferTemplate(scriptContext, currentUser);
    }
}