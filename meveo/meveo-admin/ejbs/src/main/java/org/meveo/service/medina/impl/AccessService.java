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
package org.meveo.service.medina.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.model.mediation.Access;
import org.meveo.service.base.PersistenceService;

@Stateless
@LocalBean
public class AccessService extends PersistenceService<Access> {

	@SuppressWarnings("unchecked")
	public List<Access> findByUserID(String userId) {
		log.info("findByUserID '" + userId + "'");
		List<Access> result = new ArrayList<Access>();
		if (userId != null && userId.length() > 0) {
			Query query = em.createQuery("from Access a where a.accessUserId=:accessUserId")
					.setParameter("accessUserId", userId);
			result = query.getResultList();
		}
		return result;
	}

	public boolean isDuplicate(Access access) {
		String stringQuery = "SELECT COUNT(*) FROM " + Access.class.getName()
				+ " a WHERE a.accessUserId=:accessUserId AND a.subscription.id=:subscriptionId";
		Query query = em.createQuery(stringQuery);
		query.setParameter("accessUserId", access.getAccessUserId());
		query.setParameter("subscriptionId", access.getSubscription().getId());
		query.setHint("org.hibernate.flushMode", "NEVER");
		return ((Long) query.getSingleResult()).intValue() != 0;
	}

}
