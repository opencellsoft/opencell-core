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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.crm.impl.CustomFieldInstanceService;

/**
 * OCCTemplate service implementation.
 * 
 *  @author anasseh
 *  @lastModifiedVersion 5.0
 */
@Stateless
public class OCCTemplateService extends BusinessService<OCCTemplate> {

	private static final String DUNNING_OCC_CODE = "bayad.dunning.occCode";
	private static final String DDREQUEST_OCC_CODE = "bayad.ddrequest.occCode";
		
    @Inject
    private CustomFieldInstanceService customFieldInstanceService;

	@SuppressWarnings("unchecked")
	public List<OCCTemplate> getListOccSortedByName() {
		log.debug("start of find list {} SortedByName ..", "OCCTemplate");
		QueryBuilder qb = new QueryBuilder(OCCTemplate.class, "c");
		qb.addOrderCriterion("description", true);
		List<OCCTemplate> occTemplates = (List<OCCTemplate>) qb.getQuery(getEntityManager()).getResultList();
		log.debug("start of find list {} SortedByName   result {}", new Object[] { "OCCTemplate", occTemplates == null ? "null" : occTemplates.size() });
		return occTemplates;
	}

	public OCCTemplate getDunningOCCTemplate() throws Exception {
		String occCodeDefaultValue = "INV_FEE";				
		return getOccTemplateByCFKeyOrProperty(DUNNING_OCC_CODE, occCodeDefaultValue);
	}

	public OCCTemplate getDirectDebitOCCTemplate() {				
		String occCodeDefaultValue = "DD_OCC";				
		return getOccTemplateByCFKeyOrProperty(DDREQUEST_OCC_CODE, occCodeDefaultValue);
	}

    private OCCTemplate getOccTemplateByCFKeyOrProperty(String occCodeKey, String occCodeDefaultValue) {

        try {
            String occTemplateCode = null;
            occTemplateCode = (String) customFieldInstanceService.getOrCreateCFValueFromParamValue(occCodeKey, occCodeDefaultValue, appProvider, true);
            return findByCode(occTemplateCode);

        } catch (Exception e) {
            log.error("error while getting occ template ", e);
            return null;
        }
    }
    
    public OCCTemplate getOccTemplateFromInvoiceType(BigDecimal amount, InvoiceType invoiceType, Invoice invoice,BillingRun billingRun) throws BusinessException {
        OCCTemplate occTemplate = null;
        if(invoiceType == null) {
            throw new BusinessException("Cant find OccTemplate when invoiceType is null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            String occTemplateCode = evaluateStringExpression(invoiceType.getOccTemplateNegativeCodeEl(), invoice, billingRun);
            if (!StringUtils.isBlank(occTemplateCode)) {
                occTemplate = findByCode(occTemplateCode);
            }

            if(occTemplate == null) {
                occTemplate = invoiceType.getOccTemplateNegative();
            }

            if (occTemplate == null) {
                throw new BusinessException("Cant find negative OccTemplate");
            }
        } else {
            String occTemplateCode = evaluateStringExpression(invoiceType.getOccTemplateCodeEl(), invoice, billingRun);
            if (!StringUtils.isBlank(occTemplateCode)) {
                occTemplate = findByCode(occTemplateCode);
            }

            if(occTemplate == null) {
                occTemplate = invoiceType.getOccTemplate();
            }
            
            if (occTemplate == null) {
                throw new BusinessException("Cant find OccTemplate");
            }
        }
        return occTemplate;
    }
    
    /**
     * @param expression EL expression
     * @param invoice invoice
     * @param billingRun billingRun
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    public String evaluateStringExpression(String expression, Invoice invoice, BillingRun billingRun) throws BusinessException {
        String result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = constructElContext(expression, invoice, billingRun);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
        try {
            result = (String) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to string but " + res);
        }
        return result;
    }
    
    /**
     * @param expression EL expression
     * @param invoice invoice
     * @param billingRun billingRun
     * @return userMap userMap
     */
    private Map<Object, Object> constructElContext(String expression, Invoice invoice, BillingRun billingRun) {

        Map<Object, Object> userMap = new HashMap<Object, Object>();
        BillingAccount billingAccount = invoice.getBillingAccount();

        if (expression.indexOf("invoice") >= 0) {
            userMap.put("invoice", invoice);
        }
        if (expression.indexOf("br") >= 0) {
            userMap.put("br", billingRun);
        }
        if (expression.indexOf("ba") >= 0) {
            userMap.put("ba", billingAccount);
        }
        if (expression.indexOf("ca") >= 0) {
            userMap.put("ca", billingAccount.getCustomerAccount());
        }
        if (expression.indexOf("c") >= 0) {
            userMap.put("c", billingAccount.getCustomerAccount().getCustomer());
        }
        if (expression.indexOf("prov") >= 0) {
            userMap.put("prov", appProvider);
        }

        return userMap;
    }

}
