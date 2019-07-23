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
		UnitOfMeasure parent=unitOfMeasure.isBaseUnit()?unitOfMeasure:unitOfMeasure.getParentUnitOfMeasure();
		return getEntityManager().createNamedQuery("unitOfMeasure.listChildUnits", UnitOfMeasure.class).setParameter("parentUnitOfMeasure", parent).getResultList();
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
		if(entity.isBaseUnit() ) {
			List<UnitOfMeasure> childs=listCompatibleChildUnits(entity);
			if(childs!=null && !childs.isEmpty()) {
				throw new BusinessException("you cannot delete this base unit as it's referenced by "+childs.size()+" other Units Of Measure.");
			}
		}
		super.remove(entity);
	}

	public void validateEntity(UnitOfMeasure entity) throws BusinessException {
		Long multiplicator = entity.getMultiplicator();
		if (multiplicator == null || multiplicator.compareTo(0l) <= 0) {
			throw new BusinessException("multiplicator must be strict positif for a Unit of Measure, current value is "+multiplicator);
		}
		if (entity.getParentUnitOfMeasure() == null && !multiplicator.equals(1l)) {
			throw new BusinessException("multiplicator must be 1 for a Base Unit of Measure, current value is "+multiplicator);
		}
	}
}