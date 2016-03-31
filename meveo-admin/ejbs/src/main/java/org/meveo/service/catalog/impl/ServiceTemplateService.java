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
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.BusinessService;
import org.meveo.service.script.ServiceModelScriptService;

/**
 * Service Template service implementation.
 * 
 */
@Stateless
public class ServiceTemplateService extends BusinessService<ServiceTemplate> {
	
	@Inject
	private ServiceModelScriptService serviceModelScriptService;
	
	@Override
	public void create(ServiceTemplate serviceTemplate, User creator) throws BusinessException {
		super.create(serviceTemplate, creator);

		if (serviceTemplate.getBusinessServiceModel() != null && serviceTemplate.getBusinessServiceModel().getScript() != null) {
			try {
				serviceModelScriptService.createServiceTemplate(serviceTemplate, serviceTemplate.getBusinessServiceModel().getScript().getCode(), creator);
			} catch (BusinessException e) {
				log.error("Failed to execute a script {}", serviceTemplate.getBusinessServiceModel().getScript().getCode(), e);
			}
		}
	}

	@Override
	public ServiceTemplate update(ServiceTemplate serviceTemplate, User updater) throws BusinessException {
		ServiceTemplate result = super.update(serviceTemplate, updater);

		if (serviceTemplate.getBusinessServiceModel() != null && serviceTemplate.getBusinessServiceModel().getScript() != null) {
			try {
				serviceModelScriptService.updateServiceTemplate(serviceTemplate, serviceTemplate.getBusinessServiceModel().getScript().getCode(), updater);
			} catch (BusinessException e) {
				log.error("Failed to execute a script {}", serviceTemplate.getBusinessServiceModel().getScript().getCode(), e);
			}
		}

		return result;
	}

	public void removeByCode(EntityManager em, String code, Provider provider) {
		Query query = em.createQuery("DELETE ServiceTemplate t WHERE t.code=:code AND t.provider=:provider");
		query.setParameter("code", code);
		query.setParameter("provider", provider);
		query.executeUpdate();
	}

	public int getNbServiceWithNotOffer(Provider provider) {
		return ((Long) getEntityManager().createNamedQuery("serviceTemplate.getNbServiceWithNotOffer", Long.class)
				.setParameter("provider", provider).getSingleResult()).intValue();
	}

	public List<ServiceTemplate> getServicesWithNotOffer(Provider provider) {
		return (List<ServiceTemplate>) getEntityManager()
				.createNamedQuery("serviceTemplate.getServicesWithNotOffer", ServiceTemplate.class)
				.setParameter("provider", provider).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ServiceTemplate> listAllActiveExcept(ServiceTemplate st, Provider provider) {
		QueryBuilder qb = new QueryBuilder(ServiceTemplate.class, "s", null, provider);
		qb.addCriterion("id", "<>", st.getId(), true);

		try {
			return (List<ServiceTemplate>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ServiceTemplate> listNoBSM() {
		QueryBuilder qb = new QueryBuilder(ServiceTemplate.class, "s");
		qb.addSql(" s.businessServiceModel is null ");

		try {
			return (List<ServiceTemplate>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

}
