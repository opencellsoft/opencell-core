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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.service.base.AuditableMultilanguageService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;

/**
 * Charge Template service implementation.
 * 
 */
@Stateless
public class ChargeTemplateService<P extends ChargeTemplate> extends AuditableMultilanguageService<P> {
	
	@Inject
	private TriggeredEDRTemplateService edrTemplateService;
	
	@Inject
    private CustomFieldInstanceService customFieldInstanceService;
	
	public synchronized void duplicate(P entity) throws BusinessException{
		
		entity = refreshOrRetrieve(entity);
        // Lazy load related values first 
		entity.getEdrTemplates().size();
		String code=findDuplicateCode(entity);
		
        // Detach and clear ids of entity and related entities
		detach(entity);
		entity.setId(null);
        String sourceAppliesToEntity = entity.clearUuid();
        
		List<TriggeredEDRTemplate> edrTemplates=entity.getEdrTemplates();
		entity.setEdrTemplates(new ArrayList<TriggeredEDRTemplate>());
		if(edrTemplates!=null&edrTemplates.size()!=0){
			for(TriggeredEDRTemplate edrTemplate:edrTemplates){
				edrTemplateService.detach(edrTemplate);
				entity.getEdrTemplates().add(edrTemplate);
			}
		}
		entity.setChargeInstances(null);
		entity.setCode(code);
		create(entity);
        customFieldInstanceService.duplicateCfValues(sourceAppliesToEntity, entity);
	}
}
