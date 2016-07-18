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

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.service.base.BusinessService;

/**
 * Charge Template service implementation.
 * 
 */
@Stateless
public class TriggeredEDRTemplateService extends
		BusinessService<TriggeredEDRTemplate> {

	public void duplicate(TriggeredEDRTemplate entity,User currentUser) throws BusinessException{
		entity = refreshOrRetrieve(entity);
		
		Long sequence=entity.getSequence();
		entity.setSequence(++sequence);
		update(entity,currentUser);
		commit();

		// Detach and clear ids of entity and related entities
		detach(entity);
		entity.setId(null);
		entity.setSequence(0L);
		entity.setCode(entity.getCode() + " - Copy"+(sequence==1L?"":" "+sequence));
		create(entity, getCurrentUser());
	}
}
