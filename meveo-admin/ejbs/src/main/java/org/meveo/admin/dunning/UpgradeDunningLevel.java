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
package org.meveo.admin.dunning;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.payments.ActionDunning;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DunningActionStatusEnum;
import org.meveo.model.payments.DunningActionTypeEnum;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.wf.WFAction;
import org.meveo.model.wf.WFTransition;
import org.meveo.model.wf.Workflow;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.payments.impl.ActionDunningService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.OtherCreditAndChargeService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.meveo.service.wf.WFActionService;
import org.meveo.service.wf.WFTransitionService;
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
	WFTransitionService dunningPlanTransitionService;

	@Inject
	RecordedInvoiceService recordedInvoiceService;

	@Inject
	WFActionService actionPlanItemService;

	@Inject
	OCCTemplateService oCCTemplateService;

	@Inject
	CustomerAccountService customerAccountService;

	@Inject
	OtherCreditAndChargeService otherCreditAndChargeService;

	@Inject
	ActionDunningService actionDunningService;

	
	private boolean matchExpression(String expression,
			CustomerAccount customerAccount,WFTransition dunningPlanTransition,BigDecimal balanceExigible) throws BusinessException {
		Boolean result = true;
		if (StringUtils.isBlank(expression)) {
			return result;
		}
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		userMap.put("ca", customerAccount);
		
		if (expression.indexOf("dt") >= 0) {
			userMap.put("dt", dunningPlanTransition);
		}
		if (expression.indexOf("be") >= 0) {
			userMap.put("be", balanceExigible);
		}
		if (expression.indexOf("iv") >= 0) {
			List<RecordedInvoice> recordedInvoices = recordedInvoiceService
					.getRecordedInvoices(customerAccount, MatchingStatusEnum.O,
							false);
			if (recordedInvoices != null && !recordedInvoices.isEmpty()) {
				RecordedInvoice recordedInvoice = recordedInvoices.get(0);
				userMap.put("iv", recordedInvoice);
			}
		}
		Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap,
				Boolean.class);
		try {
			result = (Boolean) res;
		} catch (Exception e) {
			throw new BusinessException("Expression " + expression
					+ " do not evaluate to boolean but " + res);
		}
		return result;
	}

}
