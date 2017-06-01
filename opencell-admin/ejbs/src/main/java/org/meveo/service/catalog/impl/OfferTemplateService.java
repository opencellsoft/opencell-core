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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ImageUploadEventHandler;
import org.meveo.model.Auditable;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.service.base.AuditableMultilanguageService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;

/**
 * Offer Template service implementation.
 * 
 */
@Stateless
public class OfferTemplateService extends AuditableMultilanguageService<OfferTemplate> {

	@Inject
	private CustomFieldInstanceService customFieldInstanceService;

	@Inject
	private CatalogHierarchyBuilderService catalogHierarchyBuilderService;
	
	@SuppressWarnings("unchecked")
	public List<OfferTemplate> findByServiceTemplate(EntityManager em, ServiceTemplate serviceTemplate) {
		Query query = em.createQuery("FROM OfferTemplate t WHERE :serviceTemplate MEMBER OF t.serviceTemplates");
		query.setParameter("serviceTemplate", serviceTemplate);

		try {
			return (List<OfferTemplate>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<OfferTemplate> findByServiceTemplate(ServiceTemplate serviceTemplate) {
		Query query = getEntityManager().createQuery("FROM OfferTemplate t WHERE :serviceTemplate MEMBER OF t.serviceTemplates");
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

	public synchronized OfferTemplate duplicate(OfferTemplate entity) throws BusinessException {
		return duplicate(entity, false);
	}

	public synchronized OfferTemplate duplicate(OfferTemplate entity, boolean duplicateHierarchy) throws BusinessException {

		entity = refreshOrRetrieve(entity);
		// Lazy load related values first
		entity.getOfferServiceTemplates().size();
		entity.getBusinessAccountModels().size();
		entity.getAttachments().size();
		entity.getChannels().size();
		entity.getOfferProductTemplates().size();
		entity.getOfferTemplateCategories().size();

		if (entity.getOfferServiceTemplates() != null) {
			for (OfferServiceTemplate offerServiceTemplate : entity.getOfferServiceTemplates()) {
				offerServiceTemplate.getIncompatibleServices().size();
			}
		}

		String code = findDuplicateCode(entity);

		// Detach and clear ids of entity and related entities
		detach(entity);
		entity.setId(null);
		entity.setVersion(0);
		entity.setAuditable(new Auditable());
		String sourceAppliesToEntity = entity.clearUuid();
		
		ImageUploadEventHandler<OfferTemplate> offerImageUploadEventHandler = new ImageUploadEventHandler<>(appProvider);
		try {
			String newImagePath = offerImageUploadEventHandler.duplicateImage(entity, entity.getImagePath(), code);
			entity.setImagePath(newImagePath);
		} catch (IOException e1) {
			log.error("IPIEL: Failed duplicating offer image: {}", e1.getMessage());
		}

		entity.setCode(code);

		List<OfferServiceTemplate> offerServiceTemplates = entity.getOfferServiceTemplates();
		entity.setOfferServiceTemplates(new ArrayList<OfferServiceTemplate>());

		List<BusinessAccountModel> businessAccountModels = entity.getBusinessAccountModels();
		entity.setBusinessAccountModels(new ArrayList<BusinessAccountModel>());

		List<DigitalResource> attachments = entity.getAttachments();
		entity.setAttachments(new ArrayList<DigitalResource>());

		List<Channel> channels = entity.getChannels();
		entity.setChannels(new ArrayList<Channel>());

		List<OfferProductTemplate> offerProductTemplates = entity.getOfferProductTemplates();
		entity.setOfferProductTemplates(new ArrayList<OfferProductTemplate>());

		List<OfferTemplateCategory> offerTemplateCategories = entity.getOfferTemplateCategories();
		entity.setOfferTemplateCategories(new ArrayList<OfferTemplateCategory>());

		if (businessAccountModels != null) {
			for (BusinessAccountModel bam : businessAccountModels) {
				entity.getBusinessAccountModels().add(bam);
			}
		}

		if (attachments != null) {
			for (DigitalResource attachment : attachments) {
				entity.addAttachment(attachment);
			}
		}

		if (channels != null) {
			for (Channel channel : channels) {
				entity.getChannels().add(channel);
			}
		}		

		if (offerTemplateCategories != null) {
			for (OfferTemplateCategory offerTemplateCategory : offerTemplateCategories) {
				entity.getOfferTemplateCategories().add(offerTemplateCategory);
			}
		}

		if (!duplicateHierarchy) {
			if (offerServiceTemplates != null) {
				for (OfferServiceTemplate serviceTemplate : offerServiceTemplates) {
					serviceTemplate.getIncompatibleServices().size();
					serviceTemplate.setId(null);
					List<ServiceTemplate> incompatibleServices = serviceTemplate.getIncompatibleServices();
					serviceTemplate.setIncompatibleServices(new ArrayList<ServiceTemplate>());
					if (incompatibleServices != null) {
						for (ServiceTemplate incompatibleService : incompatibleServices) {
							serviceTemplate.addIncompatibleServiceTemplate(incompatibleService);
						}
					}
					serviceTemplate.setOfferTemplate(entity);
					entity.addOfferServiceTemplate(serviceTemplate);
				}
			}
			
			if (offerProductTemplates != null) {
				for (OfferProductTemplate offerProductTemplate : offerProductTemplates) {
					offerProductTemplate.setId(null);
					offerProductTemplate.setOfferTemplate(entity);
					entity.getOfferProductTemplates().add(offerProductTemplate);
				}
			}
		}

		create(entity);
		customFieldInstanceService.duplicateCfValues(sourceAppliesToEntity, entity);

		if (duplicateHierarchy) {
			String prefix = entity.getId() + "_";
			
			if (offerServiceTemplates != null) {			
				catalogHierarchyBuilderService.buildOfferServiceTemplate(entity, offerServiceTemplates, prefix);				
			}
			
			if (offerProductTemplates != null) {
				catalogHierarchyBuilderService.buildOfferProductTemplate(entity, offerProductTemplates, prefix);
			}
			
			update(entity);
		}

		return entity;
	}

}