/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
     * @param woList wallet operation list
     * @return list of revenue schedule.
     * @throws BusinessException business exception
     */
    List<RevenueSchedule> scheduleRevenue(ChargeInstance chargeInstance, List<WalletOperation> woList, Date startDate, Date endDate) throws BusinessException;

}