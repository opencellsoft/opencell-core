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
package org.meveo.service.base;

import java.util.List;

import javax.persistence.Query;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AccountEntity;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;

public abstract class AccountService<P extends AccountEntity> extends BusinessService<P> {

	@SuppressWarnings("unchecked")
	public P findByExternalRef1(String externalRef1) {
		log.debug("start of find {} by externalRef1 (externalRef1={}) ..", getEntityClass().getSimpleName(), externalRef1);
		final Class<? extends P> productClass = getEntityClass();
		StringBuilder queryString = new StringBuilder("from " + productClass.getName() + " a");
		queryString.append(" where a.externalRef1 = :externalRef1");
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("externalRef1", externalRef1);
		if (query.getResultList().size() == 0) {
			return null;
		}
		P e = (P) query.getResultList().get(0);
		log.debug("end of find {} by externalRef1 (externalRef1={}). Result found={}.", new Object[] { getEntityClass().getSimpleName(), externalRef1, e != null });
		return e;
	}

	@SuppressWarnings("unchecked")
	public List<P> findByNameAndAddress(Name name, Address address, Provider provider) {
		log.debug("start of find {} by name={}, address={}", getEntityClass().getSimpleName(), name, address);
		final Class<? extends P> productClass = getEntityClass();
		StringBuilder queryString = new StringBuilder("from " + productClass.getName() + " a");
		queryString.append(" WHERE 1=1");

		if (name != null) {
			if (!StringUtils.isBlank(name.getFirstName())) {
				queryString.append(" AND LOWER(a.name.firstName) LIKE :firstName");
			}
			if (!StringUtils.isBlank(name.getLastName())) {
				queryString.append(" AND LOWER(a.name.lastName) LIKE :lastName");
			}
		}

		if (address != null) {
			if (!StringUtils.isBlank(address.getAddress1())) {
				queryString.append(" AND LOWER(a.address.address1) LIKE :address1");
			}
			if (!StringUtils.isBlank(address.getAddress2())) {
				queryString.append(" AND LOWER(a.address.address2) LIKE :address2");
			}
			if (!StringUtils.isBlank(address.getAddress3())) {
				queryString.append(" AND LOWER(a.address.address3) LIKE :address3");
			}
			if (!StringUtils.isBlank(address.getCity())) {
				queryString.append(" AND LOWER(a.address.city) LIKE :city");
			}
			if (!StringUtils.isBlank(address.getCountry())) {
				queryString.append(" AND LOWER(a.address.country) LIKE :country");
			}
			if (!StringUtils.isBlank(address.getState())) {
				queryString.append(" AND LOWER(a.address.state) LIKE :state");
			}
			if (!StringUtils.isBlank(address.getZipCode())) {
				queryString.append(" AND LOWER(a.address.zipCode) LIKE :zipCode");
			}
		}

		Query query = getEntityManager().createQuery(queryString.toString());

		if (name != null) {
			if (!StringUtils.isBlank(name.getFirstName())) {
				query.setParameter("firstName", "%" + name.getFirstName().toLowerCase() + "%");
			}
			if (!StringUtils.isBlank(name.getLastName())) {
				query.setParameter("lastName", "%" + name.getLastName().toLowerCase() + "%");
			}
		}

		if (address != null) {
			if (!StringUtils.isBlank(address.getAddress1())) {
				query.setParameter("address1", "%" + address.getAddress1().toLowerCase() + "%");
			}
			if (!StringUtils.isBlank(address.getAddress2())) {
				query.setParameter("address2", "%" + address.getAddress2().toLowerCase() + "%");
			}
			if (!StringUtils.isBlank(address.getAddress3())) {
				query.setParameter("address3", "%" + address.getAddress3().toLowerCase() + "%");
			}
			if (!StringUtils.isBlank(address.getCity())) {
				query.setParameter("city", "%" + address.getCity().toLowerCase() + "%");
			}
			if (!StringUtils.isBlank(address.getCountry())) {
				query.setParameter("country", "%" + address.getCountry().toLowerCase() + "%");
			}
			if (!StringUtils.isBlank(address.getState())) {
				query.setParameter("state", "%" + address.getState().toLowerCase() + "%");
			}
			if (!StringUtils.isBlank(address.getZipCode())) {
				query.setParameter("zipCode", "%" + address.getZipCode().toLowerCase() + "%");
			}
		}

		return query.getResultList();
	}

}
