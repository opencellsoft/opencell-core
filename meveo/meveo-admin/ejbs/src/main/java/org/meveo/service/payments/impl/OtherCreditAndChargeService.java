/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.transaction.Transactional;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.payments.local.CustomerAccountServiceLocal;
import org.meveo.service.payments.local.OCCTemplateServiceLocal;
import org.meveo.service.payments.local.OtherCreditAndChargeServiceLocal;
import org.slf4j.Logger;

/**
 * OtherCreditAndCharge service implementation.
 * 
 * @author Ignas
 * @created 2009.09.04
 */
@Stateless
@Named
public class OtherCreditAndChargeService extends PersistenceService<OtherCreditAndCharge> implements OtherCreditAndChargeServiceLocal {

	@Inject
	private CustomerAccountServiceLocal customerAccountService;

	@Inject
	private OCCTemplateServiceLocal occTemplateService;

	protected Logger log;

	@Transactional
	public void addOCC(String codeOCCTemplate, String descToAppend, CustomerAccount customerAccount, BigDecimal amount, Date dueDate, User user)
			throws BusinessException, Exception {
		log.info("addOCC  codeOCCTemplate:{0}  customerAccount:{1} amount:{2} dueDate:{3}", codeOCCTemplate, (customerAccount == null ? "null"
				: customerAccount.getCode()), amount, dueDate);

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

		if (user == null) {
			log.warn("addOCC user is null");
			throw new BusinessException("user is null");
		}
		OCCTemplate occTemplate = occTemplateService.findByCode(codeOCCTemplate, customerAccount.getProvider().getCode());
		if (occTemplate == null) {
			log.warn("addOCC cannot find OCCTemplate by code:" + codeOCCTemplate);
			throw new BusinessException("cannot find OCCTemplate by code:" + codeOCCTemplate);
		}

		if (customerAccount.getStatus() == CustomerAccountStatusEnum.CLOSE) {
			log.warn("addOCC  customerAccount is closed ");
			throw new BusinessException("customerAccount is closed");
		}

		OtherCreditAndCharge otherCreditAndCharge = new OtherCreditAndCharge();
		otherCreditAndCharge.setCustomerAccount(customerAccount);
		otherCreditAndCharge.setOccCode(occTemplate.getCode());
		if (descToAppend != null) {
			otherCreditAndCharge.setOccDescription(occTemplate.getDescription() + " " + descToAppend);
		} else {
			otherCreditAndCharge.setOccDescription(occTemplate.getDescription());
		}
		otherCreditAndCharge.setAccountCode(occTemplate.getAccountCode());
		otherCreditAndCharge.setAccountCodeClientSide(occTemplate.getAccountCodeClientSide());
		otherCreditAndCharge.setTransactionCategory(occTemplate.getOccCategory());
		otherCreditAndCharge.setDueDate(dueDate);
		otherCreditAndCharge.setTransactionDate(new Date());
		otherCreditAndCharge.setAmount(amount);
		otherCreditAndCharge.setUnMatchingAmount(amount);
		otherCreditAndCharge.setMatchingStatus(MatchingStatusEnum.O);
		customerAccount.getAccountOperations().add(otherCreditAndCharge);
		create(otherCreditAndCharge, user, customerAccount.getProvider());

		log.info("addOCC  codeOCCTemplate:{0}  customerAccount:{1} amount:{2} dueDate:{3} Successful", codeOCCTemplate, customerAccount.getCode(), amount,
				dueDate);
	}

	public void addOCC(String codeOCCTemplate, Long customerAccountId, String customerAccountCode, BigDecimal amount, Date dueDate, User user)
			throws BusinessException, Exception {
		addOCC(codeOCCTemplate, null, customerAccountId, customerAccountCode, amount, dueDate, user);
	}

	public void addOCC(String codeOCCTemplate, String descToAppend, Long customerAccountId, String customerAccountCode, BigDecimal amount, Date dueDate,
			User user) throws BusinessException, Exception {
		log.info("addOCC  codeOCCTemplate:{0}  customerAccountId:{1} customerAccountCode:{2} amount:{3} dueDate:{4}", codeOCCTemplate, customerAccountId,
				customerAccountCode, amount, dueDate);
		CustomerAccount customerAccount = customerAccountService.findCustomerAccount(customerAccountId, customerAccountCode);
		addOCC(codeOCCTemplate, descToAppend, customerAccount, amount, dueDate, user);
	}
}