/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.service.catalog.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Named;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.CatMessages;
import org.meveo.service.base.PersistenceService;

/**
 * CatMessagesService service implementation.
 * 
 * @author MBAREK
 * 
 * 
 */
@Stateless
@Named
@LocalBean
public class CatMessagesService extends PersistenceService<CatMessages> {

	@SuppressWarnings("unchecked")
	public String getMessageDescription(String messageCode, String languageCode) {
		QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
		qb.addCriterionWildcard("c.messageCode", messageCode, true);
		qb.addCriterionWildcard("c.languageCode", languageCode, true);
		List<CatMessages> catMessages = qb.getQuery(em).getResultList();
		return catMessages.size() > 0 ? catMessages.get(0).getDescription() : null;
	}

	public CatMessages getCatMessages(String messageCode, String languageCode) {
		QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
		qb.addCriterionWildcard("c.messageCode", messageCode, true);
		qb.addCriterionWildcard("c.languageCode", languageCode, true);
		List<CatMessages> cats = (List<CatMessages>) qb.getQuery(em).getResultList();
		return cats != null && cats.size() > 0 ? cats.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public List<CatMessages> getCatMessagesList(String messageCode) {
		log.info("getCatMessagesList messageCode=#0 ", messageCode);
		if (StringUtils.isBlank(messageCode)) {
			return new ArrayList<CatMessages>();
		}
		QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
		qb.addCriterion("c.messageCode", "=", messageCode, true);
		List<CatMessages> cats = (List<CatMessages>) qb.getQuery(em).getResultList();
		return cats;
	}

}
