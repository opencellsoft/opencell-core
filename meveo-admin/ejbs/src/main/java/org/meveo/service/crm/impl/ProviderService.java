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
package org.meveo.service.crm.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.IEntity;
import org.meveo.model.IProvider;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.index.ElasticClient;

/**
 * Provider service implementation.
 */
@Stateless
public class ProviderService extends PersistenceService<Provider> {

    @Inject
    private UserService userService;

    @Inject
    private ElasticClient elasticClient;

    public Provider findByCode(String code) {
        return findByCodeWithFetch(code, null);
    }

    public Provider findUsersProvider(String userName) {
        User user = userService.findByUsername(userName);
        if (user != null) {
            return user.getProvider();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<Provider> getProviders() {
        List<Provider> providers = (List<Provider>) getEntityManager().createQuery("from " + Provider.class.getSimpleName()).getResultList();
        return providers;
    }

    public Provider findByCodeWithFetch(String code, List<String> fetchFields) {
        QueryBuilder qb = new QueryBuilder(Provider.class, "p", fetchFields, null);

        qb.addCriterion("p.code", "=", code, true);

        try {
            return (Provider) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void create(Provider provider, User creator) throws BusinessException {
        super.create(provider, creator);
        elasticClient.createIndexes(provider);
    }
    
    /**
     * Get provider of and entity. Handles cases when entity itself is a provider
     * 
     * @param entity Entity
     * @return Provider
     */
    public static Provider getProvider(IEntity entity) {

        if (entity instanceof Provider) {
            return (Provider) entity;

        } else {
            return ((IProvider) entity).getProvider();
        }
    }
}