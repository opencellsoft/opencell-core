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

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.RejectedBillingAccount;
import org.meveo.model.billing.ThresholdOptionsEnum;
import org.meveo.service.base.PersistenceService;

@Stateless
public class RejectedBillingAccountService extends PersistenceService<RejectedBillingAccount> {
	
	@Inject
	private BillingAccountService billingAccountService;

    /**
     * Register that billing account invoicing has failed
     * 
     * @param billingAccount Billing account
     * @param billingRun Billing run
     * @param reason Reason why it failed
     * @throws BusinessException Business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void create(Long billingAccountId, BillingRun billingRun, String reason) throws BusinessException {
        RejectedBillingAccount rejectedBA = new RejectedBillingAccount(billingAccountService.findById(billingAccountId), billingRun, reason);
        super.create(rejectedBA);
    }
    
    /**
     * Register that billing account invoicing has failed
     * 
     * @param billingRun Billing run
     * @param billingCycle billing cycle
     * @param lastTransactionDate 
     * 
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public int createRejectedBAsForThreshold(BillingRun billingRun, BillingCycle billingCycle, Date lastTransactionDate) {
		final String positiveCondition = ThresholdOptionsEnum.POSITIVE_RT.equals(billingCycle.getCheckThreshold()) ? "and rt.amount_With_Tax>0 " : "";
		final String amountToSum = appProvider.isEntreprise() ? "amount_Without_Tax" : "amount_With_Tax";
		String query = null;
		switch (billingCycle.getThresholdLevel()) {
		case CUSTOMER:
			query="insert into billing_rejected_billing_accounts(billing_account,billing_run,reject_cause) (select q.baID, "+billingRun.getId()+", 'threshold' FROM "+
					" (select b.id as baID from billing_billing_account b left join ar_customer_account ca0 on ca0.id=b.customer_account_id  where b.billing_run="+billingRun.getId()+" and ca0.customer_id in (select c.id from billing_rated_transaction rt left join billing_billing_account ba on ba.id=rt.billing_account__id left join ar_customer_account ca on c.id=ba.customer_account_id"+
					" where ba.billing_run="+billingRun.getId()+" and rt.status='OPEN' and rt.usage_date<:lastTransactionDate and c.check_threshold='"+billingCycle.getCheckThreshold()+"' "+positiveCondition+ 
					" group by c.id having sum(rt."+amountToSum+") < (case when c.invoicing_threshold is not null then c.invoicing_threshold else "+billingCycle.getInvoicingThreshold()+" end)))as q)";

			break;
		case CUSTOMER_ACCOUNT:
			query="insert into billing_rejected_billing_accounts(billing_account,billing_run,reject_cause) (select q.baID, "+billingRun.getId()+", 'threshold' FROM "+
					" (select b.id as baID from billing_billing_account b where b.billing_run="+billingRun.getId()+" and b.customer_account_id in (select ca.id  from billing_rated_transaction rt left join billing_billing_account ba on ba.id=rt.billing_account__id left join ar_customer_account ca on ca.id=ba.customer_account_id"+
					" where ba.billing_run="+billingRun.getId()+" and rt.status='OPEN' and rt.usage_date<:lastTransactionDate and ca.check_threshold='"+billingCycle.getCheckThreshold()+"' "+positiveCondition+ 
					" group by ca.id having sum(rt."+amountToSum+") < (case when ca.invoicing_threshold is not null then ca.invoicing_threshold else "+billingCycle.getInvoicingThreshold()+" end)))as q)";

			break;
		case BILLING_ACCOUNT:
			query="insert into billing_rejected_billing_accounts(billing_account,billing_run,reject_cause) (select ba.id, "+billingRun.getId()+", 'threshold' FROM "+
					" billing_rated_transaction rt left join billing_billing_account ba on ba.id=rt.billing_account__id " + 
					" where ba.billing_run="+billingRun.getId()+" and rt.status='OPEN' and rt.usage_date<:lastTransactionDate and ba.check_threshold='"+billingCycle.getCheckThreshold()+"' "+positiveCondition+ 
					" group by ba.id having sum(rt."+amountToSum+") < (case when ba.invoicing_threshold is not null then ba.invoicing_threshold else "+billingCycle.getInvoicingThreshold()+" end))";
			break;
		default:
			break;
		}
		if(query!=null) {
			log.info("========== CHECK BillingAccount REJECTS BEFORE INVOICING BY THRESHOLD {}==========",billingCycle.getCheckThreshold());
			return getEntityManager().createNativeQuery(query).setParameter("lastTransactionDate", lastTransactionDate).executeUpdate();
		}
		return 0;
	}
}