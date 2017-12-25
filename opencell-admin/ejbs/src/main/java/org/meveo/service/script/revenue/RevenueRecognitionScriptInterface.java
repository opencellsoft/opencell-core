package org.meveo.service.script.revenue;

import java.util.Date;
import java.util.List;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.finance.RevenueSchedule;
import org.meveo.service.script.ScriptInterface;

/**
 * @author phung
 *
 */
public interface RevenueRecognitionScriptInterface extends ScriptInterface {

    /**
     * Called to create the Revenue Schedule of a charge.
     * 
     * @param chargeInstance the charge whose revenue is to be recognized
     * @throws BusinessException business exception
     */
    void createRevenueSchedule(ChargeInstance chargeInstance) throws BusinessException;

    /**
     * Called byd default createRevenueSchedule impl to create the Revenue Schedule. with just recognized Revenue set
     * 
     * @param chargeInstance the charge whose revenue is to be recognized
     * @param startDate Contract's start date
     * @param endDate Contract's end date
     * @return list of revenue schedule.
     * @throws BusinessException business exception
     */
    List<RevenueSchedule> scheduleRevenue(ChargeInstance chargeInstance, List<WalletOperation> woList, Date startDate, Date endDate) throws BusinessException;

}