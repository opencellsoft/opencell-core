package org.meveo.service.script.revenue;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.service.script.ScriptInstanceService;

@Stateless
public class RevenueRecognitionScriptService implements Serializable {

    private static final long serialVersionUID = -6955270648156956130L;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public void createRevenueSchedule(String scriptCode, ChargeInstance chargeInstance) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        RevenueRecognitionScriptInterface scriptInterface = (RevenueRecognitionScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        scriptInterface.createRevenueSchedule(chargeInstance);
    }
}