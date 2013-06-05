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
package org.meveo.service.payments.impl;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.service.base.PersistenceService;

/**
 * OCCTemplate service implementation.
 */
@Stateless
@LocalBean
public class OCCTemplateService extends PersistenceService<OCCTemplate> {

	public OCCTemplate findByCode(String code, String providerCode) {
		log.debug("start of find {} by code (code={}) ..", "OCCTemplate", code);
		QueryBuilder qb = new QueryBuilder(OCCTemplate.class, "c");
		qb.addCriterion("c.code", "=", code, true);
		qb.addCriterion("c.provider.code", "=", providerCode, true);
		OCCTemplate occTemplate = (OCCTemplate) qb.getQuery(getEntityManager()).getSingleResult();
		log.debug("end of find {} by code (code={}). Result found={}.", new Object[] {
				"OCCTemplate", code, occTemplate != null });
		return occTemplate;
	}

	@SuppressWarnings("unchecked")
	public List<OCCTemplate> getListOccSortedByName(String providerCode) {
		log.debug("start of find list {} SortedByName for provider (code={}) ..", "OCCTemplate",
				providerCode);
		QueryBuilder qb = new QueryBuilder(OCCTemplate.class, "c");
		qb.addCriterion("c.provider.code", "=", providerCode, true);
		qb.addOrderCriterion("description", true);
		List<OCCTemplate> occTemplates = (List<OCCTemplate>) qb.getQuery(getEntityManager())
				.getResultList();
		log.debug("start of find list {} SortedByName for provider (code={})  result {}",
				new Object[] { "OCCTemplate", providerCode,
						occTemplates == null ? "null" : occTemplates.size() });
		return occTemplates;
	}
}
