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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.QueryBuilder.QueryLikeStyleEnum;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;
import org.meveo.model.MultilanguageEntity;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.MultilanguageEntityService;
import org.meveo.service.base.PersistenceService;

/**
 * CatMessagesService service implementation.
 */
@Stateless
public class CatMessagesService extends PersistenceService<CatMessages> {

    private static final int ENTITY_ID = 1;

	private static final int ENTITY_CLASS = 0;

	private static final String INVALID_CLASS_TYPE = "Invalid class type!";
    
    private Map<String, MultilanguageEntityService<?>> services;
    
    @PostConstruct
    private void init() {

    }
    
    @Inject
	private void initServiceList(@Any Instance<MultilanguageEntityService<?>> entityServices) {
		if (services == null) {
			services = Collections.synchronizedMap(new HashMap<String, MultilanguageEntityService<?>>());
		}
		if (services.isEmpty()) {
			for (MultilanguageEntityService<?> entityService : entityServices) {
				services.put(ReflectionUtils.getCleanClassName(entityService.getEntityClass().getSimpleName()), entityService);
			}
		}
	}

    public String getMessageDescription(BusinessEntity businessEntity, String languageCode) {
        String result = getMessageDescription(getMessageCode(businessEntity), languageCode, businessEntity.getDescription());
        if (StringUtils.isBlank(result)) {
            result = businessEntity.getCode();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public String getMessageDescription(String messageCode, String languageCode, String defaultDescription) {
        long startDate = System.currentTimeMillis();
        if (messageCode == null || languageCode == null) {
            return null;
        }
        QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
        qb.addCriterionWildcard("c.messageCode", messageCode, true);
        qb.addCriterionWildcard("c.languageCode", languageCode, true);
        List<CatMessages> catMessages = qb.getQuery(getEntityManager()).getResultList();

        String description = (catMessages.size() > 0 && !StringUtils.isBlank(catMessages.get(0).getDescription())) ? catMessages.get(0).getDescription() : defaultDescription;

        log.debug("get message " + messageCode + " description =" + description + ", time=" + (System.currentTimeMillis() - startDate));
        return description;
    }

    public CatMessages getCatMessages(IEntity businessEntity, String languageCode) {

        return getCatMessages(getMessageCode(businessEntity), languageCode);
    }

    public CatMessages getCatMessages(String messageCode, String languageCode) {
        return getCatMessages(getEntityManager(), messageCode, languageCode);
    }

    @SuppressWarnings("unchecked")
    private CatMessages getCatMessages(EntityManager em, String messageCode, String languageCode) {
        QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
        qb.addCriterionWildcard("c.messageCode", messageCode, true);
        qb.addCriterionWildcard("c.languageCode", languageCode, true);
        List<CatMessages> cats = (List<CatMessages>) qb.getQuery(em).getResultList();
        return cats != null && cats.size() > 0 ? cats.get(0) : null;
    }

    @SuppressWarnings("unchecked")
    public List<CatMessages> getCatMessagesList(String messageCode) {
        log.debug("getCatMessagesList messageCode={} ", messageCode);
        if (StringUtils.isBlank(messageCode)) {
            return new ArrayList<CatMessages>();
        }
        QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
        qb.addCriterion("c.messageCode", "=", messageCode, true);
        List<CatMessages> cats = (List<CatMessages>) qb.getQuery(getEntityManager()).getResultList();
        return cats;
    }

    /**
     * Get all messages of a given class in a given language
     * 
     * @param clazz Class to get messages for
     * @param languageCode Language to get messages in
     * @return A list of messages
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<CatMessages> getCatMessagesList(Class clazz, String languageCode) {
        QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
        qb.like("c.messageCode", getMessageCodePrefix(clazz), QueryLikeStyleEnum.MATCH_BEGINNING, false);
        qb.addCriterionWildcard("c.languageCode", languageCode, true);
        List<CatMessages> cats = (List<CatMessages>) qb.getQuery(getEntityManager()).getResultList();
        return cats;
    }

    public void batchRemove(String entityName, Long id, Provider provider) {
        String strQuery = "DELETE FROM " + CatMessages.class.getSimpleName() + " c WHERE c.messageCode=:messageCode and c.provider=:provider";

        try {
            getEntityManager().createQuery(strQuery).setParameter("messageCode", entityName + "_" + id).setParameter("provider", provider).executeUpdate();
        } catch (Exception e) {
            log.error("failed to batch remove", e);
        }
    }

    /**
     * Get a message code prefix for a given entity
     * 
     * @param entity Entity
     * @return A message code in a format "className_id"
     */
    public String getMessageCode(IEntity entity) {
        String className = ReflectionUtils.getCleanClassName(entity.getClass().getSimpleName());
        return className + "_" + entity.getId();
    }

    /**
     * Get a message code prefix for a given class
     * 
     * @param clazz Class
     * @return A message code in a format "className_"
     */
    @SuppressWarnings("rawtypes")
    public String getMessageCodePrefix(Class clazz) {
        String className = ReflectionUtils.getCleanClassName(clazz.getSimpleName());
        return className + "_";
    }

    public CatMessages findByCodeAndLanguage(String messageCode, String languageCode, Provider provider) {
        QueryBuilder qb = new QueryBuilder(CatMessages.class, "c", null, provider);
        qb.addCriterionWildcard("c.messageCode", messageCode, true);
        qb.addCriterionWildcard("c.languageCode", languageCode, true);
        try {
            return (CatMessages) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
	public List<CatMessages> findByCode(String messageCode, Provider provider) {
        QueryBuilder qb = new QueryBuilder(CatMessages.class, "c", null, provider);
        qb.addCriterionWildcard("c.messageCode", messageCode, true);
        try {
            return (List<CatMessages>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<CatMessages> list(PaginationConfiguration config) {
        List<CatMessages> catMessages = super.list(config);
        for (CatMessages catMsg : catMessages) {
            BusinessEntity entity = getBusinessEntity(catMsg);
            if (entity != null) {
                catMsg.setEntityCode(entity.getCode());
                catMsg.setEntityDescription(entity.getDescription());
            }
        }
        return catMessages;
    }

	private BusinessEntity getBusinessEntity(CatMessages catMessages) {
		BusinessEntity entity = null;
		if (catMessages != null) {
			String messagesCode = catMessages.getMessageCode();
			String[] codes = messagesCode.split("_");

			if (codes != null && codes.length == 2) {
				Long id = null;
				try {
					id = Long.valueOf(codes[ENTITY_ID]);
					MultilanguageEntityService<?> service = getMultilanguageEntityService(codes[ENTITY_CLASS]);
					if (service != null) {
						entity = service.findById(id);
					}
				} catch (NumberFormatException e) {
					log.warn("Failed to parse id. Returning null entity.");
				} catch (BusinessException e) {
					log.warn("Failed to retrieve entity. Returning null.");
				}
			}
		}
		return entity;
	}
     
	public MultilanguageEntityService<?> getMultilanguageEntityService(String entityClassName)
			throws BusinessException {
		MultilanguageEntityService<?> service = null;
		if (entityClassName != null) {
			service = this.services.get(entityClassName);
		}
		if (service == null) {
			throw new BusinessException(INVALID_CLASS_TYPE);
		}
		return service;
	}
	
	public BusinessEntity getEntityByMessageCode(String messageCode) {
		BusinessEntity entity = null;
		if (!StringUtils.isBlank(messageCode) && messageCode.contains("_")) {
			try {
				String[] classAndId = messageCode.split("_");
				String className = classAndId[0];
				long entityId = Long.parseLong(classAndId[1]);
				entity = getEntityByClassNameAndId(className, entityId);
			} catch (NumberFormatException e) {
				log.warn("Invalid Entity Id.  Will return null entity.", e);
			}
		}
		return entity;
	}

	private BusinessEntity getEntityByClassNameAndId(String className, long entityId) {
		Class<?> entityClass = ReflectionUtils.getClassBySimpleNameAndAnnotation(className, MultilanguageEntity.class);
		BusinessEntity entity = null;
		if (entityClass != null) {
			try {
				log.trace("start of find {} by id (id={}) ..", entityClass.getSimpleName(), entityId);
				entity = (BusinessEntity) getEntityManager().find(entityClass, entityId);
				log.debug("end of find {} by id (id={}). Result found={}.", entityClass.getSimpleName(), entityId,
						entity != null);
			} catch (NoResultException e) {
				log.warn("Error encountered while retrieving business entity.  Will return null.", e);
			}
		}
		return entity;
	}

}