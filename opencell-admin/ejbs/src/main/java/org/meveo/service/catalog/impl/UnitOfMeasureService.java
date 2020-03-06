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

import java.util.List;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.catalog.UnitOfMeasure;
import org.meveo.service.base.BusinessService;

/**
 * UnitOfMeasure service implementation.
 * 
 * @author Mounir Bahije
 */
@Stateless
public class UnitOfMeasureService extends BusinessService<UnitOfMeasure> {

	public List<UnitOfMeasure> listBaseUnits() {
		return getEntityManager().createNamedQuery("unitOfMeasure.listBaseUnits", UnitOfMeasure.class).getResultList();
	}

	public List<UnitOfMeasure> listCompatibleChildUnits(UnitOfMeasure unitOfMeasure) {
		UnitOfMeasure parent = unitOfMeasure.isBaseUnit() ? unitOfMeasure : unitOfMeasure.getParentUnitOfMeasure();
		return listChildUnitsOfMeasures(parent);
	}

	public List<UnitOfMeasure> listChildUnitsOfMeasures(UnitOfMeasure parent) {
		return getEntityManager().createNamedQuery("unitOfMeasure.listChildUnits", UnitOfMeasure.class)
				.setParameter("parentUnitOfMeasure", parent).getResultList();
	}

	@Override
	public void create(UnitOfMeasure entity) throws BusinessException {
		validateEntity(entity);
		super.create(entity);
	}

	@Override
	public UnitOfMeasure update(UnitOfMeasure entity) throws BusinessException {
		validateEntity(entity);
		return super.update(entity);
	}

	@Override
	public void remove(UnitOfMeasure entity) throws BusinessException {
		if (entity.isBaseUnit()) {
			List<UnitOfMeasure> childs = listCompatibleChildUnits(entity);
			if (childs != null && !childs.isEmpty()) {
				throw new BusinessException("you cannot delete this base unit as it's referenced by " + childs.size()
						+ " other Units Of Measure.");
			}
		}
		super.remove(entity);
	}

	public void validateEntity(UnitOfMeasure entity) throws BusinessException {
		Long multiplicator = entity.getMultiplicator();
		if (multiplicator == null || multiplicator.compareTo(0l) <= 0) {
			throw new BusinessException(
					"multiplicator must be strict positif for a Unit of Measure, current value is " + multiplicator);
		}
		if (entity.getParentUnitOfMeasure() == null) {
			if (!multiplicator.equals(1l)) {
				throw new BusinessException(
						"multiplicator must be 1 for a Base Unit of Measure, current value is " + multiplicator);
			}
			if (entity.getParentUnitOfMeasure() != null) {
				List<UnitOfMeasure> childs = listChildUnitsOfMeasures(entity);
				if (childs != null && !childs.isEmpty()) {
					throw new BusinessException("you cannot define a parent to this base unit as it's referenced by "
							+ childs.size() + " other Units Of Measure as the Base Unit.");
				}
			}
		}

	}
}