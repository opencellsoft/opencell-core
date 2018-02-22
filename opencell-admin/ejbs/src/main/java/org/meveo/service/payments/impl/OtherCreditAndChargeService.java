/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.DiscriminatorValue;

import org.meveo.admin.exception.BusinessException;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.service.base.PersistenceService;

/**
 * OtherCreditAndCharge service implementation.
 */
@Stateless
public class OtherCreditAndChargeService extends
		PersistenceService<OtherCreditAndCharge> {

	@Inject
	private OCCTemplateService occTemplateService;
	
	@EJB
	private CustomerAccountService customerAccountService;

	public void addOCC(String codeOCCTemplate, String descToAppend,
			CustomerAccount customerAccount, BigDecimal amount, Date dueDate
			) throws BusinessException {
		log.info(
				"addOCC  codeOCCTemplate:{}  customerAccount:{} amount:{} dueDate:{}",
				new Object[] {
						codeOCCTemplate,
						(customerAccount == null ? "null" : customerAccount
								.getCode()), amount, dueDate });

		if (codeOCCTemplate == null) {
			log.warn("addOCC codeOCCTemplate is null");
			throw new BusinessException("codeOCCTemplate is null");
		}

		if (amount == null) {
			log.warn("addOCC amount is null");
			throw new BusinessException("amount is null");
		}
		if (dueDate == null) {
			log.warn("addOCC dueDate is null");
			throw new BusinessException("dueDate is null");
		}
				
		OCCTemplate occTemplate = occTemplateService.findByCode(
				codeOCCTemplate);
		if (occTemplate == null) {
			log.warn("addOCC cannot find OCCTemplate by code:"
					+ codeOCCTemplate);
			throw new BusinessException("cannot find OCCTemplate by code:"
					+ codeOCCTemplate);
		}

		if (customerAccount.getStatus() == CustomerAccountStatusEnum.CLOSE) {
			log.warn("addOCC  customerAccount is closed ");
			throw new BusinessException("customerAccount is closed");
		}

		OtherCreditAndCharge otherCreditAndCharge = new OtherCreditAndCharge();
		otherCreditAndCharge.setCustomerAccount(customerAccount);
		otherCreditAndCharge.setOccCode(occTemplate.getCode());
		if (descToAppend != null) {
			otherCreditAndCharge.setOccDescription(occTemplate.getDescription()
					+ " " + descToAppend);
		} else {
			otherCreditAndCharge
					.setOccDescription(occTemplate.getDescription());
		}
		otherCreditAndCharge.setAccountingCode(occTemplate.getAccountingCode());
		otherCreditAndCharge.setAccountCodeClientSide(occTemplate
				.getAccountCodeClientSide());
		otherCreditAndCharge.setTransactionCategory(occTemplate
				.getOccCategory());
		otherCreditAndCharge.setDueDate(dueDate);
		otherCreditAndCharge.setTransactionDate(new Date());
		otherCreditAndCharge.setAmount(amount);
		otherCreditAndCharge.setUnMatchingAmount(amount);
		otherCreditAndCharge.setMatchingStatus(MatchingStatusEnum.O);
		customerAccount.getAccountOperations().add(otherCreditAndCharge);
		create(otherCreditAndCharge);

		log.info(
				"addOCC  codeOCCTemplate:{}  customerAccount:{} amount:{} dueDate:{} Successful",
				new Object[] { codeOCCTemplate, customerAccount.getCode(),
						amount, dueDate });
	}

    /**
     * Set the discriminatorValue value, so it would be available in the list of entities right away
     * @throws BusinessException business exception.
     */
	@MeveoAudit
    @Override
    public void create(OtherCreditAndCharge occ) throws BusinessException {

        occ.setType(OtherCreditAndCharge.class.getAnnotation(DiscriminatorValue.class).value());
        super.create(occ);
    }
	
	// public void addOCCk(String codeOCCTemplate, Long customerAccountId,
	// String customerAccountCode, BigDecimal amount, Date dueDate)
	// throws BusinessException, Exception {
	// addOCC(codeOCCTemplate, null, customerAccountId, customerAccountCode,
	// amount, dueDate);
	// }
	//
	// public void addOCC(String codeOCCTemplate, String descToAppend, Long
	// customerAccountId, String customerAccountCode, BigDecimal amount, Date
	// dueDate,
	// User user) throws BusinessException, Exception {
	// log.info("addOCC  codeOCCTemplate:{}  customerAccountId:{} customerAccountCode:{} amount:{} dueDate:{4}",
	// codeOCCTemplate, customerAccountId,
	// customerAccountCode, amount, dueDate);
	// CustomerAccount customerAccount =
	// customerAccountService.findCustomerAccount(customerAccountId,
	// customerAccountCode);
	// addOCC(codeOCCTemplate, descToAppend, customerAccount, amount, dueDate,
	// user);
	// }
}