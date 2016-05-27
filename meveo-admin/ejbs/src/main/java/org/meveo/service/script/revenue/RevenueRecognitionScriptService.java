package org.meveo.service.script.revenue;

import java.io.Serializable;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.service.script.ScriptInstanceService;

@Singleton
@Startup
public class RevenueRecognitionScriptService implements Serializable {

    private static final long serialVersionUID = -6955270648156956130L;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public void createRevenueSchedule(String scriptCode, ChargeInstance chargeInstance, User currentUser) throws ElementNotFoundException, InvalidScriptException,
            BusinessException {
        RevenueRecognitionScriptInterface scriptInterface = (RevenueRecognitionScriptInterface) scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);
        scriptInterface.createRevenueSchedule(chargeInstance, currentUser);
    }
}