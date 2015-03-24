/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.dunning;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.commons.utils.ParamBean;
import org.meveo.model.payments.ActionDunning;
import org.meveo.model.payments.ActionPlanItem;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DunningActionStatusEnum;
import org.meveo.model.payments.DunningActionTypeEnum;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.DunningPlan;
import org.meveo.model.payments.DunningPlanTransition;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.payments.impl.ActionDunningService;
import org.meveo.service.payments.impl.ActionPlanItemService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.DunningPlanTransitionService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.OtherCreditAndChargeService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Upgrade dunninglevel for one customerAccount
 * 
 * @author anasseh
 * @created 03.12.2010
 * 
 */

@Named
public class UpgradeDunningLevel {

	private static final Logger logger = LoggerFactory
			.getLogger(UpgradeDunningLevel.class);

	private static final String DUNNING_BALANCE_FLAG = "bayad.dunning.blanceFlag";

	@Inject
	DunningUtils dunningUtils;

	@Inject
	UserService userService;

	@Inject
	DunningPlanTransitionService dunningPlanTransitionService;

	@Inject
	RecordedInvoiceService recordedInvoiceService;

	@Inject
	ActionPlanItemService actionPlanItemService;

	@Inject
	OCCTemplateService oCCTemplateService;

	@Inject
	CustomerAccountService customerAccountService;

	@Inject
	OtherCreditAndChargeService otherCreditAndChargeService;

	@Inject
	ActionDunningService actionDunningService;

	public UpgradeDunningReturn execute(CustomerAccount customerAccount,
			BigDecimal balanceExigible, DunningPlan dunningPlan)
			throws Exception {
		logger.info("UpgradeDunningLevelStep ...");
		UpgradeDunningReturn upgradeDunningReturn = new UpgradeDunningReturn();
		logger.info("UpgradeDunningLevelStep customerAccount.code:"
				+ customerAccount.getCode());
		ParamBean parambean = ParamBean.getInstance();
		if (balanceExigible.compareTo(BigDecimal.ZERO) == Integer
				.parseInt(parambean.getProperty(DUNNING_BALANCE_FLAG, "1"))) {
			logger.info("UpgradeDunningLevelStep balance in dunning");
			logger.info("UpgradeDunningLevelStep customerAccount.dunningLevel:"
					+ customerAccount.getDunningLevel());
			DunningLevelEnum nextLevel = DunningUtils
					.getNextDunningLevel(customerAccount.getDunningLevel());
			if (nextLevel == null) {
				logger.info("UpgradeDunningLevelStep  max DunningLevel");
				return upgradeDunningReturn;
			} else {
				logger.info("UpgradeDunningLevelStep nextLevel:" + nextLevel);
				DunningPlanTransition dunningPlanTransition = dunningPlanTransitionService
						.getDunningPlanTransition(
								customerAccount.getDunningLevel(), nextLevel,
								dunningPlan);
				if (dunningPlanTransition == null) {
					logger.info("UpgradeDunningLevelStep dunningPlanTransition not found fromLevel:"
							+ customerAccount.getDunningLevel()
							+ " , toLevel:"
							+ nextLevel
							+ ", dunningplan:"
							+ dunningPlan.getCode());
					return upgradeDunningReturn;
				} else {
				    logger.info("UpgradeDunningLevelStep dunningPlanTransition found:"+dunningPlanTransition.getId());
				}
				if(customerAccount.getDateDunningLevel()==null && customerAccount.getAuditable()!=null){
				    customerAccount.setDateDunningLevel(customerAccount.getAuditable().getCreated());
				}
				if (DateUtils.addDaysToDate(
						customerAccount.getDateDunningLevel(),
						dunningPlanTransition.getWaitDuration()).before(
						new Date())) {
					List<RecordedInvoice> recordedInvoices = recordedInvoiceService
							.getRecordedInvoices(customerAccount,
									MatchingStatusEnum.O,false);
					if (recordedInvoices != null && !recordedInvoices.isEmpty()) {
						RecordedInvoice recordedInvoice = recordedInvoices
								.get(0);
						if (DateUtils.addDaysToDate(
								recordedInvoices.get(0).getDueDate(),
								dunningPlanTransition.getDelayBeforeProcess())
								.before(new Date())) {
							if (balanceExigible.compareTo(dunningPlanTransition
									.getThresholdAmount()) == 1) {
								for (ActionPlanItem actionPlanItem : actionPlanItemService
										.getActionPlanItems(dunningPlan,
												dunningPlanTransition)) {
									BigDecimal amoutDue = balanceExigible;
									ActionDunning actionDunning = new ActionDunning();
									actionDunning
											.setCustomerAccount(customerAccount);
									actionDunning
											.setRecordedInvoice(recordedInvoice);
									actionDunning.setCreationDate(new Date());
									actionDunning.setTypeAction(actionPlanItem
											.getActionType());
									actionDunning
											.setStatus(DunningActionStatusEnum.E);
									actionDunning.setStatusDate(new Date());
									actionDunning.setFromLevel(customerAccount
											.getDunningLevel());
									actionDunning
											.setToLevel(dunningPlanTransition
													.getDunningLevelTo());
									actionDunning
											.setActionPlanItem(actionPlanItem);
									actionDunning.setProvider(customerAccount
											.getProvider());
									if (actionPlanItem.getActionType() == DunningActionTypeEnum.CHARGE) {
										addOCC(customerAccount,
												actionPlanItem
														.getChargeAmount());
										amoutDue = amoutDue.add(actionPlanItem
												.getChargeAmount());
									}
									actionDunning.setAmountDue(amoutDue);

									upgradeDunningReturn.getListActionDunning()
											.add(actionDunning);
								}

								customerAccount
										.setDunningLevel(dunningPlanTransition
												.getDunningLevelTo());
								customerAccount.setDateDunningLevel(new Date());
								upgradeDunningReturn.setUpgraded(true);
								customerAccountService.update(customerAccount);
								logger.info("UpgradeDunningLevelStep   upgrade ok");
							} else {
								logger.info("UpgradeDunningLevelStep   ThresholdAmount < invoice.amount");
							}
						} else {
							logger.info("UpgradeDunningLevelStep DelayBeforeProcess : notYet");
						}
					} else {
						logger.info("UpgradeDunningLevelStep  no invoice founded");
					}
				} else {
					logger.info("UpgradeDunningLevelStep  in WaitDuration");
				}
			}
		}

		return upgradeDunningReturn;
	}

	private OtherCreditAndCharge addOCC(CustomerAccount customerAccount,
			BigDecimal chargeAmount) throws Exception {

		OCCTemplate dunningOccTemplate = oCCTemplateService
				.getDunningOCCTemplate(customerAccount.getProvider().getCode());
		OtherCreditAndCharge occ = new OtherCreditAndCharge();
		occ.setAccountCode(dunningOccTemplate.getAccountCode());
		occ.setOccCode(dunningOccTemplate.getCode());
		occ.setOccDescription(dunningOccTemplate.getDescription());
		occ.setTransactionCategory(dunningOccTemplate.getOccCategory());
		occ.setAccountCodeClientSide(dunningOccTemplate
				.getAccountCodeClientSide());
		occ.setAmount(chargeAmount);
		occ.setUnMatchingAmount(chargeAmount);
		occ.setMatchingAmount(BigDecimal.ZERO);
		occ.setCustomerAccount(customerAccount);
		occ.setMatchingStatus(MatchingStatusEnum.O);
		occ.setTransactionDate(new Date());
		occ.setOperationDate(new Date());
		occ.setDueDate(new Date());
		occ.setProvider(customerAccount.getProvider());
		occ.setAuditable(DunningUtils.getAuditable(userService.getSystemUser()));
		otherCreditAndChargeService.create(occ);
		return occ;

	}

}
