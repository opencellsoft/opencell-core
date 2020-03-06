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
 * 
 * @author Edward P. Legaspi
 * @author melyoussoufi
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
 */
@Stateless
public class OtherCreditAndChargeService extends
		PersistenceService<OtherCreditAndCharge> {

	@Inject
	private OCCTemplateService occTemplateService;
	
	@EJB
	private CustomerAccountService customerAccountService;

	public OtherCreditAndCharge addOCC(String codeOCCTemplate, String descToAppend,
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

		if (customerAccount != null && customerAccount.getStatus() == CustomerAccountStatusEnum.CLOSE) {
			log.warn("addOCC  customerAccount is closed ");
			throw new BusinessException("customerAccount is closed");
		}

		OtherCreditAndCharge otherCreditAndCharge = new OtherCreditAndCharge();
		otherCreditAndCharge.setCustomerAccount(customerAccount);
		otherCreditAndCharge.setCode(occTemplate.getCode());
		if (descToAppend != null) {
			otherCreditAndCharge.setDescription(occTemplate.getDescription()
					+ " " + descToAppend);
		} else {
			otherCreditAndCharge
					.setDescription(occTemplate.getDescription());
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
		
		if (customerAccount != null) {
		    customerAccount.getAccountOperations().add(otherCreditAndCharge);
		}
		
		create(otherCreditAndCharge);
		if (customerAccount != null) {
		    log.info(
		        "addOCC  codeOCCTemplate:{}  customerAccount:{} amount:{} dueDate:{} Successful",
		        new Object[] { codeOCCTemplate, customerAccount.getCode(),
		                amount, dueDate });
		}
		return otherCreditAndCharge;
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