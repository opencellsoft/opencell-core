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
import org.meveo.model.admin.User;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.BusinessService;
import org.meveo.service.script.OfferModelScriptService;

/**
 * Offer Template service implementation.
 * 
 */
@Stateless
public class OfferTemplateService extends BusinessService<OfferTemplate> {
	
	@Inject
	private OfferModelScriptService offerModelScriptService;
	
	@Override
	public void create(OfferTemplate offerTemplate, User creator) throws BusinessException {
		super.create(offerTemplate, creator);

		if (offerTemplate.getBusinessOfferModel() != null && offerTemplate.getBusinessOfferModel().getScript() != null) {
			try {
				offerModelScriptService.createOfferTemplate(offerTemplate, offerTemplate.getBusinessOfferModel().getScript().getCode(), creator);
			} catch (BusinessException e) {
				log.error("Failed to execute a script {}", offerTemplate.getBusinessOfferModel().getScript().getCode(), e);
			}
		}
	}

	@Override
	public OfferTemplate update(OfferTemplate offerTemplate, User updater) throws BusinessException {
		OfferTemplate result = super.update(offerTemplate, updater);

		if (offerTemplate.getBusinessOfferModel() != null && offerTemplate.getBusinessOfferModel().getScript() != null) {
			try {
				offerModelScriptService.updateOfferTemplate(offerTemplate, offerTemplate.getBusinessOfferModel().getScript().getCode(), updater);
			} catch (BusinessException e) {
				log.error("Failed to execute a script {}", offerTemplate.getBusinessOfferModel().getScript().getCode(), e);
			}
		}

		return result;
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
}