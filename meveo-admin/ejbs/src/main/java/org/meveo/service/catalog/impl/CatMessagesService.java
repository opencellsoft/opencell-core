/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.catalog.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.infinispan.api.BasicCache;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.util.CacheContainerProvider;

/**
 * CatMessagesService service implementation.
 */
@Stateless
@Named
@LocalBean
public class CatMessagesService extends PersistenceService<CatMessages> {
	
	@Inject
	CacheContainerProvider cacheContainerProvider;
	

    private static BasicCache<String, Object> catMessageCach;
    
    @PostConstruct
    private void init() {
		if(catMessageCach==null){
			catMessageCach=cacheContainerProvider.getCacheContainer().getCache("meveo");
		}

	}

	@SuppressWarnings("unchecked")
	public String getMessageDescription(String messageCode, String languageCode) {
		if(messageCode==null || languageCode==null){
			return null;
		}
		if(catMessageCach.containsKey(messageCode)){
			log.info("get message description from infinispan cache messageCode="+messageCode+",languageCode="+languageCode);
			return (String)catMessageCach.get(messageCode);
		}
		log.info("get message description from DB="+messageCode+",languageCode="+languageCode);
		QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
		qb.addCriterionWildcard("c.messageCode", messageCode, true);
		qb.addCriterionWildcard("c.languageCode", languageCode, true);
		List<CatMessages> catMessages = qb.getQuery(getEntityManager()).getResultList();
		
		String description= catMessages.size() > 0 ? catMessages.get(0).getDescription() : "";
		if(description!=null){
			catMessageCach.put(messageCode, description);
		}
		log.info("get message description description ="+description);
		return description;
	}

	@SuppressWarnings("unchecked")
	public CatMessages getCatMessages(String messageCode, String languageCode) {
		QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
		qb.addCriterionWildcard("c.messageCode", messageCode, true);
		qb.addCriterionWildcard("c.languageCode", languageCode, true);
		List<CatMessages> cats = (List<CatMessages>) qb.getQuery(getEntityManager())
				.getResultList();
		return cats != null && cats.size() > 0 ? cats.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public List<CatMessages> getCatMessagesList(String messageCode) {
		log.info("getCatMessagesList messageCode={} ", messageCode);
		if (StringUtils.isBlank(messageCode)) {
			return new ArrayList<CatMessages>();
		}
		QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
		qb.addCriterion("c.messageCode", "=", messageCode, true);
		List<CatMessages> cats = (List<CatMessages>) qb.getQuery(getEntityManager())
				.getResultList();
		return cats;
	}

	@Override
	public void create(EntityManager em, CatMessages e, User creator,
			Provider provider) {

		catMessageCach.putIfAbsent(e.getMessageCode(), e.getDescription());
		super.create(em, e, creator, provider);
	}

	@Override
	public void update(EntityManager em, CatMessages e, User updater) {
		catMessageCach.put(e.getMessageCode(), e.getDescription());
		super.update(em, e, updater);
	}
	
	
	
	

}
