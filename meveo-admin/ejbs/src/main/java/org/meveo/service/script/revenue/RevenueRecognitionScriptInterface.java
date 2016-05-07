package org.meveo.service.script.revenue;

import java.util.Date;
import java.util.List;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.payments.RevenueSchedule;
import org.meveo.service.script.ScriptInterface;

public interface RevenueRecognitionScriptInterface extends ScriptInterface {

    /**
     * Called to create the Revenue Schedule of a charge
     * 
     * @param chargeInstance the charge whose revenue is to be recognized
     * @param startDate Contract's start date
     * @param endDate Contract's end date
     * @param currentUser Current user
     * @throws BusinessException
     */
    public List<RevenueSchedule> scheduleRevenue(ChargeInstance chargeInstance,Date startDate,Date endDate, User currentUser) throws BusinessException;

}