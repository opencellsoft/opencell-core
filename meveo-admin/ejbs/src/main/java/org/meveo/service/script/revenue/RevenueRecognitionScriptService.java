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
import org.meveo.model.scripts.RevenueRecognitionScript;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.service.script.CustomScriptService;

@Singleton
@Startup
public class RevenueRecognitionScriptService extends CustomScriptService<RevenueRecognitionScript, RevenueRecognitionScriptInterface> {

    @Inject
    private ResourceBundle resourceMessages;

    @Override
    public void create(RevenueRecognitionScript revenueRecognitionScript, User creator) throws BusinessException {

        String className = getClassName(revenueRecognitionScript.getScript());
        if (className == null) {
            throw new BusinessException(resourceMessages.getString("message.OfferModelScript.sourceInvalid"));
        }
        revenueRecognitionScript.setCode(getFullClassname(revenueRecognitionScript.getScript()));

        super.create(revenueRecognitionScript, creator);
    }

    @Override
    public RevenueRecognitionScript update(RevenueRecognitionScript revenueRecognitionScript, User updater) throws BusinessException {

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
        List<RevenueRecognitionScript> revenueRecognitionScripts = findByType(ScriptSourceTypeEnum.JAVA);
        compile(revenueRecognitionScripts);
    }

    // Interface methods

    public List<String> scheduleRevenue(String scriptCode,ChargeInstance chargeInstance,Date startDate,Date endDate, User currentUser) throws ElementNotFoundException, InvalidScriptException, BusinessException {
    	RevenueRecognitionScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
        return scriptInterface.scheduleRevenue(chargeInstance,startDate,endDate, currentUser);
    }

}