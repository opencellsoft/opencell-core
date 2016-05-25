package org.meveo.service.script.revenue;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.script.CustomScriptService;

@Singleton
@Startup
public class RevenueRecognitionScriptService extends CustomScriptService<ScriptInstance, RevenueRecognitionScriptInterface> {

    // Interface methods
    public void createRevenueSchedule(String scriptCode, ChargeInstance chargeInstance, User currentUser) throws ElementNotFoundException, InvalidScriptException,
            BusinessException {
        RevenueRecognitionScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
        scriptInterface.createRevenueSchedule(chargeInstance, currentUser);
    }

}