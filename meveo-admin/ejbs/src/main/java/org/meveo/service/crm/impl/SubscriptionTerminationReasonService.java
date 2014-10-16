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
package org.meveo.service.crm.impl;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.service.base.PersistenceService;

/**
 * Service SubscriptionTerminationReason implementation.
 */
@Named
@Stateless
@LocalBean
public class SubscriptionTerminationReasonService extends
		PersistenceService<SubscriptionTerminationReason> {

	public SubscriptionTerminationReason findByCodeReason(String codeReason, String providerCode)
			throws Exception {
		return (SubscriptionTerminationReason) getEntityManager()
				.createQuery(
						"from " + SubscriptionTerminationReason.class.getSimpleName()
								+ " where code=:codeReason and provider.code=:providerCode")
				.setParameter("codeReason", codeReason).setParameter("providerCode", providerCode)
				.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<SubscriptionTerminationReason> listReasons() {

		Query query = new QueryBuilder(SubscriptionTerminationReason.class, "c", null,
				getCurrentProvider()).getQuery(getEntityManager());
		return query.getResultList();
	}
}
