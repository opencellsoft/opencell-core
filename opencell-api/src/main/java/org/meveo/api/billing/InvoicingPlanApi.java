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

import java.util.function.BiFunction;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.response.billing.InvoicingPlanDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.cpq.commercial.InvoicingPlan;
import org.meveo.service.cpq.order.InvoicingPlanService;

/**
 * CRUD API for {@link InvoicingPlan}.
 * 
 */
@Stateless
public class InvoicingPlanApi extends BaseCrudApi<InvoicingPlan, InvoicingPlanDto> {

	@Inject
	private InvoicingPlanService invoicingPlanService;

	/**
	 * Creates a new InvoicingPlan entity.
	 * 
	 * @param postData posted data to API
	 * 
	 * @throws MeveoApiException meveo api exception
	 * @throws BusinessException business exception.
	 */
	public InvoicingPlan create(InvoicingPlanDto postData) throws MeveoApiException, BusinessException {

		String invoicingPlanCode = postData.getCode();

		if (StringUtils.isBlank(invoicingPlanCode)) {
			missingParameters.add("invoicingPlanCode");
		}

		handleMissingParametersAndValidate(postData);

		InvoicingPlan invoicingPlan = invoicingPlanService.findByCode(invoicingPlanCode);

		if (invoicingPlan != null) {
			throw new EntityAlreadyExistsException(InvoicingPlan.class, invoicingPlanCode);
		}

		invoicingPlan = new InvoicingPlan();
		invoicingPlan.setCode(invoicingPlanCode);
		invoicingPlan.setDescription(postData.getDescription());
		populateCustomFields(postData.getCustomFields(), invoicingPlan, true);

		invoicingPlanService.create(invoicingPlan);

		return invoicingPlan;
	}

	/**
	 * Updates a InvoicingPlan Entity based on invoicingPlan code.
	 * 
	 * @param postData posted data to API
	 * 
	 * @throws MeveoApiException meveo api exception
	 * @throws BusinessException business exception.
	 */
	public InvoicingPlan update(InvoicingPlanDto postData) throws MeveoApiException, BusinessException {
		String invoicingPlanCode = postData.getCode();
		if (StringUtils.isBlank(invoicingPlanCode)) {
			missingParameters.add("invoicingPlanCode");
		}

		handleMissingParametersAndValidate(postData);
		InvoicingPlan invoicingPlan = invoicingPlanService.findByCode(invoicingPlanCode);
		if (invoicingPlan == null) {
			throw new EntityDoesNotExistsException(InvoicingPlan.class, invoicingPlanCode);
		}

		dtoToEntity(postData, invoicingPlan);
		populateCustomFields(postData.getCustomFields(), invoicingPlan, false);
		invoicingPlan = invoicingPlanService.update(invoicingPlan);

		return invoicingPlan;
	}

	private InvoicingPlan dtoToEntity(InvoicingPlanDto postData, InvoicingPlan invoicingPlan) {

		invoicingPlan.setCode(
				StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
		if (postData.getDescription() != null) {
			invoicingPlan.setDescription(postData.getDescription());
		}
		return invoicingPlan;
	}

	@Override
	protected BiFunction<InvoicingPlan, CustomFieldsDto, InvoicingPlanDto> getEntityToDtoFunction() {
		return InvoicingPlanDto::new;
	}
}