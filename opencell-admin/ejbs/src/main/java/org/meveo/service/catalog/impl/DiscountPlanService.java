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

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.meveo.service.base.BusinessService;

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
		if (entity.getStatus().equals(DiscountPlanStatusEnum.DRAFT) || entity.getStatus().equals(DiscountPlanStatusEnum.ACTIVE)) {
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

	@Override
	public void remove(DiscountPlan entity) throws BusinessException {
		if (entity.getStatus().equals(DiscountPlanStatusEnum.DRAFT) || entity.getStatus().equals(DiscountPlanStatusEnum.ACTIVE)) {
			super.remove(entity);
		} else {
			log.error("Only discount plan with status DRAFT and ACTIVE is allowed to be deleted: {}", entity.getCode());
			throw new BusinessException("Only discount plan with status DRAFT and ACTIVE is allowed to be deleted: " + entity.getCode());
		}

	}

}