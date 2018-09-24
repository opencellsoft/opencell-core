package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.BillingAccount;
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
    public void updateBRAmounts(Long billingRunId, List<BillingAccount> entites) throws BusinessException {

        log.debug("updateBRAmounts for billingRun {} in new transaction", billingRunId);
        BigDecimal amountWithoutTax = BigDecimal.ZERO;
        BigDecimal amountWithTax = BigDecimal.ZERO;
        BigDecimal amountTax = BigDecimal.ZERO;
        
        BillingRun billingRun = findById(billingRunId);
        
        for(BillingAccount entity : entites) {
            amountTax = amountTax.add(entity.getTotalInvoicingAmountTax());
            amountWithoutTax = amountWithoutTax.add(entity.getTotalInvoicingAmountWithoutTax());
            amountWithTax = amountWithTax.add(entity.getTotalInvoicingAmountWithTax());
        }
            
        billingRun.setPrAmountWithoutTax(amountWithoutTax);
        billingRun.setPrAmountWithTax(amountWithTax);
        billingRun.setPrAmountTax(amountTax);
        
        billingRun = updateNoCheck(billingRun);
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateBillingRun(Long billingRunId, Integer sizeBA, Integer billableBA, BillingRunStatusEnum status, Date dateStatus) throws BusinessException {

        log.error("UpdateBillingRun in new transaction");
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
        updateNoCheck(billingRun);
    }
}