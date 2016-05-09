package org.meveo.service.script.revenue;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.scripts.RevenueRecognitionScriptEntity;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.service.script.CustomScriptService;

@Singleton
@Startup
public class RevenueRecognitionScriptService extends CustomScriptService<RevenueRecognitionScriptEntity, RevenueRecognitionScriptInterface> {

    @Inject
    private ResourceBundle resourceMessages;

    @Override
    public void create(RevenueRecognitionScriptEntity revenueRecognitionScript, User creator) throws BusinessException {

        String className = getClassName(revenueRecognitionScript.getScript());
        if (className == null) {
            throw new BusinessException(resourceMessages.getString("message.OfferModelScript.sourceInvalid"));
        }
        revenueRecognitionScript.setCode(getFullClassname(revenueRecognitionScript.getScript()));

        super.create(revenueRecognitionScript, creator);
    }

    @Override
    public RevenueRecognitionScriptEntity update(RevenueRecognitionScriptEntity revenueRecognitionScript, User updater) throws BusinessException {

        String className = getClassName(revenueRecognitionScript.getScript());
        if (className == null) {
            throw new BusinessException(resourceMessages.getString("message.OfferModelScript.sourceInvalid"));
        }
        revenueRecognitionScript.setCode(getFullClassname(revenueRecognitionScript.getScript()));

        revenueRecognitionScript = super.update(revenueRecognitionScript, updater);

        return revenueRecognitionScript;
    }


    /**
     * Compile all Scripts
     */
    @PostConstruct
    void compileAll() {
        List<RevenueRecognitionScriptEntity> revenueRecognitionScripts = findByType(ScriptSourceTypeEnum.JAVA);
        compile(revenueRecognitionScripts);
    }

    // Interface methods
    public void createRevenueSchedule(String scriptCode,ChargeInstance chargeInstance, User currentUser) throws ElementNotFoundException, InvalidScriptException, BusinessException {
    	RevenueRecognitionScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
        scriptInterface.createRevenueSchedule(chargeInstance, currentUser);
    }

}