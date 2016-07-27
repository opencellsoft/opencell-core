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
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;

/**
 * Offer Template service implementation.
 * 
 */
@Stateless
public class OfferTemplateService extends BusinessService<OfferTemplate> {
	
	@Inject
	private CustomFieldInstanceService customFieldInstanceService;
	
	@Inject
	private OfferServiceTemplateService offerServiceTemplateService;
	
	@Override
	public void create(OfferTemplate offerTemplate, User creator) throws BusinessException {
		super.create(offerTemplate, creator);
	}

	@Override
	public OfferTemplate update(OfferTemplate offerTemplate, User updater) throws BusinessException {
		offerTemplate = super.update(offerTemplate, updater);
		return offerTemplate;
	}

	@SuppressWarnings("unchecked")
	public List<OfferTemplate> findByServiceTemplate(EntityManager em,
			ServiceTemplate serviceTemplate, Provider provider) {
		Query query = em
				.createQuery("FROM OfferTemplate t WHERE :serviceTemplate MEMBER OF t.serviceTemplates");
		query.setParameter("serviceTemplate", serviceTemplate);

		try {
			return (List<OfferTemplate>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<OfferTemplate> findByServiceTemplate(ServiceTemplate serviceTemplate) {
		Query query = getEntityManager()
				.createQuery("FROM OfferTemplate t WHERE :serviceTemplate MEMBER OF t.serviceTemplates");
		query.setParameter("serviceTemplate", serviceTemplate);

		try {
			return (List<OfferTemplate>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public long countActive() {
		Long result = 0L;
		Query query = getEntityManager().createNamedQuery("OfferTemplate.countActive");
		try {
			result = (long) query.getSingleResult();
		} catch (NoResultException e) {

		}
		return result;
	}

	public long countDisabled() {
		Long result = 0L;
		Query query = getEntityManager().createNamedQuery("OfferTemplate.countDisabled");
		try {
			result = (long) query.getSingleResult();
		} catch (NoResultException e) {

		}
		return result;
	}

	public long countExpiring() {
		Long result = 0L;
		Query query = getEntityManager().createNamedQuery("OfferTemplate.countExpiring");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		query.setParameter("nowMinus1Day", c.getTime());

		try {
			result = (long) query.getSingleResult();
		} catch (NoResultException e) {

		}
		return result;
	}
	
	public synchronized void duplicate(OfferTemplate entity,User currentUser) throws BusinessException{
		
		entity = refreshOrRetrieve(entity);
		// Lazy load related values first
		entity.getOfferServiceTemplates().size();
		String code=findDuplicateCode(entity,currentUser);
		
		// Detach and clear ids of entity and related entities
		detach(entity);
		entity.setId(null);
		String sourceAppliesToEntity = entity.clearUuid();

		List<OfferServiceTemplate> serviceTemplates = entity.getOfferServiceTemplates();
		entity.setOfferServiceTemplates(new ArrayList<OfferServiceTemplate>());
		entity.setCode(code);
		create(entity, currentUser);
		if(serviceTemplates!=null){
			for (OfferServiceTemplate serviceTemplate : serviceTemplates) {
				serviceTemplate.getIncompatibleServices().size();
				offerServiceTemplateService.detach(serviceTemplate);
				serviceTemplate.setId(null);
				List<ServiceTemplate> incompatibleServices=serviceTemplate.getIncompatibleServices();
				serviceTemplate.setIncompatibleServices(new ArrayList<ServiceTemplate>());
				if(incompatibleServices!=null){
					for(ServiceTemplate incompatibleService:incompatibleServices){
						serviceTemplate.addIncompatibleServiceTemplate(incompatibleService);
					}
				}
				serviceTemplate.setOfferTemplate(entity);
				offerServiceTemplateService.create(serviceTemplate, currentUser);
				entity.addOfferServiceTemplate(serviceTemplate);
			}
		}
		update(entity,currentUser);
		customFieldInstanceService.duplicateCfValues(sourceAppliesToEntity, entity, getCurrentUser());
	}

}