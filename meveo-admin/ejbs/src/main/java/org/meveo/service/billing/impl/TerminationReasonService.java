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
package org.meveo.service.billing.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.service.base.PersistenceService;

@Stateless
public class TerminationReasonService extends PersistenceService<SubscriptionTerminationReason> {

	public SubscriptionTerminationReason findByCode(String code) {
		QueryBuilder qb = new QueryBuilder(SubscriptionTerminationReason.class, "s");
		qb.addCriterion("code", "=", code, true);
		

		try {
			return (SubscriptionTerminationReason) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of code {} found", getEntityClass().getSimpleName(), code);
            return null;
        } catch (NonUniqueResultException e) {
            log.error("More than one entity of type {} with code {} found", entityClass, code);
            return null;
        }
	}
}