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

package org.meveo.service.catalog.impl;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.meveo.model.catalog.DiscountPlanTypeEnum;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class DiscountPlanService extends BusinessService<DiscountPlan> {

	@Override
	public void create(DiscountPlan entity) throws BusinessException {
		// check date
		if (!entity.isValid()) {
			log.error("Invalid effectivity dates");
			throw new BusinessException("Invalid effectivity dates");
		}
		if (entity.getDiscountPlanType() != null && (entity.getDiscountPlanType().equals(DiscountPlanTypeEnum.QUOTE) || entity.getDiscountPlanType()
				.equals(DiscountPlanTypeEnum.INVOICE) || entity.getDiscountPlanType().equals(DiscountPlanTypeEnum.INVOICE_LINE)) || entity.getInitialQuantity() == null) {
			entity.setInitialQuantity(0L);
		}

		if (entity.getStatus().equals(DiscountPlanStatusEnum.DRAFT) || entity.getStatus().equals(DiscountPlanStatusEnum.ACTIVE)) {
			entity.setStatusDate(new Date());
			super.create(entity);
		} else {
			log.error("Only status DRAFT and ACTIVE are allowed to create a discount plan: {}", entity.getCode());
			throw new BusinessException("Only status DRAFT and ACTIVE are allowed to create a discount plan: " + entity.getCode());
		}

	}

	@Override
	public DiscountPlan update(DiscountPlan entity) throws BusinessException {
		// check date
		if (!entity.isValid()) {
			log.error("Invalid effectivity dates");
			throw new BusinessException("Invalid effectivity dates");
		}
		return super.update(entity);
	}


    /**
     * @param expression EL exprestion
     * @param customerAccount customer account
     * @param billingAccount billing account
     * @param invoice invoice
     * @param dpi the discount plan instance
     * @return true/false
     * @throws BusinessException business exception.
     */
    public boolean matchDiscountPlanExpression(String expression, CustomerAccount customerAccount, BillingAccount billingAccount, Invoice invoice,OfferTemplate offer,Product product, DiscountPlanInstance dpi) throws BusinessException {
        Boolean result = true;

        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();

        if (expression.indexOf(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT, customerAccount);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_BILLING_ACCOUNT) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_BILLING_ACCOUNT, billingAccount);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_INVOICE_SHORT) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_INVOICE_SHORT, invoice);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_INVOICE) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_INVOICE, invoice);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_DISCOUNT_PLAN_INSTANCE) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_DISCOUNT_PLAN_INSTANCE, dpi);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_OFFER) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_OFFER, offer);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_PRODUCT) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_OFFER, product);
        }
        if (expression.indexOf("su") >= 0) {
            userMap.put("su", invoice.getSubscription());
        }
        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
        try {
            result = (Boolean) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }

	@Override
	public void remove(DiscountPlan entity) throws BusinessException {
		if (entity.getStatus().equals(DiscountPlanStatusEnum.DRAFT) || entity.getStatus().equals(DiscountPlanStatusEnum.ACTIVE)) {
			super.remove(entity);
		} else {
			log.error("Only discount plan with status DRAFT and ACTIVE is allowed to be deleted: {}", entity.getCode());
			throw new BusinessException("Only discount plan with status DRAFT and ACTIVE is allowed to be deleted: " + entity.getCode());
		}

	}

	public List<Long> getDiscountPlanToExpire(Date expireDiscountPlanToDate) {
		List<Long> ids = getEntityManager().createNamedQuery("discountPlan.getExpired", Long.class).setParameter("date", expireDiscountPlanToDate)
				.setParameter("statuses", Arrays.asList(DiscountPlanStatusEnum.ACTIVE, DiscountPlanStatusEnum.IN_USE)).getResultList();
		return ids;
	}
}