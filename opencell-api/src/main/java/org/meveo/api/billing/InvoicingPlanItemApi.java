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

package org.meveo.api.billing;

import java.math.BigDecimal;
import java.util.function.BiFunction;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.response.billing.InvoicingPlanItemDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.cpq.commercial.InvoicingPlan;
import org.meveo.model.cpq.commercial.InvoicingPlanItem;
import org.meveo.service.billing.impl.InvoicingPlanItemService;
import org.meveo.service.cpq.order.InvoicingPlanService;

/**
 * CRUD API for {@link InvoicingPlanItem}.
 * 
 */
@Stateless
public class InvoicingPlanItemApi extends BaseCrudApi<InvoicingPlanItem, InvoicingPlanItemDto> {

	@Inject
	private InvoicingPlanItemService invoicingPlanItemService;

	@Inject
	private InvoicingPlanService invoicingPlanService;

	/**
	 * Creates a new InvoicingPlanItem entity.
	 * 
	 * @param postData posted data to API
	 * 
	 * @throws MeveoApiException meveo api exception
	 * @throws BusinessException business exception.
	 */
	public InvoicingPlanItem create(InvoicingPlanItemDto postData) throws MeveoApiException, BusinessException {

		String invoicingPlanItemCode = postData.getCode();

		if (StringUtils.isBlank(invoicingPlanItemCode)) {
			missingParameters.add("invoicingPlanItemCode");
		}

		handleMissingParametersAndValidate(postData);

		InvoicingPlanItem invoicingPlanItem = invoicingPlanItemService.findByCode(invoicingPlanItemCode);
		if (invoicingPlanItem != null) {
			throw new EntityAlreadyExistsException(InvoicingPlanItem.class, invoicingPlanItemCode);
		}
		invoicingPlanItem = dtoToEntity(postData, new InvoicingPlanItem(), true);
		populateCustomFields(postData.getCustomFields(), invoicingPlanItem, true);
		invoicingPlanItemService.create(invoicingPlanItem);

		return invoicingPlanItem;
	}

	/**
	 * Updates a InvoicingPlanItem Entity based on invoicingPlanItem code.
	 * 
	 * @param postData posted data to API
	 * 
	 * @throws MeveoApiException meveo api exception
	 * @throws BusinessException business exception.
	 */
	public InvoicingPlanItem update(InvoicingPlanItemDto postData) throws MeveoApiException, BusinessException {
		String invoicingPlanItemCode = postData.getCode();
		if (StringUtils.isBlank(invoicingPlanItemCode)) {
			missingParameters.add("invoicingPlanItemCode");
		}

		handleMissingParametersAndValidate(postData);

		InvoicingPlanItem invoicingPlanItem = invoicingPlanItemService.findByCode(invoicingPlanItemCode);
		if (invoicingPlanItem == null) {
			throw new EntityDoesNotExistsException(InvoicingPlanItem.class, invoicingPlanItemCode);
		}
		dtoToEntity(postData, invoicingPlanItem, false);

		invoicingPlanItem = invoicingPlanItemService.update(invoicingPlanItem);
		populateCustomFields(postData.getCustomFields(), invoicingPlanItem, false);
		return invoicingPlanItem;
	}

	private InvoicingPlanItem dtoToEntity(InvoicingPlanItemDto postData, InvoicingPlanItem invoicingPlanItem, boolean isNewEntity) {
		final String billingPlanCode = postData.getBillingPlanCode();

		if (!StringUtils.isBlank(billingPlanCode)) {
			InvoicingPlan invoicingPlan = invoicingPlanService.findByCode(billingPlanCode);
			if (invoicingPlan == null) {
				throw new EntityDoesNotExistsException(InvoicingPlan.class, billingPlanCode);
			}
			invoicingPlanItem.setBillingPlan(invoicingPlan);
			var items = invoicingPlanItemService.findByInvoicingPlanCode(invoicingPlan);
			if(!items.isEmpty() && postData.getAdvancement() != null) {
				boolean isAdvancementExist = items.stream().anyMatch(ipi -> ipi.getAdvancement() == postData.getAdvancement() && isNewEntity);
				if(isAdvancementExist) {
					throw new EntityAlreadyExistsException("Invoicing plan lines with advancement " + postData.getAdvancement() + " already exist");
				}
				BigDecimal rateToBill =  items.stream().filter(invPlan -> invPlan.getId() !=  invoicingPlanItem.getId()).map(InvoicingPlanItem::getRateToBill).reduce(BigDecimal.ZERO, BigDecimal::add);
				BigDecimal totalRate = rateToBill.add(postData.getRateToBill() != null ? postData.getRateToBill() : BigDecimal.ZERO);
				totalRate.add(rateToBill);
				if(totalRate.intValue() > 100) {
					throw new InvalidParameterException("Down payment of invoicing plan can not be more than 100, current down payment is : " + totalRate.intValue());
				}
			}
			if(postData.getAdvancement() != null && postData.getAdvancement() > 100) {
				throw new InvalidParameterException("Advancement of invoicing plan can not be more than 100");
			}
			if(postData.getRateToBill() != null && postData.getRateToBill().intValue() > 100) {
				throw new InvalidParameterException("Down payment of invoicing plan can not be more than 100");
			}
		}
		invoicingPlanItem.setCode(
				StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
		if (postData.getDescription() != null) {
			invoicingPlanItem.setDescription(postData.getDescription());
		}
		if (postData.getAdvancement() != null) {
			invoicingPlanItem.setAdvancement(postData.getAdvancement());
		}
		if (postData.getRateToBill() != null) {
			invoicingPlanItem.setRateToBill(postData.getRateToBill());
		}
		populateCustomFields(postData.getCustomFields(), invoicingPlanItem, isNewEntity);
		return invoicingPlanItem;
	}

	@Override
	protected BiFunction<InvoicingPlanItem, CustomFieldsDto, InvoicingPlanItemDto> getEntityToDtoFunction() {
		return InvoicingPlanItemDto::new;
	}
}
