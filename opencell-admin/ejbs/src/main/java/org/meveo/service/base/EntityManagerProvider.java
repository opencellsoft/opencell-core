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

import javax.ejb.Stateless;
import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.commons.utils.ParamBean;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.crm.impl.ProviderRegistry;
import org.meveo.util.MeveoJpa;
import org.meveo.util.MeveoJpaForJobs;

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
    ProviderRegistry providerRegistry;

    @Inject
    @CurrentUser
    MeveoUser currentUser;

    private EntityManager currentEntityManager;

    public EntityManager getEntityManager() {
        final String currentProvider = currentUser != null ? currentUser.getProviderCode() : null;
        return getEntityManager(currentProvider);
    }

    public EntityManager getEntityManager(String currentProvider) {
        String isMultiTenancyActive = ParamBean.getInstance().getProperty("meveo.multiTenancy", "false");
        currentEntityManager = getDefaultEntityManager();
        if (currentProvider != null && Boolean.valueOf(isMultiTenancyActive)) {

            if (conversation != null) {
                try {
                    conversation.isTransient();
                    currentEntityManager = providerRegistry.createEntityManager(currentProvider);
                } catch (Exception e) {
                }
            } else {
                currentEntityManager = providerRegistry.createEntityManagerForJobs(currentProvider);
            }
        }
        return (EntityManager) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { EntityManager.class }, (proxy, method, args) -> {
            currentEntityManager.joinTransaction();
            return method.invoke(currentEntityManager, args);
        });
    }

    private EntityManager getDefaultEntityManager() {
        EntityManager result = emfForJobs;
        if (conversation != null) {
            try {
                conversation.isTransient();
                result = em;
            } catch (Exception e) {
            }
        }

        // log.debug("em.txKey={}, em.hashCode={}", txReg.getTransactionKey(),
        // em.hashCode());
        return result;
    }
}
