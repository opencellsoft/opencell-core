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

package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.IBillableEntity;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BillingRunExtensionService extends PersistenceService<BillingRun> {

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateBRAmounts(Long billingRunId, List<IBillableEntity> entites) throws BusinessException {

        log.debug("Update BillingRun {} total amounts", billingRunId);
        BigDecimal amountWithoutTax = BigDecimal.ZERO;
        BigDecimal amountWithTax = BigDecimal.ZERO;
        BigDecimal amountTax = BigDecimal.ZERO;

        BillingRun billingRun = findById(billingRunId);

        for (IBillableEntity entity : entites) {
            if (entity.getTotalInvoicingAmountTax() != null) {
                amountTax = amountTax.add(entity.getTotalInvoicingAmountTax());
                amountWithoutTax = amountWithoutTax.add(entity.getTotalInvoicingAmountWithoutTax());
                amountWithTax = amountWithTax.add(entity.getTotalInvoicingAmountWithTax());
            }
        }

        billingRun.setPrAmountWithoutTax(amountWithoutTax);
        billingRun.setPrAmountWithTax(amountWithTax);
        billingRun.setPrAmountTax(amountTax);

        billingRun = updateNoCheck(billingRun);
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BillingRun updateBillingRun(Long billingRunId, Integer sizeBA, Integer billableBA, BillingRunStatusEnum status, Date dateStatus) throws BusinessException {

        log.debug("Update BillingRun {} to status {}", billingRunId, status);
        BillingRun billingRun = findById(billingRunId);

        if (sizeBA != null) {
            billingRun.setBillingAccountNumber(sizeBA);
        }
        if (billableBA != null) {
            billingRun.setBillableBillingAcountNumber(billableBA);
        }
        if (dateStatus != null) {
            billingRun.setProcessDate(dateStatus);
        }
        billingRun.setStatus(status);
        return updateNoCheck(billingRun);
    }
}