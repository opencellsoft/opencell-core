package org.meveo.service.crm.impl;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.base.EntityManagerProvider;
import org.slf4j.Logger;

@Stateless
public class CurrentUserProducer {

    @Inject
    private CurrentUserProvider currentUserProvider;

    @Inject
    EntityManagerProvider entityManagerProvider;

    @Inject
    Logger log;

    /**
     * produce a current user
     * 
     * @return MeveoUser
     */
    @Produces
    @RequestScoped
    @Named("currentUser")
    @CurrentUser
    public MeveoUser getCurrentUser() {
        String providerCode = currentUserProvider.getCurrentUserProviderCode();
        EntityManager em = entityManagerProvider.getEntityManager(providerCode);
        MeveoUser meveoUser = currentUserProvider.getCurrentUser(providerCode, em);

        return meveoUser;
    }
}