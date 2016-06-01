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
package org.meveo.service.billing.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.payments.impl.OCCTemplateService;

@Stateless
public class InvoiceTypeService extends BusinessService<InvoiceType> {

	@Inject
	CustomFieldInstanceService customFieldInstanceService;

	@Inject
	OCCTemplateService oCCTemplateService;
	
	ParamBean param  = ParamBean.getInstance();

	public InvoiceType getDefaultType(String typeCode, User currentUser) throws BusinessException {
		InvoiceType defaultInvoiceType = findByCode(typeCode, currentUser.getProvider());
		if (defaultInvoiceType != null) {
			return defaultInvoiceType;
		}

		OCCTemplate occTemplate = null;

		String occCode = "accountOperationsGenerationJob.occCode";
		String occCodeDefaultValue = "FA_FACT";
		if (param.getProperty("invoiceType.adjustement.code", "ADJ").equals(typeCode)) {

			occCode = "accountOperationsGenerationJob.occCodeAdjustement";
			occCodeDefaultValue = "FA_ADJ";
		}
		String occTemplateCode = null;
		try {
			occTemplateCode = (String) customFieldInstanceService.getOrCreateCFValueFromParamValue(occCode, occCodeDefaultValue, currentUser.getProvider(), true, currentUser);
			log.debug("occTemplateCode:" + occTemplateCode);
			occTemplate = oCCTemplateService.findByCode(occTemplateCode, currentUser.getProvider());
		} catch (Exception e) {
			log.error("error while getting occ template ", e);
			throw new BusinessException("Cannot found OCC Template for invoice");
		}

		if (occTemplate == null) {
			throw new BusinessException("Cannot found OCC Template for invoice");
		}

		defaultInvoiceType = new InvoiceType();
		defaultInvoiceType.setCode(typeCode);
		defaultInvoiceType.setOccTemplate(occTemplate);
		create(defaultInvoiceType, currentUser);
		return defaultInvoiceType;
	}
	
	public Long getMaxCurrentInvoiceNumber() throws BusinessException {
		Long max = getEntityManager()
				.createNamedQuery("InvoiceType.currentInvoiceNb", Long.class).getSingleResult();
		
		return max == null ? 0 : max;
		
	}

	public InvoiceType getDefaultAdjustement(User currentUser) throws BusinessException {
		return getDefaultType(getAdjustementCode(), currentUser);
	}

	public InvoiceType getDefaultCommertial(User currentUser) throws BusinessException {
		return getDefaultType(getCommercialCode(), currentUser);
	}

	public String getCommercialCode() {
		return param.getProperty("invoiceType.commercial.code", "COM");
	}
	
	public String getAdjustementCode() {
		return param.getProperty("invoiceType.adjustement.code", "ADJ");
	}
}
