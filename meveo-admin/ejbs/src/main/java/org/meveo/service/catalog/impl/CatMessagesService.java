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
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

/**
 * CatMessagesService service implementation.
 */
@Stateless
@Named
public class CatMessagesService extends PersistenceService<CatMessages> {

	@PostConstruct
	private void init() {

	}
	
	public String getMessageDescription(BusinessEntity businessEntity, String languageCode,String defaultDescription){
		String className =businessEntity.getClass().getSimpleName();
		//supress javassist proxy suffix
		if(className.indexOf("_")>=0){
			className=className.substring(0, className.indexOf("_"));
		}
		return getMessageDescription(className + "_" + businessEntity.getId(),
				 languageCode, defaultDescription);
	}
	

	@SuppressWarnings("unchecked")
	public String getMessageDescription(String messageCode, String languageCode,String defaultDescription) {
		long startDate = System.currentTimeMillis();
		if (messageCode == null || languageCode == null) {
			return null;
		}
		QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
		qb.addCriterionWildcard("c.messageCode", messageCode, true);
		qb.addCriterionWildcard("c.languageCode", languageCode, true);
		List<CatMessages> catMessages = qb.getQuery(getEntityManager())
				.getResultList();

		String description = catMessages.size() > 0 ? catMessages.get(0)
				.getDescription() : defaultDescription;

		log.debug("get message "+messageCode+" description =" + description
				+ ", time=" + (System.currentTimeMillis() - startDate));
		return description;
	}

	public CatMessages getCatMessages(String messageCode, String languageCode) {
		return getCatMessages(getEntityManager(), messageCode, languageCode);
	}

	@SuppressWarnings("unchecked")
	private CatMessages getCatMessages(EntityManager em, String messageCode,
			String languageCode) {
		QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
		qb.addCriterionWildcard("c.messageCode", messageCode, true);
		qb.addCriterionWildcard("c.languageCode", languageCode, true);
		List<CatMessages> cats = (List<CatMessages>) qb.getQuery(em)
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
		List<CatMessages> cats = (List<CatMessages>) qb.getQuery(
				getEntityManager()).getResultList();
		return cats;
	}

	public void batchRemove(String entityName, Long id,Provider provider) {
		String strQuery = "DELETE FROM " + CatMessages.class.getSimpleName()
				+ " c WHERE c.messageCode=:messageCode and c.provider=:provider";

		try {
			getEntityManager().createQuery(strQuery)
					.setParameter("messageCode", entityName + "_" + id)
					.setParameter("provider", provider)
					.executeUpdate();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

}
