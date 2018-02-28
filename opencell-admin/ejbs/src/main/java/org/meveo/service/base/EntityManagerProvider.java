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
package org.meveo.service.base;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.meveo.commons.utils.ParamBean;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.util.MeveoJpa;
import org.meveo.util.MeveoJpaForJobs;
import org.meveo.util.MeveoJpaForMultiTenancy;
import org.meveo.util.MeveoJpaForMultiTenancyForJobs;
import org.slf4j.Logger;

@Stateless
public class EntityManagerProvider {
    @Inject
    @MeveoJpa
    private EntityManager em;

    @Inject
    @MeveoJpaForJobs
    private EntityManager emfForJobs;

    @Inject
    private Conversation conversation;

    @Inject
    CurrentUserProvider currentUserProvider;

    @Inject
    Logger log;

    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-multiTenant-cache")
    private Cache<String, EntityManagerFactory> entityManagerFactories;

    private static boolean isMultiTenancyEnabled = ParamBean.isMultitenancyEnabled();

    @Produces
    @RequestScoped
    @MeveoJpaForMultiTenancy
    public EntityManager getEntityManager() {
        String providerCode = currentUserProvider.getCurrentUserProviderCode();

        log.trace("Produce EM for provider {}", providerCode);

        if (providerCode == null || !isMultiTenancyEnabled) {
            return em;
        }

        EntityManager currentEntityManager = createEntityManager(providerCode);

        return (EntityManager) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { EntityManager.class }, (proxy, method, args) -> {
            currentEntityManager.joinTransaction();
            return method.invoke(currentEntityManager, args);
        });
    }

    @Produces
    @RequestScoped
    @MeveoJpaForMultiTenancyForJobs
    public EntityManager getEntityManagerForJobs() {
        String providerCode = currentUserProvider.getCurrentUserProviderCode();

        log.trace("Produce EM for Jobs for provider {}", providerCode);

        if (providerCode == null || !isMultiTenancyEnabled) {
            return emfForJobs;
        }

        EntityManager currentEntityManager = createEntityManager(providerCode);

        return (EntityManager) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { EntityManager.class }, (proxy, method, args) -> {
            currentEntityManager.joinTransaction();
            return method.invoke(currentEntityManager, args);
        });
    }

    public void disposeEM(@Disposes @MeveoJpaForMultiTenancy EntityManager entityManager) {
        if (conversation != null && entityManager.isOpen()) {
            // log.error("AKK dispose @MeveoJpaForMultiTenancy EM");
            entityManager.close();
        }
    }

    // public void disposeEMForJobs(@Disposes @MeveoJpaForMultiTenancyForJobs EntityManager entityManager) {
    // if (conversation != null && entityManager.isOpen()) {
    // log.error("AKK dispose @MeveoJpaForMultiTenancy EM for Jobs");
    // entityManager.close();
    // }
    // }

    /**
     * Get entity manager for a given provider
     * 
     * @param providerCode Provider code
     * @return Entity manager instance
     */
    public EntityManager getEntityManager(String providerCode) {

        log.trace("Getting EM for provider {}", providerCode);

        boolean isMultiTenancyEnabled = ParamBean.isMultitenancyEnabled();

        if (providerCode == null || !isMultiTenancyEnabled) {
            return getDefaultEntityManager();
        }

        EntityManager currentEntityManager = null;
        if (conversation != null) {
            try {
                conversation.isTransient();
                currentEntityManager = createEntityManager(providerCode);
            } catch (Exception e) {
                currentEntityManager = createEntityManager(providerCode);
            }
        } else {
            currentEntityManager = createEntityManager(providerCode);
        }

        final EntityManager currentEntityManagerFinal = currentEntityManager;
        return (EntityManager) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { EntityManager.class }, (proxy, method, args) -> {
            currentEntityManagerFinal.joinTransaction();
            return method.invoke(currentEntityManagerFinal, args);
        });
    }

    /**
     * Get a default entity manager
     * 
     * @return Entity manager instance
     */
    private EntityManager getDefaultEntityManager() {
        EntityManager result = emfForJobs;
        if (conversation != null) {
            try {
                conversation.isTransient();
                result = em;
            } catch (Exception e) {
            }
        }

        return result;
    }

    private EntityManager createEntityManager(String providerCode) {
        log.error("Creating entity manager for provider {}", providerCode);
        return entityManagerFactories.get(providerCode).createEntityManager();
    }

    public void registerEntityManagerFactory(String providerCode) {
        log.info("Creating EMF for provider {}", providerCode);

        if (entityManagerFactories.containsKey(providerCode)) {
            return;
        }

        Map<String, String> props = new TreeMap<>();
        props.put("hibernate.default_schema", providerCode);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MeveoAdminMultiTenant", props);

        entityManagerFactories.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(providerCode, emf);
    }

    public void unregisterEntityManagerFactory(String providerCode) {

        log.trace("Removing entityManagerFactory for provider {}", providerCode);

        entityManagerFactories.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(providerCode);

        log.info("Removed entityMangerFactory for provider {}, entityManagerFactories count={}", providerCode, entityManagerFactories.size());
    }
}