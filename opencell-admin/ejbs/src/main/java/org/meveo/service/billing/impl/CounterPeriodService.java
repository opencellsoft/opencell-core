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

import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.service.base.PersistenceService;

@Stateless
public class CounterPeriodService extends PersistenceService<CounterPeriod> {

    /**
     * Find an existing counter period matching a given date
     * 
     * @param counterInstance Counter instance
     * @param date Date to match
     * @return Counter period
     * @throws BusinessException
     */
    public CounterPeriod getCounterPeriod(CounterInstance counterInstance, Date date) throws BusinessException {
        Query query = getEntityManager().createNamedQuery("CounterPeriod.findByPeriodDate");
        query.setParameter("counterInstance", counterInstance);
        query.setParameter("date", date, TemporalType.TIMESTAMP);
        try {
            return (CounterPeriod) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}