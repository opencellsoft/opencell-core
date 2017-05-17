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
package org.meveo.service.base;

import java.util.Set;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.model.BusinessEntity;

/**
 * This service class is extended by class that needs audit logging on CRUD
 * operation.
 * 
 * @author Edward P. Legaspi
 **/
@Stateless
public abstract class AuditableBusinessService<T extends BusinessEntity> extends MultilanguageEntityService<T> {

	@MeveoAudit
	@Override
	public void create(T entity) throws BusinessException {
		super.create(entity);
	}

	@MeveoAudit
	@Override
	public T update(T entity) throws BusinessException {
		return super.update(entity);
	}

	@MeveoAudit
	@Override
	public void updateAudit(T e) {
		super.updateAudit(e);
	}

	@MeveoAudit
	@Override
	public T updateNoCheck(T entity) {
		return super.updateNoCheck(entity);
	}

	@MeveoAudit
	@Override
	public void remove(Long id) throws BusinessException {
		super.remove(id);
	}

	@MeveoAudit
	@Override
	public void remove(Set<Long> ids) throws BusinessException {
		super.remove(ids);
	}

	@MeveoAudit
	@Override
	public void remove(T entity) throws BusinessException {
		super.remove(entity);
	}

}
